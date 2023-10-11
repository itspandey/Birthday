package com.example.birthday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationFinder extends AppCompatActivity {


    private Button fetchFriendsButton;
    private ListView friendsListView;
    TextView backward;
    private ArrayAdapter<String> friendsAdapter;
    private List<String> friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_finder);

        fetchFriendsButton = findViewById(R.id.fetchFriendsButton);
        friendsListView = findViewById(R.id.friendsListView);
        backward = findViewById(R.id.backward);

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        friendsList = new ArrayList<>();
        friendsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(friendsAdapter);

        fetchFriendsButton.setOnClickListener(v -> fetchFacebookFriends());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Forward the result to the Facebook SDK
        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Handle successful login if needed
            }

            @Override
            public void onCancel() {
                // Handle cancelation if needed
            }

            @Override
            public void onError(FacebookException exception) {
                // Handle error if needed
            }
        });

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchFacebookFriends() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null || accessToken.isExpired()) {
            // Handle the case where the user is not logged in or the token has expired
            // You can prompt the user to log in again
            Log.d("FacebookFriendsActivity", "user is not logged in or token has expired.");
            return;
        }

        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                        handleFriendsData(jsonArray);
                    }
                }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture.type(large)"); // Include picture field
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleFriendsData(JSONArray friendsArray) {
        try {
            friendsList.clear();

            for (int i = 0; i < friendsArray.length(); i++) {
                JSONObject friend = friendsArray.getJSONObject(i);
                String friendName = friend.getString("name");

                friendsList.add(friendName);

                // You can also retrieve the friend's profile picture URL
                if (friend.has("picture")) {
                    JSONObject pictureObj = friend.getJSONObject("picture");
                    if (pictureObj.has("data")) {
                        JSONObject dataObj = pictureObj.getJSONObject("data");
                        String profilePictureUrl = dataObj.getString("url");
                        // You can use profilePictureUrl to load and display the profile picture
                    }
                }
            }

            friendsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}