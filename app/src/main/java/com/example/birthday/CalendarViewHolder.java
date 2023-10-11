package com.example.birthday;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView eventNameTextView;
    TextView eventDateTextView;
    OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
        super(itemView);
        eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
        eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
        this.onItemListener = onItemListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition());
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
