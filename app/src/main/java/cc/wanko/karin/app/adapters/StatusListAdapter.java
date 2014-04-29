package cc.wanko.karin.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cc.wanko.karin.app.R;
import twitter4j.Status;

/**
 * Created by eagletmt on 14/04/29.
 */
public class StatusListAdapter extends ArrayAdapter<Status> {
    private static class ViewHolder {
        final TextView statusText;

        public ViewHolder(View root) {
            statusText = (TextView) root.findViewById(R.id.status_text);
        }
    }

    public StatusListAdapter(Context context) {
        super(context, -1);
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

        holder.statusText.setText(status.getText());

        return convertView;
    }
}
