package com.akinropo.taiwo.coursemate.AllFragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.EndlessRecyclerViewScrollListener;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.Post;
import com.akinropo.taiwo.coursemate.PrivateClasses.SetOnMyBackPressed;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.akinropo.taiwo.coursemate.StorageClasses.FirebasePhotoStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.vstechlab.easyfonts.EasyFonts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {
    RecyclerView postList;
    SwipeRefreshLayout swipeRefreshLayout;
    PostAdapter postAdapter;
    List<Post> allPosts = new ArrayList<>();
    EndlessRecyclerViewScrollListener scrollListener;
    FloatingActionButton fab;
    ProfileListener profileListener;
    List<Integer> likedPosts = new ArrayList<>();
    List<Integer> dislikedPosts = new ArrayList<>();
    MyPreferenceManager myPreferenceManager;
    NestedScrollView badNetworkLayout;
    LinearLayoutManager manager;
    SetOnMyBackPressed setOnMyBackPressed;
    FirebasePhotoStorage firebasePhotoStorage;

    public PostFragment() {
        // Required empty public constructor
    }

    public void setSetOnMyBackPressed(SetOnMyBackPressed setOnMyBackPressed) {
        this.setOnMyBackPressed = setOnMyBackPressed;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        firebasePhotoStorage = new FirebasePhotoStorage();
        badNetworkLayout = (NestedScrollView) view.findViewById(R.id.bad_post);
        myPreferenceManager = new MyPreferenceManager(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.post_refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.primary_light));

        fab = (FloatingActionButton) view.findViewById(R.id.post_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SetInterest newPost = new SetInterest();
                NewPost newPost = new NewPost();
                newPost.setTargetFragment(PostFragment.this, 10);
                newPost.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
                newPost.setCancelable(false);
                newPost.show(getChildFragmentManager(), EndPoints.PASSED_USER);
                newPost.setListenerWatch(new SetNewPostListener() {
                    @Override
                    public void onNewPost(boolean isRefresh) {
                        scrollListener.resetState();
                        int i = allPosts.size() - 1;
                        allPosts.clear();
                        initiatePostRecycler();
                        apiGetPost(1);
                        showBadNetwork(false);
                    }
                });
            }
        });
        postList = (RecyclerView) view.findViewById(R.id.post_list);

        profileListener = new ProfileListener() {
            @Override
            public void onPostNameSelected(final int position) {
                if (position <= allPosts.size()) {
                    Handler mhandler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (allPosts.get(position).getUser() != null)
                                showProfile(allPosts.get(position).getUser());
                        }
                    };
                    if (runnable != null) {
                        mhandler.post(runnable);
                    }
                }
            }

            @Override
            public void onPostLiked(final Post post, final PostHolder holder) {
                if (holder != null) {
                    Handler mhandler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            updateLikeButton(holder, true, post);
                            apiLikePost(post);
                        }
                    };
                    if (runnable != null) {
                        mhandler.post(runnable);
                    }

                }

            }

            @Override
            public void onPostDislike(final Post post, final PostHolder holder) {
                if (holder != null) {
                    Handler mhandler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            updateLikeButton(holder, true, post);
                            apiDislikePost(post);
                        }
                    };
                    if (runnable != null) {
                        mhandler.post(runnable);
                    }
                }
            }
        };
        manager = new LinearLayoutManager(getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(manager, postAdapter, allPosts) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                apiGetPost(page);

            }
        };
        postList.addOnScrollListener(scrollListener);
        initiatePostRecycler();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollListener.resetState();
                allPosts = new ArrayList<Post>();
                initiatePostRecycler();
                apiGetPost(1);
                showBadNetwork(false);
            }
        });
        //showBadNetwork(false);
        apiGetPost(1);
        showTutorial();
        return view;
    }

    public void initiatePostRecycler() {
        //this function sets everything relating to the recycler view.
        postList.removeAllViews();
        if (postAdapter != null) {
            postAdapter.clearList();
            postAdapter.notifyDataSetChanged();
        }
        postAdapter = new PostAdapter(allPosts, profileListener);
        postList.setLayoutManager(manager);
        postList.setItemAnimator(new DefaultItemAnimator());
        postList.setAdapter(postAdapter);


    }

    public void populatePost(List<Post> posts1) {
        allPosts.addAll(posts1);
        postAdapter.notifyDataSetChanged();
    }

    public void showBadNetwork(boolean show) {
        if (show) {
            if (allPosts.size() == 0) {
                badNetworkLayout.setVisibility(View.VISIBLE);
                postList.setVisibility(View.GONE);
                swipeRefreshLayout.setNestedScrollingEnabled(true);
            }

        } else {
            badNetworkLayout.setVisibility(View.GONE);
            postList.setVisibility(View.VISIBLE);
        }

    }

    public void updateLikeButton(final PostHolder postHolder, boolean animated, Post post) {

        if (post.isLiked()) {
            if (animated) {
                //animate the like button
                AnimatorSet animatorSet = new AnimatorSet();
                //put in animation list
                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(postHolder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(postHolder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(new OvershootInterpolator(4));

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(postHolder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(new OvershootInterpolator(4));
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        postHolder.btnLike.setImageResource(R.drawable.ic_like_fill);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        postHolder.btnUnlike.setImageResource(R.drawable.ic_unlike_outline_grey);
                    }
                });

                animatorSet.start();
            } else {
                postHolder.btnLike.setImageResource(R.drawable.ic_like_fill);
            }
        } else if (post.isDisliked()) {
            if (animated) {
                //animate
                AnimatorSet animatorSet = new AnimatorSet();
                //put in animation list
                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(postHolder.btnUnlike, "rotation", 360f, 0f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(postHolder.btnUnlike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(new OvershootInterpolator(4));

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(postHolder.btnUnlike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(new OvershootInterpolator(4));
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        postHolder.btnUnlike.setImageResource(R.drawable.ic_unlike_fill);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        postHolder.btnLike.setImageResource(R.drawable.ic_like_outline_grey);
                    }
                });

                animatorSet.start();
            } else {
                postHolder.btnUnlike.setImageResource(R.drawable.ic_unlike_fill);
            }
        } else {
            postHolder.btnLike.setImageResource(R.drawable.ic_like_outline_grey);
            postHolder.btnUnlike.setImageResource(R.drawable.ic_unlike_outline_grey);
        }
    }

    public void apiGetPost(final int page) {
        /*if(scrollListener.progressAdded){
            scrollListener.addProgressBar(true);
            scrollListener.progressAdded = true;
        } */
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.startLayoutAnimation();
        Call<ServerResponse> getPost = apiInterface.getPosts(page, myPreferenceManager.getId());
        getPost.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    List<Post> postList = response.body().getPosts();
                    //scrollListener.addProgressBar(false);
                    //scrollListener.progressAdded = false;
                    populatePost(postList);
                } else {
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                // Toast.makeText(getContext(), "network error", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                showBadNetwork(true);
            }
        });
    }

    public void fetchProfile(final int id, final ImageView imad, final int position) {

        final User[] user = new User[1];
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> getUser = apiInterface.getUser(id);
        getUser.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    User user1 = response.body().getData();
                    user[0] = user1;
                    allPosts.get(position).setUser(user[0]);
                    Uri uri = Uri.parse(EndPoints.PHOTO_BASE_URL + user1.getPhoto());
                    if (uri != null) {
                        Glide.with(getContext()).load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .crossFade()
                                .transform(new CircleTransform(getContext()))
                                .into(imad);
                    }
                } else {
                    user[0] = null;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                user[0] = null;
            }


        });
    }

    public void showProfile(User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EndPoints.PASSED_USER, user);
        FriendProfile friendProfile = new FriendProfile();
        friendProfile.setPrivacy(false, true);
        friendProfile.setArguments(bundle);
        friendProfile.setCancelable(true);
        friendProfile.show(getChildFragmentManager(), EndPoints.PASSED_USER);
    }

    public void apiLikePost(final Post post) {
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> likePost = apiInterface.likePost(post.getId(), myPreferenceManager.getId());
        likePost.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    post.setIsLiked(response.body().isError());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    public void apiDislikePost(final Post post) {
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> dislikePost = apiInterface.dislikePost(post.getId(), myPreferenceManager.getId());
        dislikePost.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    post.setIsDisliked(response.body().isError());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    public void showTutorial() {
        new MaterialIntroView.Builder(getActivity())
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(true)
                .setShape(ShapeType.CIRCLE)
                .setInfoText("Click to write a new post.")
                .setTarget(fab)
                .setIdempotent(true)
                .setMaskColor(R.color.colorAccent)
                .setUsageId("post_tut") //THIS SHOULD BE UNIQUE ID
                .show();
    }

    public interface ProfileListener {

        public void onPostNameSelected(int position);

        public void onPostLiked(Post post, PostHolder holder);

        public void onPostDislike(Post post, PostHolder holder);
    }

    //this  interface is for binding with a fragment when opened in this fragment/activity and it will initiate
    // a call back to refresh the post for a new post make in the new post fragment
    public interface SetNewPostListener {
        public void onNewPost(boolean isRefresh);
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        private final DateFormat timeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        private final Date date = new Date();
        TextView composerName, postMessage, timeStamp, readMore;
        ImageView imageView, attachMent;
        ImageView btnLike, btnUnlike;
        StorageReference firebaseImage;

        public PostHolder(View itemView) {
            super(itemView);
            composerName = (TextView) itemView.findViewById(R.id.post_profile_name);
            postMessage = (TextView) itemView.findViewById(R.id.post_message_text);
            timeStamp = (TextView) itemView.findViewById(R.id.post_timestamp);
            imageView = (ImageView) itemView.findViewById(R.id.post_profile_pic);
            attachMent = (ImageView) itemView.findViewById(R.id.post_attach_image);
            readMore = (TextView) itemView.findViewById(R.id.post_message_readmore);
            btnLike = (ImageView) itemView.findViewById(R.id.post_attach_btnLike);
            btnUnlike = (ImageView) itemView.findViewById(R.id.post_attach_btnUnlike);

        }

        public void bindPost(final Post post, final int position, final ProfileListener listener, final PostHolder holder) {
            composerName.setText(post.getComposername());
            composerName.setTypeface(EasyFonts.droidSerifBold(getContext()));
            //CharSequence s = DateUtils.getRelativeTimeSpanString(Long.parseLong(post.getTime_stamp()),System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS);
            //date.setTime(Long.parseLong(post.getTime_stamp()));
            timeStamp.setText(post.getTime_stamp());
            //postMessage.setText(post.getMessage());
            shortenText(post);
            firebaseImage = firebasePhotoStorage.getPostPhotoRef().child(post.getPhoto());
            if (!TextUtils.equals(post.getPhoto().trim(), "null")) {
                Glide.with(getContext())
                        .using(new FirebaseImageLoader())
                        .load(firebaseImage)
                        .placeholder(R.drawable.loading_profile)
                        .error(R.drawable.ic_user_account)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .fitCenter()
                        .into(attachMent);
                attachMent.setVisibility(View.VISIBLE);
            }
            if (TextUtils.equals(post.getFlag().trim(), "r")) {
                composerName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onPostNameSelected(position);
                    }
                });
            } else {
                Glide.with(getContext()).load(R.drawable.anonymous)
                        .crossFade()
                        .transform(new CircleTransform(getContext()))
                        .fitCenter()
                        .into(imageView);
            }
            final Integer i = position;
            updateLikeButton(holder, false, post);
            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!likedPosts.contains(i)) {
                        likedPosts.add(i);
                    } else {
                        likedPosts.remove(i);
                    }
                    if (dislikedPosts.contains(i)) {
                        dislikedPosts.remove(i);
                    }
                    if (post.isLiked()) {
                        post.setIsLiked(false);
                    } else {
                        post.setIsLiked(true);
                    }
                    listener.onPostLiked(post, holder);
                }
            });
            btnUnlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dislikedPosts.contains(i)) {
                        dislikedPosts.add(i);
                    } else {
                        dislikedPosts.remove(i);
                    }
                    if (likedPosts.contains(i)) {
                        likedPosts.remove(i);
                    }
                    ;
                    if (post.isDisliked()) {
                        post.setIsDisliked(false);
                    } else {
                        post.setIsDisliked(true);
                    }
                    listener.onPostDislike(post, holder);
                }
            });
        }

        public void bindProfilePic(final int position) {
            Runnable taiwo = new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.equals(allPosts.get(position).getFlag().trim(), "r")) {
                        if (allPosts.get(position).getUser() == null) {
                            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
                            final Call<ServerResponse> getUser = apiInterface.getUser(allPosts.get(position).getComposerId());
                            getUser.enqueue(new Callback<ServerResponse>() {
                                @Override
                                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                    if (response.isSuccessful()) {
                                        User user1 = response.body().getData();
                                        allPosts.get(position).setUser(user1);
                                        FirebasePhotoStorage firebasePhotoStorage = new FirebasePhotoStorage();
                                        StorageReference firebaseProfile = firebasePhotoStorage.getProfilePhotoRef().child(user1.getPhoto());
                                        if (firebaseProfile != null) {
                                            Glide.with(getContext())
                                                    .using(new FirebaseImageLoader())
                                                    .load(firebaseProfile)
                                                    .crossFade()
                                                    .transform(new CircleTransform(getContext()))
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .placeholder(R.drawable.pro_loading)
                                                    .error(R.drawable.loading_profile)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(imageView);
                                        }
                                    } else {


                                    }
                                }

                                @Override
                                public void onFailure(Call<ServerResponse> call, Throwable t) {

                                }


                            });
                        } else {

                            FirebasePhotoStorage firebasePhotoStorage = new FirebasePhotoStorage();
                            StorageReference firebaseProfile = firebasePhotoStorage.getProfilePhotoRef().child(allPosts.get(position).getUser().getPhoto());
                            if (firebaseProfile != null) {
                                Glide.with(getContext())
                                        .using(new FirebaseImageLoader())
                                        .load(firebaseProfile)
                                        .crossFade()
                                        .transform(new CircleTransform(getContext()))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.pro_loading)
                                        .error(R.drawable.loading_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imageView);
                            }
                        }
                    }
                }
            };
            Handler two = new Handler();
            if (taiwo != null) {
                two.post(taiwo);
            }

        }

        public void shortenText(final Post post) {
            postMessage.setTypeface(EasyFonts.robotoRegular(getContext()));
            if (post.getMessage().toCharArray().length >= 400) {
                postMessage.setText(post.getMessage().substring(0, 399));
                readMore.setVisibility(View.VISIBLE);
                readMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postMessage.setText(post.getMessage());
                        readMore.setVisibility(View.GONE);
                    }
                });
            } else {
                postMessage.setText(post.getMessage());
            }
        }

    }

    public class ProgressHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public ProgressHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_in_progressview);
        }
    }

    public class PostAdapter extends RecyclerView.Adapter {
        List<Post> posts = new ArrayList<>();
        ProfileListener selectionListener;

        public PostAdapter(List<Post> posts1, ProfileListener listener) {
            this.posts = posts1;
            this.selectionListener = listener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == EndPoints.VIEW_TYPE_REAL) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_single, parent, false);
                return new PostHolder(view);
            } else if (viewType == EndPoints.VIEW_TYPE_PROGRESS_BAR) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_layout, parent, false);
                return new ProgressHolder(view);
            } else {
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof PostHolder) {
                ((PostHolder) holder).bindPost(posts.get(position), position, selectionListener, ((PostHolder) holder));
                ((PostHolder) holder).bindProfilePic(holder.getAdapterPosition());
                ((PostHolder) holder).setIsRecyclable(false);
            } else {
                ((ProgressHolder) holder).progressBar.setIndeterminate(true);
            }


        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (allPosts.get(position) != null) {
                return EndPoints.VIEW_TYPE_REAL;
            } else if (allPosts.get(position) == null) {
                return EndPoints.VIEW_TYPE_PROGRESS_BAR;
            } else {
                return 500;
            }
        }

        public void clearList() {
            this.posts.clear();
        }
    }
}
