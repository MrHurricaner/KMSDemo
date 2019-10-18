package com.kms.demo.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.kms.demo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author matrixelement
 */
public enum TransactionStatus implements Parcelable {

    MPC_EXECUTING(0) {
        @Override
        public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
            return context.getResources().getString(R.string.mpc_executing);
        }

        @Override
        public int getStatusDescTextColor() {
            return R.color.color_508dff;
        }

        @Override
        public int getStatusDrawable(boolean isReceiver) {
            return R.drawable.icon_pending;
        }
    }, MPC_FAILED(1) {
        @Override
        public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
            return context.getResources().getString(R.string.mpc_failed);
        }

        @Override
        public int getStatusDescTextColor() {
            return R.color.color_ff3030;
        }

        @Override
        public int getStatusDrawable(boolean isReceiver) {
            return isReceiver ? R.drawable.icon_received : R.drawable.icon_sent;
        }
    }, PENDING(2) {
        @Override
        public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
            return context.getResources().getString(R.string.pending, String.format("%d/%d", signedBlockNumber, requiredSignNumber));
        }

        @Override
        public int getStatusDescTextColor() {
            return R.color.color_508dff;
        }

        @Override
        public int getStatusDrawable(boolean isReceiver) {
            return R.drawable.icon_pending;
        }
    }, PENDING_FAILED(3) {
        @Override
        public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
            return context.getResources().getString(R.string.failed);
        }

        @Override
        public int getStatusDescTextColor() {
            return R.color.color_ff3030;
        }

        @Override
        public int getStatusDrawable(boolean isReceiver) {
            return isReceiver ? R.drawable.icon_received : R.drawable.icon_sent;
        }
    }, COMPLETED(4) {
        @Override
        public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
            return context.getResources().getString(R.string.completed);
        }

        @Override
        public int getStatusDescTextColor() {
            return R.color.color_47ff2f;
        }

        @Override
        public int getStatusDrawable(boolean isReceiver) {
            return isReceiver ? R.drawable.icon_received : R.drawable.icon_sent;
        }
    }, FAILED(5) {
        @Override
        public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
            return context.getResources().getString(R.string.failed);
        }

        @Override
        public int getStatusDescTextColor() {
            return R.color.color_ff3030;
        }

        @Override
        public int getStatusDrawable(boolean isReceiver) {
            return isReceiver ? R.drawable.icon_received : R.drawable.icon_sent;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusCode);
    }


    public static final Creator<TransactionStatus> CREATOR = new Creator<TransactionStatus>() {
        @Override
        public TransactionStatus createFromParcel(Parcel in) {
            return TransactionStatus.values()[in.readInt()];
        }

        @Override
        public TransactionStatus[] newArray(int size) {
            return new TransactionStatus[size];
        }
    };

    public int statusCode;
    private static final Map<Integer, TransactionStatus> map = new HashMap<Integer, TransactionStatus>();

    TransactionStatus(int code) {
        this.statusCode = code;
    }

    static {
        for (TransactionStatus status : values()) {
            map.put(status.statusCode, status);
        }
    }

    private boolean isExecuting() {
        return statusCode == TransactionStatus.MPC_EXECUTING.statusCode || statusCode == TransactionStatus.PENDING.statusCode;
    }

    private boolean isFinished() {
        return statusCode == TransactionStatus.FAILED.statusCode || statusCode == TransactionStatus.MPC_FAILED.statusCode || statusCode == TransactionStatus.COMPLETED.statusCode;
    }

    public String getTransactionStatusTitle(Context context, boolean isReceiver) {
        if (isReceiver) {
            if (isExecuting()) {
                return context.getString(R.string.receiving);
            } else {
                return context.getString(R.string.received);
            }
        } else {
            if (isExecuting()) {
                return context.getString(R.string.sending);
            } else {
                return context.getString(R.string.sent);
            }
        }
    }

    public abstract String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber);

    public abstract int getStatusDescTextColor();

    public abstract int getStatusDrawable(boolean isReceiver);

    public static TransactionStatus getTransactionStatus(int statusCode) {
        return map.get(statusCode);
    }

}
