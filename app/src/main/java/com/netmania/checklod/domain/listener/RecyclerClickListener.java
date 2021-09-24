package com.netmania.checklod.domain.listener;

import android.view.View;

public interface RecyclerClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
