package com.akinropo.taiwo.coursemate.AllFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.SetInterestInterface;
import com.akinropo.taiwo.coursemate.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFeature extends Fragment {
    AppCompatSpinner sexChoose, fMajor, fFaculty;
    EditText setInterest;
    Button discoverButton;
    SetInterestInterface setInterestInterface;
    NestedScrollView nestedView;
    TextView progressText;
    ProgressBar progressBar;
    String freechatsex = "null";
    RelativeLayout relativeLayout;
    int majPos, facPos = 0;
    String[] majors, faculties;
    private discoverResultGotten mListener;

    public DiscoverFeature() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_feature, container, false);
        majors = getResources().getStringArray(R.array.department_picker);
        faculties = getResources().getStringArray(R.array.faculty_picker);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.disFeature_loading_freechaters);
        nestedView = (NestedScrollView) view.findViewById(R.id.disFeature_scrollView);
        progressText = (TextView) view.findViewById(R.id.disFeature_progressbar_text);
        progressBar = (ProgressBar) view.findViewById(R.id.disFeature_progressbar);
        showProgress(false);

        fMajor = (AppCompatSpinner) view.findViewById(R.id.disFeature_department);
        fFaculty = (AppCompatSpinner) view.findViewById(R.id.disFeature_faculty);
        sexChoose = (AppCompatSpinner) view.findViewById(R.id.disFeature_sex);
        discoverButton = (Button) view.findViewById(R.id.disFeature_discoverBut);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.sex_picker, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexChoose.setAdapter(adapter);
        sexChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        freechatsex = "male";
                        break;
                    case 2:
                        freechatsex = "female";
                        break;
                    default:
                        freechatsex = "null";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setUpSpinners();

        setInterest = (EditText) view.findViewById(R.id.disFeature_interest);
        setInterest.setVisibility(View.GONE);
        setInterestInterface = new SetInterestInterface() {
            @Override
            public void doneSettingInterest(String interests) {
                setInterest.setText(interests);
            }
        };
        setInterest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //show a the set interest dialog when has focus
                    showSetInterest();
                }
            }
        });
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFreechatApi();

            }
        });
        setRetainInstance(true);
        return view;
    }

    public void showSetInterest() {
        SetInterest setInterest1 = new SetInterest();
        setInterest1.setInterFAce(setInterestInterface, false);
        setInterest1.show(getChildFragmentManager(), EndPoints.DISCOVER_FRAGMENT);
    }

    public void onButtonPressed(ServerResponse serverResponse) {
        if (mListener != null) {
            mListener.discoverApiResult(serverResponse);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof discoverResultGotten) {
            mListener = (discoverResultGotten) context;
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

    public void apiGetFreechaters(String dep, String inte, String sex, String fac) {
        showProgress(true);
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> discoverFreechater = apiInterface.discoverFreechater(dep, fac, inte, sex);
        discoverFreechater.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    if (response.body().getCoursemates().size() > 0) {
                        onButtonPressed(response.body());
                    } else {
                        AlertDialog d = new AlertDialog.Builder(getContext())
                                .setTitle(" ")
                                .setMessage("No freechater found with the attributes.")
                                .create();
                        d.show();
                    }

                } else {
                    Toast.makeText(getContext(), "error fetching freechaters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showProgress(false);
            }
        });
    }

    public void showProgress(boolean show) {
        if (show) {
            nestedView.setAlpha(0.1f);
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            nestedView.setAlpha(1.0f);
            relativeLayout.setVisibility(View.GONE);
        }
    }

    public void callFreechatApi() {
        String dep = majors[majPos];
        if (majPos == 0) dep = "null";
        String fac = faculties[facPos];
        if (facPos == 0) fac = "null";
        String inte = setInterest.getText().toString().trim();
        if (inte.isEmpty()) inte = "null";
        String sex = freechatsex;
        apiGetFreechaters(dep, inte, sex, fac);
    }

    public void setUpSpinners() {
        ArrayAdapter<CharSequence> facAdapter = ArrayAdapter.createFromResource(getContext(), R.array.faculty_picker, android.R.layout.simple_list_item_checked);
        facAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        fFaculty.setAdapter(facAdapter);
        fFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(getContext(), R.array.department_picker, android.R.layout.simple_spinner_item);
        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fMajor.setAdapter(majorAdapter);
        fMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                majPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public interface discoverResultGotten {
        void discoverApiResult(ServerResponse response);
    }

}
