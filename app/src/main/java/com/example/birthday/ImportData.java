package com.example.birthday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ImportData extends AppCompatActivity {

    Button logout;
    FirebaseAuth auth;
    TextView textView;
    FirebaseUser user;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
        logout = findViewById(R.id.logout);
        textView = findViewById(R.id.userId);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String name = getIntent().getStringExtra("name");
        textView.setText(name);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        Button retrieveContactsButton = findViewById(R.id.retrieveContactsButton);
        retrieveContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ImportData.this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImportData.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {

                    readContacts();
                }
            }
        });
    }

    private void readContacts() {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String contactBirthday = getBirthdayForContact(name);

                String contactAge = ""; //collect this data from users
                String contactGender = ""; //collect this data from users

                // Process contact information here
                Log.d("Contact", "name: " + name);
                Log.d("Contact", "dob: " + contactBirthday);
                Log.d("Contact", "age: " + contactAge);
                Log.d("Contact", "gender: " + contactGender);

                saveContactDataInSharedPreferences(name, contactBirthday, contactAge, contactGender);

                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("name", name);
                dataMap.put("dob", contactBirthday);
                dataMap.put("age", contactAge);
                dataMap.put("gender", contactGender);

                sendContactDataToAPI(dataMap);
            }
            cursor.close();
        }
    }

    private String getBirthdayForContact(String contactName) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Event.START_DATE
        };

        Cursor cursor = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + " = ?",
                new String[]{contactName, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                        String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)},
                null
        );

        String birthday = null;
        if (cursor != null && cursor.moveToFirst()) {
            birthday = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
            cursor.close();
        }

        return birthday;
    }

    private void saveContactDataInSharedPreferences(String name, String dob, String age, String gender) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("dob", dob);
        editor.putString("age", age);
        editor.putString("gender", gender);

        editor.apply();
    }

    private void sendContactDataToAPI(HashMap<String, String> dataMap) {
        String url = "https://testbeds.space/apps/birthday-reminder-app/public/api/submit";


        JSONObject jsonBody = new JSONObject(dataMap);               // Convert the HashMap to a JSON object

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API Response", response.toString());
                        try {
                            String message = response.getString("message");
                            Toast.makeText(ImportData.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", "Failed to send data to API: " + error.toString());
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
