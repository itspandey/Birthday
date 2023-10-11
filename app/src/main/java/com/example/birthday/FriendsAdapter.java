package com.example.birthday;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birthday.Module.Friend;

import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private final Context context;
    private List<Friend> friends;
    private List<Friend> dailyBirthdays;
    private List<Friend> weeklyBirthdays;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Friend friend);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FriendsAdapter(Context context) {
        this.context = context;
        this.friends = new ArrayList<>();
        this.dailyBirthdays = new ArrayList<>();
        this.weeklyBirthdays = new ArrayList<>();
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public void setDailyBirthdays(List<Friend> dailyBirthdays) {
        this.dailyBirthdays = dailyBirthdays;
        notifyDataSetChanged();
    }

    public void setWeeklyBirthdays(List<Friend> weeklyBirthdays) {
        this.weeklyBirthdays = weeklyBirthdays;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < dailyBirthdays.size()) {
            bindFriendData(holder, dailyBirthdays.get(position));
        } else if (position < dailyBirthdays.size() + weeklyBirthdays.size()) {
            bindFriendData(holder, weeklyBirthdays.get(position - dailyBirthdays.size()));
        } else {
            bindFriendData(holder, friends.get(position - dailyBirthdays.size() - weeklyBirthdays.size()));
        }

    }

    private void bindFriendData(ViewHolder holder, Friend friend) {
        holder.nameTextView.setText("Name: " + friend.getName());
        holder.dobTextView.setText("DOB: " + friend.getDob());



        holder.greetingbytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showCustomMessageDialog(friend);

            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("Adapter Data", "Item count: " + (dailyBirthdays.size() + weeklyBirthdays.size() + friends.size()));
        return dailyBirthdays.size() + weeklyBirthdays.size() + friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dobTextView;
        ImageView greetingbytext,greetingbycard;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dobTextView = itemView.findViewById(R.id.dobTextView);
            greetingbytext = itemView.findViewById(R.id.greetingbytext);
            greetingbycard = itemView.findViewById(R.id.greetingbycard);

        }
    }

    private void sendBirthdayWish(String message) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Send via"));
    }


    private void showCustomMessageDialog(Friend friend) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Write Greeting Message for " + friend.getName());

        View customLayout = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null);
        EditText customMessageEditText = customLayout.findViewById(R.id.customMessageEditText);
        Button sendButton = customLayout.findViewById(R.id.sendButton);
        Button cancelButton = customLayout.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.setView(customLayout).create();

        customMessageEditText.setText("Happy Birthday " + friend.getName() + "!");


        sendButton.setOnClickListener(view -> {
            String customMessage = customMessageEditText.getText().toString().trim();
            if (!customMessage.isEmpty()) {
//                customMessage = "Happy birthday, " + friend.getId() + "! " + customMessage;
                sendBirthdayWish(customMessage);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please write something", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }


}