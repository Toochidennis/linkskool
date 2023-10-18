package com.digitaldream.linkskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.digitaldream.linkskool.models.TestSettingModel;
import com.digitaldream.linkskool.R;

import java.util.ArrayList;
import java.util.List;

public class TestSettingHeaderAdapter extends RecyclerView.Adapter<TestSettingHeaderAdapter.TestSettingHeaderViewHolder> {
    private Context context;
    private List<TestSettingModel> testSettingList;
    OnTestSettingHeaderClickListener onTestSettingHeaderClickListener;

    public TestSettingHeaderAdapter(Context context, List<TestSettingModel> testSettingList,OnTestSettingHeaderClickListener onTestSettingHeaderClickListener) {
        this.context = context;
        this.testSettingList = testSettingList;
        this.onTestSettingHeaderClickListener = onTestSettingHeaderClickListener;
    }

    @NonNull
    @Override
    public TestSettingHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.test_setting_header,viewGroup,false);
        return new TestSettingHeaderViewHolder(view,onTestSettingHeaderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TestSettingHeaderViewHolder testSettingHeaderViewHolder, int i) {
        TestSettingModel tsm = testSettingList.get(i);
        if(!tsm.getTitle().isEmpty()) {
            testSettingHeaderViewHolder.title.setText(tsm.getTitle().toUpperCase());
        }else{
            testSettingHeaderViewHolder.title.setText("Title");

        }

        List<String> weekList = new ArrayList<>();
        for(int a=1;a<30;a++){
            weekList.add("Week "+a);
        }
        String weekText = "Week "+tsm.getWeek();
        testSettingHeaderViewHolder.week.setText(weekText);

    }

    @Override
    public int getItemCount() {
        return testSettingList.size();
    }

    class TestSettingHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView week;
        OnTestSettingHeaderClickListener onTestSettingHeaderClickListener;

        public TestSettingHeaderViewHolder(@NonNull View itemView,OnTestSettingHeaderClickListener onTestSettingHeaderClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.debt_fee_title);
            week = itemView.findViewById(R.id.weeks);
            this.onTestSettingHeaderClickListener = onTestSettingHeaderClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTestSettingHeaderClickListener.onTestSettingHeaderClick(getAdapterPosition());
        }
    }

    public interface OnTestSettingHeaderClickListener{
        void onTestSettingHeaderClick(int position);
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }
}
