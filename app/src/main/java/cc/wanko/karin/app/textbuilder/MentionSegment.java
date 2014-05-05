package cc.wanko.karin.app.textbuilder;

import android.content.Context;
import android.content.Intent;

import cc.wanko.karin.app.activities.UserStatusesActivity;
import twitter4j.UserMentionEntity;

/**
 * Created by eagletmt on 14/05/06.
 */
public class MentionSegment extends Segment {
    private final UserMentionEntity entity;

    public MentionSegment(UserMentionEntity entity) {
        super(entity.getStart(), entity.getEnd(), "@" + entity.getScreenName());
        this.entity = entity;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = UserStatusesActivity.createIntent(context, entity);
        context.startActivity(intent);
    }
}
