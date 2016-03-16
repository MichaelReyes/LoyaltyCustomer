package ph.com.gs3.loyaltycustomer.presenters;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ph.com.gs3.loyaltycustomer.models.DeviceInfo;
import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltycustomer.models.recievers.WifiDirectBroadcastReceiver;
import ph.com.gs3.loyaltycustomer.models.tasks.AcquireTransactionsTask;

/**
 * Responsibilities in relation to wifi direct/P2P connectivity is delegated here
 * <p/>
 * Created by Ervinne Sodusta on 8/17/2015.
 */
public class WifiDirectConnectivityDataPresenter implements
        WifiDirectConnectivityState.WifiDirectPeerConnectivityStateListener {

    public static final String TAG = WifiDirectConnectivityDataPresenter.class.getSimpleName();

    private WifiDirectConnectivityPresentationListener wifiDirectConnectivityPresentationListener;

    private WifiDirectBroadcastReceiver wifiDirectBroadcastReceiver;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    private DeviceInfo deviceInfo;

    private Activity sourceActivity;
    private Context context;


    private boolean isPreviouslyConnected;

    public WifiDirectConnectivityDataPresenter(Activity sourceActivity, DeviceInfo deviceInfo) {
        this.sourceActivity = sourceActivity;
        this.context = sourceActivity;
        this.deviceInfo = deviceInfo;


        this.wifiDirectConnectivityPresentationListener = (WifiDirectConnectivityPresentationListener) sourceActivity;

        WifiDirectConnectivityState.getInstance().reset();
        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);

        wifiDirectBroadcastReceiver = new WifiDirectBroadcastReceiver(wifiP2pManager, channel);

        isPreviouslyConnected = false;

        initializeDeviceInfo();

    }

    public void onResume() {

        // register this as an observer of the wifi direct connectivity state
        WifiDirectConnectivityState.getInstance().addPeerConnectivityStateListener(this);

        // start listenting to wifi direct broadcasts
        context.registerReceiver(wifiDirectBroadcastReceiver, wifiDirectBroadcastReceiver.getIntentFilter());

        discoverPeers();
    }

    public void onDestroy() {
        WifiDirectConnectivityState.getInstance().deletePeerConnectivityStateListener(this);
        context.unregisterReceiver(wifiDirectBroadcastReceiver);
    }


    public void discoverPeers() {
        wifiP2pManager.discoverPeers(channel, peerDiscoveryActionListener);

    }

    @Override
    public void onPeerDeviceConnectionEstablished() {
        isPreviouslyConnected = true;
        WifiDirectConnectivityState observableState = WifiDirectConnectivityState.getInstance();
        Log.v(TAG, "Connected to a device");


        /*AquireDataTask aquireDataTask = new AquireDataTask(3001, context);
        aquireDataTask.setAcquirePurchaseInfoListener((AcquirePurchaseInfoTask.AcquirePurchaseInfoListener) sourceActivity);
        aquireDataTask.execute();*/

        AcquireTransactionsTask acquireTransactionsTask =
                new AcquireTransactionsTask(
                    3001,
                    context,
                    (AcquireTransactionsTask.AcquireTransactionsTaskListener) sourceActivity
                );

        acquireTransactionsTask.execute();

        Log.d(TAG, "Connection established");
        wifiDirectConnectivityPresentationListener.onConnectionEstablished();
    }

    @Override
    public void onPeerDeviceConnectionFailed() {
        isPreviouslyConnected = false;
        wifiDirectConnectivityPresentationListener.onConnectionTerminated();
    }

    public void resetDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        initializeDeviceInfo();
    }

    public void disconnect(final WifiP2pManager.ActionListener actionListener) {

        if (wifiP2pManager != null && channel != null) {
            wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && wifiP2pManager != null && channel != null /*&& group.isGroupOwner()*/) {
                        wifiP2pManager.removeGroup(channel, actionListener);
                    }
                }
            });
        }
    }

    public void initializeDeviceInfo() {

        try {
            Method m = wifiP2pManager.getClass().getMethod(
//                    "setSecondaryDeviceType",
                    "setDeviceName",
                    new Class[]{WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class});

            final String serializedDeviceInfo = deviceInfo.serialize();

            Log.d(TAG, "Setting device information: " + serializedDeviceInfo);

            m.invoke(wifiP2pManager, channel, serializedDeviceInfo, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    //Code for Success in changing name
                    Log.d(TAG, "Successfully set device information to: " + serializedDeviceInfo);
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                    Log.e(TAG, "Failed to set device information. Reason number: " + reason);
                }
            });

        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Failed to set device information, the method setSecondaryDeviceType does not exist in the WifiP2pManager class");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.w(TAG, "Failed to serialize device information");
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


    public interface WifiDirectConnectivityPresentationListener {

        void onCurrentDeviceInfoUpdated();

        void onConnectionEstablished();

        void onConnectionTerminated();

        void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices);

    }
}


