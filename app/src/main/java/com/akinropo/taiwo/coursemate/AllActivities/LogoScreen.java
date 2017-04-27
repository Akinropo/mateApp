package com.akinropo.taiwo.coursemate.AllActivities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.R;

public class LogoScreen extends AppCompatActivity {
    public Thread mThread;
    MyPreferenceManager manager;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_screen);
        manager = new MyPreferenceManager(getApplicationContext());
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //start loginAcitivity from here;
                Intent i;
                if(manager.isLaunchFirst()){
                     i = new Intent(LogoScreen.this,MateIntro.class);
                    manager.setLaunchFirst(false);
                }else {
                    i = new Intent(LogoScreen.this,LoginActivity.class);
                }
                startActivity(i);
                LogoScreen.this.finish();
            }
        },2000);
    }
}
