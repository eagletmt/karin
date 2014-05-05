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
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;

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

        if (statusSource != null) {
            retrieveStatuses();
        }
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
                return statusSource.getStatuses(paging);
            }

            @Override
            protected void onSuccess(ResponseList<Status> statuses) throws Exception {
                RateLimitStatus limit = statuses.getRateLimitStatus();
                Ln.d("access level=" + statuses.getAccessLevel() + ", rate limit=" + limit.getRemaining() + "/" + limit.getLimit());

                boolean isFirstFetch = statusListAdapter.isEmpty();
                int position = statusList.getFirstVisiblePosition();
                Collections.reverse(statuses);
                for (Status status : statuses) {
                    statusListAdapter.insert(status, 0);
                }
                if (!isFirstFetch) {
                    statusList.setSelection(position + statuses.size());
                }
                Toast.makeText(getContext(), "Got " + statuses.size() + " tweets", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), message + ": " + e.getClass() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}
