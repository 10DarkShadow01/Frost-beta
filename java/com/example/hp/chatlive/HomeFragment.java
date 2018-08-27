package com.example.hp.chatlive;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.special.ResideMenu.ResideMenu;

public class HomeFragment extends Fragment {

    private ViewPager mViewPager;
    private  SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mSectionsPagerAdapter=new SectionsPagerAdapter(getFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_home, container, false);

        //Tabs
        mViewPager=(ViewPager)mMainView.findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter=new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout=(TabLayout)mMainView.findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        return mMainView;
    }



}
