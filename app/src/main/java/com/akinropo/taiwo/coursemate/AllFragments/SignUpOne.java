package com.akinropo.taiwo.coursemate.AllFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.PrivateClasses.ConfirmForm;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpOne.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SignUpOne extends Fragment {
    Button subMit;
    RadioGroup radioGroup;
    EditText sSurname,sOthername,sEmail,sPhone,sPassword,sConfirmPassword;
    List<String> signUpValues = new ArrayList<>();
    ConfirmForm confirmForm;
    public static boolean isFormFill = false;
    private OnFragmentInteractionListener mListener;

    public SignUpOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.sign_up_one, container, false);
        radioGroup = (RadioGroup)view.findViewById(R.id.signup_sex);
        sSurname = (EditText)view.findViewById(R.id.signup_surname);
        sOthername = (EditText)view.findViewById(R.id.signup_othername);
        sEmail = (EditText)view.findViewById(R.id.signup_email);
        sPhone = (EditText)view.findViewById(R.id.signup_phone_number);
        sPassword = (EditText)view.findViewById(R.id.signup_password);
        sConfirmPassword = (EditText)view.findViewById(R.id.signup_confirm_password);
        List<EditText> cEditexts = new ArrayList<>();
        cEditexts.add(sSurname);
        cEditexts.add(sOthername);
        cEditexts.add(sEmail);
        cEditexts.add(sPhone);
        cEditexts.add(sPassword);
        cEditexts.add(sConfirmPassword);
        List<String> cStrings = new ArrayList<>();
        cStrings.add("Type in your Surname");
        cStrings.add("Type in Other names");
        cStrings.add("Type in your email");
        cStrings.add("Type in your phone");
        cStrings.add("Type in your password");
        cStrings.add("Confirm your password");
        subMit = (Button)view.findViewById(R.id.signup_button);
        confirmForm = new ConfirmForm(cEditexts,subMit,getContext().getApplicationContext().getResources().getDrawable(R.drawable.button_backgroun_accent));
        confirmForm.setUp(getContext());
        confirmForm.setUpEditText(cStrings);

        subMit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!confirmForm.checkError()){
                    if(confirmForm.checkValidEmail(sEmail.getText().toString()))
                    checkAndSend();
                    else Snackbar.make(getView(),"Input a valid email address",Snackbar.LENGTH_SHORT).show();
                }else {
                    Snackbar.make(getView(),"Please fill the form completely",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        setRetainInstance(true);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(List<String> uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(List<String> myList);
    }
    public void checkAndSend(){
        String password = sPassword.getText().toString();
        String confirmPassword = sConfirmPassword.getText().toString();
        if(password.equals(confirmPassword)){
            String surname = sSurname.getText().toString();
            String oname = sOthername.getText().toString();
            String email = sEmail.getText().toString().trim();
            String phone = sPhone.getText().toString().trim();
            String sex = null;
            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.signup_sex_male:
                    sex = "male";
                    break;
                case R.id.signup_sex_female:
                    sex = "female";
                    break;
            }
            if(sex != null){
                signUpValues.add(surname);
                signUpValues.add(oname);
                signUpValues.add(email);
                signUpValues.add(phone);
                signUpValues.add(sex);
                signUpValues.add(password);
                onButtonPressed(signUpValues);
            }
            else {
                Snackbar.make(getView(),"Please select your sex",Snackbar.LENGTH_SHORT).show();
            }

        }else {
            Snackbar.make(getView(),"Confirm password correctly.",Snackbar.LENGTH_SHORT).show();
        }
    }
}
