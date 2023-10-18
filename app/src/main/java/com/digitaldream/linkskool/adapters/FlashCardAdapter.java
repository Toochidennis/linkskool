package com.digitaldream.linkskool.adapters;

import com.digitaldream.linkskool.fragments.FlashcardFragment;
import com.digitaldream.linkskool.models.FlashCardModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FlashCardAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public FlashCardAdapter(@NonNull FragmentManager fm, List<FlashCardModel> list) {
        super(fm);
        this.fragments = new ArrayList<Fragment>();
        for (int i = 0; i < list.size(); i++) {
            FlashCardModel fl = list.get(i);
            String term = fl.getTerm();
            try {

                term = term.substring(0, 1).toUpperCase() + term.substring(1).toLowerCase();
                String definition = fl.getDefinition();
                definition = definition.substring(0,1).toUpperCase()+definition.substring(1).toLowerCase();
                fragments.add(FlashcardFragment.newInstance(term, definition));
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
