package com.akinropo.taiwo.coursemate.AllFragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akinropo.taiwo.coursemate.AllActivities.AddCoursemateActivity;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.FirebaseChat.ChatActivity;
import com.akinropo.taiwo.coursemate.FirebaseChat.FirebaseChatDatabase;
import com.akinropo.taiwo.coursemate.PrivateClasses.EndlessRecyclerViewScrollListener;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursemateFragment extends Fragment {
    RecyclerView cmList;
    FloatingActionButton addCoursemate;
    List<User> coursemateList = new ArrayList<>();
    List<Integer> pageList = new ArrayList<>();
    CoursemateAdapter coursemateAdapter;
    EndlessRecyclerViewScrollListener scrollListener;
    ProgressBar progressBar;
    LinearLayout noCoursemate;
    LinearLayout checkInternet;
    ImageButton refreshButton;
    CoursemateSelectionListener selectionListener;
    View thisView;


    public CoursemateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coursemate, container, false);
        thisView = view;
        checkInternet = (LinearLayout) view.findViewById(R.id.check_internet);
        refreshButton = (ImageButton) view.findViewById(R.id.check_internet_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiGetCoursemates(1);
                cmList.setVisibility(View.VISIBLE);
                checkInternet.setVisibility(View.INVISIBLE);
                ShowProgressBar(true);
            }
        });
        cmList = (RecyclerView) view.findViewById(R.id.coursemate_list);
        addCoursemate = (FloatingActionButton) view.findViewById(R.id.coursemate_add);
        noCoursemate = (LinearLayout) view.findViewById(R.id.no_coursemate);
        progressBar = (ProgressBar) view.findViewById(R.id.coursemate_progressbar);
        selectionListener = new CoursemateSelectionListener() {
            @Override
            public void onCmSelected(User user) {
                if (user != null) {
                    Intent i = ChatActivity.createIntentFor(getContext(), user);
                    startActivity(i);
                }
            }
        };
        coursemateAdapter = new CoursemateAdapter(coursemateList, selectionListener);
        coursemateAdapter.notifyDataSetChanged();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        cmList.setLayoutManager(linearLayoutManager);
        cmList.setItemAnimator(new DefaultItemAnimator());
        cmList.setAdapter(coursemateAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                apiGetCoursemates(page);
            }
        };
        cmList.addOnScrollListener(scrollListener);
        apiGetCoursemates(1);
        addCoursemate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddCoursemateActivity.class);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                startActivity(i);
            }
        });
        checkIfAcceptRequest();
        return view;
    }

    public void populate(List<User> mlist) {
        coursemateList.addAll(mlist);
        coursemateAdapter.notifyDataSetChanged();
    }

    public void refreshList() {
        scrollListener.resetState();
        int i = coursemateList.size();
        coursemateList.clear();
        pageList.clear();
        coursemateAdapter.notifyItemRangeRemoved(0, i);
        apiGetCoursemates(1);
    }

    public void apiGetCoursemates(final int currentpage) {
        if (!pageList.contains(currentpage)) {
            if (currentpage == 1) {
                ShowProgressBar(true);
            }
            MyPreferenceManager manager = new MyPreferenceManager(getContext());
            int id = manager.getId();
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> getCoursemates = apiInterface.getCoursemates(id, currentpage);
            getCoursemates.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ShowProgressBar(false);
                    if (response.isSuccessful()) {
                        // Toast.makeText(getContext(),"response is successful.",Toast.LENGTH_SHORT).show();
                        List<User> lUser = response.body().getCoursemates();
                        populate(lUser);
                        pageList.add(currentpage);
                    } else {
                        if (coursemateList.size() == 0) {
                            cmList.setVisibility(View.INVISIBLE);
                            noCoursemate.setVisibility(View.VISIBLE);
                        }
                        // Toast.makeText(getContext(),"response is unsuccessful.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    ShowProgressBar(false);
                    if (currentpage == 1) {
                        cmList.setVisibility(View.INVISIBLE);
                        checkInternet.setVisibility(View.VISIBLE);
                    }
                    // Toast.makeText(getContext(),"response failure.",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Toast.makeText(getContext(),"Already loaded page : "+currentpage,Toast.LENGTH_SHORT).show();
        }

    }

    public void ShowProgressBar(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            cmList.setVisibility(show ? View.GONE : View.VISIBLE);
            cmList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cmList.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            cmList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void checkIfAcceptRequest() {
        //this functions check if the user accepted a friend request and show ability to refresh.
        if (EndPoints.isAcceptRequest()) {
            //do the stuff here
            showWheterRefresh();
            EndPoints.setIsAcceptRequest(false);
        }
    }

    public void showWheterRefresh() {
        final Snackbar d = Snackbar.make(getThisView(), "Refresh your mate list.", Snackbar.LENGTH_INDEFINITE);
        d.setAction("yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getContext(),"Yes is clicked",Toast.LENGTH_SHORT).show();
                refreshList();
            }
        });

        d.show();
    }

    public View getThisView() {
        return thisView;
    }

    public void showTutorial() {
        EndPoints.getBuilder(getActivity())
                .setInfoText("Click here search and add coursemate.")
                .setTarget(addCoursemate)
                .setTargetPadding(20)
                .setUsageId("mat_tut")
                .setShape(ShapeType.CIRCLE)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .performClick(false)
                .show();
    }

    public interface CoursemateSelectionListener {
        public void onCmSelected(User user);
    }

    public class CoursemateHolder extends RecyclerView.ViewHolder {
        TextView mateName, mateMajor;
        ImageView matePhoto;
        DatabaseReference matePresence;
        View mateOnline;

        public CoursemateHolder(View itemView) {
            super(itemView);
            mateName = (TextView) itemView.findViewById(R.id.request_name);
            mateMajor = (TextView) itemView.findViewById(R.id.request_major);
            matePhoto = (ImageView) itemView.findViewById(R.id.request_photo);
            mateOnline = (View) itemView.findViewById(R.id.request_presence);
        }

        public void bindCourse(final User c, final int position, final CoursemateSelectionListener listener) {
            mateName.setText(c.getFirstname() + " " + c.getOthername());
            mateMajor.setText(c.getMajor());
            EndPoints.loadFirebasePic(c.getPhoto(), matePhoto, getContext());
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

    public class CoursemateAdapter extends RecyclerView.Adapter<CoursemateHolder> {
        List<User> courseList = new ArrayList<>();
        CoursemateSelectionListener listener;
        FirebaseChatDatabase firebaseChatDatabase;

        public CoursemateAdapter(List<User> list, CoursemateSelectionListener selectionListener) {
            this.courseList = list;
            this.listener = selectionListener;
            firebaseChatDatabase = new FirebaseChatDatabase();
        }

        @Override
        public CoursemateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursemate_single_view, parent, false);
            return new CoursemateHolder(view);
        }

        @Override
        public void onBindViewHolder(CoursemateHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.bindCourse(courseList.get(position), position, listener);
            holder.trackOnlinePrescence(firebaseChatDatabase, courseList.get(position));
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }
    }


}
