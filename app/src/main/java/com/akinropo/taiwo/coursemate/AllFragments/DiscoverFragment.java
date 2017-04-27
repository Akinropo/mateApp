package com.akinropo.taiwo.coursemate.AllFragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllActivities.DiscoverActivity;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.EndlessRecyclerViewScrollListener;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.SetOnMyBackPressed;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {
    RecyclerView suggestCoursemates,requestCoursemates;
    int userId;
    LinearLayout errorView;
    FloatingActionButton freeChatNow;
    MyPreferenceManager myPreferenceManager;
    List<User> suggestionList = new ArrayList<>();
    List<Integer> pressedButton = new ArrayList<>();//keep users that request has been sent to
    List<Integer> pressButtonR = new ArrayList<>(); //keep users that has been accepted or rejected;
    List<Integer> acceptedUsers = new ArrayList<>(); //keep users that are accepted.
    List<User> requestList = new ArrayList<>();
    SuggestAdapter suggestAdapter;
    RequestAdapter requestAdapter;
    EndlessRecyclerViewScrollListener scrollListener,requestScrollListener;
    private static int suggestPageNumber = 1; //this will be gotten from the last suggest request.
    private static boolean suggestHasNext = true; //this will also be gotten from the last request.
    TextView errorText;
    ImageView errorImage;
    SparseBooleanArray openendSuggestPages = new SparseBooleanArray();
    SparseBooleanArray openedRequestPages = new SparseBooleanArray();
    SetOnMyBackPressed setOnMyBackPressed;

    public void setSetOnMyBackPressed(SetOnMyBackPressed setOnMyBackPressed) {
        this.setOnMyBackPressed = setOnMyBackPressed;
    }

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        myPreferenceManager = new MyPreferenceManager(getContext());
        errorView = (LinearLayout)view.findViewById(R.id.suggest_error_view);
        errorImage = (ImageView)view.findViewById(R.id.suggest_error_view_image);
        errorText = (TextView)view.findViewById(R.id.suggest_error_view_word);
        suggestCoursemates = (RecyclerView)view.findViewById(R.id.discover_sugggests);
        requestCoursemates = (RecyclerView)view.findViewById(R.id.discover_requests);
        myPreferenceManager = new MyPreferenceManager(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(suggestHasNext){
                    apiForSuggests(suggestPageNumber + 1);
                }
            }
        };
        suggestSelectionListener selectionListener = new suggestSelectionListener() {
            @Override
            public void onUserSelected(User user) {
                if(user != null){
                    showProfile(user);
                }
            }

            @Override
            public void sendRequestToUser(User user) {
                if(user != null){
                    apiSendCmRequest(user.getId());
                }
            }

            @Override
            public void deleteRequestToUser(User user) {
                if(user != null){
                    apiDeleteCmRequest(user.getId());
                }
            }

            @Override
            public void acceptRequestFromUser(User user) {
                if(user != null){
                    apiAcceptRequest(user.getId());
                }
            }
        };
        freeChatNow = (FloatingActionButton)view.findViewById(R.id.discover_freechat_fab);
        freeChatNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DiscoverActivity.class);
                startActivity(i);
            }
        });
        suggestCoursemates.setItemAnimator(new DefaultItemAnimator());
        suggestCoursemates.setLayoutManager(manager);
        suggestCoursemates.addOnScrollListener(scrollListener);
        suggestAdapter = new SuggestAdapter(suggestionList,selectionListener);
        suggestCoursemates.setAdapter(suggestAdapter);
        apiForSuggests(1);
        LinearLayoutManager manager1 = new LinearLayoutManager(getContext());
        requestScrollListener = new EndlessRecyclerViewScrollListener(manager1) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                apiForRequests(page);
            }
        };

        requestCoursemates.setItemAnimator(new DefaultItemAnimator());
        requestCoursemates.setLayoutManager(manager1);
        requestCoursemates.addOnScrollListener(requestScrollListener);
        requestAdapter = new RequestAdapter(requestList,selectionListener);
        requestCoursemates.setAdapter(requestAdapter);
        apiForRequests(1);
        return view;
    }
    public void showProfile(User user){
        Bundle bundle = new Bundle();
        bundle.putParcelable(EndPoints.PASSED_USER,user);
        FriendProfile friendProfile = new FriendProfile();
        friendProfile.setPrivacy(false,false);
        friendProfile.setArguments(bundle);
        friendProfile.setCancelable(true);
        friendProfile.show(getChildFragmentManager(),EndPoints.PASSED_USER);
    }

    private void apiForSuggests(final int page){
        if(!openendSuggestPages.get(page,false)){
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getSuggestions = apiInterface.getCoursematesSuggestion(myPreferenceManager.getId(),page);
            getSuggestions.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()){
                        List<User> gotten = response.body().getCoursemates();
                        //call function to populate
                        openendSuggestPages.put(page, true);
                        if(gotten.size() > 0){
                            populateSuggestion(gotten);
                        }
                        showErrorSuggest(false," ");
                        suggestHasNext = response.body().isHasNext();
                        suggestPageNumber = response.body().getLast_page();
                    }else {
                        showErrorSuggest(true,"Error fetching suggestions,Try again.");
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    //code to initiate error view;
                    showErrorSuggest(true,"Network Failure");
                }
            });
        }else {
            showErrorSuggest(false," ");
        }

    }
    private void populateSuggestion(List<User> gotten){
            suggestionList.addAll(gotten);
            suggestAdapter.notifyDataSetChanged();
    }
    private void populateRequest(List<User> gotten){
       if(gotten != null){
           requestList.addAll(gotten);
           requestAdapter.notifyDataSetChanged();
       }
    }
    public class SuggestionViewHolder extends RecyclerView.ViewHolder{
        TextView suggestName,suggestDepartment;
        ImageView suggestImage;
        Button suggestSendRequest;
        View view;
        public SuggestionViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            suggestName = (TextView)itemView.findViewById(R.id.suggest_name);
            suggestDepartment = (TextView)itemView.findViewById(R.id.suggest_department);
            suggestImage = (ImageView)itemView.findViewById(R.id.suggest_image);
            suggestSendRequest = (Button)itemView.findViewById(R.id.suggest_request_button);


        }

        public void bindSuggestion(final User user, final suggestSelectionListener listener){
            suggestName.setText(user.getFirstname() + " " + user.getOthername());
            suggestDepartment.setText(user.getMajor());

            EndPoints.loadFirebasePic(user.getPhoto(),suggestImage,getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUserSelected(user);
                }
            });
            if(pressedButton.contains(user.getId())){
                suggestSendRequest.setBackgroundResource(R.drawable.request_button_selector_inverse);
                suggestSendRequest.setText("Request Sent");
                suggestSendRequest.setTextColor(getResources().getColor(android.R.color.white));
            }else {
                suggestSendRequest.setBackgroundResource(R.drawable.request_button_selector);
                suggestSendRequest.setText("Add Coursemate");
                suggestSendRequest.setTextColor(getResources().getColor(android.R.color.black));
            }
            suggestSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pressedButton.contains(user.getId())){
                        Integer i = user.getId();
                        pressedButton.remove(i);
                        listener.deleteRequestToUser(user);
                        suggestSendRequest.setBackgroundResource(R.drawable.request_button_selector);
                        suggestSendRequest.setText("Add Coursemate");
                        suggestSendRequest.setTextColor(getResources().getColor(android.R.color.black));
                    }else {
                        Integer i = user.getId();
                        pressedButton.add(i);
                        listener.sendRequestToUser(user);
                        suggestSendRequest.setBackgroundResource(R.drawable.request_button_selector_inverse);
                        suggestSendRequest.setText("Request Sent");
                        suggestSendRequest.setTextColor(getResources().getColor(android.R.color.white));

                    }
                }
            });
        }

    }
    public class SuggestAdapter extends RecyclerView.Adapter<SuggestionViewHolder>{
       List<User> list = new ArrayList<>();
        suggestSelectionListener selectionListener;
        public SuggestAdapter(List<User> mList,suggestSelectionListener listener){
            this.list  = mList;
            this.selectionListener = listener;
        }

        @Override
        public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursemate_suggest_single,parent,false);

            return new SuggestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SuggestionViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.bindSuggestion(list.get(position), selectionListener);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    private void apiForRequests(final int page){
        if(!openedRequestPages.get(page,false)){
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getRequests = apiInterface.getCoursematesRequests(myPreferenceManager.getId(),page);
            getRequests.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if(response.isSuccessful()){
                        List<User> rgotten = response.body().getCoursemates();
                        populateRequest(rgotten);
                        openedRequestPages.put(page,true);
                    }else {

                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {

                }
            });
        }else {

        }

    }
    class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView requestName,requestMajor;
        ImageView requestPhoto;
        Button requestAccept,requestReject,requestDecision;
        View view;
        LinearLayout buttonsLayout;

        public RequestViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            requestName = (TextView)itemView.findViewById(R.id.request_name);
            requestMajor = (TextView)itemView.findViewById(R.id.request_major);
            requestPhoto = (ImageView)itemView.findViewById(R.id.request_photo);
            requestAccept = (Button)itemView.findViewById(R.id.request_accept);
            requestReject = (Button)itemView.findViewById(R.id.request_reject);
            requestDecision = (Button)itemView.findViewById(R.id.request_decision_button);
            buttonsLayout = (LinearLayout)itemView.findViewById(R.id.request_buttons);

        }

        public void bindRequest(final User user, final suggestSelectionListener listener){
            requestName.setText(user.getFirstname()+" "+user.getOthername());
            requestMajor.setText(user.getMajor());

            EndPoints.loadFirebasePic(user.getPhoto(),requestPhoto,getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUserSelected(user);
                }
            });
            if(pressButtonR.contains(user.getId())){
                buttonsLayout.setVisibility(View.GONE);
                requestDecision.setVisibility(View.VISIBLE);
                if(acceptedUsers.contains(user.getId())){
                    requestDecision.setText("Accepted");
                    requestDecision.setBackgroundResource(R.drawable.request_button_border_pressed);
                }else {
                    requestDecision.setText("Rejected");
                    requestDecision.setBackgroundResource(R.drawable.request_button_border);
                }
            }else {
                buttonsLayout.setVisibility(View.VISIBLE);
                requestDecision.setVisibility(View.GONE);
            }
            final Integer i = user.getId();
            requestAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pressButtonR.add(i);
                    acceptedUsers.add(i);
                    listener.acceptRequestFromUser(user); //tell listener to accept user
                    requestDecision.setText("Accepted");
                    requestDecision.setBackgroundResource(R.drawable.request_button_border_pressed);
                    buttonsLayout.setVisibility(View.GONE);
                    requestDecision.setVisibility(View.VISIBLE);

                }
            });
            requestReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestDecision.setText("Rejected");
                    requestDecision.setBackgroundResource(R.drawable.request_button_border);
                    pressButtonR.add(i);
                    listener.deleteRequestToUser(user);
                    buttonsLayout.setVisibility(View.GONE);
                    requestDecision.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    class RequestAdapter extends RecyclerView.Adapter<RequestViewHolder>{
        List<User> theList;
        suggestSelectionListener selectionListener;
        public RequestAdapter(List<User> mList,suggestSelectionListener listener){
            this.theList = mList;
            this.selectionListener = listener;
        }
        @Override
        public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursemate_request_single,parent,false);
            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RequestViewHolder holder, int position) {
            holder.bindRequest(requestList.get(position),selectionListener);
        }

        @Override
        public int getItemCount() {
            return theList.size();
        }
    }
    public interface suggestSelectionListener{
        void onUserSelected(User user);
        void sendRequestToUser(User user);
        void deleteRequestToUser(User user);
        void acceptRequestFromUser(User user);
    }

    //COURSEMATE REQUEST API'S

    public void apiSendCmRequest(final int friend){
        MyPreferenceManager manager = new MyPreferenceManager(getContext());
        int user = manager.getId();
        final  Integer i = friend;
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        final Call<ServerResponse> sendRequest = apiInterface.sendCmRequest(user, friend);
        sendRequest.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                   // Toast.makeText(getContext(), "request sent to "+friend, Toast.LENGTH_SHORT).show();
                }else {
                    pressedButton.remove(i);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                pressedButton.remove(i);
            }
        });
    }
    public void apiDeleteCmRequest(final int friend){
        MyPreferenceManager manager = new MyPreferenceManager(getContext());
        int user = manager.getId();
        final Integer i = friend;
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> deleteRequest = apiInterface.deleteCmRequest(user, friend);
        deleteRequest.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                if(response.isSuccessful()){
                   // Toast.makeText(getContext(), "request deleted: "+friend, Toast.LENGTH_SHORT).show();
                }else {
                    pressedButton.add(i);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                pressedButton.add(i);
            }
        });
    }
    public void apiAcceptRequest(final int friend){
        MyPreferenceManager manager = new MyPreferenceManager(getContext());
        int user = manager.getId();
        final Integer i = friend;
        final ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> acceptRequest = apiInterface.acceptCmRequest(user, friend);
        acceptRequest.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                   // Toast.makeText(getContext(),"request accepted for "+friend,Toast.LENGTH_SHORT).show();
                    EndPoints.setIsAcceptRequest(true);
                }else {
                    acceptedUsers.remove(i);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                acceptedUsers.remove(i);
            }
        });
    }
    public void showErrorSuggest(boolean show,String describe){
        if(show){
            errorImage.setImageResource(R.drawable.ic_user_account);
            errorText.setText(describe);
            errorView.setVisibility(View.VISIBLE);
            suggestCoursemates.setVisibility(View.GONE);
        }else {
            errorView.setVisibility(View.GONE);
            suggestCoursemates.setVisibility(View.VISIBLE);
        }
    }
    public void showTutorial(View v,String w){
        EndPoints.getBuilder(getActivity())
                .setInfoText(w)
                .setTarget(v)
                .setTargetPadding(20)
                .setUsageId(w.substring(2,4))
                .setShape(ShapeType.CIRCLE)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .performClick(false)
                .show();
    }
    public void perform(){
        showTutorial(freeChatNow,"Chat freely with any student now.");
        showTutorial(suggestCoursemates,"Scroll to the right to see more suggestions.");
    }
}
