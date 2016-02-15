package ph.com.gs3.loyaltycustomer.models.protocols;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Ervinne Sodusta on 10/20/2015.
 */
public class AcquireAdvertisementProtocol implements AcquireDataProtocol {

    public static final String TAG = AcquireAdvertisementProtocol.class.getSimpleName();

    private String advertisement;

    @Override
    public void acquire(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {

        String storeName = dataInputStream.readUTF();
        advertisement = dataInputStream.readUTF();

        Log.v(TAG, "Received from " + storeName + ": " + advertisement);

    }

    public String getAdvertisement() {
        return advertisement;
    }
}
