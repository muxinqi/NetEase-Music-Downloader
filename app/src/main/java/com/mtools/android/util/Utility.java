package com.mtools.android.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mtools.android.class_define.single_music.A_Song;
import com.mtools.android.class_define.playlist.imjad.A_Playlist;

import java.io.IOException;

public class Utility {
    /**
     * 使用Gson将传入的JSON数据解析成 A_Song 实体类并返回
     * */
    public static A_Song handleA_SongResponse(String response) {
        try {
//            JSONObject jsonObject = new JSONObject(response);
//            JSONArray jsonArray = jsonObject.getJSONArray("songs");
//            String A_SongContent = jsonArray.getJSONObject(0).toString();
//            return new Gson().fromJson(A_SongContent, A_Song.class);
            return new Gson().fromJson(response, A_Song.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用Gson将传入的Json数据解析成 A_Playlist 实体类并返回
     * */
    public static A_Playlist handleA_PlaylistResponse(String response) {
        try {
            return new Gson().fromJson(response, A_Playlist.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static A_Playlist handleA_PlayistToObject(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        A_Playlist a_playlist = null;
        try {
            a_playlist = objectMapper.readValue(response, A_Playlist.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a_playlist;
    }

    /**
     * 使用Jackson将json反序列化为实体对象
     *     jsonResponse - 传入的json字符串
     *     Class<T> - 返回类的类型
     *     failOnUnknownProperties - 转换时是否不忽略类中未知的属性
     * */
    public static <T> T handleJsonToObject(String jsonResponse, Class<T> theClass, boolean failOnUnknownProperties) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        T result = null;
        try {
            result = objectMapper.readValue(jsonResponse, theClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
