package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.StudentTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentContactAdapter extends RecyclerView.Adapter<StudentContactAdapter.ContactViewHolder> {
    private Context context;
    private List<StudentTable> studentContactList;
    OnStudentContactClickListener onStudentContactClickListener;
    public static List<String> guardianPhones = new ArrayList<>();
    public static List<String> guardianEmails = new ArrayList<>();
    public static boolean flagValue = false;
    public static int selectedCounter = 0;

    public StudentContactAdapter(Context context, List<StudentTable> studentContactList,
                                 OnStudentContactClickListener onStudentContactClickListener) {
        this.context = context;
        this.studentContactList = studentContactList;
        this.onStudentContactClickListener = onStudentContactClickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_item, viewGroup, false);
        return new ContactViewHolder(view, onStudentContactClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactViewHolder contactViewHolder, int i) {
        final int a = i;
        final StudentTable st = studentContactList.get(i);
        contactViewHolder.itemCounter.setText(String.valueOf(i + 1));
        String studentName =
                st.getStudentSurname() + " " + st.getStudentMiddlename() + " " + st.getStudentFirstname();
        GradientDrawable gd =
                (GradientDrawable) contactViewHolder.counterBackground.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        contactViewHolder.counterBackground.setBackground(gd);


        contactViewHolder.name.setText(studentName.toUpperCase());
        if (flagValue) {
            selectAll();
            contactViewHolder.cardView.setBackgroundColor(
                    st.isSelected() ? Color.GRAY : Color.WHITE);

        } else {
            st.setSelected(false);
            flagValue = false;
        }

        contactViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                st.setSelected(!st.isSelected());
                contactViewHolder.cardView.setBackgroundColor(
                        st.isSelected() ? Color.parseColor("#ECECEC") : Color.WHITE);
                if (st.isSelected()) {
                    String phoneNo = st.getGuardianPhoneNo();
                    guardianPhones.add(phoneNo);
                    guardianPhones.indexOf(phoneNo);
                    String emails = st.getGuardianEmail();
                    guardianEmails.add(emails);

                    selectedCounter = +1;

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportActionBar().setTitle("");
                    activity.invalidateOptionsMenu();


                } else {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.invalidateOptionsMenu();
                    String ph = st.getGuardianPhoneNo();
                    // insert code here
                    int index = -1;
                    for (int i = 0; i < guardianPhones.size(); i++) {
                        if (guardianPhones.get(i).equals(ph)) {
                            index = i;
                            break;
                        }
                    }
                    guardianPhones.remove(index);
                    guardianEmails.remove(index);

                    selectedCounter = -1;
                    if (guardianPhones.size() == 0) {
                        flagValue = false;
                    }

                }

                return true;
            }
        });
        if (studentContactList.get(i).getGuardianPhoneNo().equals("")) {
            contactViewHolder.callButton.setVisibility(View.INVISIBLE);
            contactViewHolder.smsButton.setVisibility(View.INVISIBLE);
        } else {
            contactViewHolder.callButton.setVisibility(View.VISIBLE);
            contactViewHolder.smsButton.setVisibility(View.VISIBLE);
        }
        contactViewHolder.callButton.setOnClickListener(view -> {
            Intent i1 = new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + studentContactList.get(a).getGuardianPhoneNo()));
            context.startActivity(i1);
        });


        contactViewHolder.smsButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("smsto:" + studentContactList.get(a).getGuardianPhoneNo()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studentContactList.size();

    }


    static class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final TextView itemCounter;
        private final ImageView callButton;
        private final ImageView smsButton;
        CardView cardView;
        private final LinearLayout counterBackground;
        OnStudentContactClickListener onStudentContactClickListener;

        public ContactViewHolder(@NonNull View itemView,
                                 OnStudentContactClickListener onStudentContactClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name_contact_item);
            callButton = itemView.findViewById(R.id.call_contact_item);
            smsButton = itemView.findViewById(R.id.sms_contact_item);
            cardView = itemView.findViewById(R.id.cardview_student_contact);
            counterBackground = itemView.findViewById(R.id.contact_counter_cnt);
            itemCounter = itemView.findViewById(R.id.teacher_counter);
            this.onStudentContactClickListener = onStudentContactClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onStudentContactClickListener.onStudentContactClick(getAdapterPosition());
        }


    }

    public interface OnStudentContactClickListener {
        void onStudentContactClick(int position);
    }

    public void selectAll() {
        guardianPhones.clear();
        guardianEmails.clear();
        for (int a = 0; a < studentContactList.size(); a++) {
            StudentTable st = studentContactList.get(a);
            st.setSelected(true);
            String phoneNo = st.getGuardianPhoneNo();
            guardianPhones.add(phoneNo);
            guardianPhones.indexOf(phoneNo);
            String emails = st.getGuardianEmail();
            guardianEmails.add(emails);
        }

    }

    public void clearAll() {
        for (int a = 0; a < studentContactList.size(); a++) {
            StudentTable st = studentContactList.get(a);
            st.setSelected(false);

        }
        guardianPhones.clear();
        guardianEmails.clear();
    }


}
