package com.example.wangyiyun.view;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.wangyiyun.R;


public class UserFragment extends Fragment {
    private LinearLayout mLly1;
    private View rootView;
    private ImageButton mibLoader;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            rootView =inflater.inflate(R.layout.fragment_user, container, false);
        }
        initWidgets();
        mibLoader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), LoginActivity.class);//给后面开启的活动传值
                startActivity(intent);
                //getActivity().finish();
            }
        });
        return rootView;
    }
    private void initWidgets(){
        mLly1 = rootView.findViewById(R.id.lly1);
        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable1.setCornerRadius(20);
        gradientDrawable1.setColor(getResources().getColor(R.color.gray));
        mLly1.setBackground(gradientDrawable1);
        mibLoader=rootView.findViewById(R.id.user_profile_photo);
    }
}