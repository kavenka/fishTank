package com.mibo.fishtank.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mibo.fishtank.R;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class AddingLoadingDialog {

    LoadingAddingDeviceView mLoadingView;
    Dialog mLoadingDialog;

    public AddingLoadingDialog(Context context) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(
                R.layout.adding_loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.add_dialog_view);
        // 页面中的LoadingView
        mLoadingView = (LoadingAddingDeviceView) view.findViewById(R.id.adding_loading_view);
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public void show() {
        mLoadingDialog.show();
        mLoadingView.startAnim();
    }

    public void close() {
        if (mLoadingDialog != null) {
            mLoadingView.stopAnim();
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public void setCancelable(boolean cancelable) {
        // 设置返回键无效
        mLoadingDialog.setCancelable(cancelable);
    }
}
