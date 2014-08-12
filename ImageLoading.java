import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;


public class ImagesLoading extends Activity {

    private LruCache<String,Bitmap> cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_loading);


        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);

        final int cacheSize =  maxMemory/8;

        cache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
            }
        };
        AssetManager mg = getAssets();
        String[] data = null;
        try {
            data = mg.list("images");
            for (String aData : data) {
                addBitmapToMemoryCache(aData, BitmapFactory.decodeStream(mg.open("images/" + aData)));
                //Log.w("DEMOOO", "Image :: " + aData + " is added");
            }
        int count = 0;
        for (String aData : data) {
                if(getBitmapFromMemCache(aData) != null)
                {
                    Log.w("DEMOOO", "Image :: " + aData + " found in cache");
                    count++;
                }
                else
                {
                    Log.e("DEMOOO", "Image not found in cache");
                //addBitmapToMemoryCache(aData, BitmapFactory.decodeStream(mg.open("images/" + aData)));
                }
            }
            Log.e("DEMOOO", "Hit count :: "+count);
            Log.e("DEMOOO", "Total count :: "+data.length);
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            cache.put(key, bitmap);
        }
    }
    public Bitmap getBitmapFromMemCache(String key) {
        return cache.get(key);
    }
}
