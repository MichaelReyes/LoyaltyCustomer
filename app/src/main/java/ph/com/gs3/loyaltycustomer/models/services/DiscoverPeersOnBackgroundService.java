package ph.com.gs3.loyaltycustomer.models.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ph.com.gs3.loyaltycustomer.LoyaltyCustomerApplication;
import ph.com.gs3.loyaltycustomer.MainActivity;
import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Store;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.StoreDao;

/**
 * Created by GS3-MREYES on 10/13/2015.
 */
public class DiscoverPeersOnBackgroundService extends Service implements Observer {

    public static final String TAG = DiscoverPeersOnBackgroundService.class.getSimpleName();
    public static final String NAME = DiscoverPeersOnBackgroundService.class.getName();

    public static final String EXTRA_OWNER_DEVICE = "EXTRA_OWNER_DEVICE";

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiP2pDevice device;

    private Context context = this;

    private Customer currentCustomer;

    private StoreDao storeDao;
    private List<Store> stores;

    @Override
    public void onCreate() {
        super.onCreate();
        WifiDirectConnectivityState.getInstance().addObserver(this);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        storeDao = LoyaltyCustomerApplication.getInstance().getSession().getStoreDao();

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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void update(Observable observable, Object data) {

        WifiDirectConnectivityState connectivityState = (WifiDirectConnectivityState) observable;


        Log.d(TAG, "Detected change in the WifiDirectConnectivityState instance");

        // always update the devices regardless of object update
        // TODO: change this ^ implementation

        // filter the devices
        List<WifiP2pDevice> readableDevices = new ArrayList<>();
        for (WifiP2pDevice device : connectivityState.getDeviceList()) {

            readableDevices.add(device);
        }

        searchForStoreDevices(readableDevices);

    }

    private void searchForStoreDevices(List<WifiP2pDevice> wifiP2pDevices){


        for(WifiP2pDevice wifiP2pDevice : wifiP2pDevices){
            if(isMacAddressOfBranch(wifiP2pDevice)){

                showNotification(wifiP2pDevice.deviceName);

            }
        }


    }

    private boolean isMacAddressOfBranch(WifiP2pDevice wifiP2pDevice){

        boolean found = false;

        stores = getStoresByMacAddress(wifiP2pDevice.deviceAddress);

        for(Store store : stores){

            found = true;

        }

        return found;
    }

    private List<Store> getStoresByMacAddress(String deviceAddress){

        return storeDao.queryRaw(
                "WHERE " + StoreDao.Properties.Mac_address.columnName + "=?",
                new String[]{deviceAddress}
        );

    }

    private void showNotification(String branchName) {
        int icon = R.mipmap.icon_don_benitos;

        int mNotificationId = 001;

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        Notification notification = mBuilder.setSmallIcon(icon).setTicker("Hello There!").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Hello There!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "You are near " + branchName ))
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.icon_don_benitos))
                .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);
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
