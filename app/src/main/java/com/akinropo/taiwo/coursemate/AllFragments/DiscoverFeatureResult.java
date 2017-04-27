package com.akinropo.taiwo.coursemate.AllFragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.FirebaseChat.ChatActivity;
import com.akinropo.taiwo.coursemate.FirebaseChat.FirebaseChatDatabase;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.GridLayoutManager.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoverFeatureResult.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DiscoverFeatureResult extends Fragment {
    ServerResponse response;
    List<User> results = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    RecyclerView resultRecycler;
    ResultAdapter resultAdapter;
    ResultSelectionListener resultSelectionListener;

    public DiscoverFeatureResult() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_feature_result, container, false);
        resultRecycler = (RecyclerView)view.findViewById(R.id.result_users);
        response = getArguments().getParcelable(EndPoints.PASSED_USER);
        results = response.getCoursemates();

        resultSelectionListener = new ResultSelectionListener() {
            @Override
            public void onUserSelected(User user,int actionIndex) {
                if(actionIndex == 0 ){
                   // Toast.makeText(getContext(),"launch chat with user"+user.getOthername(),Toast.LENGTH_SHORT).show();
                    Intent i = ChatActivity.createIntentFor(getContext(),user);
                    i.putExtra(EndPoints.PASSED_USER_CHATYPE,EndPoints.TOPIC_FLAG_FREECHAT);
                    startActivity(i);

                }else if(actionIndex == 1){
                    showProfile(user);
                }

            }
        };
        resultRecycler.addItemDecoration(new ChannelItemDecoration());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),DEFAULT_SPAN_COUNT);
        resultAdapter = new ResultAdapter(results,resultSelectionListener);
        resultRecycler.setAdapter(resultAdapter);


        return view;
    }
    
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void showProfile(User user){
        Bundle bundle = new Bundle();
        bundle.putParcelable(EndPoints.PASSED_USER,user);
        FriendProfile friendProfile = new FriendProfile();
        friendProfile.setPrivacy(false,true);
        friendProfile.setArguments(bundle);
        friendProfile.setCancelable(true);
        friendProfile.show(getChildFragmentManager(),EndPoints.PASSED_USER);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    class ResultHolder extends RecyclerView.ViewHolder{
        ImageView resultPhoto;
        TextView resultName,resultMajor;
        View view;
        DatabaseReference matePresence;
        View mateOnline;

        public ResultHolder(View itemView) {
            super(itemView);
            view = itemView;
            resultPhoto = (ImageView)itemView.findViewById(R.id.suggest_image);
            resultName = (TextView)itemView.findViewById(R.id.suggest_name);
            resultMajor = (TextView)itemView.findViewById(R.id.suggest_department);
            mateOnline = (View)itemView.findViewById(R.id.request_presence);
        }
        public void bindResult(final User user, final ResultSelectionListener listener){
            resultName.setText(user.getOthername());
            resultMajor.setText(user.getMajor());

            EndPoints.loadFirebasePic(user.getPhoto(),resultPhoto,getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(),v);
                    popupMenu.getMenuInflater().inflate(R.menu.feature_result_option, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.result_option_profile:
                                    listener.onUserSelected(user, 1);
                                    break;
                                case R.id.result_option_chat:
                                    listener.onUserSelected(user, 0);
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();

                }
            });
        }

        public void trackOnlinePrescence(FirebaseChatDatabase db,User u){

            matePresence = db.getDbForPrescence(u.getId());
            matePresence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        //the user is online
                        mateOnline.setBackgroundResource(R.drawable.count_bg);
                        mateOnline.setVisibility(View.VISIBLE);

                    } else {
                        mateOnline.setBackgroundResource(R.drawable.circle_notify);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
    class ResultAdapter extends RecyclerView.Adapter<ResultHolder>{
        List<User> users = new ArrayList<>();
        ResultSelectionListener selectionListener;
        FirebaseChatDatabase firebaseChatDatabase;

        public ResultAdapter(List<User> userList,ResultSelectionListener resultSelectionListener){
            this.users = userList;
            this.selectionListener = resultSelectionListener;
            firebaseChatDatabase = new FirebaseChatDatabase();
        }

        @Override
        public ResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_discover_result_single,parent,false);
            return new ResultHolder(view);
        }

        @Override
        public void onBindViewHolder(ResultHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.bindResult(users.get(position),resultSelectionListener);
            holder.trackOnlinePrescence(firebaseChatDatabase,users.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
    private int getSpanCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
    }
    private class ChannelItemDecoration extends RecyclerView.ItemDecoration {

        private final int itemPaddingInPixel = getResources().getDimensionPixelOffset(R.dimen.channel_item_padding);
        private final int gridPaddingInPixel = getResources().getDimensionPixelOffset(R.dimen.channel_grid_padding);

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = itemPaddingInPixel;
            outRect.bottom = itemPaddingInPixel;
            outRect.left = itemPaddingInPixel;
            outRect.right = itemPaddingInPixel;

            int position = parent.getChildAdapterPosition(view);
            int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();

            if (isTopRow(position, spanCount)) {
                outRect.top += gridPaddingInPixel;
            } else if (isBottomRow(parent, position, spanCount)) {
                outRect.bottom += gridPaddingInPixel;
            }

            if (isLeftEdge(position, spanCount)) {
                outRect.left += gridPaddingInPixel;
            } else if (isRightEdge(position, spanCount)) {
                outRect.right += gridPaddingInPixel;
            }
        }

        private boolean isTopRow(int position, int spanCount) {
            return position < spanCount;
        }

        private boolean isBottomRow(RecyclerView parent, int position, int spanCount) {
            return position >= parent.getAdapter().getItemCount() - spanCount;
        }

        private boolean isLeftEdge(int position, int spanCount) {
            return (position % spanCount) == 0;
        }

        private boolean isRightEdge(int position, int spanCount) {
            return (position % spanCount) == (spanCount - 1);
        }
    }
    public interface ResultSelectionListener{
        //action index tell us the menu item clicked.
        //0 for chat
        //1 for view profile
        void onUserSelected(User user,int actionIndex);
    }
}

