package com.example.birthday;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.birthday.Module.Friend;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<Friend> friends = new ArrayList<>();
    private List<Friend> allBirthdays = new ArrayList<>();
    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private FriendsAdapter adapter1, adapter2, adapter3;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private common common;
    private ImageView profileImageView;
    private SharedPreferences sharedPreferences;
    private Friend selectedFriend;
    BottomNavigationView bottomNavigationView;
//    private void navigateToProfileSettings() {
//        String userEmail = getLoggedInUserEmail();
//
//        if (userEmail != null) {
//            Intent intent = new Intent(HomeScreen.this, ProfileSettings.class);
//            intent.putExtra("email", userEmail); // Put the user's email as an extra
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        common = new common();
        profileImageView = findViewById(R.id.profile);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, ProfileSettings.class);
//                intent.putExtra("email", userEmail); // userEmail is the email you want to pass
                startActivity(intent);
            }
        });


        BottomAppBar bottomAppBar = findViewById(R.id.bottobar);
        BottomNavigationView bottomNavigationView = bottomAppBar.findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_1:
                        Intent intent = new Intent(HomeScreen.this, HomeScreen.class);
                        startActivity(intent);
                        return true;
                    case R.id.page_2:
                        Intent page2 = new Intent(HomeScreen.this, CardScreen.class);
                        startActivity(page2);
                        return true;
                    case R.id.page_3:
                        Intent page3 = new Intent(HomeScreen.this, AddBirthday.class);
                        startActivity(page3);
                        return true;
                    case R.id.page_4:
                        Intent page4 = new Intent(HomeScreen.this, CalendarScreen.class);
                        startActivity(page4);
                        return true;
                }
                return false;
            }
        });



        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        int selectedImageResource = sharedPreferences.getInt("selectedImageResource", R.drawable.boy1);
        profileImageView.setImageResource(selectedImageResource);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        recyclerView1 = findViewById(R.id.upcomingBirthdaysRecyclerView);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter1 = new FriendsAdapter(this);
        recyclerView1.setAdapter(adapter1);

        recyclerView2 = findViewById(R.id.DailyBirthdaysRecyclerView);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new FriendsAdapter(this);
        recyclerView2.setAdapter(adapter2);

        recyclerView3 = findViewById(R.id.WeeklyBirthdaysRecyclerView);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        adapter3 = new FriendsAdapter(this);
        recyclerView3.setAdapter(adapter3);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fetchFriendsData();
    }



    private String getLoggedInUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_email", null);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_logout:
                SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

//                Intent intent = new Intent(HomeScreen.this, Login.class);
//                intent.FLAG_ACTIVITY_CLEAR_TOP;
//                startActivity(intent);
                Intent a = new Intent(this,Login.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);

        }
        drawerLayout.closeDrawers();
        return true;
    }

    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;

        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return date;
    }

    private void fetchFriendsData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest birthdayObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                common.UPCOMING_URL,
                null,
                response -> {
                    try {
                        JSONArray upcomingBirthdaysArray = response.getJSONArray("upcoming_birthdays");
                        JSONArray dailyBirthdaysArray = response.getJSONArray("daily_birthdays");
                        JSONArray weeklyBirthdaysArray = response.getJSONArray("weekly_birthdays");

                        List<Friend> upcomingBirthdays = parseJsonResponse(upcomingBirthdaysArray);
                        List<Friend> dailyBirthdays = parseJsonResponse(dailyBirthdaysArray);
                        List<Friend> weeklyBirthdays = parseJsonResponse(weeklyBirthdaysArray);

                        adapter1.setFriends(upcomingBirthdays);
                        adapter2.setDailyBirthdays(dailyBirthdays);
                        adapter3.setWeeklyBirthdays(weeklyBirthdays);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON Parsing Error", e.getMessage());
                        common.showToast(HomeScreen.this, "Error parsing JSON");
                    }
                },
                error -> {
                    Log.e("API Error", error.toString());
                    common.showToast(HomeScreen.this, "Error fetching data");
                }
        );

        requestQueue.add(birthdayObjectRequest);
    }


    private List<Friend> parseJsonResponse(JSONArray response) {
        List<Friend> friends = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                String name = jsonObject.optString("name", "Unknown");
                String dobFromJson = jsonObject.optString("dob", "Unknown");
                Log.d("Friend Info", "Name: " + name + ", DOB: " + dobFromJson);
                Date dob = parseDate(dobFromJson);
                int age = calculateAge(dob);
                friends.add(new Friend(name, dobFromJson)); // Adding "Unknown" as gender, adjust this accordingly
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friends;
    }


    private void filterUpcomingBirthdays(List<Friend> friends) {

        List<Friend> upcomingBirthdays = new ArrayList<>();
        Calendar currentDate = Calendar.getInstance();

        Collections.sort(friends, (friend1, friend2) -> {
            Date dob1 = parseDate(friend1.getDob());
            Date dob2 = parseDate(friend2.getDob());
            return dob1.compareTo(dob2);
        });

        for (Friend friend : friends) {
            if (upcomingBirthdays.size() >= 5) {
                break;
            }

            Date dob = parseDate(friend.getDob());
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);
            dobCalendar.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));

            if (dobCalendar.before(currentDate)) {
                dobCalendar.set(Calendar.YEAR, currentDate.get(Calendar.YEAR) + 1);
            }

            int daysUntilBirthday = (int) ((dobCalendar.getTimeInMillis() - currentDate.getTimeInMillis()) / (24 * 60 * 60 * 1000));

            if (daysUntilBirthday >= 0) {
                upcomingBirthdays.add(friend);
            }
        }

        adapter1.setFriends(upcomingBirthdays);
    }

    private void filterDailyBirthday(List<Friend> friends) {
        Calendar currentDate = Calendar.getInstance();
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;

        List<Friend> dailyBirthdays = new ArrayList<>();

        for (Friend friend : friends) {
            String dob = friend.getDob();
            String[] parts = dob.split("-");
            int birthMonth = Integer.parseInt(parts[1]);
            int birthDay = Integer.parseInt(parts[2]);

            Log.d("Friend DOB", "Month: " + birthMonth + ", Day: " + birthDay);

            if (birthMonth == currentMonth && birthDay == currentDay) {
                dailyBirthdays.add(friend);
                Log.d("Added Friend", "Name: " + friend.getName());
            }
        }

        Log.d("Daily Birthdays Count", String.valueOf(dailyBirthdays.size()));
        for (Friend friend : dailyBirthdays) {
            Log.d("Daily Friend", "Name: " + friend.getName() + ", DOB: " + friend.getDob());
        }

        adapter2.setDailyBirthdays(dailyBirthdays);
    }


    private int calculateAge(Date dob) {
        if (dob == null) {
            return 0;
        }

        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);
        Calendar currentCalendar = Calendar.getInstance();

        int age = currentCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

}