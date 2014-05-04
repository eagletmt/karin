package cc.wanko.karin.app.utils;

import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import roboguice.inject.InjectView;

/**
 * Created by eagletmt on 14/05/05.
 */
public class RoboViewHolder {
    public RoboViewHolder(View root) {
        injectViews(root);
    }

    private void injectViews(View view) {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectView.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new UnsupportedOperationException("@InjectView is used to static field " + field.getName());
                }
                if (!View.class.isAssignableFrom(field.getType())) {
                    throw new UnsupportedOperationException("@InjectView is used to non-view field " + field.getName());
                }
                injectView(view, field);
            }
        }
    }

    private void injectView(View view, Field field) {
        InjectView annot = field.getAnnotation(InjectView.class);
        field.setAccessible(true);
        try {
            field.set(this, view.findViewById(annot.value()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
