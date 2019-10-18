package com.kms.demo.component.adapter;

import android.content.Context;

import com.kms.demo.R;
import com.kms.demo.component.adapter.base.ViewHolder;
import com.kms.demo.entity.Country;
import com.kms.demo.utils.LanguageUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author matrixelement
 */
public class PhoneRegionNumberListAdapter extends CommonAdapter<Country> {

    public PhoneRegionNumberListAdapter(int layoutId, List<Country> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Country item, int position) {
        if (item != null) {
            Locale locale = LanguageUtil.getLocale(context);
            viewHolder.setText(R.id.tv_region_name, locale == Locale.US ? item.getCountryEnName() : item.getCountryZhName());
            viewHolder.setText(R.id.tv_region_number, String.format("+%d", item.getCountryCode().longValue()));
        }
    }
}
