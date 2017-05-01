package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.akinropo.taiwo.coursemate.AllActivities.LoginActivity;
import com.akinropo.taiwo.coursemate.AllFragments.SignUpOne;

import java.util.List;

/**
 * Created by TAIWO on 12/11/2016.
 */
public class ConfirmForm {

    final List<EditText> editTextList;
    final Button submitBut;
    final Drawable butBg;
    String result = "this is the initial value";
    Handler mHanldler;
    List<String> editTextStrings;

    public ConfirmForm(List<EditText> editTextLists, Button submitButs, Drawable pBg) {
        this.editTextList = editTextLists;
        this.submitBut = submitButs;
        this.butBg = pBg;
        mHanldler = new Handler();
    }

    public void setUp(final Context context) {

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                EditText lastEdit = editTextList.get(editTextList.size() - 1);
                if (lastEdit != null) {
                    lastEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus || hasFocus) {
                                int confirm = 0;
                                for (int i = 0; i < editTextList.size() - 1; ++i) {
                                    String text = editTextList.get(i).getText().toString();
                                    if (text.equals("")) {
                                        confirm++;
                                    }
                                }
                                if (confirm == 0) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        submitBut.setBackground(butBg);
                                    } else {
                                        submitBut.setBackgroundDrawable(butBg);
                                    }
                                    if (editTextList.size() == 2) {
                                        LoginActivity.isFormFill = true;
                                    }
                                    if (editTextList.size() == 6) {
                                        SignUpOne.isFormFill = true;
                                    }

                                }
                            }
                        }
                    });
                }

            }
        };
        if (myRunnable != null) {
            mHanldler.post(myRunnable);
        }
    }

    public void setUpEditText(List<String> editTextSign) {
        this.editTextStrings = editTextSign;
        for (int i = 0; i < editTextStrings.size(); ++i) {
            bindError(this.editTextList.get(i), editTextStrings.get(i));
        }
    }

    public void bindError(final EditText editText, final String error) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {

                } else {
                    editText.setError(error);
                }
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (editText.getText().toString().length() == 0) {
                    editText.setError(error);
                }
            }
        });
    }

    public boolean checkError() {
        int confirm = 0;
        for (int i = 0; i < editTextList.size() - 1; ++i) {
            String text = editTextList.get(i).getText().toString();
            if (text.equals("")) {
                confirm++;
            }
        }
        if (confirm == 0) return false;
        else return true;
    }

    public boolean checkValidEmail(String email) {
        if (email.contains("@") && email.contains(".")) {
            //the email may be valid
            return true;
        } else {
            return false;
        }
    }

}
