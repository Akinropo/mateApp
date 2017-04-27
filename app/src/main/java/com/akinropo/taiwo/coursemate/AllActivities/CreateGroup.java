package com.akinropo.taiwo.coursemate.AllActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.FirebaseChat.ChatActivity;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroup extends AppCompatActivity {
    Toolbar toolbar;
    TextView groupName;
    Button submitBut;
    MyPreferenceManager manager;
    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        toolbar = (Toolbar)findViewById(R.id.create_group_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Group");
        groupName = (TextView)findViewById(R.id.create_group_name);
        submitBut = (Button)findViewById(R.id.create_group_submit);
        manager = new MyPreferenceManager(getApplicationContext());
        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCreateGroup();
            }
        });

    }

    public void apiCreateGroup(){
        String name = manager.getLoggedName();
        String group_name = groupName.getText().toString();
        int user_id = manager.getId();
        showProgressDialog(true);
        if(!group_name.isEmpty()){
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> createGroup = apiInterface.createGroup(user_id,group_name,name);
            createGroup.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    showProgressDialog(false);
                    if(response.isSuccessful()){
                        //start the intent to the group activity
                        GroupRes res = response.body().getNewGroup();
                        if(res != null){
                            Intent i = ChatActivity.createIntentForGroup(getApplicationContext(), res);
                            startActivity(i);
                            CreateGroup.this.finish();
                        }
                        Toast.makeText(CreateGroup.this, "Group Created successfully.", Toast.LENGTH_SHORT).show();
                    }else {
                        //error response do nothing
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    //network error do nothing
                    showProgressDialog(false);
                }
            });
        }else {
            showProgressDialog(false);
            Toast.makeText(CreateGroup.this, "Fill in the Group name", Toast.LENGTH_SHORT).show();
        }

    }
    public void showProgressDialog(boolean show){
        if(p == null){
            p = new ProgressDialog(CreateGroup.this);
            p.setMessage("Creating group ...");
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

}
