package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.app.LoadingTransformer;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.widget.CustomUnderlineEditText;
import com.kms.demo.engine.NodeManager;
import com.kms.demo.engine.TransactionManager;
import com.kms.demo.entity.Node;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class AddNodeActivity extends BaseActivity {

    private final static String NODE_ADDRESS_REGREX = "^(http(s?)://)?((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{4})";

    @BindView(R.id.et_node_address)
    CustomUnderlineEditText etNodeAddress;
    @BindView(R.id.tv_node_address_error)
    TextView tvNodeAddressError;
    @BindView(R.id.btn_add_node)
    Button btnAddNode;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_node);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        long selectedNodeId = getIntent().getLongExtra(Constants.Extra.EXTRA_SELECTED_NODE_ID, 0);

        RxTextView.textChanges(etNodeAddress).skipInitialValue().subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                btnAddNode.setEnabled(!TextUtils.isDigitsOnly(charSequence));
            }
        });

        RxView.clicks(btnAddNode).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                String nodeAddress = etNodeAddress.getText().toString().trim();
                if (!nodeAddress.matches(NODE_ADDRESS_REGREX)) {
                    showNodeAddressErrMsg(string(R.string.incorrect_node_address));
                } else {

                    Node node = new Node(System.currentTimeMillis(), nodeAddress, false, true);

                    NodeManager.getInstance()
                            .insertOrUpdate(node.parseNodeEntity())
                            .filter(new Predicate<Boolean>() {
                                @Override
                                public boolean test(Boolean aBoolean) throws Exception {
                                    return aBoolean;
                                }
                            })
                            .switchIfEmpty(new SingleSource<Boolean>() {
                                @Override
                                public void subscribe(SingleObserver<? super Boolean> observer) {
                                    observer.onError(new Throwable());
                                }
                            })
                            .flatMap(new Function<Boolean, SingleSource<Node>>() {
                                @Override
                                public SingleSource<Node> apply(Boolean aBoolean) throws Exception {
                                    if (selectedNodeId == 0) {
                                        return Single.just(Node.getNullNode());
                                    } else {
                                        return NodeManager.getInstance().updateNodeChecked(selectedNodeId, false);
                                    }

                                }
                            })
                            .compose(new SchedulersTransformer())
                            .compose(LoadingTransformer.bindToLifecycle(AddNodeActivity.this))
                            .compose(bindToLifecycle())
                            .subscribe(new Consumer<Node>() {
                                @Override
                                public void accept(Node n) throws Exception {
                                    TransactionManager.getInstance().setWeb3jUrl(node.getNodeAddress());
                                    setResult(node, n);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    showLongToast(R.string.invalid_node_address);
                                }
                            });
                }
            }
        });

    }

    private void showNodeAddressErrMsg(String errMsg) {
        tvNodeAddressError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvNodeAddressError.setText(errMsg);
        etNodeAddress.setStatus(TextUtils.isEmpty(errMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    private void setResult(Node newSelectedNode, Node oldSelectedNode) {
        Intent intent = new Intent();
        intent.putExtra(Constants.Extra.EXTRA_NEW_SELECTED_NODE, newSelectedNode);
        intent.putExtra(Constants.Extra.EXTRA_PRE_SELECTED_NODE, oldSelectedNode);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStartWithExtraForResult(Context context, long selectedNodeId) {
        Intent intent = new Intent(context, AddNodeActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_SELECTED_NODE_ID, selectedNodeId);
        ((BaseActivity) context).startActivityForResult(intent, Constants.RequestCode.REQUEST_CODE_ADD_NODE);
    }
}
