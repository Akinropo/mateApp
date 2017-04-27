package com.akinropo.taiwo.coursemate.AllActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;

import com.akinropo.taiwo.coursemate.App;
import com.akinropo.taiwo.coursemate.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MateIntro extends AppIntro2{



    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("Find",getString(R.string.desc1),R.drawable.find_img,R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance("Meet",getString(R.string.desc2),R.drawable.meet_img,R.color.secondary_text));
        addSlide(AppIntroFragment.newInstance("Post",getString(R.string.des3),R.drawable.post_img,R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance("Chat",getString(R.string.des4),R.drawable.chat_img,R.color.deep_blue));

    }

    @Override
    public void onDonePressed() {
        Intent i = new Intent(MateIntro.this,LoginActivity.class);
        startActivity(i);
        MateIntro.this.finish();

    }
}
