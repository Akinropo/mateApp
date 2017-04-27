package com.akinropo.taiwo.coursemate.StorageClasses;

import android.net.Uri;

import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by TAIWO on 4/18/2017.
 */
public class FirebasePhotoStorage {
    FirebaseStorage storage;

    public FirebasePhotoStorage() {
        this.storage = FirebaseStorage.getInstance(); //get the storage instance.
    }
    public StorageReference getProfilePhotoRef(){
        //get the profile pictures directory.
        return storage.getReference().child(EndPoints.FIREBASE_PROFILE_PHOTO_PATH);
    }
    public StorageReference getRefForProfilePic(String picxName){
        //get a picture file/or create a new one.
        return getProfilePhotoRef().child(picxName);
    }
    public StorageReference getPostPhotoRef(){
        //get the post pictures directory.
        return storage.getReference().child(EndPoints.FIREBASE_POST_PHOTO_PATH);
    }
    public StorageReference getRefForPost(String picxName){
        //get a post picture file/or create a new one.
        return getPostPhotoRef().child(picxName);
    }
    public UploadTask getUploadTaskforProfile(Uri uri,String pixName){
        //get the upload task for a profilePicture
        UploadTask t = getRefForProfilePic(pixName).putFile(uri, getJpgMeta());
        return t;
    }
    public UploadTask getUploadTaskforPost(Uri uri,String pixName){
        //get the upload task for a profilePicture
        UploadTask t = getRefForPost(pixName).putFile(uri,getJpgMeta());
        return t;
    }
    public StorageMetadata getJpgMeta(){
        //return the image type for the picture.
        StorageMetadata m = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        return m;
    }
    public String getNameForProfilePic(String surname,int userId){
        //this generate the profilepicture name.
        return userId+surname+".jpg";
    }
    public String getNameForPost(int id){
        return System.currentTimeMillis()+"_id_"+id+".jpg";
    }

}
