package cc.wanko.karin.app.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.adapters.StatusListAdapter;
import cc.wanko.karin.app.client.TwitterProvider;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;


public class MainActivity extends RoboFragmentActivity {

    @InjectView(R.id.home_timeline_list)
    private ListView homeTimelineList;

    private StatusListAdapter statusListAdapter;

    private Twitter twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!TwitterProvider.hasAccessToken(this)) {
            startActivity(OAuthActivity.createIntent(this));
            return;
        }

        statusListAdapter = new StatusListAdapter(this);
        homeTimelineList.setAdapter(statusListAdapter);

        twitter = TwitterProvider.get(this);
        retrieveHomeTimeline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        twitter = TwitterProvider.get(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void retrieveHomeTimeline() {
        new RoboAsyncTask<ResponseList<Status>>(this) {
            @Override
            public ResponseList<Status> call() throws Exception {
                return twitter.getHomeTimeline();
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
        }.execute();
    }

    private void reportException(String message, Exception e) {
        Toast.makeText(this, message + ": " + e.getClass() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}
