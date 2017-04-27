package com.akinropo.taiwo.coursemate.AllActivities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.AddUserAdapter;
import com.akinropo.taiwo.coursemate.PrivateClasses.EndlessRecyclerViewScrollListener;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.google.android.gms.common.api.Api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberGroup extends AppCompatActivity  {
    RecyclerView pickRecyler;
    ProgressBar progressBar;
    Toolbar toolbar;
    AddUserAdapter addUserAdapter;
    List<User> userList = new ArrayList<>();
    EndlessRecyclerViewScrollListener scrollListener;
    GroupRes theGroup;
    LastUserWatcher lastUserWatcher;
    private static int lastUser = 0;
    public static SparseBooleanArray alreadyMember = new SparseBooleanArray();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_member_menu,menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_group);
        theGroup = getIntent().getParcelableExtra(EndPoints.PASSED_GROUP);
        pickRecyler = (RecyclerView)findViewById(R.id.member_group_pick);
        progressBar = (ProgressBar)findViewById(R.id.coursemate_progressbar);
        toolbar = (Toolbar)findViewById(R.id.add_member_toolbar);
        toolbar.setTitle("Add member to "+ theGroup.getGroupName());
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_member_done:
                        //Toast.makeText(AddMemberGroup.this, "the count is "+addUserAdapter.getSelectedItemCount(), Toast.LENGTH_SHORT).show();
                        processMembers();
                        break;
                }
                return true;
            }
        });

        addUserAdapter = new AddUserAdapter(getApplicationContext(),userList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        pickRecyler.setLayoutManager(linearLayoutManager);
        pickRecyler.setItemAnimator(new DefaultItemAnimator());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                apiGetCoursemates(page);
            }
        };
        pickRecyler.setAdapter(addUserAdapter);
        apiGetCoursemates(1);

    }
    public void populate(List<User> mlist){
        lastUser = mlist.size() - 1;
        userList.addAll(mlist);
        lastUserWatcher = new LastUserWatcher() {
            @Override
            public void onLastUser() {
                addUserAdapter.notifyDataSetChanged();
            }
        };
        for(int i = 0;i < mlist.size();++i){
            apiCheckIfMember(mlist.get(i).getId(),i);
        }
    }
    public void processMembers(){
        if(addUserAdapter.getSelectedItemCount() > 0){
            List<Integer> members = addUserAdapter.getSelectedItems();
            List<Integer> corresponding  = new ArrayList<>();
            String memString = "";
            String memId = "";
            StringBuilder buid = new StringBuilder();
            Iterator<Integer> iterate = members.iterator();
            while (iterate.hasNext()){
                Integer i = iterate.next();
                corresponding.add(i);
                buid.append(" "+userList.get(i).getId());
                memString = memString + " "+userList.get(i).getFirstname()+" "+userList.get(i).getOthername();
                memId = memId+" "+userList.get(i).getId();
            }
            //apiAddMembers(theGroup.getGroupId(),theGroup.getGroupName(),memId);
            apiAddMembers(theGroup.getGroupId(), theGroup.getGroupName(),memId.toString().trim());
        }else {
            AddMemberGroup.this.finish();
        }
    }

    public void apiGetCoursemates(final int currentpage){
            if(currentpage == 1) ShowProgressBar(true);

            MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
            int id = manager.getId();
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getCoursemates = apiInterface.getCoursemates(id,currentpage);
            getCoursemates.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ShowProgressBar(false);
                    if (response.isSuccessful()) {
                        List<User> lUser = response.body().getCoursemates();
                        populate(lUser);
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    ShowProgressBar(false);
                }
            });

    }
    public void ShowProgressBar(final boolean show){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            pickRecyler.setVisibility(show ? View.GONE : View.VISIBLE);
            pickRecyler.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    pickRecyler.setVisibility(show ?  View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            pickRecyler.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public void apiAddMembers(int group_id,String groupName,String members){
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> addMember = apiInterface.addMembertoGroup(group_id,groupName,members);
        addMember.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    AddMemberGroup.this.finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }
    public void apiCheckIfMember(final int member, final int position){

        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> check = apiInterface.checkGroup(theGroup.getGroupId(),member);
        check.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    alreadyMember.put(member,true);
                }
                if(lastUser == position){
                    lastUserWatcher.onLastUser();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
            }
        });
    }

    public interface LastUserWatcher{
        void onLastUser();
    }

}
