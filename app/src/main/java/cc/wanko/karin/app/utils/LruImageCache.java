package cc.wanko.karin.app.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by eagletmt on 14/04/30.
 */
public class LruImageCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> cache;

    public LruImageCache() {
        cache = new LruCache<String, Bitmap>(cacheSize()) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    private static int cacheSize() {
        return (int) (Runtime.getRuntime().maxMemory() / 8);
    }

    @Override
    public Bitmap getBitmap(String key) {
        return cache.get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
    }
}
