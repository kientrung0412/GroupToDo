package com.hanabi.todoapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.hanabi.todoapp.adapter.ToDoFragAdapter;

import java.util.ArrayList;


public class ToDoFragment extends Fragment {

    private ViewPager viewPager;
    private Activity activity;
    private ToDoFragAdapter adapter;
    private TabLayout tabLayout;

    private MyToDoFragment myToDoFragment = new MyToDoFragment();
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();

        viewPager = activity.findViewById(R.id.vp_tab_todo);
        tabLayout = activity.findViewById(R.id.tl_tab_todo);

        initFrag();
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initFrag() {
        fragments.add(myToDoFragment);
        adapter = new ToDoFragAdapter(getActivity().getSupportFragmentManager(), fragments);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}