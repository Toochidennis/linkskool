package com.digitaldream.winskool.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.winskool.activities.TestUpload;
import com.digitaldream.winskool.models.QuestionsModel;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.utils.ItemMoveCallBack;
import com.digitaldream.winskool.dialog.QuestionDialog2;

import java.util.Collections;
import java.util.List;

public class QuestionAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemMoveCallBack.ItemTouchHelperContract {
    private Context context;
    public static List<QuestionsModel> list;
    public static int questionNumber=1;
    private  StartDragListener mStartDragListener;


    public QuestionAdapter2(Context context, List<QuestionsModel> list,StartDragListener startDragListener) {
        this.context = context;
        this.list = list;
        this.mStartDragListener = startDragListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType){
            case 0:
                view = inflater.inflate(R.layout.short_answer_layout,parent,false);
                return new ShortAnswerVH(view);
            case 1:
                 view = inflater.inflate(R.layout.long_answer_layout,parent,false);
                return new LongAnswerVH(view);
            case 2:
                view = inflater.inflate(R.layout.email_answer_layout,parent,false);
                return new EmailVH(view);
            case 3:
                view = inflater.inflate(R.layout.number_answer_layout,parent,false);
                return new NumberVH(view);
            case 4:
                view = inflater.inflate(R.layout.multiple_choice_layout,parent,false);
                return new MultipleVH(view);
            case 5:
                view = inflater.inflate(R.layout.checkbox_answer_layout,parent,false);
                return new CheckBoxVH(view);
            case 6:
                view = inflater.inflate(R.layout.dropdown_answer_layout,parent,false);
                return new DropDownVH(view);
            case 7:
                view = inflater.inflate(R.layout.date_answer_layout,parent,false);
                return new DateVH(view);
            case 8:
                view = inflater.inflate(R.layout.time_answer_layout,parent,false);
                return new TimeVH(view);
            case 9:
                view = inflater.inflate(R.layout.section_answer_layout,parent,false);
                return new SectionVH(view);
            case 10:
                view = inflater.inflate(R.layout.flashcard_item_layout,parent,false);
                return new FlashCardVH(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuestionsModel qm = list.get(position);

            questionNumber =position+1;

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mStartDragListener.requestDrag(holder);
                return true;
            }
        });

        switch (holder.getItemViewType()){
            case 0:
                ShortAnswerVH shortAnswerVH = (ShortAnswerVH) holder;
                shortAnswerVH.question.setText(questionNumber+". "+qm.getQuestion());
                qm.setQuestionNumber(String.valueOf(questionNumber));
                shortAnswerVH.edit.setTag(questionNumber);
                shortAnswerVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(context,v.getTag().toString(),Toast.LENGTH_SHORT).show();
                        showDialog(qm,position, (Integer) v.getTag());
                    }
                });

                if(!qm.getQuestion().isEmpty()){
                    shortAnswerVH.rTitle.setVisibility(View.GONE);
                }else{
                    shortAnswerVH.rTitle.setVisibility(View.VISIBLE);
                }
                shortAnswerVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                shortAnswerVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });
                break;
            case 1:
                LongAnswerVH longAnswerVH = (LongAnswerVH) holder;
                longAnswerVH.question.setText(questionNumber+". "+qm.getQuestion());
                longAnswerVH.edit.setTag(questionNumber);
                longAnswerVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position, (Integer) v.getTag());
                    }
                });
                if(!qm.getQuestion().isEmpty()){
                    longAnswerVH.rTitle.setVisibility(View.GONE);
                }else{
                    longAnswerVH.rTitle.setVisibility(View.VISIBLE);

                }
                longAnswerVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                qm.setQuestionNumber(String.valueOf(questionNumber));
                longAnswerVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });

                break;
            case 2:
                EmailVH emailVH = (EmailVH) holder;
                emailVH.question.setText(questionNumber+". "+qm.getQuestion());
                emailVH.edit.setTag(questionNumber);
                emailVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,(Integer) v.getTag());
                    }
                });
                if(!qm.getQuestion().isEmpty()){
                    emailVH.rTitle.setVisibility(View.GONE);
                }else{
                    emailVH.rTitle.setVisibility(View.VISIBLE);

                }
                emailVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                qm.setQuestionNumber(String.valueOf(questionNumber));
                emailVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });

                break;
            case 3:
                NumberVH numberVH = (NumberVH) holder;
                numberVH.question.setText(questionNumber+". "+qm.getQuestion());
                numberVH.edit.setTag(questionNumber);
                numberVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,(Integer) v.getTag());
                    }
                });
                if(!qm.getQuestion().isEmpty()){
                    numberVH.rTitle.setVisibility(View.GONE);
                }else{
                    numberVH.rTitle.setVisibility(View.VISIBLE);

                }

                numberVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                qm.setQuestionNumber(String.valueOf(questionNumber));
                numberVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });

                break;
            case 4:
                MultipleVH multipleVH = (MultipleVH) holder;
                multipleVH.question.setText(questionNumber+". "+qm.getQuestion());
                multipleVH.edit.setTag(questionNumber);
                multipleVH.edit.setOnClickListener(v -> showDialog(qm,position,(Integer) v.getTag()));
                if(!qm.getQuestion().isEmpty()){
                    multipleVH.rTitle.setVisibility(View.GONE);
                }else{
                    multipleVH.rTitle.setVisibility(View.VISIBLE);

                }
                if(qm.getOptions().size()>0){
                    multipleVH.showOptionBtn.setText("Options("+qm.getOptions().size()+")");
                }else{
                    multipleVH.showOptionBtn.setText("Add Options");
                }
                multipleVH.delete.setOnClickListener(v -> deleteRow(position));
                qm.setQuestionNumber(String.valueOf(questionNumber));
                multipleVH.copy.setOnClickListener(v -> copyRow(position));

                break;
            case 5:
                CheckBoxVH checkBoxVH = (CheckBoxVH) holder;
                checkBoxVH.question.setText(questionNumber+". "+qm.getQuestion());
                checkBoxVH.edit.setTag(questionNumber);
                checkBoxVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,(Integer) v.getTag());
                    }
                });
                if(!qm.getQuestion().isEmpty()){
                    checkBoxVH.rTitle.setVisibility(View.GONE);
                }else{
                    checkBoxVH.rTitle.setVisibility(View.VISIBLE);

                }
                if(qm.getOptions().size()>0){
                    checkBoxVH.showOptionBtn.setText("Show Options("+qm.getOptions().size()+")");
                }else{
                   checkBoxVH.showOptionBtn.setText("Add Options");
                }
                checkBoxVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                qm.setQuestionNumber(String.valueOf(questionNumber));
                checkBoxVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });

                break;
            case 6:
                DropDownVH dropDownVH = (DropDownVH) holder;
                dropDownVH.question.setText(questionNumber+". "+qm.getQuestion());
                dropDownVH.edit.setTag(questionNumber);
                dropDownVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,(Integer) v.getTag());
                    }
                });
                if(!qm.getQuestion().isEmpty()){
                    dropDownVH.rTitle.setVisibility(View.GONE);
                }else{
                    dropDownVH.rTitle.setVisibility(View.VISIBLE);

                }

                if(qm.getOptions().size()>0){
                    dropDownVH.showOptionBtn.setText("Show Options("+qm.getOptions().size()+")");
                }else{
                    dropDownVH.showOptionBtn.setText("Add Options");
                }
                dropDownVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });

                qm.setQuestionNumber(String.valueOf(questionNumber));
                dropDownVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });
                break;
            case 7:
                DateVH dateVH = (DateVH) holder;
                dateVH.question.setText(questionNumber+"."+qm.getQuestion());
                dateVH.edit.setTag(questionNumber);
                dateVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,(Integer) v.getTag());
                    }
                });
                if(!qm.getQuestion().isEmpty()){
                    dateVH.rTitle.setVisibility(View.GONE);
                }else{
                    dateVH.rTitle.setVisibility(View.VISIBLE);

                }
                dateVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                qm.setQuestionNumber(String.valueOf(questionNumber));
                dateVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });
                break;
            case 8:
                TimeVH timeVH = (TimeVH) holder;
                timeVH.question.setText(questionNumber+"."+qm.getQuestion());
                timeVH.edit.setTag(questionNumber);
                timeVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,(Integer) v.getTag());
                    }
                });
                if(!qm.getQuestion().isEmpty()){
                    timeVH.rTitle.setVisibility(View.GONE);
                }else{
                    timeVH.rTitle.setVisibility(View.VISIBLE);

                }
                timeVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                qm.setQuestionNumber(String.valueOf(questionNumber));
                timeVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });
                break;
            case 9:
                SectionVH sectionVH = (SectionVH) holder;
                sectionVH.sectionTitle.setText(qm.getSectionTitle());
                if(qm.getSectionTitle().isEmpty()){
                    sectionVH.errorTitle.setVisibility(View.VISIBLE);
                }else{
                    sectionVH.errorTitle.setVisibility(View.GONE);
                }
                sectionVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,0);
                    }
                });
                sectionVH.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });
                sectionVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                break;
            case 10:
                FlashCardVH flashCardVH = (FlashCardVH) holder;
                flashCardVH.flashcardTerm.setText(qm.getQuestion());
                flashCardVH.flashcardDesc.setText(qm.getAnswer());
                flashCardVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(qm,position,0);
                    }
                });
                flashCardVH.copyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copyRow(position);
                    }
                });

                flashCardVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(position);
                    }
                });
                break;
        }
        if(!qm.getQuestionType().equals("section")) {
            questionNumber++;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        QuestionsModel qm = list.get(position);
        switch (qm.getQuestionType()){
            case "short_answer":
                return 0;
            case "long_answer":
                return 1;
            case "email":
                return 2;
            case "number":
                return 3;
            case "multiple_choice":
                return 4;
            case "check_box":
                return 5;
            case "drop_down":
                return 6;
            case "date":
                return 7;
            case "time":
                return 8;
            case "section":
                return 9;
            case "flash_card":
                return 10;

            default:
                return position;

        }
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        this.notifyDataSetChanged();
    }

    @Override
    public void onRowSelected(RecyclerView.ViewHolder v) {

    }

    @Override
    public void onRowClear(RecyclerView.ViewHolder v) {

    }


    public class ShortAnswerVH extends RecyclerView.ViewHolder  {
        TextView question,rTitle;
        ImageView copy;
        ImageView delete,edit;
        CardView rootView;

        public ShortAnswerVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            rootView = itemView.findViewById(R.id.root);
        }


    }

    public class LongAnswerVH extends RecyclerView.ViewHolder{
        TextView question,rTitle;
        ImageView copy,delete,edit;
        public LongAnswerVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);

        }
    }

    public class EmailVH extends RecyclerView.ViewHolder{
        TextView question,rTitle;
        ImageView copy,delete,edit;
        public EmailVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);


        }
    }

    public class NumberVH extends RecyclerView.ViewHolder{
        TextView question,rTitle;
        ImageView copy,delete,edit;
        public NumberVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    public class MultipleVH extends RecyclerView.ViewHolder{
        TextView question,rTitle,showOptionBtn;
        ImageView copy,delete,edit;
        public MultipleVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            showOptionBtn = itemView.findViewById(R.id.addOptionsBtn);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    public class CheckBoxVH extends RecyclerView.ViewHolder{
        TextView question,rTitle,showOptionBtn;
        ImageView copy,delete,edit;
        public CheckBoxVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            showOptionBtn = itemView.findViewById(R.id.addOptionsBtn);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    public class DropDownVH extends RecyclerView.ViewHolder{
        TextView question,rTitle,showOptionBtn;
        ImageView copy,delete,edit;
        public DropDownVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            showOptionBtn = itemView.findViewById(R.id.addOptionsBtn);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    public class DateVH extends RecyclerView.ViewHolder{
        TextView question,rTitle;
        ImageView copy,delete,edit;
        public DateVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    public class TimeVH extends RecyclerView.ViewHolder{
        TextView question,rTitle;
        ImageView copy,delete,edit;
        public TimeVH(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_field);
            rTitle = itemView.findViewById(R.id.required_title);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    public class SectionVH extends RecyclerView.ViewHolder{
        TextView sectionTitle,sectionDesc,errorTitle;
        ImageView copy,delete,edit;

        public SectionVH(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.section_title);
            errorTitle = itemView.findViewById(R.id.err_msg);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }
    public class FlashCardVH extends RecyclerView.ViewHolder{
        TextView flashcardTerm,flashcardDesc;
        ImageView deleteBtn,copyBtn,edit;

        public FlashCardVH(@NonNull View itemView) {
            super(itemView);
            flashcardTerm = itemView.findViewById(R.id.flashcard_name);
            flashcardDesc = itemView.findViewById(R.id.flashcard_desc);
            deleteBtn = itemView.findViewById(R.id.delete);
            copyBtn = itemView.findViewById(R.id.copy);
            edit = itemView.findViewById(R.id.edit);

        }
    }

    private void showDialog(QuestionsModel qm,int position,int questionNo){
        /*QuestionDialog2 dialog2 = new QuestionDialog2((Activity) context,qm,position,questionNumber);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.show();
        Window window = dialog2.getWindow();
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);*/

        FragmentActivity activityF = (FragmentActivity) context;
        FragmentManager manager = activityF.getSupportFragmentManager();
        QuestionDialog2 dialog2 = new QuestionDialog2((Activity) context,qm,
                TestUpload.questionAdapter2.getItemCount()-1, QuestionAdapter2.questionNumber);
        dialog2.setCancelable(false);
        dialog2.show(manager, "tag");
    }

    public void updateQuestion(int position, String question,QuestionsModel qm){
        switch (qm.getQuestionType()){
            case "short_answer":
            case "long_answer":
            case "email":
            case "number":
            case "multiple_choice":
            case "check_box":
            case "drop_down":
            case "date":
            case "time":

            case "flash_card":
                list.get(position).setQuestion(question);
                this.notifyDataSetChanged();
                break;
            case "section":
                break;
        }

    }

    public void updateSection(int position,QuestionsModel qm){
        list.get(position).setSectionTitle(qm.getSectionTitle());
        list.get(position).setSectionDescription(qm.getSectionDescription());
        this.notifyDataSetChanged();
    }

    private void deleteRow(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                list.remove(position);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

    private void copyRow(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Copy ?");
        builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QuestionsModel q = list.get(position);
                QuestionsModel qm = new QuestionsModel();
                qm.setQuestion(q.getQuestion());
                qm.setOptions(q.getOptions());
                qm.setQuestionType(q.getQuestionType());
                qm.setAnswer(q.getAnswer());
                qm.setSectionTitle(q.getSectionTitle());
                list.add(position+1,qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public interface StartDragListener {
        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }
}
