package com.mibo.fishtank.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator
 * on 2017/5/30 0030.
 */

public class BaseFragment extends Fragment {
    public Context context;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
