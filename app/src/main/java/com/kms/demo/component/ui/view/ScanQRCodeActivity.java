package com.kms.demo.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureProviderHandler;
import com.google.zxing.decoding.ICaptureProvider;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.view.ViewfinderView;
import com.jakewharton.rxbinding3.view.RxView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.widget.CommonTitleBar;
import com.kms.demo.config.PermissionConfigure;
import com.kms.demo.utils.PhotoUtil;
import com.kms.demo.utils.QRCodeDecoder;

import org.reactivestreams.Subscription;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;


/**
 * @author ziv
 */
public class ScanQRCodeActivity extends BaseActivity implements ICaptureProvider, SurfaceHolder.Callback {

    private static final int REQUEST_CODE_SCAN_GALLERY = 100;
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.scanner_view)
    SurfaceView scannerView;
    @BindView(R.id.viewfinder_content)
    ViewfinderView viewfinderContent;
    @BindView(R.id.iv_flash)
    ImageView ivFlash;

    private CaptureProviderHandler handler;
    private Vector<BarcodeFormat> decodeFormats;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private Uri photoUri;
    private String characterSet;
    private boolean isFlashOn = false;
    private boolean hasSurface;
    private boolean playBeep;
    private boolean vibrate;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        hasSurface = false;

        CameraManager.init(getApplication());

        inactivityTimer = new InactivityTimer(this);

        RxView.clicks(ivFlash).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                try {
                    boolean isSuccess = CameraManager.get().setFlashLight(!isFlashOn);
                    if (!isSuccess) {
                        return;
                    }
                    ivFlash.setImageResource(isFlashOn ? R.drawable.icon_flash_off : R.drawable.icon_flash_on);
                    isFlashOn = !isFlashOn;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        commonTitleBar.setRightTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    handleAlbumPic(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openAlbum() {
        final BaseActivity activity = currentActivity();

        requestPermission(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SCAN_GALLERY);
            }

            @Override
            public void onHasPermission(int what) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SCAN_GALLERY);
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {

            }
        }, Constants.Permission.WRITE_STORAG);
    }

    private void handleAlbumPic(Intent data) {

        photoUri = Uri.parse(PhotoUtil.getPath(this, data.getData()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(photoUri.getPath()));
        }

        Flowable.just(photoUri).takeWhile(new Predicate<Uri>() {
            @Override
            public boolean test(Uri uri) throws Exception {
                return uri != null;
            }
        }).map(new Function<Uri, Result>() {

            @Override
            public Result apply(Uri uri) throws Exception {
                return QRCodeDecoder.syncDecodeQRCode(PhotoUtil.getDecodeAbleBitmap(ScanQRCodeActivity.this, photoUri));
            }
        }).groupBy(new Function<Result, Boolean>() {

            @Override
            public Boolean apply(Result result) throws Exception {
                return result != null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnTerminate(new Action() {
            @Override
            public void run() throws Exception {
                dismissLoadingDialogImmediately();
            }
        }).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(Subscription subscription) throws Exception {
                showLoadingDialog();
            }
        }).subscribe(new Consumer<GroupedFlowable<Boolean, Result>>() {
            @Override
            public void accept(GroupedFlowable<Boolean, Result> booleanResultGroupedFlowable) throws Exception {
                if (booleanResultGroupedFlowable.getKey()) {
                    booleanResultGroupedFlowable.subscribe(new Consumer<Result>() {
                        @Override
                        public void accept(Result result) throws Exception {
                            setResult(result.getText());
                        }
                    });
                } else {
                    showLongToast(R.string.invalid_qr_code);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    private void setResult(String result) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, result);
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureProviderHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderContent;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        //FIXME
        if (TextUtils.isEmpty(resultString)) {
            showLongToast(R.string.invalid_qr_code);
        } else {
            setResult(resultString);
        }
    }

    @Override
    public void onScanResult(int resultCode, Intent data) {
        String scanResult = data.getExtras().getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
        showLongToast(scanResult);
        setResult(resultCode, data);
        finish();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //When the beep has finished playing, rewind to queue up another one.
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(0);
                }
            });
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    public static void actionStartForResult(Context context, int requestCode) {
        Intent intent = new Intent(context, ScanQRCodeActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void actionStartForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), ScanQRCodeActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void actionStartWithAction(Context context, String action) {
        Intent intent = new Intent(context, ScanQRCodeActivity.class);
        context.startActivity(intent);
    }
}
