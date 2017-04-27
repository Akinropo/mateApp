package com.akinropo.taiwo.coursemate.AllActivities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllFragments.FriendProfile;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.EndlessRecyclerViewScrollListener;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCoursemateActivity extends AppCompatActivity {
    Button byName,byPhone,byEmail,byDept,byFaculty,byCode;
    EditText searchBox;
    ImageButton searchButton;
    LinearLayout searchLayout;
    ProgressBar progressBar;
    static int searchType = 0;
    static String searchQuery = null;
    RecyclerView resultList;
    List<User> searchList = new ArrayList<>();
    CoursemateAdapter coursemateAdapter;
    EndlessRecyclerViewScrollListener scrollListener;
    MyPreferenceManager man;
    int userId;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coursemate);
        toolbar = (Toolbar)findViewById(R.id.cm_add_toolbar);
        toolbar.setTitle("Add Coursemate");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.mipmap.ic_logo);
        man = new MyPreferenceManager(getApplicationContext());
        userId = man.getId();
        searchBox = (EditText)findViewById(R.id.coursemate_add_editext);
        searchButton = (ImageButton)findViewById(R.id.coursemate_add_search);
        searchLayout = (LinearLayout)findViewById(R.id.coursemate_add_search_layout);
        progressBar = (ProgressBar)findViewById(R.id.coursemate_add_progress);
        resultList = (RecyclerView)findViewById(R.id.coursemate_add_list);
        byEmail = (Button)findViewById(R.id.coursemate_add_email);
        byName = (Button)findViewById(R.id.coursemate_add_name);
        byPhone = (Button)findViewById(R.id.coursemate_add_phone);
        byDept = (Button)findViewById(R.id.coursemate_add_dept);
        byFaculty = (Button)findViewById(R.id.coursemate_add_faculty);
        byCode = (Button)findViewById(R.id.coursemate_add_code);
        byEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisibility(byEmail,byName,byPhone,byDept,byFaculty,byCode);
            }
        });
        byName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisibility(byName,byEmail,byPhone,byDept,byFaculty,byCode);
            }
        });
        byPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisibility(byPhone,byEmail,byName,byDept,byFaculty,byCode);
            }
        });
        byDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisibility(byDept,byEmail,byName,byPhone,byFaculty,byCode);
            }
        });
        byFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisibility(byFaculty,byEmail,byName,byDept,byPhone,byCode);
            }
        });
        byCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisibility(byCode,byEmail,byName,byDept,byFaculty,byPhone);
            }
        });
        coursemateAdapter = new CoursemateAdapter(searchList);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        resultList.setLayoutManager(manager);
        scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                apiSearchCoursemateAdd(page,searchType,searchQuery);
            }
        };
        resultList.setItemAnimator(new DefaultItemAnimator());
        resultList.addOnScrollListener(scrollListener);
        resultList.setAdapter(coursemateAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = searchBox.getText().toString();
                if(!q.equals("")){
                    searchQuery = q;
                    if(searchList.size() == 0){
                        apiSearchCoursemateAdd(1,searchType,q);
                    }else{
                        searchList.clear();
                        coursemateAdapter.notifyDataSetChanged();
                        scrollListener.resetState();
                        apiSearchCoursemateAdd(0,searchType,q);

                    }



                }else {
                    Toast.makeText(AddCoursemateActivity.this, "Can not search an empty query.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void setButtonVisibility(Button selButton,Button b1,Button b2,Button b3,Button b4,Button b5){
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
        b4.setVisibility(View.GONE);
        b5.setVisibility(View.GONE);
        switch (selButton.getId()){
            case R.id.coursemate_add_name:
                searchBox.setHint("Type in the name");
                searchBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                searchType = 1;
                break;
            case R.id.coursemate_add_email:
                searchBox.setHint("Type in the email");
                searchBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                searchType = 2;
                break;
            case R.id.coursemate_add_phone:
                searchBox.setHint("Type in the phone number");
                searchBox.setInputType(InputType.TYPE_CLASS_PHONE);
                searchType = 3;
                break;
            case R.id.coursemate_add_dept:
                searchBox.setHint("Type in the department");
                searchType = 4;
                break;
            case R.id.coursemate_add_faculty:
                searchBox.setHint("Type in the faculty");
                searchType = 5;
                break;
            case R.id.coursemate_add_code:
                searchBox.setHint("Type in the course code");
                searchType = 6;
                break;
        }
        searchLayout.setVisibility(View.VISIBLE);
    }
    public void populate(List<User> mlist){
        searchList.addAll(mlist);
        coursemateAdapter.notifyDataSetChanged();
    }


    public void showBar(final boolean show){
        final LinearLayout box = searchLayout;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if(show){
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setAlpha(1.0f);
                    box.setAlpha(0.4f);
                    searchButton.setAlpha(0.6f);
                    searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_loading));
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    box.setAlpha(1f);
                    searchButton.setAlpha(1f);
                    searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_search));
                }
            }
        };
        Handler mHandler = new Handler();
        if(run != null){
            mHandler.post(run);
        }
    }
    public class CoursemateHolder extends RecyclerView.ViewHolder{
        TextView mateName,mateMajor;
        ImageView matePhoto;
        View mateView;
        public CoursemateHolder(View itemView) {
            super(itemView);
            mateName = (TextView)itemView.findViewById(R.id.request_name);
            mateMajor = (TextView)itemView.findViewById(R.id.request_major);
            matePhoto = (ImageView)itemView.findViewById(R.id.request_photo);
            this.mateView = itemView;
        }
        public void bindCourse(final User c,final int position){
            if(c.getId() != userId){
                mateName.setText(c.getFirstname()+" "+c.getOthername());
                mateMajor.setText(c.getMajor());

                EndPoints.loadFirebasePic(c.getPhoto(),matePhoto,getApplicationContext());
                mateView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendProfile friendProfile = new FriendProfile();
                        Bundle arg = new Bundle();
                        arg.putParcelable(EndPoints.PASSED_USER,searchList.get(position));
                        friendProfile.setArguments(arg);
                        friendProfile.setPrivacy(false,true);
                        friendProfile.show(getSupportFragmentManager(), EndPoints.PASSED_USER);
                    }
                });
            }else {
                mateView.setVisibility(View.GONE);
            }

        }
    }
    public class CoursemateAdapter extends RecyclerView.Adapter<CoursemateHolder>{
        List<User> courseList = new ArrayList<>();

        public CoursemateAdapter(List<User> list){
            this.courseList = list;
        }
        @Override
        public CoursemateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursemate_single_view, parent, false);
            return new CoursemateHolder(view);
        }

        @Override
        public void onBindViewHolder(CoursemateHolder holder, int position) {
            holder.bindCourse(courseList.get(position),position);
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }
    }

    public void apiSearchCoursemateAdd(int page,int type,String query){
        showBar(true);
        final ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> searchCoursemate = apiInterface.searchCoursemateAdd(query,type,page);
        searchCoursemate.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showBar(false);
                if(response.isSuccessful()){
                    List<User> list = response.body().getCoursemates();
                    populate(list);
                   //Toast.mekText(AddCoursemateActivity.this, "search was successful", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddCoursemateActivity.this, "no result ohhh "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showBar(false);
                Toast.makeText(AddCoursemateActivity.this, "Network error,try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
