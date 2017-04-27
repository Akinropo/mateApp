package com.akinropo.taiwo.coursemate.AllFragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllActivities.CreateGroup;
import com.akinropo.taiwo.coursemate.AllActivities.GroupProfile;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.FirebaseChat.ChatActivity;
import com.akinropo.taiwo.coursemate.PrivateClasses.EndlessRecyclerViewScrollListener;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GroupFragment extends Fragment {
    RecyclerView groupList;
    FloatingActionButton addGroup;
    List<GroupRes> groupResList = new ArrayList<>();
    List<Integer> pageList = new ArrayList<>();
    GroupAdapter groupAdapter;
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayout noGroup;
    LinearLayout checkInternet;
    ImageButton refreshButton;
    ProgressBar progressBar;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_group, container, false);
        checkInternet = (LinearLayout)view.findViewById(R.id.check_internet_group);
        refreshButton = (ImageButton)view.findViewById(R.id.check_internet_group_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiGetGroups(1);
                groupList.setVisibility(View.VISIBLE);
                checkInternet.setVisibility(View.INVISIBLE);
                ShowProgressBar(true);
            }
        });
        groupList = (RecyclerView)view.findViewById(R.id.group_list);
        addGroup = (FloatingActionButton)view.findViewById(R.id.group_add);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreateGroup.class);
                startActivity(i);
            }
        });
        noGroup = (LinearLayout)view.findViewById(R.id.no_group);
        progressBar = (ProgressBar)view.findViewById(R.id.group_progressbar);
        OnGroupListener listener = new OnGroupListener() {
            @Override
            public void OnGroupSelected(GroupRes groupRes) {
                if(groupRes != null){
                    Intent i = ChatActivity.createIntentForGroup(getContext(),groupRes);
                    startActivity(i);
                }
            }
        };
        groupAdapter = new GroupAdapter(groupResList,listener);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                apiGetGroups(page);
            }
        };
        groupList.setLayoutManager(manager);
        groupList.setItemAnimator(new DefaultItemAnimator());
        groupList.addOnScrollListener(scrollListener);
        groupList.setAdapter(groupAdapter);
        apiGetGroups(1);
        return view;
    }
    public void populateGroup(List<GroupRes> list){
        groupResList.addAll(list);
        groupAdapter.notifyDataSetChanged();
        subscibeToGroups(list);
        EndPoints.setGroupIds(groupResList);
    }
    public class GroupHolder extends RecyclerView.ViewHolder{
        TextView groupLogo,groupName;
        public GroupHolder(View itemView) {
            super(itemView);
            groupLogo = (TextView)itemView.findViewById(R.id.group_single_logo);
            groupName = (TextView)itemView.findViewById(R.id.group_single_name);

        }
        public void BindGroup(final GroupRes groupRes, final OnGroupListener listener){
            this.groupName.setText(groupRes.getGroupName());
            this.groupLogo.setText(groupRes.getGroupName().substring(0, 1));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnGroupSelected(groupRes);
                }
            });
        }
    }

    public class GroupAdapter extends RecyclerView.Adapter<GroupHolder>{
        List<GroupRes> groupRes = new ArrayList<>();
        OnGroupListener groupListener;

        public GroupAdapter(List<GroupRes> mRes,OnGroupListener listener){
            this.groupRes = mRes;
            this.groupListener = listener;
        }

        @Override
        public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_single_view, parent, false);
            return new GroupHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupHolder holder, int position) {
            holder.BindGroup(groupRes.get(position),groupListener);
        }

        @Override
        public int getItemCount() {
            return groupRes.size();
        }
    }
    public void apiGetGroups(final int currentPage){
        if(!pageList.contains(currentPage)){
            if(currentPage == 1){
                ShowProgressBar(true);
            }
            MyPreferenceManager manager = new MyPreferenceManager(getContext());
            int id = manager.getId();
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getGroups = apiInterface.getGroups(id,currentPage);
            getGroups.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ShowProgressBar(false);
                    if(response.isSuccessful()){
                        List<GroupRes> res = response.body().getGroups();
                        populateGroup(res);
                        pageList.add(currentPage);
                       // Toast.makeText(getContext(), "Groups fetch successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(groupResList.size() == 0){
                            groupList.setVisibility(View.INVISIBLE);
                            noGroup.setVisibility(View.VISIBLE);
                        }
                       // Toast.makeText(getContext(), "Groups not fetch successfully", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    ShowProgressBar(false);
                    if(currentPage == 1){
                        groupList.setVisibility(View.INVISIBLE);
                        checkInternet.setVisibility(View.VISIBLE);
                    }
                   // Toast.makeText(getContext(), "Check your internet Connection", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void ShowProgressBar(final boolean show){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            groupList.setVisibility(show ? View.GONE : View.VISIBLE);
            groupList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    groupList.setVisibility(show ? View.GONE : View.VISIBLE);
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
            groupList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public void subscibeToGroups(final List<GroupRes> theGroup){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Iterator<GroupRes> theList = theGroup.iterator();
                while (theList.hasNext()){
                    FirebaseMessaging.getInstance().subscribeToTopic("group__"+theList.next().getGroupId());
                }
            }
        };
        Handler h = new Handler();
        if(r != null) h.post(r);
    }
    public interface OnGroupListener{
        void OnGroupSelected(GroupRes groupRes);
    }
    public void showTutorial(){
        EndPoints.getBuilder(getActivity())
                .setInfoText("Click here search and add coursemate.")
                .setTarget(addGroup)
                .setTargetPadding(20)
                .setUsageId("add_tut")
                .setShape(ShapeType.CIRCLE)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .performClick(false)
                .show();
    }

}
