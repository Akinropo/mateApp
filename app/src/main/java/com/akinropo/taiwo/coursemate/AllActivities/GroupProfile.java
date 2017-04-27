package com.akinropo.taiwo.coursemate.AllActivities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllFragments.CoursemateFragment;
import com.akinropo.taiwo.coursemate.AllFragments.FriendProfile;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.EndlessRecyclerViewScrollListener;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupProfile extends AppCompatActivity {
    Toolbar toolbar;
    GroupRes theGroup;
    ProgressBar progressBar;
    RecyclerView groupMembers;
    CoursemateFragment.CoursemateSelectionListener selectionListener;
    EndlessRecyclerViewScrollListener scrollListener;
    List<User> memberList = new ArrayList<>();
    memberAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        progressBar = (ProgressBar)findViewById(R.id.coursemate_progressbar);
        theGroup = getIntent().getParcelableExtra(EndPoints.PASSED_GROUP);
        toolbar = (Toolbar)findViewById(R.id.group_profile_toolbar);
        toolbar.setTitle(theGroup.getGroupName());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        groupMembers = (RecyclerView)findViewById(R.id.groupMembers);
        setSupportActionBar(toolbar);
        selectionListener = new CoursemateFragment.CoursemateSelectionListener() {
            @Override
            public void onCmSelected(User user) {
                if(user != null) showProfile(user);
            }
        };
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                apiGetGroupMember(page);
            }
        };
        adapter = new memberAdapter(memberList,selectionListener);
        groupMembers.setItemAnimator(new DefaultItemAnimator());
        groupMembers.setLayoutManager(manager);
        groupMembers.setAdapter(adapter);
        groupMembers.addOnScrollListener(scrollListener);
        apiGetGroupMember(1);

    }
    public void populate(List<User> mlist){
        memberList.addAll(mlist);
        adapter.notifyDataSetChanged();
       // Toast.makeText(getApplicationContext(),"The list is just populated right now",Toast.LENGTH_SHORT).show();
    }

    public void apiGetGroupMember(int currentPage){
        if(currentPage == 1) ShowProgressBar(true);
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> getGroupMembers = apiInterface.getGroupMembers(theGroup.getGroupId(),currentPage);
        getGroupMembers.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ShowProgressBar(false);
                if(response.isSuccessful()){
                    List<User>  users = response.body().getCoursemates();
                    populate(users);
                }else {
                    Toast.makeText(GroupProfile.this, "No user in the group yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                ShowProgressBar(false);
                Toast.makeText(GroupProfile.this, "Opps! Error Network", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class memberAdapter extends RecyclerView.Adapter<memberAdapter.memberHolder>{

        List<User> courseList = new ArrayList<>();
        CoursemateFragment.CoursemateSelectionListener listener;

        public class memberHolder extends RecyclerView.ViewHolder {

            TextView mateName,mateMajor;
            ImageView matePhoto;

            public memberHolder(View itemView) {
                super(itemView);
                mateName = (TextView)itemView.findViewById(R.id.request_name);
                mateMajor = (TextView)itemView.findViewById(R.id.request_major);
                matePhoto = (ImageView)itemView.findViewById(R.id.request_photo);
            }
            public void bindCourse(final User c,final int position, final CoursemateFragment.CoursemateSelectionListener listener){
                mateName.setText(c.getFirstname() + " " + c.getOthername());
                mateMajor.setText(c.getMajor());
                Uri uri = Uri.parse(EndPoints.PHOTO_BASE_URL+c.getPhoto());
                if(uri != null){
                    Glide.with(getApplicationContext()).load(uri)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(getApplicationContext()))
                            .error(R.drawable.ic_user_account)
                            .into(matePhoto);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCmSelected(c);
                    }
                });
            }

        }
        public memberAdapter(List<User> list,CoursemateFragment.CoursemateSelectionListener selectionListener){
            this.courseList = list;
            this.listener = selectionListener;
        }
        @Override
        public memberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursemate_single_view, parent, false);
            return new memberHolder(view);
        }
        @Override
        public void onBindViewHolder(memberHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.bindCourse(courseList.get(position),position,listener);
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }


    }
    public void showProfile(User user){
        Bundle bundle = new Bundle();
        bundle.putParcelable(EndPoints.PASSED_USER,user);
        FriendProfile friendProfile = new FriendProfile();
        friendProfile.setPrivacy(false,false);
        friendProfile.setArguments(bundle);
        friendProfile.setCancelable(true);
        friendProfile.show(getSupportFragmentManager(),EndPoints.PASSED_USER);
    }
    public void ShowProgressBar(final boolean show){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            groupMembers.setVisibility(show ? View.GONE : View.VISIBLE);
            groupMembers.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    groupMembers.setVisibility(show ? View.GONE : View.VISIBLE);
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
            groupMembers.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
