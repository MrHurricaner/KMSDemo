package com.kms.demo.db;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author matrixelement
 */
public class TransactionDao {

    private TransactionDao() {

    }

    public static boolean insertTransaction(TransactionEntity transactionEntity) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(transactionEntity);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static boolean insertTransaction(List<TransactionEntity> transactionEntityList) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(transactionEntityList);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static TransactionEntity getTransactionByHash(String hash) {
        Realm realm = null;
        TransactionEntity transactionEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            transactionEntity = realm.copyFromRealm(realm.where(TransactionEntity.class).equalTo("hash", hash).findFirst());
            realm.commitTransaction();
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return transactionEntity;
    }

    public static TransactionEntity getTransactionById(String id) {
        Realm realm = null;
        TransactionEntity transactionEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            transactionEntity = realm.copyFromRealm(realm.where(TransactionEntity.class).equalTo("id", id).findFirst());
            realm.commitTransaction();
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return transactionEntity;
    }

    public static List<TransactionEntity> getTransactionList() {

        List<TransactionEntity> list = new ArrayList<TransactionEntity>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TransactionEntity> results = realm.where(TransactionEntity.class).findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static List<TransactionEntity> getTransactionList(String address) {

        List<TransactionEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TransactionEntity> results = realm.where(TransactionEntity.class)
                    .equalTo("from", address)
                    .or()
                    .equalTo("to", address)
                    .sort("createTime", Sort.DESCENDING)
                    .findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static List<TransactionEntity> getTransactionListByPage(String address, int pageNum) {

        List<TransactionEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TransactionEntity> results = realm.where(TransactionEntity.class)
                    .equalTo("from", address)
                    .or()
                    .equalTo("to", address)
                    .sort("createTime", Sort.DESCENDING)
                    .findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static boolean updateTransaction(String id, String hash, int transactionStatusCode) {

        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TransactionEntity transactionEntity = realm.where(TransactionEntity.class).equalTo("id", id).findFirst();
                    transactionEntity.setHash(hash);
                    transactionEntity.setTransactionStatusCode(transactionStatusCode);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


}
