package cc.wanko.karin.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.client.UserStatusSource;
import cc.wanko.karin.app.fragments.StatusListFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectFragment;
import twitter4j.User;

public class UserStatusesActivity extends RoboActionBarActivity {

    private static final String ARGS_USER_ID = "user_id";
    private static final String ARGS_SCREEN_NAME = "screen_name";

    @InjectExtra(ARGS_USER_ID)
    private long userId;
    @InjectExtra(ARGS_SCREEN_NAME)
    private String screenName;

    @InjectFragment(R.id.user_statuses)
    private StatusListFragment statusListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statuses);

        getSupportActionBar().setTitle("@" + screenName);

        UserStatusSource source = new UserStatusSource(this, userId);
        statusListFragment.setStatusSource(source);
        statusListFragment.retrieveStatuses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_statuses, menu);
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

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context, UserStatusesActivity.class);
        intent.putExtra(ARGS_USER_ID, user.getId());
        intent.putExtra(ARGS_SCREEN_NAME, user.getScreenName());
        return intent;
    }
}
