package com.kms.demo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kms.appcore.apiservice.HttpClient;
import com.kms.appcore.networkstate.NetConnectivity;
import com.kms.appcore.networkstate.NetState;
import com.kms.demo.engine.DeviceManager;
import com.kms.demo.engine.TransactionManager;
import com.kms.demo.event.EventPublisher;

import java.util.HashMap;
import java.util.Map;

/**
 * @author matrixelement
 */
public class AppFramework {

    private final static String TAG = AppFramework.class.getSimpleName();

    private static final AppFramework APP_FRAMEWORK = new AppFramework();

    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    private AppFramework() {

    }

    public static AppFramework getAppFramework() {
        return APP_FRAMEWORK;
    }

    public void initAppFramework(Context context) {

        mContext = context;
        //注册eventBus
        EventPublisher.getInstance().register(this);
        //注册网络状态变化
        NetConnectivity.getConnectivityManager().registerNetworkStateChange(new NetStateBroadcastReceiver());
        //初始化设备管理器
        DeviceManager.getInstance().init(context);
        //网络组件初始化
        HttpClient.getInstance().init(context, Constants.Api.URL_APP_SERVER_BASE, buildUrlMap());
        //注册web3j
        TransactionManager.getInstance().init();
    }

    private Map<String, Object> buildUrlMap() {
        Map<String, Object> urlMap = new HashMap<String, Object>();
        urlMap.put(Constants.Api.NAME_KMS_URL, Constants.Api.URL_KMS_BASE_URL);
        return urlMap;
    }

    static class NetStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final NetState state = (NetState) intent.getSerializableExtra(NetConnectivity.EXTRA_NETSTATE);
            EventPublisher.getInstance().sendNetWorkStateChangedEvent(state);
        }
    }


}

