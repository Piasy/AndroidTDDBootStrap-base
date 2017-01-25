package com.github.piasy.bootstrap.base.ux;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import butterknife.ButterKnife;

/**
 * Created by Piasy{github.com/Piasy} on 23/01/2017.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(final ViewGroup parent, final @LayoutRes int layout) {
        super(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
        ButterKnife.bind(this, itemView);
    }
}
