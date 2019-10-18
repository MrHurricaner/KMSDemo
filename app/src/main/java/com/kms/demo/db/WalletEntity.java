package com.kms.demo.db;

import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.JSONUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class WalletEntity extends RealmObject {
    /**
     * 钱包id
     */
    @PrimaryKey
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
     * 钱包头图
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
     * 私钥分量sk1
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

    public WalletEntity() {

    }

    private WalletEntity(Builder builder) {
        setId(builder.id);
        setName(builder.walletName);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setBackuped(builder.isBackuped);
        setAvatar(builder.avatar);
        setSymbol(builder.symbol);
        setUserPhone(builder.phoneNumber);
        setKey(builder.key);
        setPk(builder.pk);
        setMultSk(builder.multSk);
        setMultPk(builder.multPk);
        setSk2(builder.sk2);
    }

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

    public String getKey() {
        return key;
    }

    public void setKey(String sk1) {
        this.key = sk1;
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

    public String getSk2() {
        return sk2;
    }

    public void setSk2(String sk2) {
        this.sk2 = sk2;
    }

    public static final class Builder {
        private String id;
        private String walletName;
        private long createTime;
        private long updateTime;
        private boolean isBackuped;
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

        public Builder symbol(String val) {
            symbol = val;
            return this;
        }

        public Builder avatar(int val) {
            avatar = val;
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

        public WalletEntity build() {
            return new WalletEntity(this);
        }
    }

    public Wallet parseWallet() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), Wallet.class);
    }

}
