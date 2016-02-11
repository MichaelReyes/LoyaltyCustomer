package ph.com.gs3.loyaltycustomer.models.services.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ph.com.gs3.loyaltycustomer.R;

/**
 * Created by Michael Reyes on 10/29/2015.
 */
public class ImageLoader {

    public static final String TAG = ImageLoader.class.getSimpleName();

    private final MemoryCache memoryCache;
    private final PersistentCache persistentCache;

    private final Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    private final ExecutorService executorService;

    private final Handler handler = new Handler();// handler to display images in UI thread

    private int mImageNotAvailableResource = -1;

    private String mDefaultServer;

    public ImageLoader(Context context) {

        memoryCache = new MemoryCache();
        persistentCache = new PersistentCache(context);

        executorService = Executors.newFixedThreadPool(5);

    }

    /**
     * Attaches the image from the specified URL to the image view passed to
     * this method without scaling the image. *Note: the method displayImage(
     * String url, ImageView imageView, int requiredSize ) is recommended over
     * this method so that the images will be scaled accordingly.
     *
     * @param url       - the URL where the image will be fetched.
     * @param imageView - the image view where the image will be attached.
     */
    public void displayImage(String url, ImageView imageView) {

        displayImage(url, imageView, 0);

    }

    /**
     * Attaches the image from the specified URL to the image view passed to
     * this method. The user may specify the required size (should be power of
     * 2) which scales the image to save RAM space.
     *
     * @param url          - the URL where the image will be fetched.
     * @param imageView    - the image view where the image will be attached.
     * @param requiredSize - scale of the image. This should be a value that is power of
     *                     2 (2, 4, 8 and so on...). The larger the number, the smaller
     *                     the image.
     */
    public void displayImage(String url, ImageView imageView, int requiredSize) {

        // if no url is specified, display the image not available image
        if (url == null) {
            imageView.setImageResource(mImageNotAvailableResource);
            return;
        }

        // add default server if specified
        if (mDefaultServer != null) {
            if (url.indexOf("/") == 0) {
                url = mDefaultServer + url;
            } else {
                url = mDefaultServer + "/" + url;
            }
        }

        imageViews.put(imageView, url);

        String id = generateImageMemoryCacheId(url, requiredSize);
        Bitmap bitmap = memoryCache.get(id);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queueImage(url, imageView, requiredSize);
            imageView.setImageResource(R.drawable.loading_img);
        }

    }

    public void copyStream(InputStream is, OutputStream os) throws IOException {

        final int buffer_size = 1024;
        byte[] bytes = new byte[buffer_size];
        for (; ; ) {
            int count = is.read(bytes, 0, buffer_size);
            if (count == -1)
                break;
            os.write(bytes, 0, count);
        }

    }

    /**
     * Removes all resources from the cache.
     */
    public void clearCache() {
        memoryCache.clear();
        persistentCache.clear();
    }

    private void queueImage(String url, ImageView imageView, int requiredSize) {

        ImageViewHolder holder = new ImageViewHolder(url, imageView);
        executorService.submit(new ImageLoadTask(holder, requiredSize));

    }

    // =======================================================================
    // Utility Methods

    private String generateImageMemoryCacheId(String url, int requiredSize) {

        String id = String.valueOf(url.hashCode());

        if (requiredSize > 0) {
            id += "_" + Integer.toString(requiredSize);
        }

        return id;

    }

    private boolean imageViewReused(ImageViewHolder holder) {

        String tag = imageViews.get(holder.imageView);
        return tag == null || !tag.equals(holder.url);

    }

    private Bitmap getBitmap(String url, int requiredSize) {
        File cachedImage = persistentCache.getFile(url);

        // if the image is already downloaded to persistent cache, return it
        Bitmap bitmap = decodeFile(cachedImage, requiredSize);
        if (bitmap != null) {
            return bitmap;
        }

        InputStream is = null;
        OutputStream os = null;

        // the image is not yet downloaded, get it from web
        try {

            bitmap = null;

            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();

            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            conn.setInstanceFollowRedirects(true);
            is = conn.getInputStream();
            os = new FileOutputStream(cachedImage);

            copyStream(is, os);

            conn.disconnect();
            bitmap = decodeFile(cachedImage, requiredSize);

            return bitmap;

        } catch (Throwable ex) {
            if (ex instanceof OutOfMemoryError) {
                memoryCache.clear();
            } else {
//				ex.printStackTrace();
                Log.w(TAG, ex.getMessage());
            }

            return null;

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    os = null;
                }
            }

        }
    }

    private Bitmap decodeFile(File image, int requiredSize) {

        Bitmap bitmap = null;

        try {

            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream originalSizeFIS = new FileInputStream(image);
            BitmapFactory.decodeStream(originalSizeFIS, null, o);
            originalSizeFIS.close();

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = requiredSize;

            int tempWidth = o.outWidth, tempHeight = o.outHeight;
            int scale = 1;

            if (requiredSize > 0) {

                while (true) {

                    if (tempWidth / 2 < REQUIRED_SIZE || tempHeight / 2 < REQUIRED_SIZE) {
                        break;
                    }

                    tempWidth /= 2;
                    tempHeight /= 2;
                    scale *= 2;

                }

            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            FileInputStream sampledSizeFIS = new FileInputStream(image);
            bitmap = BitmapFactory.decodeStream(sampledSizeFIS, null, o2);
            sampledSizeFIS.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return bitmap;
    }

    // =======================================================================
    // Private Models

    private class ImageViewHolder {

        public final String url;
        public final ImageView imageView;

        public ImageViewHolder(String u, ImageView i) {
            url = u;
            imageView = i;
        }

    }

    // =======================================================================
    // Tasks

    /**
     * Task that loads/downloads the image for displaying. This task will run on
     * the background so that the user interface will not be affected.
     *
     * @author ervinne
     */
    private class ImageLoadTask implements Runnable {

        private final ImageViewHolder holder;
        private final int requiredSize;

        public ImageLoadTask(ImageViewHolder holder, int requiredSize) {
            this.holder = holder;
            this.requiredSize = requiredSize;
        }

        @Override
        public void run() {

            if (imageViewReused(holder)) {
                return;
            }

            // image view is new, load the image

            try {

                Bitmap bitmap = getBitmap(holder.url, requiredSize);
                memoryCache.put(generateImageMemoryCacheId(holder.url, requiredSize), bitmap);
                if (imageViewReused(holder)) {
                    return;
                }

                DisplayBitmapTask displayTask = new DisplayBitmapTask(bitmap, holder);
                handler.post(displayTask);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    /**
     * Task for displaying the bitmap to the image view. This task will run on
     * the background so that the user interface will not be affected.
     */
    private class DisplayBitmapTask implements Runnable {

        private final Bitmap bitmap;
        private final ImageViewHolder holder;

        public DisplayBitmapTask(Bitmap bitmap, ImageViewHolder holder) {

            this.bitmap = bitmap;
            this.holder = holder;

        }

        @Override
        public void run() {
            if (imageViewReused(holder)) {
                return;
            }

            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                if (mImageNotAvailableResource != -1) {
                    holder.imageView.setImageResource(mImageNotAvailableResource);
                }
            }

        }
    }

    // =======================================================================
    // Getters and Setters

    public String getDefaultServer() {
        return mDefaultServer;
    }

    public void setDefaultServer(String defaultServer) {
        this.mDefaultServer = defaultServer;
    }

    public int getImageNotAvailableResource() {
        return mImageNotAvailableResource;
    }

    /**
     * Sets the image to be displayed if the image fetched via any URL failed to
     * load.
     *
     * @param imageNotAvailableResource the resource id of the image to be displayed.
     */
    public void setImageNotAvailableResource(int imageNotAvailableResource) {
        this.mImageNotAvailableResource = imageNotAvailableResource;
    }
}
