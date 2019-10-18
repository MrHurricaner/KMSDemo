package com.kms.demo.component.ui.view;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseViewPageFragment;
import com.opensource.svgaplayer.SVGAImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class GuidePageFragment extends BaseViewPageFragment {

//    @BindView(R.id.animation_view)
//    SVGAImageView animationView;

    private Unbinder unbinder;


    public static GuidePageFragment newInstance(@LayoutRes int resource) {
        GuidePageFragment fragment = new GuidePageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Bundle.BUNDLE_LAYOUT_RES, resource);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onPageShow() {
//        if (!animationView.isAnimating()) {
//            animationView.startAnimation();
//        }
    }

    @Override
    protected void onPageHidden() {
//        if (animationView.isAnimating()) {
//            animationView.pauseAnimation();
//        }
    }

    @Override
    protected View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutRes = getArguments().getInt(Constants.Bundle.BUNDLE_LAYOUT_RES);
        View rootView = inflater.inflate(layoutRes, null
                , false);
        unbinder = ButterKnife.bind(this, rootView);

//        animationView.setLayerType(View.LAYER_TYPE_SOFTWARE, new Paint());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
