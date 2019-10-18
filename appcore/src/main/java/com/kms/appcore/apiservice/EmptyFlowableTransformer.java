package com.kms.appcore.apiservice;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Consumer;

/**
 * Created by Villey on 2017/8/4.
 */

public class EmptyFlowableTransformer<U> implements FlowableTransformer<U, U> {

    public static <U> EmptyFlowableTransformer<U> get() {
        return new EmptyFlowableTransformer<U>();
    }

    @Override
    public Publisher<U> apply(Flowable<U> upstream) {
        return upstream.doOnNext(new Consumer<U>() {
            @Override
            public void accept(U u) throws Exception {

            }
        });
    }
}
