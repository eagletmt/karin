package cc.wanko.karin.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.activities.UserStatusesActivity;
import cc.wanko.karin.app.client.TwitterProvider;
import cc.wanko.karin.app.textbuilder.StatusTextBuilder;
import cc.wanko.karin.app.utils.LruImageCache;
import cc.wanko.karin.app.utils.RoboViewHolder;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.User;

/**
 * Created by eagletmt on 14/04/29.
 */
public class StatusListAdapter extends ArrayAdapter<Status> {
    private static class ViewHolder extends RoboViewHolder {
        @InjectView(R.id.status_user_icon)
        ImageView userIcon;
        @InjectView(R.id.status_user_name)
        TextView userName;
        @InjectView(R.id.status_created_at)
        TextView createdAt;
        @InjectView(R.id.status_text)
        TextView statusText;
        @InjectView(R.id.status_media_image)
        NetworkImageView mediaImage;
        @InjectView(R.id.status_retweeter_area)
        LinearLayout retweeterArea;
        @InjectView(R.id.status_retweeter_name)
        TextView retweeterName;
        @InjectView(R.id.status_favorite_button)
        ToggleButton favoriteButton;
        @InjectView(R.id.status_retweet_button)
        ToggleButton retweetButton;
        @InjectView(R.id.status_destroy_button)
        ToggleButton destroyButton;

        public ViewHolder(View root) {
            super(root);
        }
    }

    private static class UserIconTag {
        final ImageLoader.ImageContainer imageContainer;
        final long userId;
        final String screenName;

        public UserIconTag(ImageLoader.ImageContainer imageContainer, User user) {
            this.imageContainer = imageContainer;
            this.userId = user.getId();
            this.screenName = user.getScreenName();
        }
    }

    private static class UserNameTag {
        final long userId;
        final String screenName;

        public UserNameTag(User user) {
            this.userId = user.getId();
            this.screenName = user.getScreenName();
        }
    }

    private static class StatusButtonTag {
        final int position;

        public StatusButtonTag(int position) {
            this.position = position;
        }
    }

    private final ImageLoader imageLoader;

