
package com.akinropo.taiwo.coursemate.AllActivities;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllFragments.DiscoverFeature;
import com.akinropo.taiwo.coursemate.AllFragments.DiscoverFeatureResult;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.R;

public class DiscoverActivity extends AppCompatActivity implements DiscoverFeature.discoverResultGotten,DiscoverFeatureResult.OnFragmentInteractionListener{
    FragmentTransaction fragmentTransaction;
    DiscoverFeature discoverFeature;
    DiscoverFeatureResult discoverFeatureResult;
    Toolbar toolbar;
    int index = 0;// this is use to keep track of the fragment running currently

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar)findViewById(R.id.discover_activity_toolbar);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setTitle("Filter Freechater");
        toolbar.setNavigationIcon(R.drawable.ic_action_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(DiscoverActivity.this, "you clicked the menu", Toast.LENGTH_SHORT).show();
                DiscoverActivity.this.finish();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState != null){
            discoverFeature = (DiscoverFeature)getSupportFragmentManager().findFragmentByTag(EndPoints.DISCOVER_FEATURE);
            discoverFeatureResult = (DiscoverFeatureResult)getSupportFragmentManager().findFragmentByTag(EndPoints.DISCOVER_FEATURE_RESULT);
            index = savedInstanceState.getInt(EndPoints.PASSED_USER);
        }else if(discoverFeature == null){
            discoverFeature = new DiscoverFeature();
        }else if(discoverFeatureResult == null){
            discoverFeatureResult = new DiscoverFeatureResult();
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if(index == 0){
            fragmentTransaction.add(R.id.discover_fragments_holder,discoverFeature,EndPoints.DISCOVER_FEATURE);
        }else if(index == 1){
            fragmentTransaction.add(R.id.discover_fragments_holder,discoverFeatureResult,EndPoints.DISCOVER_FEATURE_RESULT);
        }
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void discoverApiResult(ServerResponse response) {
        if(response != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(EndPoints.PASSED_USER, response);
            if(discoverFeatureResult == null) discoverFeatureResult = new DiscoverFeatureResult();
            discoverFeatureResult.setArguments(bundle);
            changeFragment(discoverFeatureResult);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void changeFragment(Fragment fragment){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.discover_fragments_holder,fragment,EndPoints.DISCOVER_FEATURE);
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
