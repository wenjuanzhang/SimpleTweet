package com.codepath.apps.restclienttemplate.models;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {


    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long likeCount;

    @ColumnInfo
    public long retweetCount;

    @ColumnInfo
    public Boolean liked;

    @ColumnInfo
    public Boolean retweeted;
    //public int replyCount;
    //public PublicMetrics publicMetrics;

    @ColumnInfo
    public long userId;

    @ColumnInfo
    public String userName;

    @ColumnInfo
    public String userScreenName;

    @ColumnInfo
    public String userProfileImageUrl;


    @Ignore
    public User user;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");
        tweet.likeCount = jsonObject.getInt("favorite_count");
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.liked = jsonObject.getBoolean("favorited");
        tweet.userId = user.id;
        tweet.userName = user.name;
        tweet.userScreenName = user.screenName;
        tweet.userProfileImageUrl = user.profileImageUrl;

        //tweet.publicMetrics = PublicMetrics.fromJson(jsonObject.getJSONObject("public_metrics"));
        return tweet;


    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getFormattedTimestamp(){
        TimeFormatter timeFormatter = new TimeFormatter();
        return timeFormatter.getTimeDifference(this.createdAt) + " ago";
    }

}
