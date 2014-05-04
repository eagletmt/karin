package cc.wanko.karin.app.client;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Created by eagletmt on 14/05/05.
 */
public interface StatusSource {
    public ResponseList<Status> getStatuses(Paging paging) throws TwitterException;
}
