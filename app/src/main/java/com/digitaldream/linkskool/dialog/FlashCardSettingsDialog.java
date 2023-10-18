package com.digitaldream.linkskool.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.utils.FlashCardTagsSettings;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.j256.ormlite.dao.Dao;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class FlashCardSettingsDialog extends BottomSheetDialogFragment {
    private DatabaseHelper databaseHelper;
    Dao<CourseTable,Long> courseTableDao;
    Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private List<CourseTable> coursesList;
    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private List<CourseOutlineTable> list;
    List<CourseOutlineTable> courseList;
    private String accessLevel,sender,db;
    public static String title="";
    public static JSONObject levelObj ;
    public static JSONObject courseObj;

        public static FlashCardSettingsDialog newInstance(String from) {
        FlashCardSettingsDialog bottomSheetFragment = new FlashCardSettingsDialog();
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        bottomSheetFragment .setArguments(bundle);
        return bottomSheetFragment ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.flash_card_setting,container,false);
        EditText titleEDT = view.findViewById(R.id.debt_fee_title);
        TextView submitBtn = view.findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleEDT.getText().toString();
                if (title.isEmpty()){
                    Toast.makeText(getContext(),"Title can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    FragmentTransaction transaction = ((FragmentActivity) getContext())
                            .getSupportFragmentManager()
                            .beginTransaction();
                    FlashCardTagsSettings dailog = new FlashCardTagsSettings();
                    dailog.show(transaction, "TagsBottomSheet");
                }
            }
        });

        return view;
    }
}
