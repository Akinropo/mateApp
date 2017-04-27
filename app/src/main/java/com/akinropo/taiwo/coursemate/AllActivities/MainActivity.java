package com.akinropo.taiwo.coursemate.AllActivities;

//import android.support.design.widget.BottomNavigationView;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllFragments.ChatFragment;
import com.akinropo.taiwo.coursemate.AllFragments.CmFragment;
import com.akinropo.taiwo.coursemate.AllFragments.DiscoverFragment;
import com.akinropo.taiwo.coursemate.AllFragments.MeFragment;
import com.akinropo.taiwo.coursemate.AllFragments.PostFragment;
import com.akinropo.taiwo.coursemate.AllFragments.SetInterest;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.PrivateClasses.BarNotification;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.Post;
import com.akinropo.taiwo.coursemate.PrivateClasses.SetInterestInterface;
import com.akinropo.taiwo.coursemate.PrivateClasses.SetOnMyBackPressed;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class MainActivity extends AppCompatActivity implements BarNotification.SetNofificationListener,SetOnMyBackPressed {
    BottomBar bottomBar;
    FragmentTransaction fragmentTransaction;
    Handler mHandler;
    ViewPager viewPager;
    ViewAdapter viewAdapter;
    List<Fragment> fragmentList = new ArrayList<>();
    List<String> stringList = new ArrayList<>();
    Toolbar toolbar;

    ActionBar actionBar;
    BarNotification barNotification;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_acitivity_test, menu);
        checkNotification();
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barNotification = new BarNotification(getApplicationContext());
        barNotification.setListener(this);

        toolbar = (Toolbar)findViewById(R.id.mainActivity_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        final MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
        if(1 > 1){
            Handler sInterest = new Handler();
            sInterest.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SetInterest setInterest = new SetInterest();
                    setInterest.setInterFAce(new SetInterestInterface() {
                        @Override
                        public void doneSettingInterest(String interests) {
                            Toast.makeText(getApplicationContext(),"setinterest gave me "+interests,Toast.LENGTH_LONG).show();
                        }
                    },true);
                    setInterest.show(getSupportFragmentManager(), EndPoints.PASSED_USER);
                    manager.setInterest(true);
                }
            },5000);

        }
        viewPager = (ViewPager)findViewById(R.id.main_fragments);
        fragmentList.add(new PostFragment());
        fragmentList.add(new CmFragment());
        fragmentList.add(new ChatFragment());
        fragmentList.add(new DiscoverFragment());
        fragmentList.add(new MeFragment());
        //fragmentList.add(new DiscoverFeature());
        stringList.add("Post Fragment");
        stringList.add("Cm Fragment");
        stringList.add("Chat Fragment");
        stringList.add("Discover Fragment");
        stringList.add("Discover Feature");
        stringList.add("Me Fragment");
        viewAdapter = new ViewAdapter(getSupportFragmentManager(),fragmentList,stringList);
        viewPager.setAdapter(viewAdapter);
        bottomBar = (BottomBar)findViewById(R.id.main_bottom_bar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                int i = bottomBar.findPositionForTabWithId(tabId);
                viewPager.setCurrentItem(i,false);
                /*Fragment fragment = null;
                String tag = null;
                switch (tabId){
                    case R.id.tab_post:
                        fragment =StaticFragments.getPostFragment();
                        tag = EndPoints.POST_FRAGMENT;
                        break;
                    case R.id.tab_cm:
                        fragment = StaticFragments.getCmFragment();
                        tag = EndPoints.CM_FRAGMENT;
                        break;
                    case R.id.tab_chat:
                        fragment = StaticFragments.getChatFragment();
                        tag = EndPoints.CHAT_FRAGMENT;
                        break;
                    case R.id.tab_discover:
                        fragment = StaticFragments.getDiscoverFragment();
                        tag = EndPoints.DISCOVER_FRAGMENT;
                        break;
                    case R.id.tab_me:
                        fragment = StaticFragments.getMeFragment();
                        tag = EndPoints.ME_FRAGMENT;
                        /*ActionBar actionBar = getSupportActionBar();
                        if(actionBar != null){
                            actionBar.hide();
                        }
                        break;
                }
                if(fragment != null ){
                    loadFragment(fragment,tag);
                }*/
            }
        });
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                Handler mHand = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        switch (position){
                            case 1:
                                actionBar.hide();
                                ((CmFragment)fragmentList.get(1)).showTutorial();
                                break;
                            case 4:
                                actionBar.hide();
                                ((MeFragment)fragmentList.get(4)).showTutorial();
                                break;
                            case 0:
                                actionBar.show();
                                checkNotification();
                                actionBar.setTitle("");
                                break;
                            case 3:
                                actionBar.show();
                                checkNotification();
                                actionBar.setTitle("discover");
                                ((DiscoverFragment)fragmentList.get(3)).perform();
                                break;
                            case 2:
                                actionBar.show();
                                checkNotification();
                                actionBar.setTitle("chats");
                                break;
                            default:
                                actionBar.show();
                                checkNotification();
                                break;
                        }
                    }
                };
               if(runnable != null){
                   mHand.post(runnable);
               }
            }

            @Override
            public void onPageSelected(int position) {
                setNavBar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);
        //setOnlineStatus(); //call function to set online status;
        checkForUpdate(true);
        showTutorial(toolbar);
    }
    public void checkNotification(){
        barNotification.init();
    }

    public void loadFragment(final Fragment fragment,final String TAG){
        if(!fragment.isInLayout()){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragments, fragment, TAG);
                    fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };
            if(runnable != null){
                mHandler.post(runnable);
            }
        }
        else{
            Toast.makeText(MainActivity.this, "this fragment tagged "+fragment.getTag()+" has been added in the layout before. ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMessageChange(boolean state) {
        //this is triggered when there is a new message.
        toggleMessageIcon(state);
    }

    @Override
    public void onRequestChange(boolean state) {
        //this is triggered when there is a new request.
        toggleRequestIcon(state);
    }
    public void toggleMessageIcon(boolean show){
        if(show) {
            toolbar.getMenu().getItem(1).setActionView(R.layout.menu_dot_chat);
            bottomBar.getTabAtPosition(2).setBadgeCount(5);
        }
        else {
            toolbar.getMenu().getItem(1).collapseActionView();
            bottomBar.getTabAtPosition(2).removeBadge();
        }
    }
    public void toggleRequestIcon(boolean show){
        if(show){
            toolbar.getMenu().getItem(0).setActionView(R.layout.menu_dot_request);
            bottomBar.getTabAtPosition(3).setBadgeCount(5);
        }
        else {
            toolbar.getMenu().getItem(0).collapseActionView();
            bottomBar.getTabAtPosition(3).removeBadge();
        }
    }
    public void setNavBar(int pos){
        bottomBar.selectTabAtPosition(pos);
    }

    @Override
    public void setTheFragment(int position) {
        if(!(position < 1)){
            int prevPos = calcultePrevPos(position);
            viewPager.setCurrentItem(position);
            setNavBar(position);
        }
    }
    public int calcultePrevPos(int position){
        if(position > 0){
            return position-1;
        }else {
            return 4;
        }
    }

    @Override
    public void onBackPressed() {
        int curPos = viewPager.getCurrentItem();
        int prevPos = calcultePrevPos(curPos);
        viewPager.setCurrentItem(prevPos);
        setNavBar(prevPos);
    }

    public class ViewAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        public ViewAdapter(FragmentManager fm,List<Fragment> fragments,List<String> list) {
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

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        //the onback listener is attach to each fragment in this method.
        if((fragment instanceof MeFragment))  ((MeFragment)fragment).setSetOnMyBackPressed(this);
        else if(fragment instanceof ChatFragment) ((ChatFragment)fragment).setSetOnMyBackPressed(this);
        else if(fragment instanceof CmFragment) ((CmFragment)fragment).setSetOnMyBackPressed(this);
        else if(fragment instanceof PostFragment) ((PostFragment)fragment).setSetOnMyBackPressed(this);
        else if(fragment instanceof DiscoverFragment){
            ((DiscoverFragment)fragment).setSetOnMyBackPressed(this);
        }
    }

    public void setOnlineStatus(){
        MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference connectionRef = firebaseDatabase.getReference("/users/" + manager.getId() + "/connections");

        final DatabaseReference lastOnlineRef = firebaseDatabase.getReference("/users/"+manager.getId()+"/lastOnline");
        final DatabaseReference connectedRef = firebaseDatabase.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected){
                    //add this device to connection list
                    DatabaseReference con = connectionRef.push();
                    con.setValue(Boolean.TRUE);
                    con.onDisconnect().removeValue(); //remove value when device disconnects.

                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error at setOnlineStatus()", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.notification_chat:
                viewPager.setCurrentItem(2,false);
                break;
            case R.id.notification_request:
                viewPager.setCurrentItem(3,false);
                break;
            case R.id.notification_search:
                startSearchActivity();
                break;
        }
        return false;
    }
    public void checkForUpdate(boolean check){
        if(check){
            AppUpdater d = new AppUpdater(this);
            d.setUpdateFrom(UpdateFrom.XML);
            d.setUpdateXML("https://imate.herokuapp.com/app/update.xml");
            d.start();
        }
    }
    public void startSearchActivity(){
        Intent i = new Intent(MainActivity.this,AddCoursemateActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    public void showTutorial(View v){
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(true)
                .setShape(ShapeType.RECTANGLE)
                .setInfoText("Click on the search icon to search for fellow students.")
                .setTarget(v)
                .setIdempotent(true)
                .setMaskColor(R.color.colorAccent)
                .setUsageId("tool_tut") //THIS SHOULD BE UNIQUE ID
                .show();
    }

}

