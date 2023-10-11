package com.example.birthday;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddBirthday extends AppCompatActivity {

    private EditText friendNameEditText;
    private EditText friendBirthdayEditText;
    private EditText friendAgeEditText;
    private EditText friendGenderEditText;

    private int year, month, day;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);

        friendNameEditText = findViewById(R.id.friendNameEditText);
        friendBirthdayEditText = findViewById(R.id.friendBirthdayEditText);
        friendAgeEditText = findViewById(R.id.friendAgeEditText);
        friendGenderEditText = findViewById(R.id.friendGenderEditText);
        Button button = findViewById(R.id.homescreen);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddBirthday.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        friendBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String userId = sharedPreferences.getString("email", ""); // Retrieve user ID from "id" field
        Log.d("UserID", "User ID retrieved: " + userId);
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendName = friendNameEditText.getText().toString();
                String friendBirthday = friendBirthdayEditText.getText().toString();
                String friendAge = friendAgeEditText.getText().toString();
                String friendGender = friendGenderEditText.getText().toString();

                if (TextUtils.isEmpty(friendName) || TextUtils.isEmpty(friendBirthday) || TextUtils.isEmpty(friendAge) || TextUtils.isEmpty(friendGender)) {
                    Toast.makeText(AddBirthday.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, String> friendData = new HashMap<>();

                friendData.put("user_id", userId);
                friendData.put("name", friendName);
                friendData.put("dob", friendBirthday);
                friendData.put("age", friendAge);
                friendData.put("gender", friendGender);
                Log.d("API Request", "User ID: " + userId);
                Log.d("API Request", "Friend Name: " + friendName);
                Log.d("API Request", "Friend Birthday: " + friendBirthday);
                Log.d("API Request", "Friend Age: " + friendAge);
                Log.d("API Request", "Friend Gender: " + friendGender);
                JSONObject jsonBody = new JSONObject(friendData);

                String apiUrl = "https://testbeds.space/apps/birthday-reminder-app/public/api/submit";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message = response.getString("message");
                                    if ("Friend added successfully".equals(message)) {
                                        Toast.makeText(AddBirthday.this, "Friend added successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(AddBirthday.this, "Failed to add friend", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(AddBirthday.this, "Failed to parse JSON response", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle the error response from the API (e.g., show an error message)
                                Toast.makeText(AddBirthday.this, "Failed to add friend: " + error.toString(), Toast.LENGTH_LONG).show();
                                // Log the full error response for further analysis
                                Log.e("API Error Response", error.toString());
                            }
                        });

                // Add the request to the Volley queue
                Volley.newRequestQueue(AddBirthday.this).add(jsonObjectRequest);
            }
        });
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

                        // Update the EditText with the selected date in the desired format
                        String selectedDate = String.format(Locale.US, "%02d-%02d-%04d", day, month + 1, year);
                        friendBirthdayEditText.setText(selectedDate);
                    }
                },
                year, month, year // Pass the 'year' variable as the initial year
        );

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        String formattedDate = String.format(Locale.US, "%02d-%02d-%04d", day, month + 1, year);
        friendBirthdayEditText.setText(formattedDate);
    }
}
