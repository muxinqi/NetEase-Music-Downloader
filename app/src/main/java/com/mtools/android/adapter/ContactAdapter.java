package com.mtools.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mtools.android.R;
import com.mtools.android.class_define.Contact;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private int resourceId;

    public ContactAdapter(Context context, int textViewResourceId, List<Contact> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        Contact contact = getItem(position);
        View view;
        if (convertView == null) {
            // convertView 用于缓存之前加载好的布局
            // 若其为空则需要加载
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        } else {
            // 若其不为空则直接调用它
            view = convertView;
        }
        TextView contactFullName = view.findViewById(R.id.contact_full_name_text);
        TextView contactPhoneNumber = view.findViewById(R.id.contact_phone_number_text);
        contactFullName.setText(Objects.requireNonNull(contact).getFullName());
        contactPhoneNumber.setText(contact.getPhoneNumber());
        return view;
    }
}
