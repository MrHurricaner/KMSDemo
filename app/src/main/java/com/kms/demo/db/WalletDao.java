package com.kms.demo.db;

import com.kms.demo.db.WalletEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class WalletDao {

    private WalletDao() {

    }

    public static List<WalletEntity> getWalletEntityList() {

        Realm realm = null;

        List<WalletEntity> walletEntityList = new ArrayList<>();

        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> realmResults = realm.where(WalletEntity.class).sort("createTime").findAll();
            walletEntityList.addAll(realm.copyFromRealm(realmResults));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return walletEntityList;
    }

    public static boolean insertOrUpdate(List<WalletEntity> walletEntityList) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(walletEntityList);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static boolean insertOrUpdate(WalletEntity walletEntity) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(walletEntity);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static String getKey(String id) {

        Realm realm = null;
        WalletEntity walletEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            walletEntity = realm.copyFromRealm(realm.where(WalletEntity.class).equalTo("id", id).findFirst());
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return walletEntity == null ? null : walletEntity.getKey();

    }

    public static int getAvatar(String id) {

        Realm realm = null;
        WalletEntity walletEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            walletEntity = realm.copyFromRealm(realm.where(WalletEntity.class).equalTo("id", id).findFirst());
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        if (walletEntity == null || walletEntity.getAvatar() == 0) {
            return new Random().nextInt(4) + 1;
        } else {
            return walletEntity.getAvatar();
        }
    }

    public static WalletEntity getWalletEntity(String id) {

        Realm realm = null;
        WalletEntity walletEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            walletEntity = realm.copyFromRealm(realm.where(WalletEntity.class).equalTo("id", id).findFirst());
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletEntity;
    }

}
