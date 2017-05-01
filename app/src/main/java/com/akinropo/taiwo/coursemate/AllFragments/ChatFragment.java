package com.akinropo.taiwo.coursemate.AllFragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.FirebaseChat.ChatActivity;
import com.akinropo.taiwo.coursemate.FirebaseChat.FirebaseChatDatabase;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.RecentChatManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.RecentChatModel;
import com.akinropo.taiwo.coursemate.PrivateClasses.SetOnMyBackPressed;
import com.akinropo.taiwo.coursemate.PrivateClasses.SimpleDividerItemSeparator;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.akinropo.taiwo.coursemate.StorageClasses.FirebasePhotoStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    public static final int VIEW_TYPE_GROUP = EndPoints.TOPIC_FLAG_GROUP;
    public static final int VIEW_TYPE_USER = EndPoints.TOPIC_FLAG_MESSAGE;
    public static final int VIEW_TYPE_FREECHAT = EndPoints.TOPIC_FLAG_FREECHAT;
    RecyclerView recyclerView;
    RecentChatManager recentChatManager;
    CoursemateAdapter adapter;
    List<RecentChatModel> mylist = new ArrayList<>();
    List<User> userlist = new ArrayList<>();
    CoursemateSelectionListener listener;
    GroupFragment.OnGroupListener onGroupListener;
    BroadcastReceiver broadcastReceiver;
    SetOnMyBackPressed setOnMyBackPressed;

    public ChatFragment() {
        // Required empty public constructor
    }

    public void setSetOnMyBackPressed(SetOnMyBackPressed setOnMyBackPressed) {
        this.setOnMyBackPressed = setOnMyBackPressed;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recent_recycler);
        recentChatManager = new RecentChatManager(getContext());
        //setUpRecent();
        listener = new CoursemateSelectionListener() {
            @Override
            public void onCmSelected(User user) {
                Intent i = ChatActivity.createIntentFor(getContext(), user);
                i.putExtra(EndPoints.PASSED_ID, user.getId());
                startActivity(i);
            }
        };
        onGroupListener = new GroupFragment.OnGroupListener() {
            @Override
            public void OnGroupSelected(GroupRes groupRes) {
                Intent i = ChatActivity.createIntentForGroup(getContext(), groupRes);
                startActivity(i);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(EndPoints.NEW_MESSAGE_BROADCAST)) {
                    //this is to refresh the activity
                    onResume();
                    //Toast.makeText(getContext(), "the broadcast recived a new message intent for a push", Toast.LENGTH_SHORT).show();
                }

            }
        };
        return view;
    }

    public void setUpRecent() {
        mylist = recentChatManager.getRecent();
        RecentChatModel holder;

        if (mylist.size() != 15) {
            //Toast.makeText(getContext(), "the initial value is "+recentChatManager.getRecent().size(), Toast.LENGTH_SHORT).show();
            recentChatManager.addRecent(VIEW_TYPE_USER, (int) Math.ceil(Math.random() * 100), "Arotiba Temiloluwa" + (int) Math.ceil(Math.random() * 100), 1, "profile/1Akinropo", "168946383638", 1);
            recentChatManager.addRecent(VIEW_TYPE_FREECHAT, (int) Math.ceil(Math.random() * 100), "Mark Zuckerberg" + (int) Math.ceil(Math.random() * 100), 1, "profile/1Akinropo", "168946284438", 0);
            recentChatManager.addRecent(VIEW_TYPE_FREECHAT, (int) Math.ceil(Math.random() * 100), "Micheal Sayman" + (int) Math.ceil(Math.random() * 100), 24, "profile/1Akinropo", "168946383638", 0);
            recentChatManager.addRecent(VIEW_TYPE_GROUP, (int) Math.ceil(Math.random() * 100), "RECODE" + (int) Math.ceil(Math.random() * 100), 1, "profile/1Akinropo", "168946284438", 1);
            recentChatManager.addRecent(VIEW_TYPE_GROUP, (int) Math.ceil(Math.random() * 100), "TechCrunch REcode" + (int) Math.ceil(Math.random() * 100), 9, "profile/1Akinropo", "168946284478", 29);
            //Toast.makeText(getContext(), "the size is "+recentChatManager.getRecent().size(), Toast.LENGTH_SHORT).show();
        }
        mylist = recentChatManager.getRecent();
        //Iterator<RecentChatModel> iterator = mylist.iterator();
        //while (iterator.hasNext()){
        //holder = iterator.next();
            /*if( holder.getFlag() == EndPoints.TOPIC_FLAG_GROUP){
                GroupRes res = RecentChatModel.convertToGroup(holder);
            };
            User user = RecentChatModel.converToUser(holder);
            userlist.add(user);*/
        //}
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.addItemDecoration(new SimpleDividerItemSeparator(getContext()));
        mylist = recentChatManager.getRecent();
        adapter = new CoursemateAdapter(mylist, listener, onGroupListener);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(EndPoints.NEW_MESSAGE_BROADCAST));
    }

    public void setRecycler() {

    }

    private interface CoursemateSelectionListener {
        public void onCmSelected(User user);
    }

    public class CoursemateHolder extends RecyclerView.ViewHolder {
        TextView mateName, mateMajor, freeChatSymbol, unReadCount;
        ImageView matePhoto;
        DatabaseReference matePresence;
        View mateOnline;

        public CoursemateHolder(View itemView) {
            super(itemView);
            mateName = (TextView) itemView.findViewById(R.id.request_name);
            mateMajor = (TextView) itemView.findViewById(R.id.request_major);
            matePhoto = (ImageView) itemView.findViewById(R.id.request_photo);
            freeChatSymbol = (TextView) itemView.findViewById(R.id.freechat_symbol);
            unReadCount = (TextView) itemView.findViewById(R.id.unread_count);
            mateOnline = (View) itemView.findViewById(R.id.request_presence);
        }

        public void bindCourse(final User c, final int position, final CoursemateSelectionListener listener) {
            mateName.setText(c.getFirstname() + " " + c.getOthername());
            mateMajor.setText(c.getMajor());

            FirebasePhotoStorage firebasePhotoStorage = new FirebasePhotoStorage();
            StorageReference firebaseProfile = firebasePhotoStorage.getProfilePhotoRef().child(c.getPhoto());
            if (firebaseProfile != null) {
                Glide.with(getContext())
                        .using(new FirebaseImageLoader())
                        .load(firebaseProfile)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.pro_loading)
                        .error(R.drawable.loading_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(getContext()))
                        .into(matePhoto);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCmSelected(c);
                }
            });
        }

        public void trackOnlinePrescence(FirebaseChatDatabase db, User u) {

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

    public class GroupHolder extends RecyclerView.ViewHolder {
        TextView groupLogo, groupName, unReadCount;

        public GroupHolder(View itemView) {
            super(itemView);
            groupLogo = (TextView) itemView.findViewById(R.id.group_single_logo);
            groupName = (TextView) itemView.findViewById(R.id.group_single_name);
            unReadCount = (TextView) itemView.findViewById(R.id.unread_count);

        }

        public void BindGroup(final GroupRes groupRes, final GroupFragment.OnGroupListener listener) {
            this.groupName.setText(groupRes.getGroupName());
            this.groupLogo.setText(groupRes.getGroupName().substring(0, 1));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnGroupSelected(groupRes);
                }
            });
        }
    }

    public class CoursemateAdapter extends RecyclerView.Adapter {
        List<RecentChatModel> courseList = new ArrayList<>();
        CoursemateSelectionListener listener;
        GroupFragment.OnGroupListener groupListener;
        FirebaseChatDatabase firebaseChatDatabase;

        public CoursemateAdapter(List<RecentChatModel> list, CoursemateSelectionListener selectionListener, GroupFragment.OnGroupListener grouplistener) {
            this.courseList = list;
            this.listener = selectionListener;
            this.groupListener = grouplistener;
            firebaseChatDatabase = new FirebaseChatDatabase();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_GROUP) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_single_view, parent, false);
                return new GroupHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursemate_single_view, parent, false);
                return new CoursemateHolder(view);
            }

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            if (holder instanceof GroupHolder) {
                //Toast.makeText(getContext(), "it is group at position "+position, Toast.LENGTH_SHORT).show();
                ((GroupHolder) holder).BindGroup(RecentChatModel.convertToGroup(courseList.get(position), 1), groupListener);
                if (courseList.get(position).getUnreadCount() > 0) {
                    ((GroupHolder) holder).unReadCount.setText(String.valueOf(courseList.get(position).getUnreadCount()));
                    ((GroupHolder) holder).unReadCount.setVisibility(View.VISIBLE);
                } else {
                    ((GroupHolder) holder).unReadCount.setVisibility(View.GONE);
                }

            } else if (holder instanceof CoursemateHolder) {
                //Toast.makeText(getContext(), "it is user at position "+position, Toast.LENGTH_SHORT).show();
                if (getItemViewType(position) == VIEW_TYPE_FREECHAT) {
                    ((CoursemateHolder) holder).freeChatSymbol.setVisibility(View.VISIBLE); //show the freechat symbol
                }
                ((CoursemateHolder) holder).bindCourse(RecentChatModel.converToUser(courseList.get(position)), position, listener);
                ((CoursemateHolder) holder).trackOnlinePrescence(firebaseChatDatabase, RecentChatModel.converToUser(courseList.get(position)));
                if (courseList.get(position).getUnreadCount() > 0) {
                    ((CoursemateHolder) holder).unReadCount.setText(Integer.toString(courseList.get(position).getUnreadCount()));
                    ((CoursemateHolder) holder).unReadCount.setVisibility(View.VISIBLE);
                } else {
                    ((CoursemateHolder) holder).unReadCount.setVisibility(View.GONE);
                }
            }
            //holder.setIsRecyclable(false);
            //holder.bindCourse(courseList.get(position),position,listener);
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }

        @Override
        public long getItemId(int position) {
            if (courseList.get(position).getFlag() == VIEW_TYPE_GROUP) {
                return VIEW_TYPE_GROUP;
            } else {
                return VIEW_TYPE_USER;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (courseList.get(position).getFlag()) {
                case VIEW_TYPE_GROUP:
                    return VIEW_TYPE_GROUP;
                case VIEW_TYPE_FREECHAT:
                    return VIEW_TYPE_FREECHAT;
                case VIEW_TYPE_USER:
                    return VIEW_TYPE_USER;
                default:
                    return 0;
            }
        }
    }
}
