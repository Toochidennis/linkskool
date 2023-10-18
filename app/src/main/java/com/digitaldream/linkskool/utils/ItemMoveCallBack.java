package com.digitaldream.linkskool.utils;

import com.digitaldream.linkskool.adapters.QuestionAdapter2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallBack extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallBack(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }


    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof QuestionAdapter2.ShortAnswerVH) {
                QuestionAdapter2.ShortAnswerVH myViewHolder= (QuestionAdapter2.ShortAnswerVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.LongAnswerVH){
                QuestionAdapter2.LongAnswerVH myViewHolder= (QuestionAdapter2.LongAnswerVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.MultipleVH){
                QuestionAdapter2.MultipleVH myViewHolder= (QuestionAdapter2.MultipleVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.CheckBoxVH){
                QuestionAdapter2.CheckBoxVH myViewHolder= (QuestionAdapter2.CheckBoxVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.DropDownVH){
                QuestionAdapter2.DropDownVH myViewHolder= (QuestionAdapter2.DropDownVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.DateVH){
                QuestionAdapter2.DateVH myViewHolder= (QuestionAdapter2.DateVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.NumberVH){
                QuestionAdapter2.NumberVH myViewHolder= (QuestionAdapter2.NumberVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.SectionVH){
                QuestionAdapter2.SectionVH myViewHolder= (QuestionAdapter2.SectionVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if (viewHolder instanceof QuestionAdapter2.EmailVH){
                QuestionAdapter2.EmailVH myViewHolder= (QuestionAdapter2.EmailVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.TimeVH){
                QuestionAdapter2.TimeVH myViewHolder= (QuestionAdapter2.TimeVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }else if(viewHolder instanceof QuestionAdapter2.FlashCardVH){
                QuestionAdapter2.FlashCardVH myViewHolder= (QuestionAdapter2.FlashCardVH) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof QuestionAdapter2.ShortAnswerVH) {
            QuestionAdapter2.ShortAnswerVH myViewHolder= (QuestionAdapter2.ShortAnswerVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.LongAnswerVH){
            QuestionAdapter2.LongAnswerVH myViewHolder= (QuestionAdapter2.LongAnswerVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.MultipleVH){
            QuestionAdapter2.MultipleVH myViewHolder= (QuestionAdapter2.MultipleVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.CheckBoxVH){
            QuestionAdapter2.CheckBoxVH myViewHolder= (QuestionAdapter2.CheckBoxVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.DropDownVH){
            QuestionAdapter2.DropDownVH myViewHolder= (QuestionAdapter2.DropDownVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.DateVH){
            QuestionAdapter2.DateVH myViewHolder= (QuestionAdapter2.DateVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.NumberVH){
            QuestionAdapter2.NumberVH myViewHolder= (QuestionAdapter2.NumberVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.SectionVH){
            QuestionAdapter2.SectionVH myViewHolder= (QuestionAdapter2.SectionVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if (viewHolder instanceof QuestionAdapter2.EmailVH){
            QuestionAdapter2.EmailVH myViewHolder= (QuestionAdapter2.EmailVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.TimeVH){
            QuestionAdapter2.TimeVH myViewHolder= (QuestionAdapter2.TimeVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }else if(viewHolder instanceof QuestionAdapter2.FlashCardVH){
            QuestionAdapter2.FlashCardVH myViewHolder= (QuestionAdapter2.FlashCardVH) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {
        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(RecyclerView.ViewHolder v);
        void onRowClear(RecyclerView.ViewHolder v);

    }
}
