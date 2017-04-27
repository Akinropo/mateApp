package com.akinropo.taiwo.coursemate.AllFragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.R;
import com.akinropo.taiwo.coursemate.StorageClasses.CompressPhoto;
import com.akinropo.taiwo.coursemate.StorageClasses.FirebasePhotoStorage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

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
public class NewPost extends DialogFragment implements EasyPermissions.PermissionCallbacks{
    RadioGroup radioGroup;
    Toolbar toolbar;
    EditText postMessage;
    FloatingActionButton fab;
    ProgressDialog p;
    String selectPhoto = null;
    String prevPhotoName = null;
    boolean uploading = false;
    boolean queUpload = false;
    ImageView attach;
    ImageView camera;
    static int GALLERY_REQUEST_CODE = 100;
    static int CAMERA_REQUEST_CODE = 200;
    ImageView previewImage;
    Call<ServerResponse> uploadImage;
    MyPreferenceManager manager;
    Uri imageUri = Uri.EMPTY;
    PostFragment.SetNewPostListener postListener; //the post listener interface
    public static boolean isNewPost = false;
    FirebasePhotoStorage firebasePhotoStorage;
    CompressPhoto compressPhoto;
    UploadTask postTask;
    View thisView;

    public NewPost() {
        // Required empty public constructor
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
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



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_new_post, container, false);
        thisView = view;
        camera = (ImageView)view.findViewById(R.id.new_post_camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionForCam(CAMERA_REQUEST_CODE);
            }
        });
        manager = new MyPreferenceManager(getContext());
        previewImage = (ImageView)view.findViewById(R.id.new_post_image);
        attach = (ImageView)view.findViewById(R.id.new_post_attach);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionForCam(GALLERY_REQUEST_CODE);
            }
        });
        postMessage = (EditText)view.findViewById(R.id.new_post_message);
        fab = (FloatingActionButton)view.findViewById(R.id.new_post_fab);
        radioGroup = (RadioGroup)view.findViewById(R.id.new_post_type);
        toolbar = (Toolbar)view.findViewById(R.id.new_post_toolbar);
        toolbar.setTitle("Write new Post");
        toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_action_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploading) {
                    postTask.cancel();
                }
                NewPost.this.dismiss();
                postListener.onNewPost(isNewPost);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploading) {
                    showProgressDialog(true);
                    queUpload = true;
                } else {
                    setUpApi();
                }
            }
        });
        return view;
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
    public void setUpApi(){
                String message = postMessage.getText().toString().trim();
                String flag = "null";
                String name = null;
                String photo,photocaption;
                int composer = -500;
                long timestamp;
                manager = new MyPreferenceManager(getContext());
                if(!message.equals("")){
                    switch (radioGroup.getCheckedRadioButtonId()){
                        case R.id.new_post_anonymous:
                            flag = "a";
                            name = "Anonymous";
                            composer = -123;
                            break;
                        case R.id.new_post_myidentity:
                            flag = "r";
                            composer = manager.getId();
                            name = manager.getLoggedName();
                            break;
                    }


                    timestamp = Calendar.getInstance().getTimeInMillis();
                    if(selectPhoto != null){
                        photo = selectPhoto;
                    }else {
                        photo = "null";
                    }
                    photocaption = "null";
                    apiNewPost(flag,message,name,photo,photocaption,composer,timestamp);
                }else {
                    Toast.makeText(getContext(), "Can not share an empty post", Toast.LENGTH_SHORT).show();
                }

    }

    public void apiNewPost(String flag,String message,String name,String photo,String photocapion,int composer,long timestamp){
        showProgressDialog(true);
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> newPost = apiInterface.newPost(flag,message,composer,name,photo,photocapion,timestamp);
        newPost.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showProgressDialog(false);
                if (response.isSuccessful()) {
                    //Toast.makeText(getContext(), "posted", Toast.LENGTH_SHORT).show();
                    NewPost.this.dismiss();
                    isNewPost = true;
                    postListener.onNewPost(isNewPost);
                } else {
                   // Toast.makeText(getContext(), "post is not successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showProgressDialog(false);
               // Toast.makeText(getContext(), "failure to make post api call", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showProgressDialog(boolean show){
        if(p == null){
            p = new ProgressDialog(getContext());
            p.setMessage("Posting...");
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,NewPost.this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(uploading && postTask.isInProgress()){
                postTask.cancel();
            }
            compressPhoto = new CompressPhoto(getContext());
            String compressIm = compressPhoto.compressImage(data.getData().toString());
            Uri uri = Uri.parse("file://" + compressIm);
            //apiUploadImage(uri);
            uploadToFirebase(uri);
            previewImage.setImageURI(uri);
            previewImage.setVisibility(View.VISIBLE);
        }else if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(uploading && postTask.isInProgress()){
                postTask.cancel();
            }
            compressPhoto = new CompressPhoto(getContext());
            String compressIm = compressPhoto.compressImage(imageUri.toString());
            Uri uri = Uri.parse("file://"+compressIm);
            //apiUploadImage(imageUri);
            uploadToFirebase(uri);
            previewImage.setImageURI(imageUri);
            previewImage.setVisibility(View.VISIBLE);
        }
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
    public void checkPermissionForCam(int RequestCode){
        if(RequestCode == CAMERA_REQUEST_CODE){
            String[] perms = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if(EasyPermissions.hasPermissions(getContext(), perms)){
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
        if(uri != null){
            firebasePhotoStorage = new FirebasePhotoStorage();
            final String picturName = firebasePhotoStorage.getNameForPost(manager.getId());
            postTask = firebasePhotoStorage.getUploadTaskforPost(uri, picturName);
            postTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Toast.makeText(getContext(), "uri: " + taskSnapshot.getDownloadUrl().toString(), Toast.LENGTH_SHORT).show();
                    uploading = false;
                    selectPhoto  = picturName;
                    if(queUpload){
                        setUpApi();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Snackbar.make(getThisView(),"Failure to upload file",Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(getThisView(),"e: "+e.toString(),Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                    showProgressDialog(false);
                    uploading = false;
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progressCount = (100.0 *(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                    uploading = true;
                }
            });
        }else {
            Snackbar.make(getThisView(),"No internet Connection",Snackbar.LENGTH_SHORT).show();
        }

    }
    public View getThisView(){
        return thisView;
    }
    public void setListenerWatch(PostFragment.SetNewPostListener postListener){
        //this function is used to set the post listener mainly from the post fragment to tell it refresh for new post.
        this.postListener = postListener;
    }

}
