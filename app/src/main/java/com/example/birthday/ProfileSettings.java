package com.example.birthday;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.birthday.Module.userData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class ProfileSettings extends AppCompatActivity {
    private ImageView profileImageView;
    private Button changeProfileButton;
    private TextView usernameEditText;
    private TextView emailEditText;
    private TextView passwordEditText;
    private TextView ageEditText;
    private TextView genderEditText;
    private TextView dob;
    private Button saveButton;
    private String userLabel;
    private int selectedImageResource;
    private SharedPreferences sharedPreferences;
    private userData updatedUserData;
    private FirebaseAuth mAuth;

    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        profileImageView = findViewById(R.id.profileImageView);
        changeProfileButton = findViewById(R.id.changeProfileButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ageEditText = findViewById(R.id.ageEditText);
        genderEditText = findViewById(R.id.genderEditText);
        dob = findViewById(R.id.dob);
        saveButton = findViewById(R.id.saveButton);

        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePictureSelectionDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                updateUserDataInDatabase();
            }
        });

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");


        String apiUrl = "https://testbeds.space/apps/birthday-reminder-app/public/api/get_user_data?email=" + userEmail;
        JsonArrayRequest userDataRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject userData = response.getJSONObject(0);
                            String userName = userData.getString("name");
                            usernameEditText.setText(userName);
                            String email = userData.getString("email");
                            String age = userData.getString("age");
                            String password = userData.getString("password");
                            String gender = userData.getString("gender");
                            String dobString = userData.getString("dob");

                            emailEditText.setText(email);
                            ageEditText.setText(age);
                            passwordEditText.setText(password);
                            genderEditText.setText(gender);
                            dob.setText(dobString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        Volley.newRequestQueue(this).add(userDataRequest);



        ImageView editUsernameButton = findViewById(R.id.nameEdit);
        ImageView editpassButton = findViewById(R.id.passEdit);
        ImageView editageButton = findViewById(R.id.ageEdit);
        ImageView editgenderButton = findViewById(R.id.genderEdit);
        ImageView editdobButton = findViewById(R.id.dobEdit);


        editUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode(usernameEditText,true);
            }
        });

        editpassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode(passwordEditText,true);
            }
        });

        editageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode(ageEditText,true);
            }
        });

        editgenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode(genderEditText,true);
            }
        });

        editdobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode(dob,true);
            }
        });


    }


    private void enableEditMode(TextInputEditText editText, boolean enable) {
        editText.setEnabled(enable);
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setClickable(enable);
        if (enable) {
            editText.requestFocus();
            showSoftKeyboard(editText);
        }
        saveButton.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void enableEditMode(TextView textView, boolean enable) {
        textView.setEnabled(enable);
        textView.setFocusable(enable);
        textView.setFocusableInTouchMode(enable);
        textView.setClickable(enable);
        if (enable) {
            textView.requestFocus();
            showSoftKeyboard(textView);
        }
        saveButton.setVisibility(enable ? View.VISIBLE : View.GONE);
    }




    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }




    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;
                        updateDateDisplay();

                        String selectedDate = String.format(Locale.US, "%02d-%02d-%04d", day, month + 1, year);
                        dob.setText(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        String formattedDate = String.format(Locale.US, "%02d-%02d-%04d", day, month + 1, year);
        dob.setText(formattedDate);
    }

    private void showProfilePictureSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture for " + userLabel);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_profile_picture_dialog, null);
        builder.setView(dialogLayout);

        ImageView maleProfile1ImageView = dialogLayout.findViewById(R.id.maleProfile1ImageView);
        ImageView maleProfile2ImageView = dialogLayout.findViewById(R.id.maleProfile2ImageView);
        ImageView maleProfile3ImageView = dialogLayout.findViewById(R.id.maleProfile3ImageView);
        ImageView femaleProfile1ImageView = dialogLayout.findViewById(R.id.femaleProfile1ImageView);
        ImageView femaleProfile2ImageView = dialogLayout.findViewById(R.id.femaleProfile2ImageView);
        ImageView femaleProfile3ImageView = dialogLayout.findViewById(R.id.femaleProfile3ImageView);

        maleProfile1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageResource = R.drawable.boy1;
                showConfirmationDialog("Male Profile 1");
            }
        });

        maleProfile2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageResource = R.drawable.boy2;
                showConfirmationDialog("Male Profile 2");
            }
        });

        maleProfile3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageResource = R.drawable.boy3;
                showConfirmationDialog("Male Profile 3");
            }
        });

        femaleProfile1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageResource = R.drawable.girl1;
                showConfirmationDialog("Female Profile 1");
            }
        });

        femaleProfile2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageResource = R.drawable.girl2;
                showConfirmationDialog("Female Profile 2");
            }
        });

        femaleProfile3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageResource = R.drawable.girl3;
                showConfirmationDialog("Female Profile 3");
            }
        });

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showConfirmationDialog(String selectedLabel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Profile Picture");
        builder.setMessage("Do you want to set the profile picture to " + selectedLabel + "?");

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userLabel", userLabel);
                editor.putInt("selectedImageResource", selectedImageResource);
                editor.apply();

                profileImageView.setImageResource(selectedImageResource);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void updateUserDataInDatabase() {
        String apiUrl = "https://testbeds.space/apps/birthday-reminder-app/public/api/save"; // Replace this with your actual API endpoint URL

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", usernameEditText.getText().toString());
            jsonBody.put("email", emailEditText.getText().toString());
            jsonBody.put("password", passwordEditText.getText().toString());
            jsonBody.put("age", ageEditText.getText().toString());
            jsonBody.put("gender", genderEditText.getText().toString());
            jsonBody.put("dob", dob.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showToast("User data updated successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors (failure case)
                        showToast("Failed to update user data: " + error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
