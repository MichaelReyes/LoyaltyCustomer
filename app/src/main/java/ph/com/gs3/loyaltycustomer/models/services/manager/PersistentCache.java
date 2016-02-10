package ph.com.gs3.loyaltycustomer.models.services.manager;

import android.content.Context;

import java.io.File;

/**
 * Created by Michael Reyes on 10/29/2015.
 */
public class PersistentCache {

    public static final String TAG = PersistentCache.class.getSimpleName();

    private final File cacheDir;

    public PersistentCache(Context context) {

        // Check for available external storage first, if not available, use the
        // default cache directory instead
        /*if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), path);
        } else {
            cacheDir = context.getCacheDir();
        }*/

        cacheDir = context.getCacheDir();

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

    }

    public File getFile(String url) {
        // String filename = URLEncoder.encode(url, "UTF-8");
        String filename = String.valueOf(url.hashCode());
        return new File(cacheDir, filename);

    }

    public void clear() {

        File[] files = cacheDir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            file.delete();
        }

    }

}