    public StatusListAdapter(Context context) {
        super(context, -1);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        imageLoader = new ImageLoader(queue, new LruImageCache());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.status_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Status status = getItem(position);
        Status retweet = status.getRetweetedStatus();
        if (retweet == null) {
            setLayoutHeight(holder.retweeterArea, 0);
        } else {
            User retweeter = status.getUser();
            holder.retweeterName.setText(retweeter.getScreenName());
            setLayoutHeight(holder.retweeterArea, ViewGroup.LayoutParams.WRAP_CONTENT);

            holder.retweeterName.setTag(new UserNameTag(retweeter));
            holder.retweeterName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserNameTag tag = (UserNameTag) view.getTag();
                    getContext().startActivity(UserStatusesActivity.createIntent(getContext(), tag.userId, tag.screenName));
                }
            });

            status = retweet;
        }
        holder.statusText.setText(new StatusTextBuilder(getContext()).buildStatus(status));
        holder.statusText.setMovementMethod(LinkMovementMethod.getInstance());
        holder.createdAt.setText(formatDate(status.getCreatedAt()));
        User user = status.getUser();
        holder.userName.setText(user.getScreenName());

        holder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserIconTag tag = (UserIconTag) view.getTag();
                getContext().startActivity(UserStatusesActivity.createIntent(getContext(), tag.userId, tag.screenName));
            }
        });

        UserIconTag tag = (UserIconTag) holder.userIcon.getTag();
        if (tag != null) {
            tag.imageContainer.cancelRequest();
        }
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.userIcon, R.drawable.ic_launcher, android.R.drawable.ic_delete);
        ImageLoader.ImageContainer container = imageLoader.get(user.getBiggerProfileImageURLHttps(), listener);
        holder.userIcon.setTag(new UserIconTag(container, user));

        setupMediaImage(holder.mediaImage, status);

        holder.favoriteButton.setChecked(status.isFavorited());
        holder.favoriteButton.setTag(new StatusButtonTag(position));
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleButton button = (ToggleButton) view;
                StatusButtonTag tag = (StatusButtonTag) button.getTag();
                final Status status = getItem(tag.position);

                if (button.isChecked()) {
                    createFavorite(button, status.getId());
                } else {
                    destroyFavorite(button, status.getId());
                }
            }
        });

        if (user.isProtected()) {
            holder.retweetButton.setEnabled(false);
        } else {
            holder.retweetButton.setEnabled(true);
            holder.retweetButton.setChecked(status.isRetweeted());
            holder.retweetButton.setTag(new StatusButtonTag(position));
            holder.retweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ToggleButton button = (ToggleButton) view;
                    StatusButtonTag tag = (StatusButtonTag) button.getTag();
                    final Status status = getItem(tag.position);

                    if (button.isChecked()) {
                        confirm("Retweet", "RT @" + status.getUser().getScreenName() + ": " + status.getText(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                retweetStatus(button, status.getId());
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                button.setChecked(false);
                                        }

                                    }
                                }
                        );
                    }
                }
            });
        }

        holder.destroyButton.setTag(new StatusButtonTag(position));
        holder.destroyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ToggleButton button = (ToggleButton) view;
                StatusButtonTag tag = (StatusButtonTag) button.getTag();
                final Status status = getItem(tag.position);

                confirm("Destroy", status.getText(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        destroyStatus(button, status.getId());
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        button.setChecked(false);
                                }

                            }
                        }
                );
            }
        });

        return convertView;
    }

    private static void setLayoutHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private void setupMediaImage(NetworkImageView imageView, Status status) {
        MediaEntity[] entities = status.getMediaEntities();
        if (entities.length == 0) {
            setLayoutHeight(imageView, 0);
            return;
        } else {
            setLayoutHeight(imageView, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        MediaEntity entity = entities[0];
        final String url = entity.getMediaURLHttps();
        imageView.setImageUrl(url, imageLoader);
        imageView.setErrorImageResId(android.R.drawable.ic_delete);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    private void createFavorite(final ToggleButton button, final long statusId) {
        new RoboAsyncTask<Status>(getContext()) {
            @Override
            public Status call() throws Exception {
                return TwitterProvider.get(getContext()).createFavorite(statusId);
            }

            @Override
            protected void onSuccess(Status status) throws Exception {
                Toast.makeText(getContext(), R.string.favorited, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Could not favorite " + statusId, e);
                button.setChecked(false);
            }
        }.execute();
    }

    private void destroyFavorite(final ToggleButton button, final long statusId) {
        new RoboAsyncTask<Status>(getContext()) {
            @Override
            public Status call() throws Exception {
                return TwitterProvider.get(getContext()).destroyFavorite(statusId);
            }

            @Override
            protected void onSuccess(Status status) throws Exception {
                Toast.makeText(getContext(), R.string.unfavorited, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Could not unfavorite " + statusId, e);
                button.setChecked(true);
            }
        }.execute();
    }

    private void retweetStatus(final ToggleButton button, final long statusId) {
        new RoboAsyncTask<Status>(getContext()) {
            @Override
            public Status call() throws Exception {
                return TwitterProvider.get(getContext()).retweetStatus(statusId);
            }

            @Override
            protected void onSuccess(Status status) throws Exception {
                Toast.makeText(getContext(), R.string.retweeted, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Could not retweet " + statusId, e);
                button.setChecked(false);
            }
        }.execute();
    }

    private void destroyStatus(final ToggleButton button, final long statusId) {
        new RoboAsyncTask<Status>(getContext()) {
            @Override
            public Status call() throws Exception {
                return TwitterProvider.get(getContext()).destroyStatus(statusId);
            }

            @Override
            protected void onSuccess(Status status) throws Exception {
                Toast.makeText(getContext(), R.string.status_destroyed, Toast.LENGTH_SHORT).show();
                // TODO: Remove status from adapter.
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Could not destroy status " + statusId, e);
                button.setChecked(false);
            }
        }.execute();
    }

    private void reportException(String message, Exception e) {
        message += ": " + e.getClass().getSimpleName() + ": " + e.getMessage();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Ln.e(message);
        e.printStackTrace();
    }

    private void confirm(String title, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, listener)
                .setNegativeButton(android.R.string.no, listener)
                .show();
    }
}
