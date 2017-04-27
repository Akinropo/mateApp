package com.akinropo.taiwo.coursemate.AllActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllFragments.SignUpOne;
import com.akinropo.taiwo.coursemate.AllFragments.SignUpTwo;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacebookActivity extends AppCompatActivity implements SignUpOne.OnFragmentInteractionListener,SignUpTwo.OnFragmentInteractionListener {
    FragmentTransaction fragmentTransaction;
    Handler mHandler;
    View mView;
    ProgressDialog p;
    List<String> signUpValues = new ArrayList<>();
    Toolbar toolbar;
    String major,name;
    int id;
    MyPreferenceManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        manager = new MyPreferenceManager(getApplicationContext());
        toolbar = (Toolbar)findViewById(R.id.signup_toolbar);
        toolbar.setTitle(getResources().getString(R.string.sign_up));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_logo));
        setSupportActionBar(toolbar);
        mView = findViewById(R.id.facebook_activity_view);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.signup_placeholder,new SignUpOne());
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if(runnable != null){
            mHandler.post(runnable);
        }

    }

    @Override
    public void onFragmentInteraction(List<String> uri) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.signup_placeholder, new SignUpTwo());
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.commitAllowingStateLoss();
        this.name = uri.get(0) +" "+ uri.get(1);
        this.signUpValues.addAll(uri);

    }

    @Override
    public void onFragmentInteraction(String major,String faculty,String year,String highSchool) {
        showProgressDialog(true);
        signUpValues.add(major);
        signUpValues.add(faculty);
        signUpValues.add(highSchool);
        signUpValues.add(year);
        this.major = major;

        signUpUser(signUpValues);
    }
    public void showProgressDialog(boolean show){
        if(p == null){
            p = new ProgressDialog(FacebookActivity.this);
            p.setMessage("Signing up ...");
            p.getWindow().setFlags(Window.FEATURE_NO_TITLE,Window.FEATURE_NO_TITLE);
            p.setCancelable(false);
        }
        if(show){
            if(!p.isShowing()){
                p.show();
            }
        }else {
            if(p.isShowing()){
                p.dismiss();
            }
        }
    }
    public void signUpUser(List<String> stringList){
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> register = apiInterface.register(stringList.get(0),stringList.get(1),stringList.get(2),stringList.get(3),stringList.get(4),
                stringList.get(5),stringList.get(6),stringList.get(7),stringList.get(8),stringList.get(9));
        register.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showProgressDialog(false);
                if(response.isSuccessful()){
                    User signedUser = response.body().getData();
                    String message = response.body().getMessage();
                    if(message != null){

                    }else if(signedUser != null){
                        MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
                        goToHome(signedUser.getId());
                    }

                }
                else {
                    AlertDialog taiwo = new AlertDialog.Builder(FacebookActivity.this)
                            .setCancelable(true)
                            .setMessage("Try again.")
                            .setTitle("Unable to Signup")
                            .create();
                    taiwo.show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showProgressDialog(false);
                AlertDialog taiwo = new AlertDialog.Builder(FacebookActivity.this)
                        .setCancelable(true)
                        .setMessage("Check your internet connection.")
                        .setTitle("Unable to Connect")
                        .create();
                taiwo.show();
            }
        });
    }
    public void goToHome(int id){
        saveCredentials(id);
        Intent i = new Intent(FacebookActivity.this,MainActivity.class);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        startActivity(i);
    }
    public void saveCredentials(int id){
        manager.setId(id);
        manager.setIsLoggedIn(true,id);
        manager.setLoggedUserName(name);
        manager.setLoggedMajor(major);
    }
}
