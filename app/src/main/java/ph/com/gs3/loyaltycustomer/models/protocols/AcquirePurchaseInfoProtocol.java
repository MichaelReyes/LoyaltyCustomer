package ph.com.gs3.loyaltycustomer.models.protocols;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Ervinne Sodusta on 10/20/2015.
 */
public class AcquirePurchaseInfoProtocol implements AcquireDataProtocol {

    public static final String TAG = AcquirePurchaseInfoProtocol.class.getSimpleName();

    private String jsonStringPurchaseInfo;

    @Override
    public void acquire(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        jsonStringPurchaseInfo = dataInputStream.readUTF();

    }

    public String getJsonStringPurchaseInfo() {
        return jsonStringPurchaseInfo;
    }
}
