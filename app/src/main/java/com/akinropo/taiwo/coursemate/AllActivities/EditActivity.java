package com.akinropo.taiwo.coursemate.AllActivities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity {
    User currentUser;
    EditText updateMajor, updateFaculty, updatePhone, updateHighSchool, updateYear;
    Button updateSubmit;
    TextView updatePassword;
    ProgressDialog p;
    AlertDialog dialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent i = getIntent();
        if (i.hasExtra(EndPoints.PASSED_USER)) {
            currentUser = i.getParcelableExtra(EndPoints.PASSED_USER);
            //getSupportActionBar().setTitle(currentUser.getFirstname() + " " + currentUser.getOthername());
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentUser.getFirstname() + " " + currentUser.getOthername());
        setSupportActionBar(toolbar);
        updatePassword = (TextView) findViewById(R.id.update_password);
        updateFaculty = (EditText) findViewById(R.id.update_faculty);
        updateMajor = (EditText) findViewById(R.id.update_major);
        updatePhone = (EditText) findViewById(R.id.update_phone_number);
        updateHighSchool = (EditText) findViewById(R.id.update_highschool);
        updateYear = (EditText) findViewById(R.id.update_year_entry);
        updateSubmit = (Button) findViewById(R.id.update_button_submit);
        populateEditext(currentUser);
        updateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiUpdateProfile();
            }
        });
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparePasswordDialog();
            }
        });
    }

    public void populateEditext(User user) {
        updateFaculty.setText(user.getFaculty());
        updateMajor.setText(user.getMajor());
        updatePhone.setText(user.getSex());
        updateHighSchool.setText(user.getHighschool());
        updateYear.setText(user.getYear());
    }

    public void apiUpdateProfile() {
        showProgressDialog(true);
        int id = currentUser.getId();
        String faculty = updateFaculty.getText().toString();
        String major = updateMajor.getText().toString();
        String phone = updatePhone.getText().toString();
        String year = updateYear.getText().toString();
        String highschool = updateHighSchool.getText().toString();
        if (!faculty.equals("") && !major.equals("") && !phone.equals("") && !year.equals("") & !highschool.equals("")) {
            ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
            Call<ServerResponse> updateProfile = apiInterface.updateProfile(id, major, faculty, year, phone, highschool);
            updateProfile.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    p.setMessage("Updated");
                    Intent i = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    showProgressDialog(false);
                    AlertDialog taiwo = new AlertDialog.Builder(EditActivity.this)
                            .setCancelable(true)
                            .setMessage("Check your internet connection.")
                            .setTitle("Unable to Connect")
                            .create();
                    taiwo.show();

                }
            });
        } else {
            showProgressDialog(false);
            Toast.makeText(EditActivity.this, "Please leave no empty box.", Toast.LENGTH_LONG).show();
        }

    }

    public void preparePasswordDialog() {
        final AlertDialog.Builder passDialog = new AlertDialog.Builder(EditActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.update_password_dialog, null);
        final EditText oldPassword = (EditText) view.findViewById(R.id.update_old_password);
        final EditText newPassword = (EditText) view.findViewById(R.id.update_new_password);
        passDialog.setView(view);
        passDialog.setTitle("Change Password");
        passDialog.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        passDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = passDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old = oldPassword.getText().toString();
                String newp = newPassword.getText().toString();
                int id = currentUser.getId();
                if (!old.equals("") && !newp.equals("")) {
                    dialog.dismiss();
                    apiUpdatePassword(id, old, newp);
                } else {
                    Toast.makeText(EditActivity.this, "Fill Completely", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void apiUpdatePassword(int id, String old_password, String new_password) {
        showProgressDialog(true);
        ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
        Call<ServerResponse> updatePassword = apiInterface.updatePassword(id, new_password, old_password);
        updatePassword.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showProgressDialog(false);
                if (response.isSuccessful()) {
                    AlertDialog taiwo = new AlertDialog.Builder(EditActivity.this)
                            .setCancelable(true)
                            .setMessage("password updated successfully.")
                            .setTitle("Change Password")
                            .create();
                    taiwo.show();
                } else {
                    AlertDialog taiwo = new AlertDialog.Builder(EditActivity.this)
                            .setCancelable(true)
                            .setMessage("Can not update password check your old password.")
                            .setTitle("Error")
                            .create();
                    taiwo.show();
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showProgressDialog(false);
                AlertDialog taiwo = new AlertDialog.Builder(EditActivity.this)
                        .setCancelable(true)
                        .setMessage("Check your internet connection.")
                        .setTitle("Unable to Connect")
                        .create();
                taiwo.show();
            }
        });
    }

    public void showProgressDialog(boolean show) {
        if (p == null) {
            p = new ProgressDialog(EditActivity.this);
            p.setMessage("Updating ...");
            p.getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
            p.setCancelable(false);
        }
        if (show) {
            if (!p.isShowing()) {
                p.show();
            }
        } else {
            if (p.isShowing()) {
                p.dismiss();
            }
        }
    }
}
