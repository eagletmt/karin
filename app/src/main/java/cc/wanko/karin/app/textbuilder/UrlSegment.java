package cc.wanko.karin.app.textbuilder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import twitter4j.URLEntity;

/**
 * Created by eagletmt on 14/05/06.
 */
public class UrlSegment extends Segment {
    private final URLEntity entity;

    public UrlSegment(URLEntity entity) {
        super(entity.getStart(), entity.getEnd(), entity.getExpandedURL());
        this.entity = entity;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.getExpandedURL()));
        context.startActivity(intent);
    }
}
