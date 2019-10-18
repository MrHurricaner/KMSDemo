package com.kms.demo.event;

import android.content.Context;

import com.kms.appcore.networkstate.NetState;
import com.kms.demo.entity.Transaction;

public class EventPublisher {

    private static final String TAG = "Portal.EventPublisher";

    public static final String ACTION_LOGIN = "com.juzix.wallet.ACTION_LOGIN";

    private Context context;

    private static EventPublisher instance = new EventPublisher();

    private EventPublisher() {
    }

    public static EventPublisher getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public static String getTag() {
        return TAG;
    }

    public void register(Object obj) {
        BusProvider.register(obj);
    }

    public void unRegister(Object obj) {
        BusProvider.unRegister(obj);
    }

    /**
     * 网络状态改变
     *
     * @param netState
     */
    public void sendNetWorkStateChangedEvent(NetState netState) {
        BusProvider.post(new Event.NetWorkStateChangedEvent(netState));
    }

    /**
     * 交易状态改变
     *
     * @param transaction
     */
    public void sendTransactionStatusChangedEvent(Transaction transaction) {
        BusProvider.post(new Event.TransactionStatusChangedEvent(transaction));
    }


}
