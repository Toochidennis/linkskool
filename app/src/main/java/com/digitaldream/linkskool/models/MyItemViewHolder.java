package com.digitaldream.linkskool.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitaldream.linkskool.R;

final class MyItemViewHolder extends RecyclerView.ViewHolder {
    final TextView classNames;
    final ImageView clearBtn;

    public MyItemViewHolder(@NonNull View itemView) {
        super(itemView);
        classNames = itemView.findViewById(R.id.classnames);
        clearBtn = itemView.findViewById(R.id.course_clear);
    }

}
