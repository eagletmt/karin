package cc.wanko.karin.app.client;

import android.content.Context;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by eagletmt on 14/05/05.
 */
public class UserStatusSource implements StatusSource {
    private final Twitter twitter;
    private final long userId;

    public UserStatusSource(Context context, long userId) {
        super();
        twitter = TwitterProvider.get(context);
        this.userId = userId;
    }

    @Override
    public ResponseList<Status> getStatuses(Paging paging) throws TwitterException {
        return twitter.getUserTimeline(userId, paging);
    }

    @Override
    public String getCacheKey() {
        return "user_" + userId;
    }
}
