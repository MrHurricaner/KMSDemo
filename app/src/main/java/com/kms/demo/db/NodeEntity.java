package com.kms.demo.db;

import com.kms.demo.entity.Node;
import com.kms.demo.utils.JSONUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

/**
 * @author matrixelement
 */
public class NodeEntity extends RealmObject {

    @PrimaryKey
    @RealmField(name = "id")
    private long mId;
    /**
     * 节点地址
     */
    @RealmField(name = "nodeAddress")
    private String mNodeAddress;
    /**
     * 是否是默认的节点
     */
    @RealmField(name = "isDefaultNode")
    private boolean mDefaultNode;
    /**
     * 是否被选中
     */
    @RealmField(name = "isChecked")
    private boolean mChecked = false;

    public NodeEntity() {
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getNodeAddress() {
        return mNodeAddress;
    }

    public void setNodeAddress(String mNodeAddress) {
        this.mNodeAddress = mNodeAddress;
    }

    public boolean isDefaultNode() {
        return mDefaultNode;
    }

    public void setDefaultNode(boolean mDefaultNode) {
        this.mDefaultNode = mDefaultNode;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }

    public Node parseNode(){
        return JSONUtil.parseObject(JSONUtil.toJSONString(this),Node.class);
    }

}
