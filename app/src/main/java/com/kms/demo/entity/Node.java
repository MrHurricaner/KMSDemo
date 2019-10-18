package com.kms.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.kms.demo.db.NodeEntity;
import com.kms.demo.utils.JSONUtil;

/**
 * @author matrixelement
 */
public class Node implements Parcelable, Comparable<Node>, Nullable {

    private long mId;
    /**
     * 节点地址
     */
    private String mNodeAddress;
    /**
     * 是否是默认的节点
     */
    private boolean mDefaultNode;
    /**
     * 是否被选中
     */
    private boolean mChecked = false;

    public Node(long mId) {
        this.mId = mId;
    }

    public Node() {
    }

    public Node(long mId, String mNodeAddress, boolean mDefaultNode, boolean mChecked) {
        this.mId = mId;
        this.mNodeAddress = mNodeAddress;
        this.mDefaultNode = mDefaultNode;
        this.mChecked = mChecked;
    }

    protected Node(Parcel in) {
        mId = in.readLong();
        mNodeAddress = in.readString();
        mDefaultNode = in.readByte() != 0;
        mChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mNodeAddress);
        dest.writeByte((byte) (mDefaultNode ? 1 : 0));
        dest.writeByte((byte) (mChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Node> CREATOR = new Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel in) {
            return new Node(in);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };

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

    public Node updateChecked(boolean mChecked) {
        setChecked(mChecked);
        return this;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Node) {
            Node node = (Node) obj;
            return mId == node.mId;
        }


        return super.equals(obj);
    }

    @Override
    public int compareTo(Node o) {
        return mId - o.mId > 0 ? 1 : -1;
    }

    public static Node getNullNode() {
        return NullNode.getInstance();
    }

    public NodeEntity parseNodeEntity() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), NodeEntity.class);
    }

    @Override
    public String toString() {
        return "Node{" +
                "mId=" + mId +
                ", mNodeAddress='" + mNodeAddress + '\'' +
                ", mDefaultNode=" + mDefaultNode +
                ", mChecked=" + mChecked +
                '}';
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
