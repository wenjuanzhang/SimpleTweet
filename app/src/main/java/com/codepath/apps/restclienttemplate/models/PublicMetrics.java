package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class PublicMetrics {

    public int likeCount;
    public int replyCount;
    public int retweetCount;

    public static PublicMetrics fromJson(JSONObject jsonObject) throws JSONException {
        PublicMetrics publicMetrics = new PublicMetrics();
        publicMetrics.likeCount =  jsonObject.getInt("like_count");
        publicMetrics.replyCount =  jsonObject.getInt("reply_count");
        publicMetrics.retweetCount =  jsonObject.getInt("retweet_count");

        return publicMetrics;
    }
}
