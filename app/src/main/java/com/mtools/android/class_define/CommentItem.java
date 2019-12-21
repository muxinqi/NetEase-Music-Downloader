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

    public String getCommentContent() {
        return commentContent;
    }

    public String getCommentAvatarUrl() {
        return commentAvatarUrl;
    }

    public Date getTime() {
        return time;
    }

    public long getLikedCount() {
        return likedCount;
    }
}
