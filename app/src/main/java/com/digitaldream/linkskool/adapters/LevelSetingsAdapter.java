package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.R;

import java.util.ArrayList;
import java.util.List;

public class LevelSetingsAdapter extends RecyclerView.Adapter<LevelSetingsAdapter.LevelSettingViewHolder> {
    private Context context;
    private List<LevelTable> levelList;
    private OnEditBtnClickListener onEditBtnClickListener;
    public static List<String> selectedId=new ArrayList<>();

    public LevelSetingsAdapter(Context context, List<LevelTable> levelList,OnEditBtnClickListener onEditBtnClickListener) {
        this.context = context;
        this.levelList = levelList;
        this.onEditBtnClickListener = onEditBtnClickListener;
    }

    @NonNull
    @Override
    public LevelSettingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.level_settings_item,viewGroup,false);
        return new LevelSettingViewHolder(view,onEditBtnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final LevelSettingViewHolder levelSettingViewHolder, int i) {
        final LevelTable lt = levelList.get(i);
        levelSettingViewHolder.levelName.setText(lt.getLevelName().toUpperCase());
        levelSettingViewHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lt.setSelected(!lt.isSelected());
                levelSettingViewHolder.rootView.setBackgroundColor(lt.isSelected()? Color.parseColor("#ECECEC") : Color.WHITE);
                if(lt.isSelected()) {
                    String id = lt.getLevelId();
                    selectedId.add(id);
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportActionBar().setTitle("");
                    activity.invalidateOptionsMenu();
                }else{
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.invalidateOptionsMenu();
                    String id = lt.getLevelId();
                    // insert code here
                    int index = -1;
                    for (int i=0;i<selectedId.size();i++) {
                        if (selectedId.get(i).equals(id)) {
                            index = i;
                            break;
                        }
                    }
                    try {
                        selectedId.remove(index);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    class LevelSettingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView levelName;
        ImageView editBtn;
        OnEditBtnClickListener onEditBtnClickListener;
        View rootView;

        public LevelSettingViewHolder(@NonNull View itemView,OnEditBtnClickListener onEditBtnClickListener) {
            super(itemView);
            levelName = itemView.findViewById(R.id.level_name);
            editBtn = itemView.findViewById(R.id.level_edit);
            this.onEditBtnClickListener = onEditBtnClickListener;
            editBtn.setOnClickListener(this);
            rootView = itemView;
        }

        @Override
        public void onClick(View v) {
            onEditBtnClickListener.onEditBtnClick(getAdapterPosition());
        }
    }

    public interface OnEditBtnClickListener{
        void onEditBtnClick(int position);
    }
}
