package cc.wanko.karin.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cc.wanko.karin.app.R;
import twitter4j.UserList;

/**
 * Created by eagletmt on 14/05/05.
 */
public class UserListListAdapter extends ArrayAdapter<UserList> {
    private static class ViewHolder {
        public final TextView name, description, info;

        public ViewHolder(View root) {
            this.name = (TextView) root.findViewById(R.id.user_list_name);
            this.description = (TextView) root.findViewById(R.id.user_list_description);
            this.info = (TextView) root.findViewById(R.id.user_list_info);
        }
    }

    public UserListListAdapter(Context context) {
        super(context, -1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.user_list_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserList list = getItem(position);

        holder.name.setText(formatName(list));
        holder.description.setText(list.getDescription());
        holder.info.setText(formatInfo(list));

        return convertView;
    }

    private static String formatName(UserList list) {
        String s = list.getFullName();
        if (!list.isPublic()) {
            s += " [P]";
        }
        return s;
    }

    private static String formatInfo(UserList list) {
        return "" + list.getMemberCount() + " members";
    }
}
