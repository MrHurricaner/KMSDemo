package com.kms.demo.db;

import com.kms.demo.entity.Transaction;
import com.kms.demo.utils.JSONUtil;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class TransactionEntity extends RealmObject {

    @PrimaryKey
    private String id;
    /**
     * 交易hash
     */
    private String hash;
    /**
     * 交易创建时间
     */
    private long createTime;
    /**
     * 交易结束时间
     */
    private long endTime;
    /**
     * 发生交易的钱包名称
     */
    private String walletName;
    /**
     * 发送地址
     */
    private String from;
    /**
     * 接收地址
     */
    private String to;
    /**
     * 转账备注
     */
    private String note;
    /**
     * 交易的钱包头像
     */
    private int avatar;
    /**
     * 交易状态
     */
    private int transactionStatusCode;
    /**
     * 手续费
     */
    private double feeAmount;
    /**
     * 发送金额
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

    public TransactionEntity() {

    }

    private TransactionEntity(Builder builder) {
        setId(builder.id);
        setHash(builder.hash);
        setCreateTime(builder.createTime);
        setEndTime(builder.endTime);
        setWalletName(builder.walletName);
        setFrom(builder.from);
        setTo(builder.to);
        setNote(builder.note);
        setAvatar(builder.avatar);
        setTransactionStatusCode(builder.transactionStatusCode);
        setFeeAmount(builder.feeAmount);
        setSendAmount(builder.sendAmount);
        setBlockNumber(blockNumber);
        setLatestBlockNumber(latestBlockNumber);
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public int getTransactionStatusCode() {
        return transactionStatusCode;
    }

    public void setTransactionStatusCode(int transactionStatusCode) {
        this.transactionStatusCode = transactionStatusCode;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public double getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(double sendAmount) {
        this.sendAmount = sendAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public static final class Builder {
        private String id;
        private String hash;
        private long createTime;
        private long endTime;
        private String walletName;
        private String from;
        private String to;
        private String note;
        private int avatar;
        private int transactionStatusCode;
        private double feeAmount;
        private double sendAmount;
        private long blockNumber;
        private long latestBlockNumber;

        public Builder() {
            id = UUID.randomUUID().toString();
        }

        public Builder hash(String val) {
            hash = val;
            return this;
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder walletName(String val) {
            walletName = val;
            return this;
        }

        public Builder from(String val) {
            from = val;
            return this;
        }

        public Builder to(String val) {
            to = val;
            return this;
        }

        public Builder note(String val) {
            note = val;
            return this;
        }

        public Builder avatar(int val) {
            avatar = val;
            return this;
        }

        public Builder transactionStatusCode(int val) {
            transactionStatusCode = val;
            return this;
        }

        public Builder feeAmount(double val) {
            feeAmount = val;
            return this;
        }

        public Builder sendAmount(double val) {
            sendAmount = val;
            return this;
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder endTime(long val) {
            endTime = val;
            return this;
        }

        public Builder blockNumber(long val) {
            blockNumber = val;
            return this;
        }

        public Builder latestBlockNumber(long val) {
            latestBlockNumber = val;
            return this;
        }

        public TransactionEntity build() {
            return new TransactionEntity(this);
        }
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", hash='" + hash + '\'' +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                ", walletName='" + walletName + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", note='" + note + '\'' +
                ", avatar=" + avatar +
                ", transactionStatusCode=" + transactionStatusCode +
                ", feeAmount=" + feeAmount +
                ", sendAmount=" + sendAmount +
                ", blockNumber=" + blockNumber +
                ", latestBlockNumber=" + latestBlockNumber +
                '}';
    }

    public Transaction parseTransaction() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), Transaction.class);
    }
}
