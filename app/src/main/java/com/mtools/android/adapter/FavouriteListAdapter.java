package com.mtools.android.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.mtools.android.class_define.FavouriteMusicItem;

import java.util.List;

public class FavouriteListAdapter extends ArrayAdapter<FavouriteMusicItem> {

    private int resourceId;

    protected int getResourceId() {
        return resourceId;
    }

    protected FavouriteListAdapter(Context context, int textViewResourceId, List<FavouriteMusicItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
}
