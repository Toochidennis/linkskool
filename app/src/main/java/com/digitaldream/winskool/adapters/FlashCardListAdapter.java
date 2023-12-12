package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.models.FlashCardModel;
import com.digitaldream.winskool.R;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FlashCardListAdapter extends RecyclerView.Adapter<FlashCardListAdapter.FlashCardVH> {
    private Context context;
    private List<FlashCardModel> list;
    OnFlashCardClickListener onFlashCardClickListener;

    public FlashCardListAdapter(Context context, List<FlashCardModel> list,OnFlashCardClickListener onFlashCardClickListener) {
        this.context = context;
        this.list = list;
        this.onFlashCardClickListener = onFlashCardClickListener;
    }

    @NonNull
    @Override
    public FlashCardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.flashcard_layout_item,parent,false);
        return new FlashCardVH(view,onFlashCardClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashCardVH holder, int position) {
        FlashCardModel fm = list.get(position);
        String title = fm.getTerm();
        holder.initials.setText(title.substring(0,1).toUpperCase());
        try {
            title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        holder.cardName.setText(title);
        GradientDrawable gd = (GradientDrawable) holder.bg.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        holder.bg.setBackground(gd);
        holder.date.setText(fm.getDate());
        String cardCount = fm.getCount();
        if(Integer.parseInt(cardCount)>1) {
            holder.cardNo.setText(fm.getCount() + " cards");
        }else {
            holder.cardNo.setText(fm.getCount() + " card");

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FlashCardVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cardName,initials,date,cardNo;
        LinearLayout bg;
        OnFlashCardClickListener onFlashCardClickListener;
        public FlashCardVH(@NonNull View itemView,OnFlashCardClickListener onFlashCardClickListener) {
            super(itemView);
            cardName = itemView.findViewById(R.id.card_name);
            initials = itemView.findViewById(R.id.initial);
            date = itemView.findViewById(R.id.date_layout);
            cardNo = itemView.findViewById(R.id.card_no);
            bg = itemView.findViewById(R.id.initials_bg);
            this.onFlashCardClickListener =onFlashCardClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFlashCardClickListener.onCardClick(getAdapterPosition());
        }
    }

    public interface OnFlashCardClickListener{
        void onCardClick(int position);
    }
}
