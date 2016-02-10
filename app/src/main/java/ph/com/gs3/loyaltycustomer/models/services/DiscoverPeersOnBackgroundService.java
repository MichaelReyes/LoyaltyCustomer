package ph.com.gs3.loyaltycustomer.models.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by GS3-MREYES on 10/13/2015.
 */
public class DiscoverPeersOnBackgroundService extends Service {

    public static final String TAG = DiscoverPeersOnBackgroundService.class.getSimpleName();
    public static final String NAME = DiscoverPeersOnBackgroundService.class.getName();

    public static final String EXTRA_OWNER_DEVICE = "EXTRA_OWNER_DEVICE";

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiP2pDevice device;

    private Context context = this;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Discover peers on background.");

        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);
        //device = (WifiP2pDevice) intent.getExtras().get(EXTRA_OWNER_DEVICE);

        Thread discoverPeersThread = new Thread(new DiscoverPeersThread());
        discoverPeersThread.start();

        //onHandleIntent(intent);
        return START_STICKY;
/*
        if (getState() == 0) {
            writeState(1);
            stopSelf();
        } else {
            writeState(0);
        }
        return START_NOT_STICKY;
        */
    }

    private void writeState(int state) {
        Editor editor = getSharedPreferences("serviceStart", MODE_MULTI_PROCESS)
                .edit();
        editor.clear();
        editor.putInt("normalStart", state);
        editor.commit();
    }

    private int getState() {
        return getApplicationContext().getSharedPreferences("serviceStart",
                MODE_MULTI_PROCESS).getInt("normalStart", 1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DiscoverPeersThread extends Thread {

        @Override
        public void run() {

            while (true) {
                wifiP2pManager.discoverPeers(channel, peerDiscoveryActionListener);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String getDeviceStatus(int deviceStatus){
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default :
                return "Unknown";
        }
    }

    private WifiP2pManager.ActionListener peerDiscoveryActionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            Log.v(TAG, "Successfully discovered peers");
        }

        @Override
        public void onFailure(int reason) {
            String message;

            switch (reason) {
                case WifiP2pManager.BUSY:
                    message = "Failed to discover peers, manager is busy";
                    break;
                case WifiP2pManager.ERROR:
                    message = "There was an error trying to search for peers";
                    break;
                case WifiP2pManager.NO_SERVICE_REQUESTS:
                    message = "Failed to discover peers, no service requests";
                    break;
                case WifiP2pManager.P2P_UNSUPPORTED:
                    message = "Failed to discover peers, peer to peer is not supported on this device";
                    break;
                default:
                    message = "Failed to discover peers, unable to determine why.";
            }

            Log.v(TAG, message);
        }
    };
}
