package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.app.LoadingTransformer;
import com.kms.demo.component.adapter.NodeListAdapter;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.ui.dialog.ConfirmDialogFragment;
import com.kms.demo.component.ui.dialog.OnConfirmBtnClickListener;
import com.kms.demo.component.widget.NodeListDecoration;
import com.kms.demo.component.widget.WrapContentLinearLayoutManager;
import com.kms.demo.db.NodeDao;
import com.kms.demo.db.NodeEntity;
import com.kms.demo.engine.NodeManager;
import com.kms.demo.engine.TransactionManager;
import com.kms.demo.entity.Node;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class NodeSettingsActivity extends BaseActivity {

    private final static String TAG = NodeSettingsActivity.class.getSimpleName();

    @BindView(R.id.list_nodes)
    RecyclerView listNodes;
    @BindString(R.string.delete_node_tips)
    String deleteNodeTips;

    private Unbinder unbinder;
    private NodeListAdapter mNodeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_settings);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        mNodeListAdapter = new NodeListAdapter(this);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listNodes.setLayoutManager(layoutManager);
        listNodes.addItemDecoration(new NodeListDecoration(this));
        listNodes.setAdapter(mNodeListAdapter);

        mNodeListAdapter.setOnDeletedNodeBtnClickListener(new NodeListAdapter.OnDeletedNodeBtnClickListener() {
            @Override
            public void onDeletedNodeBtnClick(int position) {

                ConfirmDialogFragment.getInstance(deleteNodeTips).setOnConfirmBtnClickListener(new OnConfirmBtnClickListener() {
                    @Override
                    public void onConfirmBtnClick() {

                        Node node = mNodeListAdapter.get(position);

                        Node nextSelectedNode = mNodeListAdapter.getNextSelectedNode(position);

                        Single<Object> single = null;

                        if (node.isChecked()) {
                            single = deleteCurrentSelectedItem(node.getId(), nextSelectedNode.getId());
                        } else {
                            single = deleteNotCurrentSelectedItem(node.getId());
                        }

                        single
                                .compose(new SchedulersTransformer())
                                .compose(LoadingTransformer.bindToLifecycle(NodeSettingsActivity.this))
                                .subscribe(new Consumer() {
                                    @Override
                                    public void accept(Object o) throws Exception {
                                        mNodeListAdapter.removeItem(node.getId());
                                        if (o instanceof Node) {
                                            TransactionManager.getInstance().setWeb3jUrl(nextSelectedNode.getNodeAddress());
                                            mNodeListAdapter.updateItem(nextSelectedNode.updateChecked(true));
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        showLongToast(R.string.delete_node_address_failed);
                                    }
                                });
                    }
                }).show(getSupportFragmentManager(), "showConfirmDialog");
            }
        });

        mNodeListAdapter.setOnAddNodeBtnClickListener(new NodeListAdapter.OnAddNodeBtnClickListener() {
            @Override
            public void OnAddNodeBtnClick() {
                int currentSelectedPosition = mNodeListAdapter.getSelectedNodePosition();
                Node currentSelectedNode = mNodeListAdapter.get(currentSelectedPosition);
                AddNodeActivity.actionStartWithExtraForResult(NodeSettingsActivity.this, currentSelectedNode == null ? 0 : currentSelectedNode.getId());
            }
        });

        mNodeListAdapter.setOnNodeViewClickListener(new NodeListAdapter.OnNodeViewClickListener() {
            @Override
            public void OnNodeViewClick(int preSelectedPosition, int position) {
                Node preSelectedNode = mNodeListAdapter.get(preSelectedPosition);
                Node newSelectedNode = mNodeListAdapter.get(position);
                if (preSelectedNode != null && newSelectedNode != null) {

                    TransactionManager.getInstance().setWeb3jUrl(newSelectedNode.getNodeAddress());

                    NodeManager.getInstance().updateNodeChecked(preSelectedNode.getId(), false)
                            .concatWith(NodeManager.getInstance().updateNodeChecked(newSelectedNode.getId(), true))
                            .subscribe(new Consumer<Node>() {
                                @Override
                                public void accept(Node node) throws Exception {
                                    mNodeListAdapter.updateItem(node);
                                }
                            });
                }

            }
        });

        Flowable.fromIterable(NodeDao.getNodeEntityList())
                .map(new Function<NodeEntity, Node>() {

                    @Override
                    public Node apply(NodeEntity nodeEntity) throws Exception {
                        return nodeEntity.parseNode();
                    }
                })
                .toList()
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(this))
                .subscribe(new BiConsumer<List<Node>, Throwable>() {
                    @Override
                    public void accept(List<Node> nodes, Throwable throwable) throws Exception {
                        nodes.add(Node.getNullNode());
                        mNodeListAdapter.notifyDataChanged(nodes);
                    }
                });

    }

    private Single<Object> deleteCurrentSelectedItem(long id, long nextSelectedItemId) {

        return Single.just(NodeDao.deleteNode(id)).flatMap(new Function<Boolean, SingleSource<Object>>() {
            @Override
            public SingleSource<Object> apply(Boolean aBoolean) throws Exception {
                return Single.just(NodeDao.updateNodeChecked(nextSelectedItemId, true)).map(new Function<NodeEntity, Object>() {

                    @Override
                    public Object apply(NodeEntity nodeEntity) throws Exception {
                        return nodeEntity.parseNode();
                    }
                });
            }
        });

    }

    private Single<Object> deleteNotCurrentSelectedItem(long id) {
        return Single.just(NodeDao.deleteNode(id));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constants.RequestCode.REQUEST_CODE_ADD_NODE) {
                Node newSelectedNode = data.getParcelableExtra(Constants.Extra.EXTRA_NEW_SELECTED_NODE);
                Node preSelectedNode = data.getParcelableExtra(Constants.Extra.EXTRA_PRE_SELECTED_NODE);
                mNodeListAdapter.insertItem(newSelectedNode);
                mNodeListAdapter.updateItem(preSelectedNode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, NodeSettingsActivity.class);
        context.startActivity(intent);
    }
}
