package com.akinropo.taiwo.coursemate.AllActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.akinropo.taiwo.coursemate.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0");
        Element versionElement2 = new Element();
        versionElement2.setTitle("+2347066870767");
        Element versionElement3 = new Element();
        versionElement3.setTitle("Made by Taiwo Studio");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.mate_logo_inverse)
                .addItem(versionElement)
                .addItem(versionElement2)
                .addItem(versionElement3)
                .addGroup("Connect with us")
                .addEmail("taiwo.akinropo83@gmail.com")
                .setDescription(getString(R.string.app_description))
                .create();
        setContentView(aboutPage);
    }
}
