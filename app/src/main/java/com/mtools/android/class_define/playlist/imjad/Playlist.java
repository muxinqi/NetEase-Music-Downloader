/**
  * Copyright 2019 bejson.com 
  */
package com.mtools.android.class_define.playlist.imjad;
import java.util.List;

/**
 * Auto-generated: 2019-12-10 15:39:30
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Playlist {

    // -- BEGIN --
    private long id;
    private String name;
    private String description;
    private String commentThreadId;
    private String coverImgUrl;
    private Creator creator;
    private int playCount;
    private int trackCount;
    private long createTime;
    private long trackUpdateTime;
    private int subscribedCount;
    private int shareCount;
    private int commentCount;
    private List<Tracks> tracks;
    // --  END  --



    private String coverImgId_str;
    public void setCreator(Creator creator) {
         this.creator = creator;
     }
     public Creator getCreator() {
         return creator;
     }

    public void setTracks(List<Tracks> tracks) {
         this.tracks = tracks;
     }
     public List<Tracks> getTracks() {
         return tracks;
     }



    public void setPlayCount(int playCount) {
         this.playCount = playCount;
     }
     public int getPlayCount() {
         return playCount;
     }

    public void setCreateTime(long createTime) {
         this.createTime = createTime;
     }
     public long getCreateTime() {
         return createTime;
     }

    public void setCommentThreadId(String commentThreadId) {
         this.commentThreadId = commentThreadId;
     }
     public String getCommentThreadId() {
         return commentThreadId;
     }

    public void setTrackUpdateTime(long trackUpdateTime) {
         this.trackUpdateTime = trackUpdateTime;
     }
     public long getTrackUpdateTime() {
         return trackUpdateTime;
     }

    public void setTrackCount(int trackCount) {
         this.trackCount = trackCount;
     }
     public int getTrackCount() {
         return trackCount;
     }

    public void setSubscribedCount(int subscribedCount) {
         this.subscribedCount = subscribedCount;
     }
     public int getSubscribedCount() {
         return subscribedCount;
     }

    public void setDescription(String description) {
         this.description = description;
     }
     public String getDescription() {
         return description;
     }

    public void setCoverImgUrl(String coverImgUrl) {
         this.coverImgUrl = coverImgUrl;
     }
     public String getCoverImgUrl() {
         return coverImgUrl;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setShareCount(int shareCount) {
         this.shareCount = shareCount;
     }
     public int getShareCount() {
         return shareCount;
     }

    public void setCoverImgId_str(String coverImgId_str) {
         this.coverImgId_str = coverImgId_str;
     }
     public String getCoverImgId_str() {
         return coverImgId_str;
     }

    public void setCommentCount(int commentCount) {
         this.commentCount = commentCount;
     }
     public int getCommentCount() {
         return commentCount;
     }

}