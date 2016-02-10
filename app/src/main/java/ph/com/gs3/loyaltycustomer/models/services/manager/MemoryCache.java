package ph.com.gs3.loyaltycustomer.models.services.manager;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Michael Reyes on 10/29/2015.
 */
public class MemoryCache {

    private final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    private long size = 0;// current allocated size
    private long limit = 1000000;// max memory in bytes

    public MemoryCache() {
        // use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    // ================================================================================
    // Data modifiers

    public Bitmap get( String id ) {
        try {

            if ( !cache.containsKey(id) ) {
                return null;
            }
            // NullPointerException sometimes happen here
            // http://code.google.com/p/osmdroid/issues/detail?id=78
            return cache.get(id);

        } catch ( NullPointerException ex ) {
            ex.printStackTrace();
            return null;
        }
    }

    public void put( String id, Bitmap bitmap ) {
        try {

            if ( cache.containsKey(id) )
                size -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();

        } catch ( Throwable th ) {
            th.printStackTrace();
        }
    }

    public void clear() {
        try {
            // NullPointerException sometimes happen here
            // http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear();
            size = 0;
        } catch ( NullPointerException ex ) {
            ex.printStackTrace();
        }
    }

    // ================================================================================
    // Status

    /**
     * Ensures that the size of the cache does not exceed the limit
     */
    private void checkSize() {

        if ( size > limit ) {

            // least recently accessed item will be the first one iterated
            Iterator<Map.Entry<String, Bitmap>> iterator = cache.entrySet().iterator();

            while ( iterator.hasNext() ) {
                Map.Entry<String, Bitmap> entry = iterator.next();
                size -= getSizeInBytes(entry.getValue());
                iterator.remove();
                if ( size <= limit )
                    break;
            }

        }

    }

    // ================================================================================
    // Utility Methods

    private long getSizeInBytes( Bitmap bitmap ) {
        if ( bitmap == null ) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    // ================================================================================
    // Getters and Setters

    public void setLimit( long limit ) {
        this.limit = limit;
    }

}
