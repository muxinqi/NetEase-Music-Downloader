package com.mtools.android.class_define.MusicDownload;

public class Data {

    private long id;
    private String url;
    private long br;
    private long size;
    private String md5;
    private int code;
    private int expi;
    private String type;
    private String level;
    private String encodeType;
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setBr(long br) {
        this.br = br;
    }
    public long getBr() {
        return br;
    }

    public void setSize(long size) {
        this.size = size;
    }
    public long getSize() {
        return size;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public String getMd5() {
        return md5;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setExpi(int expi) {
        this.expi = expi;
    }
    public int getExpi() {
        return expi;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public String getLevel() {
        return level;
    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }
    public String getEncodeType() {
        return encodeType;
    }

}