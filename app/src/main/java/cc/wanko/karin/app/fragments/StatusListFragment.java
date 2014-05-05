package cc.wanko.karin.app.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.adapters.StatusListAdapter;
import cc.wanko.karin.app.client.StatusSource;
import cc.wanko.karin.app.database.Database;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import twitter4j.HttpResponseCode;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Created by eagletmt on 14/05/05.
 */
public class StatusListFragment extends RoboFragment {
    @InjectView(R.id.swipe_refresh)
    private SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.status_list)
    private ListView statusList;

    private StatusListAdapter statusListAdapter;

    private StatusSource statusSource;
    private Database db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusListAdapter = new StatusListAdapter(getActivity());
        statusList.setAdapter(statusListAdapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (statusListAdapter.isEmpty()) {
                    retrieveStatuses();
                } else {
                    Status status = statusListAdapter.getItem(0);
                    retrieveStatuses(new Paging(1, PAGE_COUNT, status.getId()));
                }
            }
        });

        db = new Database(getActivity());
        if (statusSource != null) {
            retrieveStatuses();
        }
    }

    private void storeTopId() {
        if (statusListAdapter.isEmpty()) {
            return;
        }
        String key = statusSource.getCacheKey();
        long topId = statusListAdapter.getItem(0).getId();
        Ln.d("Store " + topId + " with " + key);
        db.storeTopId(key, topId);
    }

    public void setStatusSource(StatusSource statusSource) {
        this.statusSource = statusSource;
    }

    private static final int PAGE_COUNT = 100;

    public void retrieveStatuses() {
        retrieveStatuses(new Paging(1, PAGE_COUNT));
    }

    public void retrieveStatuses(final Paging paging) {
        swipeRefresh.setEnabled(false);
        swipeRefresh.setRefreshing(true);

        new RoboAsyncTask<ResponseList<Status>>(getActivity()) {
            @Override
            public ResponseList<Status> call() throws Exception {
                Ln.d("Get statuses with sinceId=" + paging.getSinceId());
                return statusSource.getStatuses(paging);
            }

            @Override
            protected void onSuccess(ResponseList<Status> statuses) throws Exception {
                RateLimitStatus limit = statuses.getRateLimitStatus();
                Ln.d("access level=" + statuses.getAccessLevel() + ", rate limit=" + limit.getRemaining() + "/" + limit.getLimit());

                boolean isFirstFetch = statusListAdapter.isEmpty();
                int position = statusList.getFirstVisiblePosition();
                Collections.reverse(statuses);
                long topId = db.getTopId(statusSource.getCacheKey());
                int pos = 0;
                for (Status status : statuses) {
                    statusListAdapter.insert(status, 0);
                    ++pos;
                    if (status.getId() == topId) {
                        pos = 0;
                    }
                }
                if (!isFirstFetch) {
                    statusList.setSelection(position + pos);
                }

                Toast.makeText(getContext(), "Got " + statuses.size() + " tweets", Toast.LENGTH_SHORT).show();

                storeTopId();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Cannot get statuses from " + statusSource.getClass().getSimpleName(), e);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                swipeRefresh.setEnabled(true);
                swipeRefresh.setRefreshing(false);
            }
        }.execute();
    }

    private void reportException(String message, Exception e) {
        if (e instanceof TwitterException) {
            TwitterException te = (TwitterException) e;
            if (te.getStatusCode() == HttpResponseCode.TOO_MANY_REQUESTS) {
                String msg = "Rate limit exceeded.";
                if (te.getRetryAfter() != -1) {
                    msg += " Retry after " + te.getRetryAfter() + " seconds!";
                }
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }
        }
        Toast.makeText(getActivity(), message + ": " + e.getClass() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}
