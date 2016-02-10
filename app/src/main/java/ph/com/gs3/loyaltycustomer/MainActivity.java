package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import ph.com.gs3.loyaltycustomer.fragments.MainViewFragment;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.models.services.DiscoverPeersOnBackgroundService;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProductDao;
import ph.com.gs3.loyaltycustomer.models.tasks.AcquirePurchaseInfoTask;
import ph.com.gs3.loyaltycustomer.presenters.WifiDirectConnectivityDataPresenter;


public class MainActivity extends Activity implements MainViewFragment.MainViewFragmentEventListener,
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener,
        AcquirePurchaseInfoTask.AcquirePurchaseInfoListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Customer currentCustomer;

    private MainViewFragment mainViewFragment;

    private boolean viewReady;
    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private Intent discoverPeersOnBackgroundIntent;

    private WifiManager wifiManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentCustomer = Customer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, currentCustomer.getDeviceInfo()
        );

        mainViewFragment = (MainViewFragment) getFragmentManager().findFragmentByTag(MainViewFragment.TAG);

        if ( mainViewFragment == null ) {
            mainViewFragment = new MainViewFragment();
            getFragmentManager().beginTransaction().add(R.id.container, mainViewFragment, MainViewFragment.TAG).commit();
        }

        viewReady = false;

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        getDeviceMobileNumber();
        startBackgroundService();
        //showNotification();

    }

    /*
    private void showNotification() {
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
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You are near our Don Benito's Blumentritt branch. You can contact us on (02)123-45678. "))
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.icon_don_benitos))
                .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);
    }
    */

    private void startBackgroundService() {

        setServiceIntent();

        if ( !isServiceRunning(DiscoverPeersOnBackgroundService.class) ) {
            this.startService(discoverPeersOnBackgroundIntent);

        } else {
            Log.d(TAG, "DiscoverPeersOnBackgroundService SERVICE ALREADY RUNNING!");
        }

    }


    private void setServiceIntent() {

        discoverPeersOnBackgroundIntent = new Intent(this, DiscoverPeersOnBackgroundService.class);

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for ( ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE) ) {
            if ( serviceClass.getName().equals(service.service.getClassName()) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiDirectConnectivityDataPresenter.onResume();

        if (!wifiManager.isWifiEnabled() ) {
            wifiManager.setWifiEnabled(true);
        }
        else{
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiDirectConnectivityDataPresenter.onDestroy();

    }

    @Override
    public void onViewReady() {
        viewReady = true;

    }


    @Override
    public void onRefreshPresence() {

        if (!wifiManager.isWifiEnabled() ) {
            wifiManager.setWifiEnabled(true);
        }
        else{
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        }

        wifiDirectConnectivityDataPresenter.discoverPeers();

        Log.d(TAG, "WIFI ON ? " + wifiManager.isWifiEnabled());

    }

    @Override
    public void onEditProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckHistory() {
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onViewPromo() {



    }

    @Override
    public void onCurrentDeviceInfoUpdated() {

    }

    @Override
    public void onConnectionEstablished() {
        Toast.makeText(MainActivity.this, "Connection established", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionTerminated() {
        Toast.makeText(MainActivity.this, "Connection terminated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

    }

    private void getDeviceMobileNumber(){
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String number = tm.getLine1Number();

        currentCustomer.setProfileMobileNumber(number);
        currentCustomer.save(this);
    }


    @Override
    public void onInfoAcquired(String jsonStringPurchaseInfo) {
        TransactionDao transactionDao =
                LoyaltyCustomerApplication.getInstance().getSession().getTransactionDao();
        TransactionProductDao transactionProductDao =
                LoyaltyCustomerApplication.getInstance().getSession().getTransactionProductDao();



    }
}
