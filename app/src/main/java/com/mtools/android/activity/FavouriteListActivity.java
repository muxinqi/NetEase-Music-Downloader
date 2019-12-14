package com.mtools.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mtools.android.R;
import com.mtools.android.adapter.FavouriteListAdapter;
import com.mtools.android.class_define.FavouriteMusicItem;
import com.mtools.android.db.MyDatabaseHelper;
import com.mtools.android.other.GlideApp;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FavouriteListActivity extends AppCompatActivity {

    private ListView favouritelistListView;

    private List<FavouriteMusicItem> favouriteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        // 激活 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 组件初始化
        favouritelistListView = findViewById(R.id.favourite_listview);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "FavouriteList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 查询表中所有的数据
        Cursor cursor = db.query("FavouriteList", null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            do {
                // 遍历Cursor对象 取出数据并显示
                String musicId = cursor.getString(cursor.getColumnIndex("musicId"));
                String songName = cursor.getString(cursor.getColumnIndex("songName"));
                String artistName = cursor.getString(cursor.getColumnIndex("artistName"));
                String songCoverUrl = cursor.getString(cursor.getColumnIndex("songCoverUrl"));
                FavouriteMusicItem item = new FavouriteMusicItem(musicId, songName, artistName, songCoverUrl);
                favouriteList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        showFavouriteItem();
    }

    // Toolbar的事件---返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFavouriteItem() {
        favouritelistListView.setAdapter(new FavouriteListAdapter(FavouriteListActivity.this, R.layout.playlist_item, favouriteList) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                FavouriteMusicItem favouriteMusicItem = getItem(position);
                View view;
                if (convertView == null) {
                    // convertView 用于缓存之前加载好的布局
                    // 若其为空则需要加载
                    view = LayoutInflater.from(getContext()).inflate(getResourceId(), parent, false);
                } else {
                    // 若其不为空则直接调用它
                    view = convertView;
                }
                ImageView songImage = view.findViewById(R.id.playlist_item_song_image);
                TextView songName = view.findViewById(R.id.playlist_item_song_name_text);
                TextView artistName = view.findViewById(R.id.playlist_item_singer_name_text);
                // Glide
                GlideApp.with(getContext())
                        .load(Objects.requireNonNull(favouriteMusicItem).getSongCoverUrl())
                        .transform(new RoundedCornersTransformation(10, 0, RoundedCornersTransformation.CornerType.ALL))
                        .into(songImage);
                songName.setText(favouriteMusicItem.getSongName());
                artistName.setText(favouriteMusicItem.getArtistName());
                return view;
            }
        });
    }

}
