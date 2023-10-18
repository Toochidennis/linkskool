package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseRegAdapter extends RecyclerView.Adapter<CourseRegAdapter.CourseRegVH> {
    private Context context;
    private List<StudentTable> students;
    private OnStudentSelectListener onStudentSelectListener;
    private static GradientDrawable gd;

    public CourseRegAdapter(Context context, List<StudentTable> students,OnStudentSelectListener onStudentSelectListener) {
        this.context = context;
        this.students = students;
        this.onStudentSelectListener = onStudentSelectListener;
    }

    @NonNull
    @Override
    public CourseRegVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_reg_layout_item,parent,false);
        return new CourseRegVH(view,onStudentSelectListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseRegVH holder, int position) {
        StudentTable st = students.get(position);
        holder.container.setVisibility(View.VISIBLE);
        String surname = st.getStudentSurname();
        String middle = st.getStudentMiddlename();
        String firstName = st.getStudentFirstname();
        try{
            if(surname!=null) {
                surname = surname.substring(0, 1).toUpperCase() + "" + surname.substring(1).toLowerCase();
            }else {
                surname = "";
            }
            if(middle!=null) {
                middle = middle.substring(0, 1).toUpperCase() + "" + middle.substring(1).toLowerCase();
            }else {
                middle="";
            }
            if(firstName!=null) {
                firstName = firstName.substring(0, 1).toUpperCase() + "" + firstName.substring(1).toLowerCase();
            }else {
                firstName = "";
            }

        }catch (StringIndexOutOfBoundsException | NullPointerException e){
            e.printStackTrace();
        }
        holder.name.setText(surname+" "+firstName+" "+middle);
         gd = (GradientDrawable) holder.imgLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(st.getColor());
        holder.imgLayout.setBackground(gd);
        if(st.isSelected()){
            holder.img.setImageResource(R.drawable.good);
        }else {
            holder.img.setImageResource(R.drawable.ic_check_box);
        }try {
            int cnt = Integer.parseInt(st.getCourseCount());
            if (cnt < 2) {
                holder.courseTxt.setText("course");
            } else {
                holder.courseTxt.setText("courses");
            }
            holder.count.setText(st.getCourseCount());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        if( st.isShowText()){
            holder.img.setVisibility(View.GONE);
            holder.initial.setVisibility(View.VISIBLE);
            try {
                holder.initial.setText(surname.substring(0, 1).toUpperCase());
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class CourseRegVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private LinearLayout imgLayout;
        private TextView name;
        private ImageView img;
        OnStudentSelectListener onStudentSelectListener;
        private LinearLayout container;
        TextView count,initial,courseTxt;

        public CourseRegVH(@NonNull View itemView,OnStudentSelectListener onStudentSelectListener) {
            super(itemView);
            imgLayout = itemView.findViewById(R.id.check_layout);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.check);
            this.onStudentSelectListener = onStudentSelectListener;
            container = itemView.findViewById(R.id.container);
            count = itemView.findViewById(R.id.no_of_courses);
            initial = itemView.findViewById(R.id.initial);
            courseTxt = itemView.findViewById(R.id.course_txt);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onStudentSelectListener.onStudentSelect(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onStudentSelectListener.onStudentLongClick(getAdapterPosition());
            //Toast.makeText(context,"long click",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public interface OnStudentSelectListener{
        void onStudentSelect(int position);
        void onStudentLongClick(int position);
    }
}
