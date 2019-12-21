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
public class Comment {

    private boolean isMusician;
    private int userId;
    private List<String> topComments;
    private boolean moreHot;
    private List<HotComments> hotComments;
    private int code;
    private long total;
    private boolean more;

    public boolean isMusician() {
        return isMusician;
    }

    public void setMusician(boolean musician) {
        isMusician = musician;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<String> getTopComments() {
        return topComments;
    }

    public void setTopComments(List<String> topComments) {
        this.topComments = topComments;
    }

    public boolean isMoreHot() {
        return moreHot;
    }

    public void setMoreHot(boolean moreHot) {
        this.moreHot = moreHot;
    }

    public List<HotComments> getHotComments() {
        return hotComments;
    }

    public void setHotComments(List<HotComments> hotComments) {
        this.hotComments = hotComments;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}