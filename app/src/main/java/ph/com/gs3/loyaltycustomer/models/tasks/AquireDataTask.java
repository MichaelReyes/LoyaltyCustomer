package ph.com.gs3.loyaltycustomer.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltycustomer.models.protocols.AcquireDataProtocol;
import ph.com.gs3.loyaltycustomer.models.protocols.AcquirePurchaseInfoProtocol;

/**
 * Created by Ervinne Sodusta on 10/20/2015.
 */
public class AquireDataTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = AquireDataTask.class.getSimpleName();

    public static final String DATA_TYPE_PURCHASE_INFO = "PURCHASE_INFO";
    public static final String DATA_TYPE_ADVERTISEMENT = "ADVERTISEMENT";

    private int port;
    private Context context;
    private AcquireDataProtocol protocol;

    private AcquirePurchaseInfoTask.AcquirePurchaseInfoListener acquirePurchaseInfoListener;

    public AquireDataTask(int port, Context context) {
        this.port = port;
        this.context = context;
    }

    public void setAcquirePurchaseInfoListener(AcquirePurchaseInfoTask.AcquirePurchaseInfoListener acquirePurchaseInfoListener) {
        this.acquirePurchaseInfoListener = acquirePurchaseInfoListener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.v(TAG, "Task Started");
        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();

        ServerSocket serverSocket = null;
        Socket socket;

        try {
            if (connectivityState.isServer()) {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
            } else {
                socket = new Socket();

                try {
                    socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
                } catch (ConnectException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (!connectivityState.isServer()) {
                sendClientReadyConfirmation(dataOutputStream);
            }

            protocol = getProtocol(dataInputStream);

            if (protocol != null) {
                protocol.acquire(dataInputStream, dataOutputStream);
            }

            dataInputStream.close();
            dataOutputStream.close();
            socket.close();

            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.d(TAG, "Task ENDED");

        if (protocol != null) {

            if (protocol instanceof AcquirePurchaseInfoProtocol && acquirePurchaseInfoListener != null) {

                AcquirePurchaseInfoProtocol acquirePurchaseInfoProtocol = (AcquirePurchaseInfoProtocol) protocol;
                acquirePurchaseInfoListener.onInfoAcquired(acquirePurchaseInfoProtocol.getJsonStringPurchaseInfo());

            }

        }
    }

    private void sendClientReadyConfirmation(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CLIENT_READY");
        dataOutputStream.flush();

    }

    private AcquireDataProtocol getProtocol(DataInputStream dataInputStream) throws IOException {
        String dataType = dataInputStream.readUTF();

        Log.v(TAG, "Determining protocol to use for " + dataType);

        if (DATA_TYPE_PURCHASE_INFO.equals(dataType)) {
            protocol = new AcquirePurchaseInfoProtocol();
        } else {
            Log.e(TAG, "Failed to determine data type: " + dataType);
        }

        return protocol;
    }
}
