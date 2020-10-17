package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind value based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of tweets
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }


    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivLike;
        ImageView ivRetweet;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTimeStamp;
        TextView tvLikeCount;
        TextView tvReplyCount;
        TextView tvRetweetCount;
        FloatingActionButton fabCompose;
        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            fabCompose = itemView.findViewById(R.id.fabCompose);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.userName);
            tvScreenName.setText("@" + tweet.userScreenName);
            tvTimeStamp.setText(tweet.getFormattedTimestamp());
            if (tweet.likeCount >0) {
                tvLikeCount.setText(String.valueOf(tweet.likeCount));
            }
            if (tweet.retweetCount > 0){
                tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            }
            if (tweet.liked) {
                //ivLike.setColorFilter(ContextCompat.getColor(context, R.color.twitter_blue_30), android.graphics.PorterDuff.Mode.SRC_IN);
                DrawableCompat.setTint(ivLike.getDrawable(), ContextCompat.getColor(context, R.color.twitter_blue_30));
            }
            if (tweet.retweeted) {
                ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.twitter_blue_30), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            //tvReplyCount.setText(String.valueOf(tweet.replyCount));//.publicMetrics.replyCount));
            Glide.with(context).load(tweet.userProfileImageUrl)
                    .transform(new MultiTransformation(new FitCenter(), new RoundedCorners(10)))
                    .into(ivProfileImage);
            container.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         // 2. Navigate to a new activity on tap
                         //Toast.makeText(context, tweet.user.name, Toast.LENGTH_SHORT).show();
                         Intent i = new Intent(context, DetailActivity.class);
                         Pair<View, String> p1 = Pair.create((View)tvName, "name");
                         Pair<View, String> p2 = Pair.create((View)ivProfileImage, "profileImage");
                         Pair<View, String> p3 = Pair.create((View)tvScreenName, "screenName");
                         ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
                         i.putExtra("tweet", Parcels.wrap(tweet));
                         context.startActivity(i, options.toBundle());
//                    Toast.makeText(context, movie.getTitle(), Toast.LENGTH_SHORT).show();
                     }
                 }
            );

        }
    }
}
