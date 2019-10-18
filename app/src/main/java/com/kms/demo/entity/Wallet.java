package com.kms.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kms.demo.db.WalletEntity;
import com.kms.demo.utils.JSONUtil;
import com.kms.demo.utils.WalletUtil;

import java.util.Random;

/**
 * @author matrixelement
 */
public class Wallet implements Parcelable, Nullable, Cloneable, Comparable<Wallet> {
    /**
     * 钱包id
     */
    private String id;
    /**
     * 钱包名称
     */
    private String name;
    /**
     * 钱包创建时间
     */
    private long createTime;
    /**
     * 钱包更新时间
     */
    private long updateTime;
    /**
     * 钱包是否已经备份
     */
    private boolean isBackuped;
    /**
     * 余额
     */
    private double balance;
    /**
     * 钱包头图 1,2,3,4
     */
    private int avatar;
    /**
     * 币种
     */
    private String symbol;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * json字符串，包含私钥分量sk和对应的公钥
     */
    private String key;
    /**
     * 全量公钥
     */
    private String pk;
    /**
     * 乘法器公钥
     */
    private String multPk;
    /**
     * 乘法器私钥
     */
    private String multSk;
    /**
     * 私钥分量s2
     */
    private String sk2;

    public Wallet() {

    }

    private Wallet(Builder builder) {
        setId(builder.id);
        setName(builder.walletName);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setBackuped(builder.isBackuped);
        setBalance(builder.balance);
        setAvatar(builder.avatar);
        setSymbol(builder.symbol);
        setUserPhone(builder.phoneNumber);
        setKey(builder.key);
        setPk(builder.pk);
        setMultSk(builder.multSk);
        setMultPk(builder.multPk);
        setSk2(builder.sk2);
    }

    protected Wallet(Parcel in) {
        id = in.readString();
        name = in.readString();
        createTime = in.readLong();
        updateTime = in.readLong();
        isBackuped = in.readByte() != 0;
        balance = in.readDouble();
        avatar = in.readInt();
        symbol = in.readString();
        userPhone = in.readString();
        key = in.readString();
        pk = in.readString();
        multSk = in.readString();
        multPk = in.readString();
        sk2 = in.readString();
    }

    public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel in) {
            return new Wallet(in);
        }

        @Override
        public Wallet[] newArray(int size) {
            return new Wallet[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isBackuped() {
        return isBackuped;
    }

    public void setBackuped(boolean backuped) {
        isBackuped = backuped;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getMultPk() {
        return multPk;
    }

    public void setMultPk(String multPk) {
        this.multPk = multPk;
    }

    public String getMultSk() {
        return multSk;
    }

    public void setMultSk(String multSk) {
        this.multSk = multSk;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSk2() {
        return sk2;
    }

    public void setSk2(String sk2) {
        this.sk2 = sk2;
    }

    public Wallet updateKey(String key) {
        setKey(key);
        return this;
    }

    public Wallet updateAvatar(int avatar) {
        setAvatar(avatar);
        return this;
    }

    public Wallet generateRandomAvatar() {
        setAvatar(new Random().nextInt(4) + 1);
        return this;
    }

    public Wallet updateMultPk(String multPk){
        setMultPk(multPk);
        return this;
    }

    public Wallet updateMultSk(String multSk){
        setMultSk(multSk);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeByte((byte) (isBackuped ? 1 : 0));
        dest.writeDouble(balance);
        dest.writeInt(avatar);
        dest.writeString(symbol);
        dest.writeString(userPhone);
        dest.writeString(key);
        dest.writeString(pk);
        dest.writeString(multSk);
        dest.writeString(multPk);
        dest.writeString(sk2);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Wallet) {
            Wallet wallet = (Wallet) obj;
            return !TextUtils.isEmpty(id) && id.equals(wallet.id);
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(Wallet wallet) {
        return wallet != null && getCreateTime() > wallet.getCreateTime() ? 1 : -1;
    }

    public static final class Builder {
        private String id;
        private String walletName;
        private long createTime;
        private long updateTime;
        private boolean isBackuped;
        private double balance;
        private int avatar;
        private String symbol;
        private String phoneNumber;
        private String key;
        private String pk;
        private String multPk;
        private String multSk;
        private String sk2;

        public Builder(String val) {
            id = val;
            avatar = new Random().nextInt(4) + 1;
        }

        public Builder walletName(String val) {
            walletName = val;
            return this;
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder updateTime(long val) {
            updateTime = val;
            return this;
        }

        public Builder isBackuped(boolean val) {
            isBackuped = val;
            return this;
        }

        public Builder balance(double val) {
            balance = val;
            return this;
        }

        public Builder symbol(String val) {
            symbol = val;
            return this;
        }

        public Builder phoneNumber(String val) {
            phoneNumber = val;
            return this;
        }

        public Builder key(String val) {
            key = val;
            return this;
        }

        public Builder pk(String val) {
            pk = val;
            return this;
        }

        public Builder multSk(String val) {
            multSk = val;
            return this;
        }

        public Builder multPk(String val) {
            multPk = val;
            return this;
        }

        public Builder sk2(String val) {
            sk2 = val;
            return this;
        }

        public Wallet build() {
            return new Wallet(this);
        }

    }

    public String getWalletAddress() {

        if (TextUtils.isEmpty(pk)) {
            return null;
        }

        String walletAddress = WalletUtil.getWalletAddressByPublicKey(pk);

        if (TextUtils.isEmpty(walletAddress)) {
            return null;
        }
        if (walletAddress.toLowerCase().startsWith("0x")) {
            return walletAddress;
        }
        return "0x" + walletAddress;
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

    public WalletEntity parseWalletEntity() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), WalletEntity.class);
    }

    @Override
    public Wallet clone() {

        Wallet wallet = null;
        try {
            wallet = (Wallet) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return wallet;
    }
}
