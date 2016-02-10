package ph.com.gs3.loyaltycustomer.models.protocols;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Ervinne Sodusta on 10/20/2015.
 */
public interface AcquireDataProtocol {

    void acquire(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException;

}
