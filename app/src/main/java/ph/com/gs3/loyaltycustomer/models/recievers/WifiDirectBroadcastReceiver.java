package ph.com.gs3.loyaltycustomer.models.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;

/**
 * Created by Ervinne Sodusta on 8/17/2015.
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = WifiDirectBroadcastReceiver.class.getSimpleName();

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    public WifiDirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
    }

    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            connectivityState.setEnabled(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers

            if (wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            if (wifiP2pManager == null) {
                Log.w(TAG, "Connection changed but the manager was null, this change was then ignored");
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // We are connected with the other device, request connection
                // info to find group owner IP
                Log.d(TAG, "Connected to a device");
                wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);

            } else {
                //Disconnected
                Log.d(TAG, "No device connection detected");
            }


        }

        if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            connectivityState.setCurrentDeviceAddress(device.deviceAddress);
        }

    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            WifiDirectConnectivityState.getInstance().setDeviceList(peers.getDeviceList());
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            WifiDirectConnectivityState observableState = WifiDirectConnectivityState.getInstance();

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.

                Log.d(TAG, "This device is currently the server of the group");
                observableState.setGroupOwnerAddress(null);
                observableState.setIsServer(true);
                observableState.setCurrentDeviceConnectionInfo(info);
                observableState.setIsConnectedToDevice(true);   //  triggers change
            } else if (info.groupFormed) {
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.

                Log.d(TAG, "This device is currently a client to a group");
                observableState.setGroupOwnerAddress(info.groupOwnerAddress);
                observableState.setIsServer(false);
                observableState.setCurrentDeviceConnectionInfo(info);
                observableState.setIsConnectedToDevice(true);  //  triggers change
            } else {
                observableState.setGroupOwnerAddress(null);
                observableState.setIsServer(false);
                observableState.setCurrentDeviceConnectionInfo(info);
                observableState.setIsConnectedToDevice(false);
            }
        }
    };

}
