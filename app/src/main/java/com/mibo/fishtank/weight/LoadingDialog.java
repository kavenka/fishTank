package com.mibo.fishtank.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mibo.fishtank.R;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class LoadingDialog {
    LoadingView mLoadingView;
    Dialog mLoadingDialog;
    private String mMsg ;

    public LoadingDialog(Context context, String msg) {
        mMsg = msg;
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(
                R.layout.loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);
        // 页面中的LoadingView
        mLoadingView = (LoadingView) view.findViewById(R.id.lv_circularring);
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void baseShow(){
        mLoadingDialog.show();
        mLoadingView.startAnim();
    }
    public void show() {
        ((TextView)mLoadingView.findViewById(R.id.loading_text)).setText(mMsg);
        mLoadingDialog.setTitle("");
        baseShow();
    }
    public void show(String msg){
        ((TextView)mLoadingView.findViewById(R.id.loading_text)).setText(msg);
        mLoadingDialog.setTitle(msg);
        baseShow();
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
