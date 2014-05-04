package cc.wanko.karin.app.adapters;

import android.content.Context;
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

        ImageLoader.ImageContainer container = (ImageLoader.ImageContainer) holder.userIcon.getTag();
        if (container != null) {
            container.cancelRequest();
        }
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.userIcon, R.drawable.ic_launcher, android.R.drawable.ic_delete);
        container = imageLoader.get(user.getBiggerProfileImageURLHttps(), listener);
        holder.userIcon.setTag(container);

        return convertView;
    }
}
