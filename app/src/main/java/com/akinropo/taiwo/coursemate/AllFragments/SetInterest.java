package com.akinropo.taiwo.coursemate.AllFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.akinropo.taiwo.coursemate.PrivateClasses.SetInterestInterface;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetInterest extends DialogFragment implements View.OnClickListener {
    Button inNews, inMusic, inStudying, inSport, inMovie, inFootball, inBasket, inWorldNews, inPolitics, inSportNw, inWriting, inEntertain, inDone;
    View view;
    List<Integer> pressedIn = new ArrayList<>();
    StringBuilder interests = new StringBuilder();
    List<String> pressedValues = new ArrayList<>();
    SetInterestInterface interestInterface;
    TextView description;
    boolean fullscreen;

    public SetInterest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_set_interest, container, false);
        description = (TextView) view.findViewById(R.id.interest_description);
        if (!fullscreen) {
            description.setText("Pick interests of freechater");
        }
        inNews = (Button) view.findViewById(R.id.interest_news);
        inNews.setOnClickListener(this);
        inMusic = (Button) view.findViewById(R.id.interest_music);
        inMusic.setOnClickListener(this);
        inStudying = (Button) view.findViewById(R.id.interest_studying);
        inStudying.setOnClickListener(this);
        inSport = (Button) view.findViewById(R.id.interest_sport);
        inSport.setOnClickListener(this);
        inMovie = (Button) view.findViewById(R.id.interest_movie);
        inMovie.setOnClickListener(this);
        inFootball = (Button) view.findViewById(R.id.interest_football);
        inFootball.setOnClickListener(this);
        inBasket = (Button) view.findViewById(R.id.interest_basketball);
        inBasket.setOnClickListener(this);
        inWorldNews = (Button) view.findViewById(R.id.interest_world_news);
        inWorldNews.setOnClickListener(this);
        inPolitics = (Button) view.findViewById(R.id.interest_politics);
        inPolitics.setOnClickListener(this);
        inSportNw = (Button) view.findViewById(R.id.interest_sport_news);
        inSportNw.setOnClickListener(this);
        inWriting = (Button) view.findViewById(R.id.interest_writing);
        inWriting.setOnClickListener(this);
        inEntertain = (Button) view.findViewById(R.id.interest_entertainment);
        inEntertain.setOnClickListener(this);
        inDone = (Button) view.findViewById(R.id.interest_done);
        inDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interests.delete(0, interests.length());
                for (int i = 0; i < pressedValues.size(); ++i) {
                    String val = pressedValues.get(i);
                    interests.append(" " + val);
                }
                interestInterface.doneSettingInterest(interests.toString());
                SetInterest.this.dismiss();
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (fullscreen) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialog.setTitle("Set Interest");
        }
        return dialog;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        if (fullscreen) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        } else {
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int index = -15;
        Button b = null;
        try {
            b = (Button) view.findViewById(v.getId());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (pressedIn.contains(v.getId()) && b != null) {
            //if this is true i.e this view has been pressed b4 and added to the list.
            b.setBackgroundResource(R.drawable.interest_button_background);
            Integer i = v.getId();
            pressedIn.remove(i);
            index = 1;
        } else {
            //this views is just being clicked
            pressedIn.add(v.getId());
            b.setBackgroundResource(R.drawable.interest_button_background_inverse);
            index = 0;
        }
        String theIn = null;
        switch (v.getId()) {
            case R.id.interest_news:
                theIn = "News";
                break;
            case R.id.interest_sport:
                theIn = "Sports";
                break;
            case R.id.interest_basketball:
                theIn = "Basketball";
                break;
            case R.id.interest_entertainment:
                theIn = "Entertainment";
                break;
            case R.id.interest_football:
                theIn = "Football";
                break;
            case R.id.interest_movie:
                theIn = "Movies";
                break;
            case R.id.interest_music:
                theIn = "Music";
                break;
            case R.id.interest_politics:
                theIn = "Politics";
                break;
            case R.id.interest_sport_news:
                theIn = "SportNews";
                break;
            case R.id.interest_studying:
                theIn = "Studying";
                break;
            case R.id.interest_world_news:
                theIn = "WorldNews";
                break;
            case R.id.interest_writing:
                theIn = "Writing";
                break;
        }
        if (index == 1) {
            //means already added thus remove;
            pressedValues.remove(theIn);
        } else if (index == 0) {
            //means not added,thus add;
            pressedValues.add(theIn);
        }
        // Toast.makeText(getContext(), "the pressval is "+pressedValues.size(), Toast.LENGTH_SHORT).show();

    }

    public void setInterFAce(SetInterestInterface interFAce, boolean fullscreen) {
        this.interestInterface = interFAce;
        this.fullscreen = fullscreen;
    }
}
