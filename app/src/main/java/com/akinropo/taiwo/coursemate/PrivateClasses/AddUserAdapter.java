package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akinropo.taiwo.coursemate.AllActivities.AddMemberGroup;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAIWO on 3/1/2017.
 */
public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.MyViewHolder> {
    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    private Context mContext;
    private List<User> users;
    private SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    public AddUserAdapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coursemate_add_single, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        User user = users.get(position);

        // displaying text view data
        holder.from.setText(user.getFirstname() + " " + user.getOthername());
        holder.message.setText(user.getMajor());


        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // handle icon animation
        applyIconAnimation(holder, position, user);

        // display profile image
        applyProfilePicture(holder, user);

        // apply click events
        applyClickEvents(holder, position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelection(position);
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        });
    }

    private void applyProfilePicture(MyViewHolder holder, User user) {
        Uri uri = Uri.parse(EndPoints.PHOTO_BASE_URL + user.getPhoto());

        EndPoints.loadFirebasePic(user.getPhoto(), holder.imgProfile, mContext);
        if (AddMemberGroup.alreadyMember.get(user.getId(), false)) {
            holder.messageContainer.setAlpha(0.4f);
            holder.messageContainer.setClickable(false);
            holder.iconContainer.setAlpha(0.4f);
            holder.iconContainer.setClickable(false);
            holder.viewA.setClickable(false);
        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position, User user) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }

    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, message;
        public ImageView imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;
        public View viewA;

        public MyViewHolder(View view) {
            super(view);
            viewA = view;
            from = (TextView) view.findViewById(R.id.from);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

}
