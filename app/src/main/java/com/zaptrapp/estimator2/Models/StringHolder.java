package com.zaptrapp.estimator2.Models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nishanth on 10/11/17.
 */

public class StringHolder extends RecyclerView.ViewHolder  {
    public final TextView mTextView;
    public StringHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(android.R.id.text1);

    }
}
