package ph.com.gs3.loyaltycustomer.models;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

/**
 * Created by Ervinne Sodusta on 8/17/2015.
 */
public class WifiDirectConnectivityState extends Observable {

    public static final String TAG = WifiDirectConnectivityState.class.getSimpleName();

    private boolean enabled;
    private boolean isServer;
    private InetAddress groupOwnerAddress;
    private String currentDeviceAddress;
    private List<WifiP2pDevice> deviceList;

    private boolean isConnectedToDevice;
    private WifiP2pInfo currentDeviceConnectionInfo;

    // Maintain only one instance of wifi direct connectivity state
    private static WifiDirectConnectivityState instance;

    public static WifiDirectConnectivityState getInstance() {
        if (instance == null) {
            instance = new WifiDirectConnectivityState();
        }

        return instance;
    }

    private WifiDirectConnectivityState() {
        deviceList = new ArrayList<>();
        reset();
    }

    public void reset() {
        deviceList.clear();
        enabled = false;
        isServer = false;
        groupOwnerAddress = null;
        isConnectedToDevice = false;
        currentDeviceConnectionInfo = null;
    }

    public void setDeviceList(Collection<WifiP2pDevice> deviceList) {
        this.deviceList.clear();
        this.deviceList.addAll(deviceList);

        synchronized (this) {
            setChanged();
            notifyObservers();
        }

        Log.v(TAG, "Peers changed, " + deviceList.size() + " peer(s) available");

    }

    public List<WifiP2pDevice> getDeviceList() {
        return deviceList;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        Log.d(TAG, "Wifi direct connectivity is currently " + (enabled ? "enabled" : "disabled"));
        notifyObservers();
    }

    public boolean isServer() {
        return isServer;
    }

    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
//        setChanged();
//        notifyObservers();
    }

    public InetAddress getGroupOwnerAddress() {
        return groupOwnerAddress;
    }

    public void setGroupOwnerAddress(InetAddress groupOwnerAddress) {
        this.groupOwnerAddress = groupOwnerAddress;
    }

    public String getCurrentDeviceAddress() {
        return currentDeviceAddress;
    }

    public void setCurrentDeviceAddress(String currentDeviceAddress) {
        this.currentDeviceAddress = currentDeviceAddress;
        setChanged();
        notifyObservers();
    }

    public WifiP2pInfo getCurrentDeviceConnectionInfo() {
        return currentDeviceConnectionInfo;
    }

    public void setCurrentDeviceConnectionInfo(WifiP2pInfo currentDeviceConnectionInfo) {
        this.currentDeviceConnectionInfo = currentDeviceConnectionInfo;
    }

    public boolean isConnectedToDevice() {
        return isConnectedToDevice;
    }

    public void setIsConnectedToDevice(boolean isConnectedToDevice) {
        this.isConnectedToDevice = isConnectedToDevice;
//        setChanged();
//        notifyObservers();

        if (isConnectedToDevice) {
            notifyPeerDeviceConnectionEstablished();
        } else {
            notifyPeerDeviceConnectionFailed();
        }

    }

    public interface WifiDirectConnectivityStateListener {

        void onDeviceListUpdated(List<WifiP2pDevice> updatedWifiP2pDeviceList);

    }

    public interface WifiDirectPeerConnectivityStateListener {
        void onPeerDeviceConnectionEstablished();

        void onPeerDeviceConnectionFailed();
    }

    //======================================================================================

    private List<WifiDirectPeerConnectivityStateListener> peerConnectivityStateListeners = new ArrayList<>();
    private List<WifiDirectConnectivityStateListener> connectivityStateListeners = new ArrayList<>();

    public synchronized void addPeerConnectivityStateListener(WifiDirectPeerConnectivityStateListener peerConnectivityStateListener) {
        peerConnectivityStateListeners.add(peerConnectivityStateListener);
    }

    public synchronized void deletePeerConnectivityStateListener(WifiDirectPeerConnectivityStateListener peerConnectivityStateListener) {
        peerConnectivityStateListeners.remove(peerConnectivityStateListener);
    }

    public synchronized void addConnectivityStateListener(WifiDirectConnectivityStateListener connectivityStateListener) {
        connectivityStateListeners.add(connectivityStateListener);
    }

    public synchronized void notifyPeerDeviceConnectionEstablished() {
        for (WifiDirectPeerConnectivityStateListener listener : peerConnectivityStateListeners) {
            listener.onPeerDeviceConnectionEstablished();
        }
    }

    public synchronized void notifyPeerDeviceConnectionFailed() {
        for (WifiDirectPeerConnectivityStateListener listener : peerConnectivityStateListeners) {
            listener.onPeerDeviceConnectionFailed();
        }
    }

}
