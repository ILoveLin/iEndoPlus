package com.company.iendo.widget.casemodel;

import android.view.View;

import com.company.iendo.bean.model.Province;


public interface OnItemViewClickListener {
    void setOnViewClickListener(View view, int position, Province.City city);
}
