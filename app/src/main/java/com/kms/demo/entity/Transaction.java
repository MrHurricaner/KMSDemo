package com.kms.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.kms.demo.db.TransactionEntity;
import com.kms.demo.utils.JSONUtil;

import androidx.annotation.NonNull;

/**
 * @author matrixelement
 */
public class Transaction implements Cloneable, Parcelable, Comparable<Transaction> {

    private String id;
    /**
     * 交易hash
     */
    private String hash;
    /**
     * 交易发送方地址
     */
    @JSONField(name = "from")
    private String fromAddress;
    /**
     * 交易接收方地址
     */
    @JSONField(name = "to")
    private String toAddress;
    /**
     * 交易创建时间
     */
    private long createTime;
    /**
     * 交易结束时间
     */
    private long endTime;
    /**
     * 交易金额
     */
    private double sendAmount;
    /**
     * 当前交易区块
     */
    private long blockNumber;
    /**
     * 最新交易区块
     */
    private long latestBlockNumber;
    /**
     * 发生交易的签名名称
     */
    private String walletName;
    /**
     * 手续费
     */
    private double feeAmount;
    /**
     * 转账备注
     */
    private String note;
    /**
     * 钱包头图 1,2,3,4
     */
    private int avatar;
    /**
     * 交易状态
     */
    private int transactionStatusCode;

    public Transaction() {

    }

    private Transaction(Builder builder) {
        setId(builder.id);
        setHash(builder.hash);
        setFromAddress(builder.fromAddress);
        setToAddress(builder.toAddress);
        setCreateTime(builder.createTime);
        setEndTime(builder.endTime);
        setSendAmount(builder.sendAmount);
        setBlockNumber(builder.blockNumber);
        setLatestBlockNumber(builder.latestBlockNumber);
        setWalletName(builder.walletName);
        setFeeAmount(builder.feeAmount);
        setNote(builder.note);
        setAvatar(builder.avatar);
        setTransactionStatusCode(builder.transactionStatusCode);
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        hash = in.readString();
        fromAddress = in.readString();
        toAddress = in.readString();
        createTime = in.readLong();
        endTime = in.readLong();
        sendAmount = in.readDouble();
        blockNumber = in.readLong();
        latestBlockNumber = in.readLong();
        walletName = in.readString();
        feeAmount = in.readDouble();
        note = in.readString();
        avatar = in.readInt();
        transactionStatusCode = in.readInt();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(double sendAmount) {
        this.sendAmount = sendAmount;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public long getLatestBlockNumber() {
        return latestBlockNumber;
    }

    public void setLatestBlockNumber(long latestBlockNumber) {
        this.latestBlockNumber = latestBlockNumber;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public long getSignedBlockNumber() {
        return blockNumber == 0 ? 0 : latestBlockNumber - blockNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAvatar() {
        return avatar;
    }

    public int getTransactionStatusCode() {
        return transactionStatusCode;
    }

    public void setTransactionStatusCode(int transactionStatusCode) {
        this.transactionStatusCode = transactionStatusCode;
    }

    public TransactionStatus getTransactionStatus() {
        return TransactionStatus.getTransactionStatus(transactionStatusCode);
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Transaction updateLatestBlockNumber(long latestBlockNumber){
        setLatestBlockNumber(latestBlockNumber);
        return this;
    }

    public boolean isReceiver(String walletAddress) {
        return !TextUtils.isEmpty(toAddress) && toAddress.equals(walletAddress);
    }

    public String getSmallAvatar() {
        return "icon_avatar_small_" + avatar;
    }

    public String getMediumAvatar() {
        return "icon_avatar_medium_" + avatar;
    }

    public String getLargeAvatar() {
        return "icon_avatar_large_" + avatar;
    }

    public TransactionEntity parseTransactionEntity() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), TransactionEntity.class);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(hash);
        dest.writeString(fromAddress);
        dest.writeString(toAddress);
        dest.writeLong(createTime);
        dest.writeLong(endTime);
        dest.writeDouble(sendAmount);
        dest.writeLong(blockNumber);
        dest.writeLong(latestBlockNumber);
        dest.writeString(walletName);
        dest.writeDouble(feeAmount);
        dest.writeString(note);
        dest.writeInt(avatar);
        dest.writeInt(transactionStatusCode);
    }

    @Override
    public int compareTo(@NonNull Transaction o) {
        return Long.compare(o.createTime, createTime);
    }

    public static final class Builder {
        private String id;
        private String hash;
        private String fromAddress;
        private String toAddress;
        private long createTime;
        private long endTime;
        private double sendAmount;
        private long blockNumber;
        private long latestBlockNumber;
        private String walletName;
        private double feeAmount;
        private String note;
        private int avatar;
        private int transactionStatusCode;

        public Builder(String id, long createTime, String walletName) {
            this.id = id;
            this.createTime = createTime;
            this.walletName = walletName;
        }

        public Builder fromAddress(String val) {
            fromAddress = val;
            return this;
        }

        public Builder toAddress(String val) {
            toAddress = val;
            return this;
        }

        public Builder sendAmount(double val) {
            sendAmount = val;
            return this;
        }

        public Builder blockNumber(long va1) {
            blockNumber = va1;
            return this;
        }

        public Builder latestBlockNumber(long va1) {
            latestBlockNumber = va1;
            return this;
        }

        public Builder feeAmount(double va1) {
            feeAmount = va1;
            return this;
        }

        public Builder note(String va1) {
            note = va1;
            return this;
        }

        public Builder hash(String va1) {
            hash = va1;
            return this;
        }

        public Builder avatar(int va1) {
            avatar = va1;
            return this;
        }

        public Builder transactionStatusCode(int va1) {
            transactionStatusCode = va1;
            return this;
        }

        public Builder endTime(long va1) {
            endTime = va1;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    @Override
    public Transaction clone() {
        Transaction transactionEntity = null;
        try {
            transactionEntity = (Transaction) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return transactionEntity;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", hash='" + hash + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", createTime=" + createTime +
                ", sendAmount=" + sendAmount +
                ", blockNumber=" + blockNumber +
                ", latestBlockNumber=" + latestBlockNumber +
                ", walletName='" + walletName + '\'' +
                ", feeAmount=" + feeAmount +
                ", note='" + note + '\'' +
                ", avatar=" + avatar +
                ", transactionStatusCode=" + transactionStatusCode +
                '}';
    }


}
