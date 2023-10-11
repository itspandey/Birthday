package com.example.birthday;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.birthday.Module.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarScreen extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private List<Friend> friends = new ArrayList<>();
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_screen);

        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        calendarAdapter = new CalendarAdapter(friends, this);
        recyclerView.setAdapter(calendarAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            fetchFriendsBirthday(selectedDate);
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String currentSelectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month, dayOfMonth);
        fetchFriendsBirthday(currentSelectedDate);
    }

    private void fetchFriendsBirthday(String selectedDate) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest birthdayObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                common.UPCOMING_URL + "?date=" + selectedDate,
                null,
                response -> {
                    Log.d("Server Response", response.toString());

                    List<Friend> friends = parseJsonResponse(response);
                    calendarAdapter.setFriends(friends);

                },
                error -> {
                    Log.e("API Error", error.toString());
                    common.showToast(CalendarScreen.this, "Error fetching data");
                }
        );

        requestQueue.add(birthdayObjectRequest);
    }

    private List<Friend> parseJsonResponse(JSONObject response) {
        List<Friend> parsedFriends = new ArrayList<>();

        JSONArray dailyBirthdaysArray = response.optJSONArray("daily_birthdays");
        JSONArray upcomingBirthdaysArray = response.optJSONArray("upcoming_birthdays");
        JSONArray weeklyBirthdaysArray = response.optJSONArray("weekly_birthdays");

        if (dailyBirthdaysArray != null) {
            parsedFriends.addAll(parseBirthdayArray(dailyBirthdaysArray));
        }

        if (upcomingBirthdaysArray != null) {
            parsedFriends.addAll(parseBirthdayArray(upcomingBirthdaysArray));
        }

        if (weeklyBirthdaysArray != null) {
            parsedFriends.addAll(parseBirthdayArray(weeklyBirthdaysArray));
        }

        return parsedFriends;
    }

    private List<Friend> parseBirthdayArray(JSONArray jsonArray) {
        List<Friend> birthdayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject birthdayObject = jsonArray.optJSONObject(i);
            if (birthdayObject != null) {
                String name = birthdayObject.optString("name", "Unknown");
                String dobFromJson = birthdayObject.optString("dob", "Unknown");
                if (!dobFromJson.equals("Unknown")) {
                    Date dob = parseDate(dobFromJson);
                    int age = calculateAge(dob);
                    birthdayList.add(new Friend(name, String.valueOf(age)));
                }
            }
        }

        return birthdayList;
    }


    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public void onItemClick(int position) {
        Friend friend = friends.get(position);
        showFriendDetailsDialog(friend);
    }

    private void showFriendDetailsDialog(Friend friend) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Friend Details");
        builder.setMessage("Name: " + friend.getName() + "\nDOB: " + friend.getDob());

        builder.setPositiveButton("Send Wishes", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
