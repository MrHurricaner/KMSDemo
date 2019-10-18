package com.kms.demo.component.ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public interface OnDialogViewClickListener {

    void onDialogViewClick(DialogFragment fragment, View view, Bundle extra);
}
