package com.zaptrapp.estimator2.Models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Nishanth on 02-Nov-17.
 */

public class ProductHolder extends RecyclerView.ViewHolder{
    public final TextView product;
    public ProductHolder(View itemView) {
        super(itemView);
        product = itemView.findViewById(android.R.id.text1);
    }

}
