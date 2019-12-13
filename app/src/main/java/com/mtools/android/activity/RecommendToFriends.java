package com.mtools.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mtools.android.class_define.Contact;
import com.mtools.android.adapter.ContactAdapter;
import com.mtools.android.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecommendToFriends extends AppCompatActivity {

    private ContactAdapter adapter;

    private List<Contact> contactsList = new ArrayList<>();

    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_to_friends);

        // 组件初始化
        ListView contactsView = findViewById(R.id.contact_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // 激活 Toolbar
        setSupportActionBar(toolbar);

        adapter = new ContactAdapter(this, R.layout.contact_item, contactsList);
        contactsView.setAdapter(adapter);

        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            readContacts();
        }

        // 联系人列表点击操作
        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactsList.get(position);
                // 直接获取 getContactNumber 中间含有空格
                phoneNumber = "tel:" + contact.getPhoneNumber().replace(" ", "");
                if (ContextCompat.checkSelfPermission(RecommendToFriends.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RecommendToFriends.this, new String[] { Manifest.permission.CALL_PHONE}, 1);
                } else {
                    call();
                }
            }
        });
    }

    private void call() {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Toast.makeText(RecommendToFriends.this, phoneNumber, Toast.LENGTH_SHORT).show();
            intent.setData(Uri.parse(phoneNumber));
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Toolbar的事件---返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void readContacts() {
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
            // 查询联系人数据
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contact contact = new Contact(displayName, number);
                    contactsList.add(contact);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int [] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
                call();
            } else {
                Toast.makeText(this, "您拒绝了权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
