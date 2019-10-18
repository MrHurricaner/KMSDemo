package com.kms.demo.engine;

import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.app.Constants;
import com.kms.demo.config.AppSettings;
import com.kms.demo.db.NodeDao;
import com.kms.demo.db.NodeEntity;
import com.kms.demo.entity.Node;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class NodeManager {


    private final static Node DEFAULT_NODE = new Node(System.currentTimeMillis(), Constants.Api.URL_WEB3J, true, true);

    private static class SingletonHolder {
        private final static NodeManager NODE_MANAGER = new NodeManager();
    }

    public static NodeManager getInstance() {
        return NodeManager.SingletonHolder.NODE_MANAGER;
    }

    private NodeManager() {

    }

    public void init() {

        if (AppSettings.getInstance().getFirstEnter()) {

            insertOrUpdate(DEFAULT_NODE.parseNodeEntity())
                    .compose(new SchedulersTransformer())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
//                            if (success) {
//                                AppSettings.getInstance().setFirstEnter(false);
//                            }
                        }
                    });
        }
    }

    public Single<String> getUrl() {
        return Single.fromCallable(new Callable<NodeEntity>() {
            @Override
            public NodeEntity call() throws Exception {
                return NodeDao.getCheckedNode();
            }
        }).onErrorResumeNext(new Single<NodeEntity>() {
            @Override
            protected void subscribeActual(SingleObserver<? super NodeEntity> observer) {
                observer.onSuccess(Node.getNullNode().parseNodeEntity());
            }
        })
                .filter(new Predicate<NodeEntity>() {
                    @Override
                    public boolean test(NodeEntity nodeEntity) throws Exception {
                        return nodeEntity != null;
                    }
                }).switchIfEmpty(new SingleSource<NodeEntity>() {
                    @Override
                    public void subscribe(SingleObserver<? super NodeEntity> observer) {
                        observer.onSuccess(Node.getNullNode().parseNodeEntity());
                    }
                }).map(new Function<NodeEntity, String>() {
                    @Override
                    public String apply(NodeEntity nodeEntity) throws Exception {
                        return nodeEntity.getNodeAddress();
                    }
                }).compose(new SchedulersTransformer());
    }

    public Single<Node> updateNodeChecked(long id, boolean checked) {
        return Single.fromCallable(new Callable<NodeEntity>() {
            @Override
            public NodeEntity call() throws Exception {
                return NodeDao.updateNodeChecked(id, checked);
            }
        }).map(new Function<NodeEntity, Node>() {

            @Override
            public Node apply(NodeEntity nodeEntity) throws Exception {
                return nodeEntity.parseNode();
            }
        });
    }

    public Single<Boolean> insertOrUpdate(NodeEntity nodeEntity) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeDao.insertNode(nodeEntity);
            }
        });
    }

    public Single<String> insertDefaultNodeAndGetUrl() {
        return insertOrUpdate(DEFAULT_NODE.parseNodeEntity())
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .toSingle()
                .flatMap(new Function<Boolean, SingleSource<? extends String>>() {
                    @Override
                    public SingleSource<? extends String> apply(Boolean aBoolean) throws Exception {
                        return getUrl();
                    }
                })
                .compose(new SchedulersTransformer());
    }


}
