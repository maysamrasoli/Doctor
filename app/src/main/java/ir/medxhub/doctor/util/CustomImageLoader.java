package ir.medxhub.doctor.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import ir.medxhub.doctor.R;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomImageLoader {

    int defaultResId = R.drawable.default_image;
    private boolean loadDefault = true;


    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    public CustomImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    public CustomImageLoader(Context context, int defaultResId) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        this.defaultResId = defaultResId;
    }

    public void DisplayImage(String url, int loader, ImageView imageView) {
        defaultResId = loader;
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
            photoToLoad.imageView.setImageResource(loader);
            new AsyncTask<PhotoToLoad, Void, Bitmap>() {
                private PhotoToLoad pt;

                @Override
                protected Bitmap doInBackground(PhotoToLoad... params) {
                    pt = params[0];

                    if (imageViewReused(pt))
                        return null;

                    Bitmap bitmap = null;

                    //from SD cache
                    File f = fileCache.getFile(pt.url);
                    try {
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null)
                        return bitmap;

                    //from web
                    try {
                        URL imageUrl = new URL(pt.url);
                        HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                        conn.setConnectTimeout(30000);
                        conn.setReadTimeout(30000);
                        conn.setInstanceFollowRedirects(true);
                        InputStream is = conn.getInputStream();
                        OutputStream os = new FileOutputStream(f);
                        Utils.CopyStream(is, os);
                        os.close();
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                        return bitmap;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    memoryCache.put(pt.url, bitmap);
                    if (imageViewReused(pt))
                        return;
                    BitmapDisplayer bd = new BitmapDisplayer(bitmap, pt);
                    Activity a = (Activity) pt.imageView.getContext();
                    a.runOnUiThread(bd);
                }
            }.execute(photoToLoad);
        }
    }

    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else {
                if (loadDefault) {
                    photoToLoad.imageView.setImageResource(defaultResId);
                }
            }
        }
    }

}