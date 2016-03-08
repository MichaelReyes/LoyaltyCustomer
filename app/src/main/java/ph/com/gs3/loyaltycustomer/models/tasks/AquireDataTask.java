package ph.com.gs3.loyaltycustomer.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import ph.com.gs3.loyaltycustomer.LoyaltyCustomerApplication;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltycustomer.models.protocols.AcquireAdvertisementProtocol;
import ph.com.gs3.loyaltycustomer.models.protocols.AcquireDataProtocol;
import ph.com.gs3.loyaltycustomer.models.protocols.AcquirePurchaseInfoProtocol;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;
import ph.com.gs3.loyaltycustomer.models.values.Announcement;

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

    private Customer customer;

    private AcquirePurchaseInfoTask.AcquirePurchaseInfoListener acquirePurchaseInfoListener;

    public AquireDataTask(int port, Context context) {
        this.port = port;
        this.context = context;

        customer = Customer.getDeviceRetailerFromSharedPreferences(context);
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

            sendCustomerId(dataOutputStream);
            sendTransaction(dataOutputStream);

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

            } else if (protocol instanceof AcquireAdvertisementProtocol) {

                AcquireAdvertisementProtocol acquireAdvertisementProtocol = (AcquireAdvertisementProtocol) protocol;
                Announcement announcement = Announcement.getAnnouncementFromSharedPreference(context);
                announcement.setCurrentAnnouncement(acquireAdvertisementProtocol.getAdvertisement());
                announcement.save(context);

            }

        }
    }

    private void sendClientReadyConfirmation(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CLIENT_READY");
        dataOutputStream.flush();

    }

    private void sendCustomerId(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF(String.valueOf(customer.getCustomerId()));
        dataOutputStream.flush();

    }

    private void sendTransaction(DataOutputStream dataOutputStream) throws  IOException {

        TransactionDao transactionDao = LoyaltyCustomerApplication.getSession().getTransactionDao();
        List<Transaction> transactions = transactionDao.loadAll();
        Gson gson = new Gson();
        String json = gson.toJson(transactions);

        dataOutputStream.writeUTF("TRANSACTIONS");
        dataOutputStream.writeUTF(json);
        dataOutputStream.flush();

    }

    private AcquireDataProtocol getProtocol(DataInputStream dataInputStream) throws IOException {
        String dataType = dataInputStream.readUTF();

        Log.v(TAG, "Determining protocol to use for " + dataType);

        if (DATA_TYPE_PURCHASE_INFO.equals(dataType)) {
            protocol = new AcquirePurchaseInfoProtocol();
        }else if (DATA_TYPE_ADVERTISEMENT.equals(dataType)) {
            protocol = new AcquireAdvertisementProtocol();
        } else {
            Log.e(TAG, "Failed to determine data type: " + dataType);
        }

        return protocol;
    }
}
