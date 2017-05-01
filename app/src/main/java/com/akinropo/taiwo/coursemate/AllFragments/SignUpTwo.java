package com.akinropo.taiwo.coursemate.AllFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.akinropo.taiwo.coursemate.PrivateClasses.ConfirmForm;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpTwo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SignUpTwo extends Fragment {
    EditText sHighSchool;
    AppCompatSpinner sFaculty, sYear, sMajor;
    Button sSubmit;
    String[] faculties;
    String[] years;
    String[] majors;
    int yearPos = 0;
    int facPos = 0;
    int majPos = 0;
    ConfirmForm confirmForm;
    List<EditText> editTexts = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    public SignUpTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_two, container, false);
        faculties = getResources().getStringArray(R.array.faculty_picker);
        years = getResources().getStringArray(R.array.year_picker);
        majors = getResources().getStringArray(R.array.department_picker);

        sFaculty = (AppCompatSpinner) view.findViewById(R.id.signup_faculty);
        sHighSchool = (EditText) view.findViewById(R.id.signup_highschool);
        sMajor = (AppCompatSpinner) view.findViewById(R.id.signup_major);
        sYear = (AppCompatSpinner) view.findViewById(R.id.signup_year_entry);
        sSubmit = (Button) view.findViewById(R.id.signup_button_two);
        sSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSend();
            }
        });
        editTexts.add(sHighSchool);
        List<String> stringList = new ArrayList<>();
        stringList.add("Type in your highschool");
        confirmForm = new ConfirmForm(editTexts, sSubmit, getResources().getDrawable(R.drawable.button_backgroun_accent));
        confirmForm.setUp(getContext());
        confirmForm.setUpEditText(stringList);

        setUpSpinners();
        setRetainInstance(true);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String major, String faculty, String year, String highSchool) {
        if (mListener != null) {
            mListener.onFragmentInteraction(major, faculty, year, highSchool);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public void checkAndSend() {
        String highSchool = sHighSchool.getText().toString();
        String faculty = faculties[facPos];
        String year = years[yearPos];
        String major = majors[majPos];
        if (!highSchool.equals("") && !year.equals("") && (majPos != 0) && (yearPos != 0) && (facPos != 0)) {
            onButtonPressed(major, faculty, year, highSchool);
        } else {
            Snackbar.make(getView(), "Fill the Form Completely", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void setUpSpinners() {
        ArrayAdapter<CharSequence> facAdapter = ArrayAdapter.createFromResource(getContext(), R.array.faculty_picker, android.R.layout.simple_list_item_checked);
        facAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        sFaculty.setAdapter(facAdapter);
        sFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facPos = position;
                // Toast.makeText(getContext(), "The position for faculty is "+facPos, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(getContext(), R.array.year_picker, android.R.layout.simple_list_item_checked);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        sYear.setAdapter(yearAdapter);
        sYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearPos = position;
                // Toast.makeText(getContext(), "The position for year is "+yearPos, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(getContext(), R.array.department_picker, android.R.layout.simple_spinner_item);
        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sMajor.setAdapter(majorAdapter);
        sMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                majPos = position;
                // Toast.makeText(getContext(), "The position for major is "+majPos, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String major, String faculty, String year, String highSchool);
    }
}
