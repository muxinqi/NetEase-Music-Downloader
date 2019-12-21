package com.mtools.android.class_define;

import java.util.Date;

public class CommentItem {

    private String commentName;
    private String commentContent;
    private String commentAvatarUrl;
    private long likedCount;
    private Date time;

    public CommentItem(String commentName, String commentContent, String commentAvatarUrl, long likedCount, Date time) {
        this.commentName = commentName;
        this.commentContent = commentContent;
        this.commentAvatarUrl = commentAvatarUrl;
        this.likedCount = likedCount;
        this.time = time;
    }

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentAvatarUrl() {
        return commentAvatarUrl;
    }

    public void setCommentAvatarUrl(String commentAvatarUrl) {
        this.commentAvatarUrl = commentAvatarUrl;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(long likedCount) {
        this.likedCount = likedCount;
    }
}
