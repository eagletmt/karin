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
public class UserListSource implements StatusSource {
    private Twitter twitter;
    private long listId;

    public UserListSource(Context context, long listId) {
        super();
        this.twitter = TwitterProvider.get(context);
        this.listId = listId;
    }

    @Override
    public ResponseList<Status> getStatuses(Paging paging) throws TwitterException {
        return twitter.getUserListStatuses(listId, paging);
    }
}
