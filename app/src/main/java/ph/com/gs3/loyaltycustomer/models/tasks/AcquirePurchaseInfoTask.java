package ph.com.gs3.loyaltycustomer.models.tasks;

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

/**
 * Created by Ervinne Sodusta on 8/19/2015.
 */
public class AcquirePurchaseInfoTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = AcquirePurchaseInfoTask.class.getSimpleName();

    private int port;

    private String storeName;
    private float amount;
    private int points;

    private AcquirePurchaseInfoListener acquirePurchaseInfoListener;

    public AcquirePurchaseInfoTask(int port, AcquirePurchaseInfoListener acquirePurchaseInfoListener) {
        this.port = port;
        this.acquirePurchaseInfoListener = acquirePurchaseInfoListener;
    }

    @Override
    protected Void doInBackground(Void... params) {

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
                }catch(ConnectException e){
                    e.printStackTrace();
                    return null;
                }
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (!connectivityState.isServer()) {
                sendClientReadyConfirmation(dataOutputStream);
            }

            awaitAmountAndPoints(dataInputStream);

            Log.v(TAG, "Received: " + amount + " " + points);

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

        Log.d(TAG, "AcquirePurchaseInfoTask ENDED");

        //acquirePurchaseInfoListener.onInfoAcquired(storeName, amount, points);

    }

    private void sendClientReadyConfirmation(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CLIENT_READY");
        dataOutputStream.flush();

    }

    private void awaitAmountAndPoints(DataInputStream dataInputStream) throws IOException {

        storeName = dataInputStream.readUTF();
        String amountString = dataInputStream.readUTF();
        String pointsString = dataInputStream.readUTF();

        amount = Float.parseFloat(amountString);
        points = Integer.parseInt(pointsString);

    }

    public interface AcquirePurchaseInfoListener {

        void onInfoAcquired(String jsonStringPurchaseInfo);

    }

}
