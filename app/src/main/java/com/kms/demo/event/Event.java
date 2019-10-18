package com.kms.demo.event;


import com.kms.appcore.networkstate.NetState;
import com.kms.demo.entity.Transaction;

public class Event {

    private Event() {

    }

    public static class NetWorkStateChangedEvent {

        public NetState netState;

        public NetWorkStateChangedEvent(NetState netState) {
            this.netState = netState;
        }
    }

    public static class TransactionStatusChangedEvent {

        public Transaction transaction;

        public TransactionStatusChangedEvent(Transaction transaction) {
            this.transaction = transaction;
        }
    }


}
