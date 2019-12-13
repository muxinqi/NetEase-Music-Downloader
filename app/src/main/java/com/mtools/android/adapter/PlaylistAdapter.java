package com.mtools.android.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.mtools.android.class_define.Music;

import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Music> {

    private int resourceId;

    protected int getResourceId() {
        return resourceId;
    }

    protected PlaylistAdapter(Context context, int textViewResourceId, List<Music> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
}


