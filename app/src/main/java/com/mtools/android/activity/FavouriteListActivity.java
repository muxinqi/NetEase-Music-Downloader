package com.mtools.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mtools.android.R;
import com.mtools.android.adapter.FavouriteListAdapter;
import com.mtools.android.class_define.FavouriteMusicItem;
import com.mtools.android.db.MyDatabaseHelper;
import com.mtools.android.other.GlideApp;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FavouriteListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView favouriteListListView;

    private SQLiteDatabase db;

    private MyDatabaseHelper favouriteListInfoDBHelper;

    private SQLiteDatabase infoDB;

    private TextView listNameTextView;

    private EditText inputListNameEditText;

    private EditText searchContentEditText;

    private ImageButton editListNameButton;

    private ImageButton doneListNameButton;

    private List<FavouriteMusicItem> favouriteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        // 激活 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 组件初始化
        listNameTextView = findViewById(R.id.favourite_list_name_text);
        inputListNameEditText = findViewById(R.id.favourite_list_name_edit);
        editListNameButton = findViewById(R.id.edit_favourite_list_name);
        doneListNameButton = findViewById(R.id.done_favourite_list_name);
        favouriteListListView = findViewById(R.id.favourite_listview);
        searchContentEditText = findViewById(R.id.search_content_edit_ext);
        ImageButton searchImgBtn = findViewById(R.id.search_list_item_img_btn);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "FavouriteList.db", null, 1);
        db = dbHelper.getWritableDatabase();
        favouriteListInfoDBHelper = new MyDatabaseHelper(this, "FavouriteListInfo.db", null, 1);

        // 按钮点击监听
        editListNameButton.setOnClickListener(this);
        doneListNameButton.setOnClickListener(this);
        searchImgBtn.setOnClickListener(this);

        // 按键长按删除
        favouriteListListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FavouriteMusicItem listItem = favouriteList.get(position);
                final String musicId = listItem.getMusicId();
                String musicName = listItem.getSongName();
                final boolean[] isDelete = {false};
                final ContentValues values = new ContentValues();
                values.put("isShow", 0);
                db.update("FavouriteList", values, "musicId = ?", new String[] { musicId });
                values.clear();
                favouriteList.clear();
                initFavouriteItem();
                showFavouriteItem();
                Snackbar.make(findViewById(R.id.favourite_layout), "已删除 "+musicName, Snackbar.LENGTH_LONG)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                values.put("isShow", 1);
                                db.update("FavouriteList", values, "musicId = ?", new String[] { musicId });
                                favouriteList.clear();
                                initFavouriteItem();
                                showFavouriteItem();
                                Toast.makeText(FavouriteListActivity.this, "已撤销删除", Toast.LENGTH_SHORT).show();
                                isDelete[0] = true;
                            }
                        })
                        .show();
                if (isDelete[0]) {
                    db.delete("FavouriteList", "musicId = ?", new String[] { musicId });
                }
                return false;
            }
        });

        db.delete("FavouriteList", "isShow = ?", new String[] { "0" });

        initListInfoDB();
        showListInfoDB();

        // 显示喜欢的歌单
        initFavouriteItem();
        showFavouriteItem();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_favourite_list_name: {
                Log.d("GOOD", "onClick: edit");
                listNameTextView.setVisibility(View.GONE);
                editListNameButton.setVisibility(View.GONE);
                inputListNameEditText.setVisibility(View.VISIBLE);
                doneListNameButton.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.done_favourite_list_name: {
                Log.d("GOOD", "onClick: done");
                String nowListName = listNameTextView.getText().toString();
                String newListName = inputListNameEditText.getText().toString();
                ContentValues values = new ContentValues();
                if (!newListName.equals("")) {
                    values.put("listName", newListName);
                    infoDB.update("FavouriteListInfo", values, "listName = ?", new String[] { nowListName });
                    listNameTextView.setText(newListName);
                }
                listNameTextView.setVisibility(View.VISIBLE);
                editListNameButton.setVisibility(View.VISIBLE);
                inputListNameEditText.setVisibility(View.GONE);
                doneListNameButton.setVisibility(View.GONE);
                break;
            }
            case R.id.search_list_item_img_btn: {
                String searchContent = searchContentEditText.getText().toString();
                // 如果搜索内容不为空 进入搜索
                if (!searchContent.equals("")) {
                    // 高级for循环中只适用于单次(删除/增加)元素并(break/return)结束操作
//                    int count = 0;
//                    for (FavouriteMusicItem item : favouriteList) {
//                        int searchNameResult = item.getSongName().indexOf(searchContent);
//                        int searchArtistResult = item.getArtistName().indexOf(searchContent);
//                        if ((searchNameResult != -1) || (searchArtistResult != -1)) {
//                            count++;
//                        } else {
//                            favouriteList.remove(item);
//                        }
//                    }
                    // 遍历并删除不含有搜索内容的元素
                    // 留下的就是含有搜索内容的列表
                    int count = 0;
                    Iterator<FavouriteMusicItem> iterator = favouriteList.iterator();
                    while (iterator.hasNext()) {
                        FavouriteMusicItem item = iterator.next();
                        int searchNameResult = item.getSongName().indexOf(searchContent);
                        int searchArtistResult = item.getArtistName().indexOf(searchContent);
                        if ((searchNameResult != -1) || (searchArtistResult != -1)) {
                            count++;
                        } else {
                            iterator.remove();
                        }
                    }
                    // 若不含有相应的搜索结果 弹出提示
                    if (count == 0) {
                        Toast.makeText(FavouriteListActivity.this, "无相应结果", Toast.LENGTH_SHORT).show();
                    } else {
                        // 含有搜索结果 则显示
                        showFavouriteItem();
                    }
                }
                else {
                    // 否则搜索内容为空 刷新列表
                    // 适用于 第一次搜索完毕后 刷新全部列表用
                    initFavouriteItem();
                    showFavouriteItem();
                }
                break;
            }
            default:
                break;
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

    /**
     * 初始化 Favourite List 数据库
     * */
    private void initListInfoDB() {
        infoDB = favouriteListInfoDBHelper.getWritableDatabase();

        //查询 List Info 表中数据
        Cursor infoCursor = infoDB.query("FavouriteListInfo", null, null, null, null, null, null);
        int count = 0;
        if (infoCursor.moveToNext()) {
            do {
                count++;
            } while (infoCursor.moveToNext());
        }
        infoCursor.close();
        Log.d("GOOD", "onCreate: count = "+count);

        // 如果表单为空（即第一次创建 则插入默认数据）
        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put("listName", "喜欢的歌单");
            infoDB.insert("FavouriteListInfo", null, values);
        }
    }

    /**
     * 显示喜欢的歌单的信息（目前只有名称）
     * */
    private void showListInfoDB() {
        Cursor infoCursor = infoDB.query("FavouriteListInfo", null, null, null, null, null, null);
        int count = 0;
        String listName = null;
        if (infoCursor.moveToNext()) {
            do {
                listName = infoCursor.getString(infoCursor.getColumnIndex("listName"));
                count++;
            } while (count == 0);
        }
        infoCursor.close();
        listNameTextView.setText(listName);
    }

    /**
     * 初始化喜欢的歌单项目
     * 遍历数据库信息添加到List中
     * */
    private void initFavouriteItem() {
        // 查询 Favourite List 表中所有的数据
        Cursor cursor = db.query("FavouriteList", null, "isShow = 1", null, null, null, null);
        favouriteList.clear();
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
    }

    /**
     * 显示喜欢的歌单列表
     * */
    private void showFavouriteItem() {
        favouriteListListView.setAdapter(new FavouriteListAdapter(FavouriteListActivity.this, R.layout.playlist_item, favouriteList) {
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
