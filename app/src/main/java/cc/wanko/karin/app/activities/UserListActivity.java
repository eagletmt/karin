package cc.wanko.karin.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.client.StatusSource;
import cc.wanko.karin.app.client.UserListSource;
import cc.wanko.karin.app.fragments.StatusListFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectFragment;
import twitter4j.UserList;

public class UserListActivity extends RoboActionBarActivity {

    private static final String ARGS_LIST_NAME = "list_name";
    private static final String ARGS_LIST_ID = "list_id";

    @InjectFragment(R.id.user_list_statuses)
    private StatusListFragment statusListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra(ARGS_LIST_NAME));
        StatusSource statusSource = new UserListSource(this, intent.getLongExtra(ARGS_LIST_ID, -1));

        statusListFragment.setStatusSource(statusSource);
        statusListFragment.retrieveStatuses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_list, menu);
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

    public static Intent createIntent(Context context, UserList list) {
        Intent intent = new Intent(context, UserListActivity.class);
        intent.putExtra(ARGS_LIST_NAME, list.getFullName());
        intent.putExtra(ARGS_LIST_ID, list.getId());
        return intent;
    }
}
