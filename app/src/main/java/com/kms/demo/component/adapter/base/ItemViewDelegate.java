package com.kms.demo.component.adapter.base;


import android.content.Context;


public interface ItemViewDelegate<T> {

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(Context context, ViewHolder holder, T t, int position);


}
