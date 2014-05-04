package cc.wanko.karin.app.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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

    public StatusListFragment(StatusSource statusSource) {
        super();
        this.statusSource = statusSource;
    }

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
                retrieveStatuses();
            }
        });

        retrieveStatuses();
    }

    private static final int PAGE_COUNT = 100;

    private void retrieveStatuses() {
        swipeRefresh.setEnabled(false);
        swipeRefresh.setRefreshing(true);

        new RoboAsyncTask<ResponseList<Status>>(getActivity()) {
            @Override
            public ResponseList<Status> call() throws Exception {
                Paging paging = new Paging(1, PAGE_COUNT);
                return statusSource.getStatuses(paging);
            }

            @Override
            protected void onSuccess(ResponseList<Status> statuses) throws Exception {
                RateLimitStatus limit = statuses.getRateLimitStatus();
                Ln.d("access level=" + statuses.getAccessLevel() + ", rate limit=" + limit.getRemaining() + "/" + limit.getLimit());
                statusListAdapter.clear();
                for (Status status : statuses) {
                    statusListAdapter.add(status);
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Cannot get home timeline", e);
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
