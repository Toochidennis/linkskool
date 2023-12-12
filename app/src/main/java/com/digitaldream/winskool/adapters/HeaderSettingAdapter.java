package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.winskool.models.TestSettingModel;
import com.digitaldream.winskool.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeaderSettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<TestSettingModel> testSettingList;

    public HeaderSettingAdapter(Context context, List<TestSettingModel> testSettingList) {
        this.context = context;
        this.testSettingList = testSettingList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType){
            case 0:
                view =  inflater.inflate(R.layout.test_setting_header,parent,false);
                return new AssessmentVH(view);
            case 1:
                view =inflater.inflate(R.layout.flashcard_layout,parent,false);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return testSettingList.size();
    }

    @Override
    public int getItemViewType(int position) {
        TestSettingModel tsm = testSettingList.get(position);
        switch (tsm.getType()){
            case "assessment":
                return 0;
            case "flashcard":
                return 1;
        }
        return 0;
    }

    class AssessmentVH extends RecyclerView.ViewHolder{

        public AssessmentVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    class FlashCardVH extends RecyclerView.ViewHolder{

        public FlashCardVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
