package cc.wanko.karin.app.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.adapters.MainPagerAdapter;
import cc.wanko.karin.app.client.TwitterProvider;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import twitter4j.Status;
import twitter4j.Twitter;

public class MainActivity extends RoboActionBarActivity {
    @InjectView(R.id.main_pager)
    private ViewPager viewPager;
    @InjectView(R.id.tweet_text)
    private EditText tweetText;
    @InjectView(R.id.tweet_button)
    private Button tweetButton;

    private Twitter twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!TwitterProvider.hasAccessToken(this)) {
            startActivity(OAuthActivity.createIntent(this));
            return;
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.selectTab(actionBar.getTabAt(position));
            }
        });
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        };
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(pagerAdapter.getPageTitle(i))
                    .setTabListener(tabListener));
        }

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = tweetText.getText().toString();
                if (!text.isEmpty()) {
                    updateStatus(text);
                }
            }
        });

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
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_authorize:
                startActivity(OAuthActivity.createIntent(this));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateStatus(final String text) {
        tweetButton.setEnabled(false);
        new RoboAsyncTask<Status>(this) {
            @Override
            public Status call() throws Exception {
                return twitter.updateStatus(text);
            }

            @Override
            protected void onSuccess(Status status) throws Exception {
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                tweetText.setText("");
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Could not update status", e);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                tweetButton.setEnabled(true);
            }
        }.execute();
    }

    private void reportException(String message, Exception e) {
        message += ": " + e.getClass().getSimpleName() + ": " + e.getMessage();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Ln.e(message);
        e.printStackTrace();
    }
}
