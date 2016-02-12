package ir.medxhub.doctor.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    ExecutorService executorService;

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        return maxMemory / 8;
    }

    public BitmapLruCache(Context context) {
        this(getDefaultLruCacheSize(), context);
    }

    public BitmapLruCache(int sizeInKiloBytes, Context context) {
        super(sizeInKiloBytes);
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        //save to memory
        if (memoryCache.get(url) == null)
            memoryCache.put(url, bitmap);

        //save to SD
        try {
            File f = fileCache.getFile(url);
            if (!f.exists()) {
                OutputStream out = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        try {
            //check memory for bitmap
            bitmap = memoryCache.get(url);
            if (bitmap != null)
                return bitmap;

            //try to get bitmap from SD cache
            File f = fileCache.getFile(url);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}