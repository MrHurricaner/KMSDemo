package com.kms.demo.db;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class NodeDao {

    private NodeDao() {

    }

    public static boolean insertNode(NodeEntity nodeEntity) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(nodeEntity);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return false;
    }

    public static boolean deleteNode(long id) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    NodeEntity nodeEntity = realm.where(NodeEntity.class).equalTo("mId", id).findFirst();
                    nodeEntity.deleteFromRealm();
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return false;
    }

    public static List<NodeEntity> getNodeEntityList() {

        List<NodeEntity> list = new ArrayList<NodeEntity>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<NodeEntity> results = realm.where(NodeEntity.class).sort("mId").findAll();
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

    public static NodeEntity updateNodeChecked(long id, boolean checked) {

        Realm realm = null;

        NodeEntity node = null;

        try {
            realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            NodeEntity nodeEntity = realm.where(NodeEntity.class).equalTo("mId", id).findFirst();
            if (nodeEntity != null) {
                nodeEntity.setChecked(checked);
                node = realm.copyFromRealm(nodeEntity);
            }
            realm.commitTransaction();

            return node;

        } catch (Exception exp) {
            exp.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return null;
    }

    public static NodeEntity getCheckedNode() {

        Realm realm = null;
        NodeEntity nodeEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            NodeEntity node = realm.where(NodeEntity.class).sort("mId").equalTo("mChecked", true).findFirst();
            if (node != null) {
                nodeEntity = realm.copyFromRealm(node);
            }
            realm.commitTransaction();
        } catch (Exception exp) {
            exp.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return nodeEntity;
    }
}
