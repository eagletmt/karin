package cc.wanko.karin.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.activities.UserStatusesActivity;
import cc.wanko.karin.app.utils.LruImageCache;
import cc.wanko.karin.app.utils.RoboViewHolder;
import roboguice.inject.InjectView;
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
        @InjectView(R.id.status_text)
        TextView statusText;

        public ViewHolder(View root) {
            super(root);
        }
    }

    private static class UserIconTag {
        final ImageLoader.ImageContainer imageContainer;
        final int position;

        public UserIconTag(ImageLoader.ImageContainer imageContainer, int position) {
            this.imageContainer = imageContainer;
            this.position = position;
        }
    }

    private ImageLoader imageLoader;

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
        User user = status.getUser();

        holder.statusText.setText(status.getText());
        holder.userName.setText(user.getScreenName());

        holder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserIconTag tag = (UserIconTag) view.getTag();
                Status status = getItem(tag.position);
                Intent intent = UserStatusesActivity.createIntent(getContext(), status.getUser());
                getContext().startActivity(intent);
            }
        });

        UserIconTag tag = (UserIconTag) holder.userIcon.getTag();
        if (tag != null) {
            tag.imageContainer.cancelRequest();
        }
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.userIcon, R.drawable.ic_launcher, android.R.drawable.ic_delete);
        ImageLoader.ImageContainer container = imageLoader.get(user.getBiggerProfileImageURLHttps(), listener);
        holder.userIcon.setTag(new UserIconTag(container, position));

        return convertView;
    }
}
