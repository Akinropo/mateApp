package com.akinropo.taiwo.coursemate.AllActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.PrivateClasses.ConfirmForm;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    EditText mUsername,mPassword;
    TextView mCreateAccount;
    Button mLoginBut;
    ConfirmForm mConfirmForm;
    ProgressDialog p;
    CheckBox loginRemember;
    public static  boolean isFormFill = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        checkLoggedState();
        mCreateAccount = (TextView)findViewById(R.id.login_create_account);
        mUsername = (EditText)findViewById(R.id.login_username);
        mPassword = (EditText)findViewById(R.id.login_password);
        mLoginBut = (Button)findViewById(R.id.login_button);
        loginRemember = (CheckBox)findViewById(R.id.login_remember_me);
        List<EditText> editTextList = new ArrayList<>();
        editTextList.add(mUsername);
        editTextList.add(mPassword);
        mConfirmForm = new ConfirmForm(editTextList,mLoginBut,getResources().getDrawable(R.drawable.button_background_active));
        mConfirmForm.setUp(getApplicationContext());
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, FacebookActivity.class);
                startActivity(i);
            }
        });
        mLoginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFormFill){
                    AttemptLogin();
                }
            }
        });

    }
    public void AttemptLogin(){
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        if(!username.equals("") && !password.equals("")){
            showProgressDialog(true);
            apiCallLogin(username, password);
            //Toast.makeText(LoginActivity.this, "Begins Login", Toast.LENGTH_SHORT).show();
        }
        else {
            //show empty fields warning.
             Toast.makeText(LoginActivity.this,"Please fill form completely.",Toast.LENGTH_SHORT).show();
        }
    }
    public void apiCallLogin(String email,String password){
        Retrofit retrofit = ApiRetrofit.getClient();
        ApiInterface apiInterface= retrofit.create(ApiInterface.class);
        Call<ServerResponse> loginCall = apiInterface.login(email,password);
        loginCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showProgressDialog(false);
                if(response.isSuccessful()){
                    //Toast.makeText(LoginActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
                    User loggedUser = response.body().getData();
                    try {
                        MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
                        if(loginRemember.isChecked()){
                            manager.setIsLoggedIn(true, loggedUser.getId());
                        }else {
                            manager.setIsLoggedIn(false,loggedUser.getId());
                        }
                        String name = loggedUser.getFirstname()+" "+loggedUser.getOthername();
                        manager.setLoggedUserName(name);
                        manager.setLoggedPhoto(loggedUser.getPhoto());
                        manager.setLoggedMajor(loggedUser.getMajor());
                        subscribeToTopic();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra(EndPoints.PASSED_USER, loggedUser);
                        startActivity(i);
                        LoginActivity.this.finish();

                    }catch (Exception e){
                       // Toast.makeText(LoginActivity.this, " "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    AlertDialog taiwo = new AlertDialog.Builder(LoginActivity.this)
                            .setCancelable(true)
                            .setMessage("Incorrect email/number or Password.")
                            .setTitle("Unable to Login")
                            .create();
                    taiwo.show();
                }


            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showProgressDialog(false);
                AlertDialog taiwo = new AlertDialog.Builder(LoginActivity.this)
                        .setCancelable(true)
                        .setMessage("Check your internet connection.")
                        .setTitle("Unable to Connect")
                        .create();
                taiwo.show();
            }
        });

    }
    public void showProgressDialog(boolean show){
        if(p == null){
            p = new ProgressDialog(LoginActivity.this);
            p.setMessage("Logging in ...");
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
    public void checkLoggedState(){
        MyPreferenceManager manager  = new MyPreferenceManager(getApplicationContext());
        if(manager.isLoggedIn() && manager.getId() >0){
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            LoginActivity.this.finish();
        }
    }
    public void subscribeToTopic(){
        //this functions subscribes users to topic for them to receive notification.
        MyPreferenceManager manager  = new MyPreferenceManager(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("user__"+manager.getId());
    }
}
