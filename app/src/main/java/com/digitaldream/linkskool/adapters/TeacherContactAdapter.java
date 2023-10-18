package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.TeacherContacts;
import com.digitaldream.linkskool.models.TeachersTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeacherContactAdapter extends RecyclerView.Adapter<TeacherContactAdapter.TeacherContactViewHolder> {
    private Context context;
    private List<TeachersTable> teacherContactList;
    OnTeacherContactClickListener onTeacherContactClickListener;
    public static List<String> staffPhones = new ArrayList<>();
    public static List<String> staffEmails = new ArrayList<>();
    public static boolean flagValue = false;
    public static int selectedCounter =0;

    public TeacherContactAdapter(Context context, List<TeachersTable> teacherContactList, OnTeacherContactClickListener onTeacherContactClickListener) {
        this.context = context;
        this.teacherContactList = teacherContactList;
        this.onTeacherContactClickListener = onTeacherContactClickListener;
    }

    @NonNull
    @Override
    public TeacherContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int a = i;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_item,viewGroup,false);
        return new TeacherContactViewHolder(view,onTeacherContactClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final TeacherContactViewHolder teacherContactViewHolder, int i) {
        final int a = i;
        final TeachersTable tch = teacherContactList.get(i);
        teacherContactViewHolder.teacherCounter.setText(String.valueOf(i+1));
        String teacherName = teacherContactList.get(i).getStaffSurname().toUpperCase() +" "+teacherContactList.get(i).getStaffFirstname().toUpperCase();
        String[] strArray = teacherName.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            if(s.length()>0) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");
            }
        }
        if(flagValue){
            selectAll();
            teacherContactViewHolder.cardview.setBackgroundColor(tch.isSelected()?Color.parseColor("#ECECEC") : Color.WHITE);
            //flagValue=false;

        }else{
            tch.setSelected(false);
            flagValue = false;
        }

        GradientDrawable gd = (GradientDrawable) teacherContactViewHolder.counterBackground.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        teacherContactViewHolder.counterBackground.setBackground(gd);

        //teacherContactViewHolder.cardview.setBackgroundColor(tch.isSelected()?Color.parseColor("#ECECEC") : Color.WHITE);
        teacherContactViewHolder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tch.setSelected(!tch.isSelected());
                teacherContactViewHolder.cardview.setBackgroundColor(tch.isSelected()?Color.parseColor("#ECECEC") : Color.WHITE);
                if(teacherContactList.size()==selectedCounter) {
                    Menu menu = TeacherContacts.getMenu();
                    menu.findItem(R.id.select_all).setVisible(false);
                }
                if(tch.isSelected()){
                    String phoneNo = tch.getStaffPhone();
                    staffPhones.add(phoneNo);
                    staffPhones.indexOf(phoneNo);
                    String emails = tch.getStaffEmail();
                    staffEmails.add(emails);

                    selectedCounter=+1;


                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportActionBar().setTitle("");
                    activity.invalidateOptionsMenu();




                    Log.i("staffPhones", staffPhones.toString());
                }else{
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.invalidateOptionsMenu();
                    String ph = tch.getStaffPhone();
                    // insert code here
                    int index = -1;
                    for (int i=0;i<staffPhones.size();i++) {
                        if (staffPhones.get(i).equals(ph)) {
                            index = i;
                            break;
                        }
                    }
                    staffPhones.remove(index);
                    staffEmails.remove(index);

                    selectedCounter =-1;
                    if(staffPhones.size()==0){
                        flagValue=false;
                    }

                }


                return true;
            }
        });

        teacherContactViewHolder.name.setText(builder.toString());

        if(teacherContactList.get(i).getStaffPhone().equals("")){
            teacherContactViewHolder.callButton.setVisibility(View.INVISIBLE);
            teacherContactViewHolder.smsButton.setVisibility(View.INVISIBLE);
        }
        teacherContactViewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.content.Intent.ACTION_DIAL,
                        Uri.parse("tel:" + teacherContactList.get(a).getStaffPhone()));
                context.startActivity(i);
            }
        });

        teacherContactViewHolder.smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + teacherContactList.get(a).getStaffPhone()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherContactList.size();
    }

    class TeacherContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name,teacherCounter;
        private ImageView callButton,smsButton;
        private CardView cardview;
        private LinearLayout counterBackground;
        OnTeacherContactClickListener onTeacherContactClickListener;

        public TeacherContactViewHolder(@NonNull View itemView, OnTeacherContactClickListener onTeacherContactClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name_contact_item);
            callButton = itemView.findViewById(R.id.call_contact_item);
            smsButton = itemView.findViewById(R.id.sms_contact_item);
            teacherCounter = itemView.findViewById(R.id.teacher_counter);
            cardview = itemView.findViewById(R.id.cardview_student_contact);
            counterBackground = itemView.findViewById(R.id.contact_counter_cnt);
            this.onTeacherContactClickListener = onTeacherContactClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTeacherContactClickListener.onTeacherContactClick(getAdapterPosition());
        }
    }
    public interface OnTeacherContactClickListener {
        void onTeacherContactClick(int position);
    }

    public void selectAll(){
        staffPhones.clear();
        staffEmails.clear();
        for(int a=0;a<teacherContactList.size();a++){
            TeachersTable tch = teacherContactList.get(a);
            tch.setSelected(true);
            String phoneNo = tch.getStaffPhone();
            staffPhones.add(phoneNo);
            staffPhones.indexOf(phoneNo);
            String emails = tch.getStaffEmail();
            staffEmails.add(emails);
            selectedCounter=+1;
        }

    }
}
