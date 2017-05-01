package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.content.Context;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by TAIWO on 3/11/2017.
 */
public class BarNotification {
    Context context;
    RecentChatManager recentChatManager;
    SetNofificationListener listener;
    MyPreferenceManager manager;

    public BarNotification(Context context) {
        this.context = context;
        this.recentChatManager = new RecentChatManager(context);
    }

    public void setListener(SetNofificationListener listener) {
        this.listener = listener;
    }

    public void init() {
        boolean show = recentChatManager.isNotification();
        listener.onMessageChange(show);
        isFriendRequest();

    }

    public void isFriendRequest() {
        manager = new MyPreferenceManager(context);
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> checkCmRequest = apiInterface.checkCmRequest(manager.getId());
        checkCmRequest.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                listener.onRequestChange(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    public interface SetNofificationListener {
        void onMessageChange(boolean state); //state is false when there is no notification and true when there is;

        void onRequestChange(boolean state);
    }
}
