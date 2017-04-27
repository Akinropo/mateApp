package com.akinropo.taiwo.coursemate.AllActivities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.PrivateClasses.Course;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseActivity extends AppCompatActivity {
    Toolbar toolbar;
    List<Course> courseList;
    List<Course> courseRepo;
    ServerResponse response;
    EditText addCourseCode,addCourseUnit;
    RecyclerView addCourseList;
    ProgressBar progressBar;
    Button addCourseButton;
    CourseAdapter courseAdapter;
    Handler mHandler;
    int deleteindex = 0;
    List<Integer> courses = new ArrayList<>();
    String delete = "";
    SparseBooleanArray seleted = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        toolbar = (Toolbar)findViewById(R.id.course_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        if(i.hasExtra(EndPoints.PASSED_USER)) {
            response = i.getParcelableExtra(EndPoints.PASSED_USER);
            courseList = response.getCourseList();
            courseRepo = response.getCourseList();
        }
        mHandler = new Handler();
        addCourseCode = (EditText)findViewById(R.id.add_course_code);
        addCourseUnit = (EditText)findViewById(R.id.add_course_unit);
        addCourseList = (RecyclerView)findViewById(R.id.course_list);
        progressBar =  (ProgressBar)findViewById(R.id.add_course_progress);
        addCourseButton = (Button)findViewById(R.id.add_course_button);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = addCourseCode.getText().toString().trim().toUpperCase();
                String unit = addCourseUnit.getText().toString().trim();
                if (!code.equals("") && !unit.equals("")) {
                    showBar(true);
                    if (deleteindex == 0) {
                        apiAddCourse(code, unit);
                    }
                } else {
                    Toast.makeText(CourseActivity.this, "Fill completely.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        populateRecyler(courseList);

    }
    public void populateRecyler(List<Course> mList){
        courseAdapter = new CourseAdapter(mList);
        addCourseList.setLayoutManager(new GridLayoutManager(CourseActivity.this,2));
        addCourseList.setItemAnimator(new DefaultItemAnimator());
        addCourseList.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_course:
                if( 0 >= seleted.size()){
                    Toast.makeText(CourseActivity.this, "Select the courses to delete", Toast.LENGTH_SHORT).show();
                }else {
                    String ids = processDelete();
                   // Toast.makeText(CourseActivity.this, "the ids "+ids, Toast.LENGTH_SHORT).show();
                    apiBulkDelete(ids);

                }
               // Toast.makeText(CourseActivity.this, "You clicked delete", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return false;
    }

    public class CourseHolder extends RecyclerView.ViewHolder{
        TextView courseCode,courseUnit;
        ImageView deleteCourse;
        CheckBox checkBox;
        public CourseHolder(View itemView) {
            super(itemView);
            courseCode = (TextView)itemView.findViewById(R.id.course_code);
            courseUnit = (TextView)itemView.findViewById(R.id.course_unit);
            //deleteCourse = (ImageView)itemView.findViewById(R.id.course_delete);
            checkBox = (CheckBox)itemView.findViewById(R.id.course_selected);
        }
        public void bindCourse(final Course c,final int position){
            courseCode.setText(c.getCourseCode());
            courseUnit.setText("unit: "+c.getCourseUnit());
            /*
            deleteCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiDeleteCourse(c.getCourseCode(),c,position);
                    courseList.remove(position);
                    courseAdapter.notifyItemRemoved(position);
                }
            });
            */
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    toggleDelete(position);

                }
            });
        }
    }
    public class CourseAdapter extends RecyclerView.Adapter<CourseHolder>{
        List<Course> courseList;
        checkedWatcher myCheckWatcher = new checkedWatcher() {
            @Override
            public void removeCheck() {

            }
        };
        public CourseAdapter(List<Course> list){
            this.courseList = list;
        }
        @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_activity_single, parent, false);
            return new CourseHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {
            holder.bindCourse(courseList.get(position),position);
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }
    }
    public void apiDeleteCourse(final String code,final Course c,final int pos){
        deleteindex = 1;
        final Course dCourse = c;
        final int position = pos;
        MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
        int id = manager.getId();
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> deleteCourse = apiInterface.deleteCourse(id,code);
        deleteCourse.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                deleteindex = 0;
                if(response.isSuccessful()){
                    Toast.makeText(CourseActivity.this, "course deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CourseActivity.this, "error deleting course.", Toast.LENGTH_SHORT).show();
                    courseList.add(position,dCourse);
                    courseAdapter.notifyItemInserted(position);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                deleteindex = 0;
                courseList.add(position,dCourse);
                courseAdapter.notifyItemInserted(position);
                AlertDialog taiwo = new AlertDialog.Builder(CourseActivity.this)
                        .setCancelable(true)
                        .setMessage("Check your internet connection.")
                        .setTitle("Unable to Connect")
                        .create();
                taiwo.show();
            }
        });
    }

    public void apiAddCourse(final String code, final String unit){
        MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
        final int id = manager.getId();
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> addCourse = apiInterface.addCourse(id, code, unit);
        addCourse.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showBar(false);
                if(response.isSuccessful()){
                    Course add = new Course(id,Integer.parseInt(unit),code);
                    courseList.add(add);
                    courseAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(CourseActivity.this, "Error adding course.Try again", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showBar(false);
                AlertDialog taiwo = new AlertDialog.Builder(CourseActivity.this)
                        .setCancelable(true)
                        .setMessage("Check your internet connection.")
                        .setTitle("Unable to Connect")
                        .create();
                taiwo.show();
            }
        });
    }
    public void showBar(final boolean show){
        final LinearLayout box = (LinearLayout)findViewById(R.id.add_course_box);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if(show){
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setAlpha(1.0f);
                    box.setAlpha(0.4f);
                    addCourseButton.setAlpha(0.6f);
                    addCourseButton.setText("adding ...");
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    box.setAlpha(1f);
                    addCourseButton.setAlpha(1f);
                    addCourseButton.setText("Add course");
                }
            }
        };
        Handler mHandler = new Handler();
        if(run != null){
            mHandler.post(run);
        }
    }
    public void toggleDelete(int i){
        Integer d = i;
        if(seleted.get(i)){
            seleted.delete(i);
            courses.remove(d);
           // Toast.makeText(CourseActivity.this, "delete process done: "+seleted.toString(), Toast.LENGTH_SHORT).show();

        }else {
            seleted.put(i,true);
            courses.add(d);
            //Toast.makeText(CourseActivity.this, "add process done: "+seleted.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public String processDelete(){
        String mem = "";
        Iterator<Integer> j = courses.iterator();
        while(j.hasNext()){
            mem+=" "+courseList.get(j.next().intValue()).getCourseCode();
        }
        return mem;
    }
    public void apiBulkDelete(final String ids){
        MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
        int id = manager.getId();
        toggleCourses(true);
        Toast.makeText(CourseActivity.this, "Running bulkdelete", Toast.LENGTH_SHORT).show();
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> bulkDelete = apiInterface.bulkDeleteCourse(id,ids.trim());
        bulkDelete.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(CourseActivity.this, "The courses has been deleted successfully", Toast.LENGTH_SHORT).show();
                }else {
                    //the response is not successful
                    toggleCourses(false);
                    Toast.makeText(CourseActivity.this,"There was an error deleting the courses ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                toggleCourses(false);
            }
        });
    }
    public void toggleCourses(final boolean remove){
        if(remove){
            Iterator<Integer> i = courses.iterator();
            while (i.hasNext()){
                final int j = i.next();
                courseList.remove(j);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        courseAdapter.notifyItemRemoved(j);
                    }
                },1000);

            }
            courses.clear();
            seleted.clear();
        }else {
            //do nothing. probably next version so something
        }
    }
    public interface checkedWatcher{
        void removeCheck();
    }
}
