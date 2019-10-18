package com.kms.demo.component.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.entity.Node;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author matrixelement
 */
public class NodeListAdapter extends RecyclerView.Adapter<NodeListAdapter.BaseViewHolder> {

    private final static int ADD_NODE_VIEW = 0;
    private final static int NODE_VIEW = 1;
    private final static String TAG = NodeListAdapter.class.getSimpleName();

    private Context context;
    private List<Node> nodeList;
    private OnDeletedNodeBtnClickListener onDeletedNodeBtnClick;
    private OnAddNodeBtnClickListener onAddNodeBtnClickListener;
    private OnNodeViewClickListener onNodeViewClickListener;
    private int mSelectedNodePosition = -1;

    public NodeListAdapter(Context context) {
        this.context = context;
    }

    public void setOnDeletedNodeBtnClickListener(OnDeletedNodeBtnClickListener listener) {
        this.onDeletedNodeBtnClick = listener;
    }

    public void setOnAddNodeBtnClickListener(OnAddNodeBtnClickListener onAddNodeBtnClickListener) {
        this.onAddNodeBtnClickListener = onAddNodeBtnClickListener;
    }

    public void setOnNodeViewClickListener(OnNodeViewClickListener onNodeViewClickListener) {
        this.onNodeViewClickListener = onNodeViewClickListener;
    }

    public int getSelectedNodePosition() {
        return mSelectedNodePosition;
    }

    public Node getNextSelectedNode(int position) {

        if (nodeList != null && !nodeList.isEmpty()) {
            if (mSelectedNodePosition != position) {
                return get(mSelectedNodePosition);
            } else {
                if (position == 0) {
                    return nodeList.get(1);
                } else {
                    return nodeList.get(0);
                }
            }
        }

        return Node.getNullNode();
    }

    public Node get(int position) {
        if (nodeList != null && position > -1 && position < nodeList.size()) {
            return nodeList.get(position);
        }

        return null;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ADD_NODE_VIEW) {
            return new AddNodeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_node_foot, parent, false));
        } else {
            return new NodeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_node, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        if (holder instanceof NodeViewHolder) {

            NodeViewHolder viewHolder = (NodeViewHolder) holder;

            Node node = nodeList.get(position);

            if (node.isChecked()) {
                mSelectedNodePosition = position;
            }

            viewHolder.ivChecked.setVisibility(node.isChecked() ? View.VISIBLE : View.INVISIBLE);
            viewHolder.ivDelete.setVisibility(node.isDefaultNode() ? View.GONE : View.VISIBLE);
            viewHolder.tvNodeAddress.setText(String.format("%s%s", node.getNodeAddress(), context.getString(R.string.default_node)));

            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeletedNodeBtnClick != null) {
                        onDeletedNodeBtnClick.onDeletedNodeBtnClick(position);
                    }
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNodeViewClickListener != null && mSelectedNodePosition != position) {
                        onNodeViewClickListener.OnNodeViewClick(mSelectedNodePosition, position);
                    }
                }
            });

        } else {
            AddNodeViewHolder viewHolder = (AddNodeViewHolder) holder;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAddNodeBtnClickListener != null) {
                        onAddNodeBtnClickListener.OnAddNodeBtnClick();
                    }
                }
            });
        }

    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {

            Node node = nodeList.get(position);

            if (node.isChecked()) {
                mSelectedNodePosition = position;
            }

            if (holder instanceof NodeViewHolder) {
                NodeViewHolder viewHolder = (NodeViewHolder) holder;
                viewHolder.ivChecked.setVisibility(node.isChecked() ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (nodeList != null) {
            return nodeList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == nodeList.size() - 1) {
            return ADD_NODE_VIEW;
        } else {
            return NODE_VIEW;
        }
    }

    static class NodeViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_checked)
        ImageView ivChecked;
        @BindView(R.id.tv_node_address)
        TextView tvNodeAddress;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        public NodeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class AddNodeViewHolder extends BaseViewHolder {


        @BindView(R.id.tv_add_node)
        TextView tvAddNode;

        public AddNodeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void notifyDataChanged(List<Node> nodeList) {
        this.nodeList = nodeList;
        notifyDataSetChanged();
    }

    public void removeItem(long id) {
        if (nodeList != null && !nodeList.isEmpty()) {
            Node node = new Node(id);
            if (nodeList.contains(node)) {
                int index = nodeList.indexOf(node);
                nodeList.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    public void insertItem(Node node) {
        if (nodeList == null) {
            nodeList = new ArrayList<>();
        }

        int size = nodeList.size();

        nodeList.add(size - 1, node);
        notifyItemInserted(nodeList.size() - 2);
    }

    public void updateItem(Node node) {
        if (nodeList != null && !nodeList.isEmpty()) {
            if (nodeList.contains(node)) {
                int index = nodeList.indexOf(node);
                nodeList.set(index, node);
                notifyItemChanged(index, "solve bug");
            }
        }
    }

    public interface OnDeletedNodeBtnClickListener {
        void onDeletedNodeBtnClick(int position);
    }

    public interface OnAddNodeBtnClickListener {
        void OnAddNodeBtnClick();
    }

    public interface OnNodeViewClickListener {
        void OnNodeViewClick(int preSelectedPosition, int newSelectedPosition);
    }
}
