package com.akinropo.taiwo.coursemate.AllFragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akinropo.taiwo.coursemate.PrivateClasses.SetOnMyBackPressed;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CmFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewAdapter viewAdapter;
    List<Fragment> mfragments = new ArrayList<>();
    List<String> fragmentTitles = new ArrayList<>();
    SetOnMyBackPressed setOnMyBackPressed;

    public CmFragment() {
        // Required empty public constructor
        mfragments.add(new CoursemateFragment());
        mfragments.add(new GroupFragment());
        String cm = "mates";
        String group = "groups";
        fragmentTitles.add(cm);
        fragmentTitles.add(group);
    }

    public void setSetOnMyBackPressed(SetOnMyBackPressed setOnMyBackPressed) {
        this.setOnMyBackPressed = setOnMyBackPressed;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cm, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.cm_tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.cm_viewpager);
        viewAdapter = new ViewAdapter(getChildFragmentManager(), mfragments, fragmentTitles);
        viewPager.setAdapter(viewAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getContext().getResources().getColor(R.color.primary_text),
                getContext().getResources().getColor(R.color.colorPrimaryDark));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        return view;
    }

    public void showTutorial() {
        ((CoursemateFragment) mfragments.get(0)).showTutorial();
    }

    public class ViewAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();

        public ViewAdapter(FragmentManager fm, List<Fragment> fragments, List<String> list) {
            super(fm);
            this.fragmentList = fragments;
            this.stringList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return stringList.get(position);
        }
    }

}
