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
public class HomeTimelineSource implements StatusSource {
    private Twitter twitter;

    public HomeTimelineSource(Context context) {
        super();
        this.twitter = TwitterProvider.get(context);
    }

    @Override
    public ResponseList<Status> getStatuses(Paging paging) throws TwitterException {
        return twitter.getHomeTimeline(paging);
    }
}
