package cc.wanko.karin.app.textbuilder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import twitter4j.MediaEntity;

/**
 * Created by eagletmt on 14/05/06.
 */
public class MediaSegment extends Segment {
    private final MediaEntity entity;

    public MediaSegment(MediaEntity entity) {
        super(entity.getStart(), entity.getEnd(), entity.getMediaURLHttps());
        this.entity = entity;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.getMediaURLHttps()));
        context.startActivity(intent);
    }
}
