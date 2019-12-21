package com.mtools.android.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.mtools.android.class_define.CommentItem;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentItem> {
    private int resourceId;

    protected int getResourceId() {
        return resourceId;
    }

    protected CommentAdapter(Context context, int textViewResourceId, List<CommentItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
}
