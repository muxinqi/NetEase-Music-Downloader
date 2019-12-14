package com.mtools.android.class_define;

public class FavouriteMusicItem {
    private String musicId;
    private String songName;
    private String artistName;
    private String songCoverUrl;

    public FavouriteMusicItem(String musicId, String songName, String artistName, String songCoverUrl) {
        this.musicId = musicId;
        this.songName = songName;
        this.artistName = artistName;
        this.songCoverUrl = songCoverUrl;
    }

    public String getMusicId() {
        return musicId;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongCoverUrl() {
        return songCoverUrl;
    }
}
