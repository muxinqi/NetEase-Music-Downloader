/**
 * Copyright 2019 bejson.com
 */
package com.mtools.android.class_define.MusicComment;
import java.util.List;

/**
 * Auto-generated: 2019-12-21 20:32:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class HotComments {

    private User user;
    private List<String> beReplied;
    private String showFloorComment;
    private int status;
    private long commentId;
    private String content;
    private long time;
    private long likedCount;
    private String expressionUrl;
    private int commentLocationType;
    private int parentCommentId;
    private String repliedMark;
    private boolean liked;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getBeReplied() {
        return beReplied;
    }

    public void setBeReplied(List<String> beReplied) {
        this.beReplied = beReplied;
    }

    public String getShowFloorComment() {
        return showFloorComment;
    }

    public void setShowFloorComment(String showFloorComment) {
        this.showFloorComment = showFloorComment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(long likedCount) {
        this.likedCount = likedCount;
    }

    public String getExpressionUrl() {
        return expressionUrl;
    }

    public void setExpressionUrl(String expressionUrl) {
        this.expressionUrl = expressionUrl;
    }

    public int getCommentLocationType() {
        return commentLocationType;
    }

    public void setCommentLocationType(int commentLocationType) {
        this.commentLocationType = commentLocationType;
    }

    public int getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(int parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getRepliedMark() {
        return repliedMark;
    }

    public void setRepliedMark(String repliedMark) {
        this.repliedMark = repliedMark;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}