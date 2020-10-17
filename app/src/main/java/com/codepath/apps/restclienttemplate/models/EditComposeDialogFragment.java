package com.codepath.apps.restclienttemplate.models;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class EditComposeDialogFragment  extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "ComposeActivity";
    public  static final int MAX_TWEET_LENGTH = 280;

    private EditText etCompose;
    private Button btnTweet;
    private Button btnCancel;
    private TextView tvWordCount;
    private TextView tvName;
    private TextView tvScreenName;
    private ImageView ivProfileImage;

    TwitterClient client = TwitterApp.getRestClient(getContext());

    public EditComposeDialogFragment(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    public interface EditComposeDiaglogListener{
        void onFinishedEditDialog(String inputText);
    }

    public static EditComposeDialogFragment newInstance(String title){
        EditComposeDialogFragment frag = new EditComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);

        //super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etCompose = (EditText) view.findViewById(R.id.etCompose);
        String title = getArguments().getString("title","Enter name");
        getDialog().setTitle(title);
        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        etCompose.setOnEditorActionListener(this);

        btnTweet = view.findViewById(R.id.btnTweet);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvWordCount = view.findViewById(R.id.tvWordCount);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);
        tvScreenName = view.findViewById(R.id.tvScreenName);

        //
        client.getUserProfile(new JsonHttpResponseHandler(){

                      @Override
                      public void onSuccess(int statusCode, Headers headers, JSON json) {
                          Log.i(TAG, "onSuccess to get user's information");
                          try {
                              User user  = User.fromJson(json.jsonObject);
                              tvName.setText(user.name);
                              tvScreenName.setText("@"+user.screenName);
                              Glide.with(getContext()).load(user.profileImageUrl).placeholder(R.drawable.ic_vector_person).
                                      transform(new MultiTransformation(new FitCenter(), new RoundedCorners(10)))
                                              .into(ivProfileImage);
                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                      }

                      @Override
                      public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                          Log.e(TAG,"onFailure to get user's information", throwable);

                      }
                  }
        );

        // Set click listener on button
        tvWordCount.setText("0/280");
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvWordCount.setText(etCompose.getText().length() +"/280");
                if (etCompose.getText().length() > MAX_TWEET_LENGTH) {
                    tvWordCount.setTextColor(Color.RED);
                    btnTweet.setEnabled(false);
                }
                else{
                    tvWordCount.setTextColor(Color.BLACK);
                    btnTweet.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Toast.makeText(getContext(), "Canceled composing tweet!", Toast.LENGTH_LONG).show();
                         dismiss();
                     }
                 }
        );
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()){
                    Toast.makeText(getContext(),"Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return ;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(getContext(),"Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return ;
                }
                Toast.makeText(getContext(), tweetContent,Toast.LENGTH_LONG).show();
                // Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent  = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // Set result and bundle data for response
//                            getContext().setResult(RESULT_OK, intent);
                            // closes the activity, pass data to parent
//                            finish();
                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to publish tweet", throwable);
                    }
                });
            }
        });
    }
}
