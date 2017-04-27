package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.support.v4.app.Fragment;

import com.akinropo.taiwo.coursemate.AllFragments.ChatFragment;
import com.akinropo.taiwo.coursemate.AllFragments.CmFragment;
import com.akinropo.taiwo.coursemate.AllFragments.DiscoverFragment;
import com.akinropo.taiwo.coursemate.AllFragments.MeFragment;
import com.akinropo.taiwo.coursemate.AllFragments.PostFragment;

/**
 * Created by TAIWO on 1/15/2017.
 */
public class StaticFragments {
    public static Fragment PostFragmentm;
    public static Fragment CmFragmentm;
    public static Fragment MeFragmentm;
    public static Fragment ChatFragmentm;
    public static Fragment DiscoverFragmentm;

    public static Fragment getPostFragment(){
        if(PostFragmentm == null){
            PostFragmentm = new PostFragment();
        }
        return PostFragmentm;
    }
    public static Fragment getCmFragment(){
        if(CmFragmentm == null){
            CmFragmentm = new CmFragment();
        }
        return CmFragmentm;
    }
    public static Fragment getMeFragment(){
        if(MeFragmentm == null){
            MeFragmentm = new MeFragment();
        }
        return MeFragmentm;
    }
    public static Fragment getChatFragment(){
        if(ChatFragmentm == null){
            ChatFragmentm = new ChatFragment();
        }
        return ChatFragmentm;
    }
    public static Fragment getDiscoverFragment(){
        if(DiscoverFragmentm == null){
            DiscoverFragmentm = new DiscoverFragment();
        }
        return DiscoverFragmentm;
    }
}

