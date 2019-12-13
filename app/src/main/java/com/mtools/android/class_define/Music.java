package com.mtools.android.class_define;

public class Music {
    private long songId;
    private String songName;
    private String songImgUrl;
    private String artistsName;

    public Music(long songId, String songName, String artistsName, String songImgUrl) {
        this.songId = songId;
        this.songName = songName;
        this.artistsName = artistsName;
        this.songImgUrl = songImgUrl;
    }
    public String getSongName() {
        return songName;
    }
    public String getSongImgUrl() {
        return songImgUrl;
    }
    public long getSongId() {
        return songId;
    }
    public String getArtistsName() {
        return artistsName;
    }
}

