package com.akinropo.taiwo.coursemate.AllFragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllActivities.AboutActivity;
import com.akinropo.taiwo.coursemate.AllActivities.CourseActivity;
import com.akinropo.taiwo.coursemate.AllActivities.EditActivity;
import com.akinropo.taiwo.coursemate.AllActivities.LoginActivity;
import com.akinropo.taiwo.coursemate.AllActivities.MainActivity;
import com.akinropo.taiwo.coursemate.AllActivities.SettingsActivity;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.Manifest;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.Course;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.RecentChatManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.SetOnMyBackPressed;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.akinropo.taiwo.coursemate.StorageClasses.CompressPhoto;
import com.akinropo.taiwo.coursemate.StorageClasses.FirebasePhotoStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    //CollapsingToolbarLayout collapsingToolbarLayout;
    ProgressDialog p;
    Toolbar toolbar;
    ImageView meImage;
    TextView meMajor,meFaculty,meYear,meEmail,mePhone,meHighschool,meSex,meManageCourse;
    ProgressBar meLoading;
    NestedScrollView meScrollView;
    Uri imageUri = Uri.EMPTY;
    User meUser;
    View meView;
    boolean populated = false;
    List<Course> meCourseList;
    RecyclerView meRecycler;
    ProgressBar meCourseProgress;
    FloatingActionButton meFab;
    public static int CAMERA_REQUEST_CODE = 200;
    public static  int GALLERY_REQUEST_CODE = 100;
    MyPreferenceManager manager;
    FirebasePhotoStorage firebasePhotoStorage;
    CompressPhoto compressPhoto;
    StorageReference firebaseProfile;

    SetOnMyBackPressed setOnMyBackPressed;

    public void setSetOnMyBackPressed(SetOnMyBackPressed setOnMyBackPressed) {
        this.setOnMyBackPressed = setOnMyBackPressed;
    }

    public MeFragment() {
        // Required empty public constructor



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        manager = new MyPreferenceManager(getContext());
        //collapsingToolbarLayout = (CollapsingToolbarLayout)view.findViewById(R.id.me_profile_collapsing_toolbar);
        toolbar = (Toolbar)view.findViewById(R.id.me_profile_toolbar);
        toolbar.setTitle(manager.getLoggedName());
        setUpToolbar();
        meManageCourse = (TextView)view.findViewById(R.id.me_profile_manage_course);
        meFab = (FloatingActionButton)view.findViewById(R.id.me_profile_fab);
        meRecycler = (RecyclerView)view.findViewById(R.id.me_profile_courselist);
        meCourseProgress = (ProgressBar)view.findViewById(R.id.me_profile_courselist_progressbar);
        meView = view;
        meLoading = (ProgressBar)view.findViewById(R.id.me_profile_progressbar);
        meScrollView = (NestedScrollView)view.findViewById(R.id.me_profile_scrollview);
        meImage = (ImageView)view.findViewById(R.id.me_profile_pic);
        meMajor = (TextView)view.findViewById(R.id.me_profile_department);
        meFaculty = (TextView)view.findViewById(R.id.me_profile_faculty);
        meYear = (TextView)view.findViewById(R.id.me_profile_year);
        meEmail = (TextView)view.findViewById(R.id.me_profile_email);
        mePhone = (TextView)view.findViewById(R.id.me_profile_phone);
        meHighschool = (TextView)view.findViewById(R.id.me_profile_highschool);
        meSex = (TextView)view.findViewById(R.id.me_profile_sex);
        meFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v, Gravity.TOP);
                popupMenu.getMenuInflater().inflate(R.menu.me_photo_choice, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.me_choice_camera:
                                checkPermissionForCam(CAMERA_REQUEST_CODE);
                                return true;
                            case R.id.me_choice_gallery:
                                checkPermissionForCam(GALLERY_REQUEST_CODE);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
        //setHasOptionsMenu(true);
        ShowProgressBar(true);
        meRecycler.setVisibility(View.INVISIBLE);
        meCourseProgress.setVisibility(View.VISIBLE);
        setRetainInstance(true);
        fetchProfile();
        fetchCourse();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCourse();
        fetchProfile();

    }
    public void setUpToolbar(){
        if(toolbar != null){
            toolbar.inflateMenu(R.menu.me_profile_menu);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.me_profile_settings:
                            startAnActivity(SettingsActivity.class);
                            break;
                        case R.id.me_profile_logout:
                            initiateLogout();
                            break;
                        case R.id.me_profile_about:
                            showAbout();
                            break;
                    }
                    return true;
                }
            });
        }
    }
    public void startAnActivity(Class t){
        Intent i = new Intent(getContext().getApplicationContext(),t);
        getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        startActivity(i);
    }
    public void initiateLogout(){
        AlertDialog d = new AlertDialog.Builder(getContext())
                .setMessage(" ")
                .setTitle("Confirm logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
    }
    public void performLogout(){
        removeTopics();
        RecentChatManager d = new RecentChatManager(getContext());
        d.deleteRecent(manager.getId());
        manager.logOut(true);
        startAnActivity(LoginActivity.class);
        getActivity().finish();
    }
    public void showAbout(){
        Intent i = new Intent(getContext(), AboutActivity.class);
        startActivity(i);
    }

    public void manageCourse(final ServerResponse response){
        meManageCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CourseActivity.class);
                i.putExtra(EndPoints.PASSED_USER,response);
                startActivity(i);
            }
        });

    }
    public void startCamera() {
        Calendar c = Calendar.getInstance();
        File photo = null;
        String fileName = "photo__" + c.getTimeInMillis();
        try {
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            photo = File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photo != null) {
            imageUri = Uri.fromFile(photo);
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(i, CAMERA_REQUEST_CODE);
        }
    }

    public void ShowProgressBar(final boolean show){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            meScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
            meScrollView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    meScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            meLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            meLoading.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    meLoading.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            meLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            meScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void fetchProfile(){
        //function to make profile detail rest call
        int id = manager.getId();
        if(id > 0){
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getUser = apiInterface.getUser(id);
            getUser.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ShowProgressBar(false);
                    if (response.isSuccessful()) {
                        meUser = response.body().getData();
                        populateProfile(meUser);
                    } else {
                        AlertDialog taiwo = new AlertDialog.Builder(getContext())
                                .setCancelable(true)
                                .setMessage("Please this user does not exist.")
                                .setTitle("Unknown User")
                                .create();
                        taiwo.show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    ShowProgressBar(false);
                    AlertDialog taiwo = new AlertDialog.Builder(getContext())
                            .setCancelable(true)
                            .setMessage("Please check your internet connection.")
                            .setTitle("Unable to Connect")
                            .create();
                    taiwo.show();
                }
            });
        }else {
            Snackbar.make(meView,"Please re-login into this app.",Snackbar.LENGTH_LONG).show();
        }

    }
    public void populateProfile(User user){
        firebasePhotoStorage = new FirebasePhotoStorage();
        firebaseProfile = firebasePhotoStorage.getProfilePhotoRef().child(user.getPhoto());
        if(firebaseProfile != null){
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(firebaseProfile)
                    .crossFade()
                    .placeholder(R.drawable.pro_loading)
                    .error(R.drawable.loading_profile)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .transform(new CircleTransform(getContext()))
                    .into(meImage);
            populated = true;
        }
        //collapsingToolbarLayout.setTitle(user.getFirstname()+" "+user.getOthername());
        toolbar.setTitle(user.getFirstname() + " " + user.getOthername());
        toolbar.setNavigationIcon(R.drawable.ic_action_edit);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditActivity.class);
                i.putExtra(EndPoints.PASSED_USER,meUser);
                startActivity(i);
            }
        });//use toolbar.inflateMenu instead
        meSex.setText(user.getSex());
        meMajor.setText(user.getMajor());
        meFaculty.setText(user.getFaculty());
        meEmail.setText(user.getEmail());
        meHighschool.setText(user.getHighschool());
        mePhone.setText(user.getPhone());
        meYear.setText(user.getYear());
    }
    public void fetchCourse(){
        int id = manager.getId();
        if(id > 0){
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getCourses = apiInterface.getCourses(id);
            getCourses.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        meCourseList = response.body().getCourseList();
                        populateCourse(meCourseList);
                        manageCourse(response.body());
                    } else {
                        ////////Toast.makeText(getContext(), "no course registered yet.", //////Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    //Toast.makeText(getContext(), "unable to load courses due to internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Snackbar.make(meView,"Please re-login into this app.",Snackbar.LENGTH_LONG).show();
        }

    }
    public void populateCourse(List<Course> cList){
        CourseAdapter courseAdapter = new CourseAdapter(cList);
        meRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        meRecycler.setItemAnimator(new DefaultItemAnimator());
        meRecycler.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
        meCourseProgress.setVisibility(View.INVISIBLE);
        meRecycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode == CAMERA_REQUEST_CODE){
            startCamera();
        }else if(requestCode == GALLERY_REQUEST_CODE){
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(getActivity(),"Mate").build().show();
        }

    }

    public class CourseHolder extends RecyclerView.ViewHolder{
        TextView courseCode,courseUnit;
        public CourseHolder(View itemView) {
            super(itemView);
            courseCode = (TextView)itemView.findViewById(R.id.course_code);
            courseUnit = (TextView)itemView.findViewById(R.id.course_unit);

        }
        public void bindCourse(Course c){
            courseCode.setText(c.getCourseCode());
            courseUnit.setText("unit: "+c.getCourseUnit());
        }
    }
    public class CourseAdapter extends RecyclerView.Adapter<CourseHolder>{
        List<Course> courseList;

        public CourseAdapter(List<Course> list){
            this.courseList = list;
        }
        @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.course_single_view,parent,false);
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




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){

           compressPhoto = new CompressPhoto(getContext());
           String compressIm = compressPhoto.compressImage(imageUri.toString());
           Uri uri = Uri.parse("file://"+compressIm);
           if(uri != null) uploadToFirebase(uri);
           else ;////Toast.makeText(getContext(),"The uri of compressIm is null",//Toast.LENGTH_SHORT).show();
       }else if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){

           compressPhoto = new CompressPhoto(getContext());
           String compressIm = compressPhoto.compressImage(data.getData().toString());
           Uri uri = Uri.parse("file://"+compressIm);
           if(uri != null) uploadToFirebase(uri);
           else ;//////Toast.makeText(getContext(),"The uri of compressIm is null",//Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,MeFragment.this);
    }


    private String getRealPathFromURIPath(Uri contentURI, ContentResolver activity) {
          Cursor cursor = activity.query(contentURI, null, null, null, null);
          if (cursor == null) {
                  return contentURI.getPath();
              } else {
                  cursor.moveToFirst();
                  int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                  return cursor.getString(idx);
              }
    }
    public void showProgressDialog(boolean show) {
        if (p == null) {
            p = new ProgressDialog(getContext());
            p.setMessage("Uploading ...");
            p.getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
            p.setCancelable(false);
        }
        if (show) {
            if (!p.isShowing()) {
                p.show();
            }
        } else {
            if (p.isShowing()) {
                p.dismiss();
            }
        }
    }
    public void apiUploadImage(Uri uri){

        String pictureName = manager.getId()+ meUser.getFirstname();
        int id  = manager.getId();

        String filePath = getRealPathFromURIPath(uri, getContext().getContentResolver());
        if(filePath != null){
            ////Toast.makeText(getContext(), "filepath: "+filePath, ////Toast.LENGTH_SHORT).show();
        }else {
            ////Toast.makeText(getContext(), "filepath: is empty", ////Toast.LENGTH_SHORT).show();
        }
        File file = new File(filePath);
        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        if(mFile != null){
            ////Toast.makeText(getContext(), "mfile is not null", ////Toast.LENGTH_SHORT).show();
        }else {
            ////Toast.makeText(getContext(), "mfile is null", ////Toast.LENGTH_SHORT).show();
        }
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photoset", file.getName(), mFile);
        if(fileToUpload != null){
            ////Toast.makeText(getContext(), "filetoUpload is not null", ////Toast.LENGTH_SHORT).show();
        }else {
            ////Toast.makeText(getContext(), "filetoUpload is  null", ////Toast.LENGTH_SHORT).show();
        }
        RequestBody filename = RequestBody.create(MediaType.parse("multipart/form-data"),pictureName);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EndPoints.PHOTO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface1 = retrofit.create(ApiInterface.class);
        Call<ServerResponse> uploadImage = apiInterface1.postProfilePhoto(fileToUpload,filename,id);
        uploadImage.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    String selectPhoto = response.body().getMessage();
                    ////Toast.makeText(getContext(), "the file is " + selectPhoto, ////Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse(EndPoints.PHOTO_BASE_URL + selectPhoto);
                    if (uri != null) {
                        clear_cache(getContext());
                        Glide.with(getContext()).load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .crossFade()
                                .transform(new CircleTransform(getContext()))
                                .into(meImage);
                    }
                } else {
                    AlertDialog taiwo = new AlertDialog.Builder(getContext())
                            .setCancelable(true)
                            .setMessage("Error uploading file : error is " + response.raw().toString())
                            .setTitle("Error")
                            .create();
                    taiwo.show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                AlertDialog taiwo = new AlertDialog.Builder(getContext())
                        .setCancelable(true)
                        .setMessage("Check your internet connection.")
                        .setTitle("Unable to Connect")
                        .create();
                taiwo.show();
                t.printStackTrace();
            }
        });

    }

    public void clear_cache(final Context context){
        meImage.setImageDrawable(null);
        Glide.clear(meImage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearMemory();
            }
        },0);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
    }
    private File createImageFile() throws IOException {
// Create an image file name
        String timeStamp = new SimpleDateFormat
                ("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, // filename
                ".jpg", // extension
                storageDir // folder
        );
// Save for use with ACTION_VIEW Intent
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    public void checkPermissionForCam(int RequestCode){
        if(RequestCode == CAMERA_REQUEST_CODE){
            String[] perms = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if(EasyPermissions.hasPermissions(getContext(),perms)){
                startCamera();
            }else {
                EasyPermissions.requestPermissions(this,"Mate",RequestCode,perms);
            }
        }else if(RequestCode == GALLERY_REQUEST_CODE){
            String[] perms = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if(EasyPermissions.hasPermissions(getContext(),perms)){
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
            }else {
                EasyPermissions.requestPermissions(this,"Mate",RequestCode,perms);
            }
        }

    }
    public void uploadToFirebase(final Uri uri){
        if(meUser != null){
            showProgressDialog(true);
            firebasePhotoStorage = new FirebasePhotoStorage();
            final String picturName = firebasePhotoStorage.getNameForProfilePic(meUser.getFirstname(),manager.getId());
            UploadTask profileTask = firebasePhotoStorage.getUploadTaskforProfile(uri,picturName);
            profileTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    p.setMessage("Done Uploading.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog(false);
                        }
                    }, 1500);
                    updateProfile(picturName);
                    Snackbar.make(getView(), "uri: " + taskSnapshot.getDownloadUrl().toString(), Snackbar.LENGTH_SHORT).show();
                    loadProfilePic(uri.getPath());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Snackbar.make(getView(),"Failure to upload file",Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(getView(),"e: "+e.toString(),Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                    showProgressDialog(false);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progressCount = (100.0 *(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                    p.setMessage("Uploading... ");
                }
            });
        }else {
            Snackbar.make(getView(),"No internet Connection",Snackbar.LENGTH_SHORT).show();
        }

    }
    public void loadProfilePic(String filepath){
        clear_cache(getContext());
        if(filepath != null){
            Glide.with(getContext()).load(filepath)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.loading_profile_picture)
                    .placeholder(R.drawable.loading_profile_picture)
                    .transform(new CircleTransform(getContext()))
                    .crossFade()
                    .into(meImage);
        }
    }
    public void updateProfile(String uri){
        if(!TextUtils.equals(uri,meUser.getPhoto())){
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> updatePic = apiInterface.updateProfilePic(manager.getId(),uri);
            updatePic.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        ////Toast.makeText(getContext(), "profile picture updated.", ////Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {

                }
            });
        }
    }
    public void removeTopics(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+manager.getId());
        unSubscibeToGroups(EndPoints.getGroupIds());
    }
    public void unSubscibeToGroups(final List<GroupRes> theGroup){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Iterator<GroupRes> theList = theGroup.iterator();
                while (theList.hasNext()){
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("group__" + theList.next().getGroupId());
                }
            }
        };
        Handler h = new Handler();
        if(r != null) h.post(r);
    }
    public void showTutorial(){
        EndPoints.getBuilder(getActivity())
                .setInfoText("Scroll down to manage your courses.")
                .setTarget(toolbar)
                .setTargetPadding(20)
                .setUsageId("man_tut")
                .setShape(ShapeType.CIRCLE)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .performClick(false)
                .show();
    }




}
