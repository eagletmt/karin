package cc.wanko.karin.app.client;

import android.content.Context;
import android.content.SharedPreferences;

import cc.wanko.karin.app.R;
import roboguice.util.Ln;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by eagletmt on 14/04/29.
 */
public class TwitterProvider {
    private static final String PREF_NAME = "twitter-access-token";
    private static final String PREF_TOKEN = "access-token";
    private static final String PREF_TOKEN_SECRET = "access-token-secret";

    public static Twitter get(Context context) {
        Twitter twitter = newInstance(context);
        AccessToken accessToken = loadAccessToken(context);
        if (accessToken != null) {
            twitter.setOAuthAccessToken(accessToken);
        }
        return twitter;
    }

    public static Twitter newInstance(Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);
        Twitter twitter = new TwitterFactory().getInstance();
        Ln.d("Set twitter consumer_key=" + consumerKey + ", consumer_secret=" + consumerSecret);
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        return twitter;
    }

    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }

    private static AccessToken loadAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(PREF_TOKEN, null);
        String secret = prefs.getString(PREF_TOKEN_SECRET, null);
        if (token != null && secret != null) {
            return new AccessToken(token, secret);
        } else {
            return null;
        }
    }

    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_TOKEN, accessToken.getToken());
        editor.putString(PREF_TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }
}