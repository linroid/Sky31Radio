package com.linroid.radio.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by linroid on 1/22/15.
 */
public class SearchProgramListFragment extends ProgramListFragment {
    public static final String KEY_KEYWORD = "keyword";

    public static SearchProgramListFragment newInstance() {
        SearchProgramListFragment fragment = new SearchProgramListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public void setKeyword(CharSequence keyword){
        conditions.put(KEY_KEYWORD, keyword.toString());
        loadData(page);
    }
    @Override
    public void loadData(int page) {
        if(!conditions.containsKey(KEY_KEYWORD) || TextUtils.isEmpty(conditions.get(KEY_KEYWORD))){
            return;
        }
        super.loadData(page);
    }
}
