package com.example.birthday;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birthday.Module.user;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONException;

import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button facebook;
    private TextView tvname, tvemail, tvgender, tvbirthdate;
    private ImageView fbprofileimage;
    private Bitmap mIcon11;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        facebook = findViewById(R.id.facebook);
        tvname = findViewById(R.id.tvname);
        tvemail = findViewById(R.id.tvemail);
        tvgender = findViewById(R.id.tvgender);
        tvbirthdate = findViewById(R.id.tvbirthdate);
        fbprofileimage = findViewById(R.id.fbprofileimage);

        facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFacebookLogin();
            }
        });
    }

    public void onClickFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            // user is not logged in or the access token has expired, initiate login
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_gender"));
        } else {
            // user is already logged in, fetch user data
            fetchFacebookData();
        }
    }

    private void fetchFacebookData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // Request user data including birthday and gender
        Bundle params = new Bundle();
        params.putString("fields", "id,first_name,last_name,email,gender,birthday,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                progressDialog.dismiss();
                handleFacebookResponse(response);
            }
        }).executeAsync();
    }

    private void handleFacebookResponse(GraphResponse response) {
        try {
            if (response != null) {
                org.json.JSONObject data = response.getJSONObject();

                String fbemail = data.optString("email", "");
                String fname = data.optString("first_name", "");
                String lname = data.optString("last_name", "");
                String mediaid = data.optString("id", "");
                String gender = data.optString("gender", "");
                String dob = data.optString("birthday", "");

                String image_url = data.getJSONObject("picture").getJSONObject("data").getString("url");

                // Store the user data in Firebase Realtime Database
                storeUserDataInFirebase(fname, lname, fbemail, gender, dob);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            InputStream in = new java.net.URL(image_url).openStream();
                            mIcon11 = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void v) {
                        tvname.setText(fname + " " + lname);
                        tvemail.setText(fbemail);
                        tvgender.setText("Gender: " + gender);
                        tvbirthdate.setText("Birthdate: " + dob);
                        if (mIcon11 != null) {
                            fbprofileimage.setImageBitmap(mIcon11);
                        } else {
                            Toast.makeText(MainActivity.this, "Null", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeUserDataInFirebase(String firstName, String lastName, String email, String gender, String birthday) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            user userData = new user(userId, firstName, lastName, email, gender, birthday);
            usersRef.child(userId).setValue(userData);
        }
    }
}
