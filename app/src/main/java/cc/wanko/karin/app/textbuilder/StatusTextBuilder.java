package cc.wanko.karin.app.textbuilder;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * Created by eagletmt on 14/05/06.
 */
public class StatusTextBuilder {
    private Context context;

    public StatusTextBuilder(Context context) {
        this.context = context;
    }

    public SpannableStringBuilder buildStatus(Status status) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        List<Segment> segments = new ArrayList<Segment>();
        for (URLEntity entity : status.getURLEntities()) {
            segments.add(new UrlSegment(entity));
        }
        for (UserMentionEntity entity : status.getUserMentionEntities()) {
            segments.add(new MentionSegment(entity));
        }
        for (MediaEntity entity : status.getMediaEntities()) {
            segments.add(new MediaSegment(entity));
        }

        Collections.sort(segments, new Comparator<Segment>() {
            @Override
            public int compare(Segment s1, Segment s2) {
                return s1.start - s2.start;
            }
        });

        String text = status.getText();
        int textIndex = 0;
        for (final Segment segment : segments) {
            if (textIndex != segment.start) {
                builder.append(text.substring(textIndex, segment.start));
            }
            textIndex = segment.end;

            int spanStart = builder.length();
            builder.append(segment.text);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    segment.onClick(context);
                }
            }, spanStart, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (textIndex != text.length()) {
            builder.append(text.substring(textIndex));
        }

        return builder;
    }
}
