package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ph.com.gs3.loyaltycustomer.fragments.MainViewFragment;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.models.services.DiscoverPeersOnBackgroundService;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProduct;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProductDao;
import ph.com.gs3.loyaltycustomer.models.tasks.AcquirePurchaseInfoTask;
import ph.com.gs3.loyaltycustomer.presenters.WifiDirectConnectivityDataPresenter;


public class MainActivity extends Activity implements MainViewFragment.MainViewFragmentEventListener,
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener,
        AcquirePurchaseInfoTask.AcquirePurchaseInfoListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DATA_TYPE_JSON_SALES = "sales";
    public static final String DATA_TYPE_JSON_SALES_PRODUCT = "sales_product";

    private Customer currentCustomer;

    private MainViewFragment mainViewFragment;

    private boolean viewReady;
    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private Intent discoverPeersOnBackgroundIntent;

    private WifiManager wifiManager;
    private ProgressDialog progressDialog;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "EEE MMM d HH:mm:ss zzz yyyy");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentCustomer = Customer.getDeviceRetailerFromSharedPreferences(this);

        progressDialog = new ProgressDialog(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, currentCustomer.getDeviceInfo()
        );

        mainViewFragment = (MainViewFragment) getFragmentManager().findFragmentByTag(MainViewFragment.TAG);

        if (mainViewFragment == null) {
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

        if (!isServiceRunning(DiscoverPeersOnBackgroundService.class)) {
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
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiDirectConnectivityDataPresenter.onResume();

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else {
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

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        }

        onResetWifi();

        wifiDirectConnectivityDataPresenter.discoverPeers();

        Log.d(TAG, "WIFI ON ? " + wifiManager.isWifiEnabled());

    }

    private void onResetWifi() {

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait while refreshing your presence...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        hideDialogAfter(7000);


    }

    protected void hideDialogAfter(int hideAfterMillis) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (progressDialog.isShowing()) {
                            progressDialog.hide();
                        }
                    }
                },
                hideAfterMillis);
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

    private void getDeviceMobileNumber() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String number = tm.getLine1Number();

        currentCustomer.setProfileMobileNumber(number);
        currentCustomer.save(this);
    }


    @Override
    public void onInfoAcquired(String jsonStringPurchaseInfo) {

        if (!jsonStringPurchaseInfo.equals("")) {


            TransactionDao transactionDao =
                    LoyaltyCustomerApplication.getInstance().getSession().getTransactionDao();
            TransactionProductDao transactionProductDao =
                    LoyaltyCustomerApplication.getInstance().getSession().getTransactionProductDao();

            try {
                JSONObject jsonObject = new JSONObject(jsonStringPurchaseInfo);

                JSONArray transactionProductsJsonArray = jsonObject.getJSONArray(DATA_TYPE_JSON_SALES_PRODUCT);
                JSONObject transactionJsonObject = jsonObject.getJSONObject(DATA_TYPE_JSON_SALES);

                JSONObject transactionProductJsonObject;

                for (int i = 0; i < transactionProductsJsonArray.length(); i++) {

                    transactionProductJsonObject = transactionProductsJsonArray.getJSONObject(i);

                    TransactionProduct transactionProduct = new TransactionProduct();
                    transactionProduct.setSales_id(transactionProductJsonObject.getLong("sales_id"));
                    transactionProduct.setProduct_id(transactionProductJsonObject.getLong("product_id"));
                    transactionProduct.setProduct_name(transactionProductJsonObject.getString("product_name"));
                    transactionProduct.setUnit_cost(Float.valueOf(transactionProductJsonObject.get("unit_cost").toString()));
                    transactionProduct.setQuantity(transactionProductJsonObject.getInt("quantity"));
                    transactionProduct.setSku(transactionProductJsonObject.getString("sku"));
                    transactionProduct.setSub_total(Float.valueOf(transactionProductJsonObject.get("sub_total").toString()));
                    transactionProduct.setSale_type(transactionProductJsonObject.getString("sale_type"));

                    transactionProductDao.insert(transactionProduct);

                }


                Transaction transaction = new Transaction();
                transaction.setStore_sales_id(transactionJsonObject.getLong("id"));
                transaction.setStore_id(transactionJsonObject.getLong("store_id"));
                transaction.setAmount(Float.valueOf(transactionJsonObject.get("amount").toString()));
                transaction.setTransaction_date(
                        formatter.parse(transactionJsonObject.get("transaction_date").toString())
                );

                transactionDao.insert(transaction);

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

        }

    }
}
