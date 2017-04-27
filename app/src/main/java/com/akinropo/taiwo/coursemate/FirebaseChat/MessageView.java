package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.R;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class MessageView extends LinearLayout {

    private final DateFormat timeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private final Date date = new Date();
    private ImageView picture;
    private EmojiconTextView body;
    private TextView time;
    private TextView name;

    private int layoutResId;

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        super.setOrientation(VERTICAL);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.layout
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            layoutResId = array.getResourceId(0, R.layout.merge_message_item_view);
            array.recycle();
        }
    }

    public void setTextBackground(MessageBubbleDrawable bubbleDrawable) {
        body.setBackground(bubbleDrawable);
    }

    @Override
    public void setOrientation(int orientation) {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = View.inflate(getContext(), layoutResId, this);
        this.picture = (ImageView)view.findViewById(R.id.message_author_image);
        this.body = (EmojiconTextView)view.findViewById(R.id.message_body);
        this.time = (TextView)view.findViewById(R.id.message_time);
        this.name = (TextView)view.findViewById(R.id.message_author_name);
    }

    public void display(Message message) {
        Context context = getContext();
        if(message.getSenderPhoto() != null){
            Uri uri = Uri.parse(EndPoints.PHOTO_BASE_URL+message.getSenderPhoto());
            if(uri != null){
                Glide.with(context)
                        .load(uri)
                        .error(R.drawable.ic_user_account)
                        .transform(new CircleTransform(context))
                        .into(picture);
            }

        }else {
            Glide.with(context)
                    .load(R.drawable.anonymous)
                    .error(R.drawable.ic_user_account)
                    .transform(new CircleTransform(context))
                    .into(picture);
        }

        body.setText(message.getBody());
        time.setText(formattedTimeFrom(message.getTimestamp()));
        if(message.getSenderName() != null){
            name.setText(message.getSenderName());
        }
    }

    private String formattedTimeFrom(long timestamp) {
        date.setTime(timestamp);
        return timeFormat.format(date);
    }

}
