package com.kms.demo.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.adapter.PhoneRegionNumberListAdapter;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.widget.ClearEditText;
import com.kms.demo.entity.Country;

import org.reactivestreams.Publisher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class SelectRegionNumberActivity extends BaseActivity {

    @BindView(R.id.et_region_number)
    ClearEditText etRegionNumber;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.list_phone_region_number)
    ListView listPhoneRegionNumber;
    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;

    private Unbinder unbinder;
    private PhoneRegionNumberListAdapter phoneRegionNumberListAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region_number);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        realm = Realm.getDefaultInstance();

        phoneRegionNumberListAdapter = new PhoneRegionNumberListAdapter(R.layout.item_phone_region_number, null);
        listPhoneRegionNumber.setAdapter(phoneRegionNumberListAdapter);
        listPhoneRegionNumber.setEmptyView(layoutEmpty);

        RxView.clicks(tvCancel).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                finish();
            }
        });

        RxTextView.textChanges(etRegionNumber).toFlowable(BackpressureStrategy.BUFFER).observeOn(AndroidSchedulers.mainThread()).switchMap(new Function<CharSequence, Publisher<? extends RealmResults<Country>>>() {
            @Override
            public Publisher<? extends RealmResults<Country>> apply(CharSequence charSequence) throws Exception {
                return realm.where(Country.class)
                        .like("countryShortEnName", String.format("*%s*", charSequence.toString()), Case.INSENSITIVE)
                        .or()
                        .like("countryEnName", String.format("*%s*", charSequence.toString()), Case.INSENSITIVE)
                        .or()
                        .like("countryZhName", String.format("*%s*", charSequence.toString()), Case.INSENSITIVE)
                        .findAllAsync().asFlowable();
            }
        }).subscribe(new Consumer<RealmResults<Country>>() {
            @Override
            public void accept(RealmResults<Country> countries) throws Exception {
                phoneRegionNumberListAdapter.notifyDataChanged(realm.copyFromRealm(countries));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

        RxAdapterView.itemClickEvents(listPhoneRegionNumber).subscribe(new Consumer<AdapterViewItemClickEvent>() {
            @Override
            public void accept(AdapterViewItemClickEvent adapterViewItemClickEvent) throws Exception {
                int position = adapterViewItemClickEvent.getPosition();
                setResult(((Country) adapterViewItemClickEvent.component1().getItemAtPosition(position)).getCountryCode().longValue());
            }
        });
    }

    private void setResult(long countryCode) {
        Intent intent = new Intent();
        intent.putExtra(Constants.Extra.EXTRA_COUNTRY_CODE, countryCode);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (realm != null) {
            realm.close();
        }
    }


    public static void actionStartForResult(Context context, int requestCode) {
        Intent intent = new Intent(context, SelectRegionNumberActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
