package com.example.birthday;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SignUpScreen extends AppCompatActivity {

    private TextInputEditText editTextName, editTextEmail, editTextPassword, ageEditAge, editTextGender;
    private Button buttonSignup;
    private ProgressBar progressBar;
    private EditText dobEditText;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonSignup = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progrssbar);
        ageEditAge = findViewById(R.id.age);
        editTextGender = findViewById(R.id.gender);
        dobEditText = findViewById(R.id.dobTextView);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    private class SignUpTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://testbeds.space/apps/birthday-reminder-app/public/api/save";

            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String age = ageEditAge.getText().toString().trim();
            String gender = editTextGender.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(age) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(dob)) {
                return "Please fill in all fields";
            }

            HashMap<String, String> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("password", password);
            userData.put("age", age);
            userData.put("gender", gender);
            userData.put("dob", dob);

            JSONObject jsonBody = new JSONObject(userData);
            JsonObjectRequest signUpRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            handleJsonResponse(response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SignUpScreen.this, "Failed to sign up: " + error.toString(), Toast.LENGTH_LONG).show();
                            Log.e("API Error Response", error.toString());
                            progressBar.setVisibility(View.GONE);
                        }
                    });

            progressBar.setVisibility(View.VISIBLE);
            Volley.newRequestQueue(SignUpScreen.this).add(signUpRequest);
            return null;
        }

        private void handleJsonResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.has("message")) {
                    String message = jsonResponse.getString("message");
                    if ("Friend added successfully".equals(message)) {
                        Toast.makeText(SignUpScreen.this, "Friend added successfully", Toast.LENGTH_SHORT).show();
                    } else if ("Email address already exists.".equals(message)) {
                        Toast.makeText(SignUpScreen.this, "Email address already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpScreen.this, "Failed to add friend: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpScreen.this, response, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(SignUpScreen.this, "Failed to parse JSON response", Toast.LENGTH_SHORT).show();
            } finally {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                Toast.makeText(SignUpScreen.this, result, Toast.LENGTH_SHORT).show();
            } else {
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void signUp() {
        new SignUpTask().execute();
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

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
                        dobEditText.setText(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        String formattedDate = String.format(Locale.US, "%02d-%02d-%04d", day, month + 1, year);
        dobEditText.setText(formattedDate);
    }
}
