package cc.wanko.karin.app.textbuilder;

import android.content.Context;

/**
 * Created by eagletmt on 14/05/06.
 */
public abstract class Segment {
    final int start, end;
    final String text;

    public Segment(int start, int end, String text) {
        this.start = start;
        this.end = end;
        this.text = text;
    }

    abstract public void onClick(Context context);
}
