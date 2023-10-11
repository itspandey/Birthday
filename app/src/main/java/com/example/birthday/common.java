package com.example.birthday;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.birthday.Module.Friend;
import com.example.birthday.Module.userData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class common {

    public static final String PREFS_NAME = "userData";
    public static final String BASE_URL = "https://testbeds.space/apps/birthday-reminder-app/public/api/get_data";
    public static final String UPCOMING_URL = "https://testbeds.space/apps/birthday-reminder-app/public/api/combinedBirthdays";
//    public static final String DAILY_URL = "https://testbeds.space/apps/birthday-reminder-app/public/api/getDailyBirthdays";
//    public static final String WEEKLY_URL = "https://testbeds.space/apps/birthday-reminder-app/public/api/getWeeklyBirthdays";

    public static void storeUserData(Context context, String name, String gender, String age, String email, String password, String birthdate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("gender", gender);
        editor.putString("age", age);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("dob", birthdate);
        editor.apply();
    }
//public static get
    public static userData retrieveUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String gender = sharedPreferences.getString("gender", "");
        String age = sharedPreferences.getString("age", "");
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        String birthdate = sharedPreferences.getString("dob", "");
        return new userData(name, gender, age, email, password, birthdate);
    }

    public static void updateUserData(Context context, String name, String gender, String age, String email, String password, String birthdate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("gender", gender);
        editor.putString("age", age);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("dob", birthdate);
        editor.apply();
    }


    public static List<Friend> parseFriendsJson(JSONArray jsonArray) {
        List<Friend> friends = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String dob = jsonObject.getString("dob");
                String gender = jsonObject.getString("gender");
                String age = jsonObject.getString("age");
                friends.add(new Friend(name, dob));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
