package ph.com.gs3.loyaltycustomer.models.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionHasReward;

/**
 * Created by Bryan-PC on 17/03/2016.
 */
public class SendClaimedRewardsTask extends AsyncTask<Void,Void,Void> {

    public static final String TAG = SendClaimedRewardsTask.class.getSimpleName();

    private int port;

    private List<TransactionHasReward> claimedRewardsList;

    private SendClaimedRewardTaskListener sendClaimedRewardTaskListener;

    public SendClaimedRewardsTask(int port, List<TransactionHasReward> claimedRewardsList,
                                            SendClaimedRewardTaskListener sendClaimedRewardTaskListener) {
        this.port = port;
        this.claimedRewardsList = claimedRewardsList;
        this.sendClaimedRewardTaskListener = sendClaimedRewardTaskListener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();

        ServerSocket serverSocket = null;
        Socket socket;

        try {
            if (connectivityState.isServer()) {

                serverSocket = new ServerSocket(); // <-- create an unbound socket first
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(port)); // <-- now bind it
                /*serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);*/
                socket = serverSocket.accept();

            } else {
                socket = new Socket();
                socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (!connectivityState.isServer()) {
                sendClientReadyConfirmation(dataOutputStream);
            }

            sendClaimedRewards(dataOutputStream,dataInputStream);

            dataOutputStream.close();
            dataInputStream.close();
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

        sendClaimedRewardTaskListener.onClaimedRewardsSent();
    }

    private void sendClientReadyConfirmation(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CLIENT_READY");
        dataOutputStream.flush();

    }

    private void sendClaimedRewards(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        Gson gson = new Gson();

        dataOutputStream.writeUTF("CLAIMED_REWARDS"); //  notify client that this is a purchase info
        dataOutputStream.writeUTF(gson.toJson(claimedRewardsList));
        dataOutputStream.flush();

        String recieveConfirmationMessage = dataInputStream.readUTF();

        Log.d(TAG,"CONFIRMATION MESSAGE : " + recieveConfirmationMessage);

    }

    public interface SendClaimedRewardTaskListener {

        void onClaimedRewardsSent();

    }
}
