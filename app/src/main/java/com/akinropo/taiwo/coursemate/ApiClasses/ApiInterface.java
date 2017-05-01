package com.akinropo.taiwo.coursemate.ApiClasses;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by TAIWO on 1/6/2017.
 */
public interface ApiInterface {
    @FormUrlEncoded
    @POST("register")
    Call<ServerResponse> register(@Field("fname") String fname, @Field("oname") String oname, @Field("email") String emaill,
                                  @Field("number") String number, @Field("sex") String sex, @Field("pass") String pass,
                                  @Field("major") String major, @Field("faculty") String faculty, @Field("highschool") String highschool, @Field("year") String year);

    @FormUrlEncoded
    @POST("login")
    Call<ServerResponse> login(@Field("email") String email, @Field("password") String password);

    @GET("user/{id}")
    Call<ServerResponse> getUser(@Path("id") int id);

    @GET("user/course/{id}")
    Call<ServerResponse> getCourses(@Path("id") int id);

    @FormUrlEncoded
    @POST("update")
    Call<ServerResponse> updateProfile(@Field("id") int id, @Field("major") String major, @Field("faculty") String faculty,
                                       @Field("year") String year, @Field("phone") String phone, @Field("highschool") String highschool);

    @FormUrlEncoded
    @POST("user/change/password")
    Call<ServerResponse> updatePassword(@Field("id") int id, @Field("new_password") String new_password, @Field("old_password") String old_password);

    @Multipart
    @POST("user/upload")
    Call<ServerResponse> uploadImage(@Part MultipartBody.Part photoset, @Part("photoset") RequestBody name);

    @FormUrlEncoded
    @POST("course/register")
    Call<ServerResponse> addCourse(@Field("id") int id, @Field("coursecode") String code, @Field("courseunit") String unit);

    @FormUrlEncoded
    @POST("course/delete")
    Call<ServerResponse> deleteCourse(@Field("id") int id, @Field("coursecode") String coursecode);

    @FormUrlEncoded
    @POST("course/bulkdelete")
    Call<ServerResponse> bulkDeleteCourse(@Field("id") int id, @Field("courseIds") String bulkIds);

    @FormUrlEncoded
    @POST("user/coursemate")
    Call<ServerResponse> getCoursemates(@Field("id") int id, @Field("current_page") int currentpage);

    @FormUrlEncoded
    @POST("user/groups")
    Call<ServerResponse> getGroups(@Field("id") int id, @Field("current_page") int currentpage);

    @GET("coursemate/search/{query}/{type}/{current_page}")
    Call<ServerResponse> searchCoursemateAdd(@Path("query") String query, @Path("type") int type, @Path("current_page") int page);

    @GET("coursemate/status/{user}/{friend}")
    Call<ServerResponse> getCmStatus(@Path("user") int user, @Path("friend") int friend);

    @FormUrlEncoded
    @POST("coursemate/add")
    Call<ServerResponse> sendCmRequest(@Field("user") int user, @Field("friend") int friend);

    @FormUrlEncoded
    @POST("coursemate/request/delete")
    Call<ServerResponse> deleteCmRequest(@Field("user") int user, @Field("friend") int friend);

    @FormUrlEncoded
    @POST("coursemate/request/accept")
    Call<ServerResponse> acceptCmRequest(@Field("user") int user, @Field("friend") int friend);

    @GET("posts/get/{current_page}/{user_id}")
    Call<ServerResponse> getPosts(@Path("current_page") int page, @Path("user_id") int user_id);

    @FormUrlEncoded
    @POST("post/new")
    Call<ServerResponse> newPost(@Field("flag") String flag, @Field("message") String message, @Field("composer") int composer,
                                 @Field("composername") String composername, @Field("photo") String photo, @Field("photocaption") String photcapion,
                                 @Field("time_stamp") long timestamp);

    @Multipart
    @POST("api/upload.php")
    Call<ServerResponse> postPhoto(@Part MultipartBody.Part file, @Part("picture_name") RequestBody pixname);

    @Multipart
    @POST("api/uploadProfile.php")
    Call<ServerResponse> postProfilePhoto(@Part MultipartBody.Part file, @Part("picture_name") RequestBody pixname, @Part("user_id") int id);

    @GET("coursemate/suggest/{id}/{page}")
    Call<ServerResponse> getCoursematesSuggestion(@Path("id") int id, @Path("page") int page);

    @FormUrlEncoded
    @POST("coursemate/requests/{page}")
    Call<ServerResponse> getCoursematesRequests(@Field("id") int id, @Path("page") int page);

    @FormUrlEncoded
    @POST("discover/freechater")
    Call<ServerResponse> discoverFreechater(@Field("department") String department, @Field("faculty") String faculty
            , @Field("interests") String interests, @Field("sex") String sex);

    @FormUrlEncoded
    @POST("post/like")
    Call<ServerResponse> likePost(@Field("post_id") int post_id, @Field("liker_id") int user);

    @FormUrlEncoded
    @POST("post/dislike")
    Call<ServerResponse> dislikePost(@Field("post_id") int post_id, @Field("liker_id") int user);

    @FormUrlEncoded
    @POST("group/new")
    Call<ServerResponse> createGroup(@Field("user_id") int user_id, @Field("group_name") String group_name,
                                     @Field("user_name") String user_name);

    @FormUrlEncoded
    @POST("group/addmembers")
    Call<ServerResponse> addMembertoGroup(@Field("group_id") int group_id, @Field("group_name") String group_name,
                                          @Field("members") String members);

    @GET("group/check/{group}/{member}")
    Call<ServerResponse> checkGroup(@Path("group") int groupId, @Path("member") int memberId);

    @FormUrlEncoded
    @POST("home/checkrequest")
    Call<ServerResponse> checkCmRequest(@Field("id") int user_id);

    @FormUrlEncoded
    @POST("group/getmembers")
    Call<ServerResponse> getGroupMembers(@Field("group_id") int groupId, @Field("current_page") int currentPage);

    @FormUrlEncoded
    @POST("notify/send")
    Call<ServerResponse> sendMessageNotify(@Field("receiverId") int receiverId, @Field("message") String message, @Field("name") String name, @Field("photo") String photo,
                                           @Field("flag") int flag, @Field("timestamp") String timestamp, @Field("senderId") int senderId, @Field("groupOwner") int groupOwner,
                                           @Field("senderMajor") String senderMajor);

    @FormUrlEncoded
    @POST("update/profilePic")
    Call<ServerResponse> updateProfilePic(@Field("id") int id, @Field("newPic") String newPic);

}
