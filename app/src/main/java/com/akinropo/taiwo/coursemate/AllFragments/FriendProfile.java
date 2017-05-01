package com.akinropo.taiwo.coursemate.AllFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.Course;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.akinropo.taiwo.coursemate.StorageClasses.FirebasePhotoStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendProfile extends DialogFragment {
    Toolbar toolbar;
    ImageView meImage;
    User friendData;
    CardView personalContent;
    TextView meMajor, meFaculty, meYear, meEmail, mePhone, meHighschool, meSex, noCourseYet;
    RecyclerView meRecycler;
    ProgressBar meCourseProgress;
    CourseAdapter courseAdapter;
    List<Course> courseList = new ArrayList<>();
    Button butPending, butAlready, butRequest;
    CardView butBox;
    boolean sendingRequest = false;
    boolean requestSent = false;
    boolean showPersonal = true;
    boolean showRequestStatus = true;
    ProgressBar progressBar;

    public FriendProfile() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_profile, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.friend_loading);
        personalContent = (CardView) view.findViewById(R.id.me_profile_personal);
        toolbar = (Toolbar) view.findViewById(R.id.me_profile_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendProfile.this.dismiss();
            }
        });
        noCourseYet = (TextView) view.findViewById(R.id.friend_no_course_yet);
        meImage = (ImageView) view.findViewById(R.id.me_profile_pic);
        meMajor = (TextView) view.findViewById(R.id.me_profile_department);
        meMajor.setTypeface(EasyFonts.robotoMedium(getContext()));
        meFaculty = (TextView) view.findViewById(R.id.me_profile_faculty);
        meYear = (TextView) view.findViewById(R.id.me_profile_year);
        meEmail = (TextView) view.findViewById(R.id.me_profile_email);
        mePhone = (TextView) view.findViewById(R.id.me_profile_phone);
        meHighschool = (TextView) view.findViewById(R.id.me_profile_highschool);
        butAlready = (Button) view.findViewById(R.id.friend_profile_already);
        butPending = (Button) view.findViewById(R.id.friend_profile_pending);
        butRequest = (Button) view.findViewById(R.id.friend_profile_request);
        butBox = (CardView) view.findViewById(R.id.friend_profile_butbox);
        meSex = (TextView) view.findViewById(R.id.me_profile_sex);
        meRecycler = (RecyclerView) view.findViewById(R.id.me_profile_courselist);
        meCourseProgress = (ProgressBar) view.findViewById(R.id.me_profile_courselist_progressbar);
        meRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        courseAdapter = new CourseAdapter(courseList);
        meRecycler.setItemAnimator(new DefaultItemAnimator());
        meRecycler.setAdapter(courseAdapter);
        meRecycler.setVisibility(View.INVISIBLE);
        meCourseProgress.setVisibility(View.VISIBLE);
        if (getArguments().getBoolean("reLoad", false)) {
            int id = getArguments().getInt(EndPoints.PASSED_USER);
            fetchProfile(id);
        } else {
            friendData = getArguments().getParcelable(EndPoints.PASSED_USER);
            setUpUser();
        }

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    public void setUpUser() {
        populateProfile(friendData);
        fetchCourse(friendData.getId());
        if (showRequestStatus) {
            apiGetCmStatus(friendData.getId());
        }
    }

    public void populateProfile(User user) {

        FirebasePhotoStorage firebasePhotoStorage = new FirebasePhotoStorage();
        StorageReference firebaseProfile = firebasePhotoStorage.getProfilePhotoRef().child(user.getPhoto());
        if (firebaseProfile != null) {
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(firebaseProfile)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.pro_loading)
                    .error(R.drawable.loading_profile)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .transform(new CircleTransform(getContext()))
                    .into(meImage);
        }
        toolbar.setTitle(user.getFirstname() + " " + user.getOthername());
        toolbar.setBackgroundResource(android.R.color.transparent);
        meSex.setText(user.getSex());
        meMajor.setText(user.getMajor());
        meFaculty.setText(user.getFaculty());
        meEmail.setText(user.getEmail());
        meHighschool.setText(user.getHighschool());
        mePhone.setText(user.getPhone());
        meYear.setText(user.getYear());
        if (!showPersonal) {
            personalContent.setVisibility(View.GONE);
        }

    }

    public void populateCourse(List<Course> cList) {
        courseList.addAll(cList);
        courseAdapter.notifyDataSetChanged();
        meCourseProgress.setVisibility(View.GONE);
        meRecycler.setVisibility(View.VISIBLE);
        if (courseList.size() == 0) {
            noCourseYet.setVisibility(View.VISIBLE);
        }
    }

    public void fetchCourse(int id) {

        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> getCourses = apiInterface.getCourses(id);
        getCourses.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    List<Course> meCourseList = response.body().getCourseList();
                    populateCourse(meCourseList);
                } else {
                    //Toast.makeText(getContext(), "no course registered yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    public void apiGetCmStatus(int friend) {
        MyPreferenceManager manager = new MyPreferenceManager(getContext());
        int use = manager.getId();
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        // Toast.makeText(getContext(),"values user:"+use+" friend:"+friend,Toast.LENGTH_LONG).show();
        Call<ServerResponse> getStatus = apiInterface.getCmStatus(use, friend);
        getStatus.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    int stat = response.body().getStatus();
                    //Toast.makeText(getContext(), "the stat is " + stat + " mes: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    cmButButton(stat);
                } else {
                    //Toast.makeText(getContext(), "error response when checking status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                // Toast.makeText(getContext(), "Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cmButButton(int stat) {
        //this function set the visibility of the right button for user friend status
        switch (stat) {
            case 0:
                butRequest.setVisibility(View.VISIBLE);
                butRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manageSendRequest();
                    }
                });
                break;
            case 1:
                butPending.setVisibility(View.VISIBLE);
                break;
            case 2:
                butAlready.setVisibility(View.VISIBLE);
                break;
        }
        butBox.setVisibility(View.VISIBLE);
    }

    public void manageSendRequest() {
        //this function switch between sending request and undoing the sent request.
        if (!sendingRequest) {
            if (!requestSent) {
                apiSendCmRequest(friendData.getId());
                butRequest.setText("Sending...");
                butRequest.setAlpha(0.5f);
            } else {
                apiDeleteCmRequest(friendData.getId());
                butRequest.setText("Deleting...");
                butRequest.setAlpha(0.5f);
            }
        }
    }

    public void apiSendCmRequest(final int friend) {
        sendingRequest = true;
        MyPreferenceManager manager = new MyPreferenceManager(getContext());
        int user = manager.getId();
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        final Call<ServerResponse> sendRequest = apiInterface.sendCmRequest(user, friend);
        sendRequest.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                sendingRequest = false;
                butRequest.setAlpha(1f);
                if (response.isSuccessful()) {
                    requestSent = true;
                    // Toast.makeText(getContext(), "request sent.", Toast.LENGTH_SHORT).show();
                    butRequest.setText("Request Sent (Undo)");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                sendingRequest = false;
                butRequest.setAlpha(1f);
                butRequest.setText("Send Coursemate Request");
            }
        });
    }

    public void apiDeleteCmRequest(int friend) {
        sendingRequest = true;
        MyPreferenceManager manager = new MyPreferenceManager(getContext());
        int user = manager.getId();
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> deleteRequest = apiInterface.deleteCmRequest(user, friend);
        deleteRequest.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                sendingRequest = false;
                butRequest.setAlpha(1f);
                if (response.isSuccessful()) {
                    requestSent = true;
                    // Toast.makeText(getContext(), "request sent.", Toast.LENGTH_SHORT).show();
                    butRequest.setText("Send Coursemate Request");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                sendingRequest = false;
                butRequest.setAlpha(1f);
                butRequest.setText("Request Sent (Undo)");
            }
        });
    }

    public void setPrivacy(boolean showPersonal, boolean showRequestStatus) {
        this.showPersonal = showPersonal;
        this.showRequestStatus = showRequestStatus;
    }

    public void fetchProfile(int id) {
        //function to make profile detail rest call
        ShowProgressBar(true);
        if (id > 0) {
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getUser = apiInterface.getUser(id);
            getUser.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ShowProgressBar(false);
                    if (response.isSuccessful()) {
                        friendData = response.body().getData();
                        setUpUser();
                    } else {
                        Toast.makeText(getContext(), "Error fetching user.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    ShowProgressBar(false);
                    // Toast.makeText(getContext(),"Error connection.",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void ShowProgressBar(boolean b) {
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public class CourseHolder extends RecyclerView.ViewHolder {
        TextView courseCode, courseUnit;

        public CourseHolder(View itemView) {
            super(itemView);
            courseCode = (TextView) itemView.findViewById(R.id.course_code);
            courseUnit = (TextView) itemView.findViewById(R.id.course_unit);

        }

        public void bindCourse(Course c) {
            courseCode.setText(c.getCourseCode());
            courseUnit.setText("unit: " + c.getCourseUnit());
        }
    }

    public class CourseAdapter extends RecyclerView.Adapter<CourseHolder> {
        List<Course> courseList;

        public CourseAdapter(List<Course> list) {
            this.courseList = list;
        }

        @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.course_single_view, parent, false);
            return new CourseHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {
            holder.bindCourse(courseList.get(position));
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }
    }
}
