package cc.wanko.karin.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.client.TwitterProvider;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuthActivity extends RoboFragmentActivity {

    @InjectView(R.id.twitter_authorize_button)
    private Button authorizeButton;
    @InjectResource(R.string.twitter_callback_url)
    private String callbackUrl;

    private Twitter twitter;

    public static Intent createIntent(Context context) {
        return new Intent(context, OAuthActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        authorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitter = TwitterProvider.newInstance(getBaseContext());
                startAuthorization();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.oauth, menu);
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

    private void startAuthorization() {
        new RoboAsyncTask<String>(this) {
            @Override
            public String call() throws Exception {
                Ln.d("Obtain RequestToken: callback=" + callbackUrl);
                RequestToken requestToken = twitter.getOAuthRequestToken(callbackUrl);
                return requestToken.getAuthorizationURL();
            }

            @Override
            protected void onSuccess(String authorizationUrl) throws Exception {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl));
                startActivity(intent);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Toast.makeText(OAuthActivity.this, "Cannot start authorization: " + e.getClass() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String verifier = getVerifier(intent);
        if (verifier == null) {
            return;
        }
        Ln.d("oauth_verifier = " + verifier);
        requestAccessToken(verifier);
    }

    private String getVerifier(Intent intent) {
        if (intent == null) {
            return null;
        }
        Uri uri = intent.getData();
        if (uri == null || !uri.toString().startsWith(callbackUrl)) {
            return null;
        }
        return uri.getQueryParameter("oauth_verifier");
    }

    private void requestAccessToken(final String verifier) {
        new RoboAsyncTask<AccessToken>(this) {
            @Override
            public AccessToken call() throws Exception {
                return twitter.getOAuthAccessToken(verifier);
            }

            @Override
            protected void onSuccess(AccessToken accessToken) throws Exception {
                TwitterProvider.storeAccessToken(OAuthActivity.this, accessToken);
                finish();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Toast.makeText(OAuthActivity.this, "Cannot get access token: " + e.getClass() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }.execute();
    }
}