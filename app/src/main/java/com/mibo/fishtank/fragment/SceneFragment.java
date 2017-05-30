package com.mibo.fishtank.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mibo.fishtank.R;


public class SceneFragment extends BaseFragment {

    public SceneFragment() {

    }

    public static SceneFragment newInstance() {
        return new SceneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene, container, false);
        return view;
    }



}
