package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ph.com.gs3.loyaltycustomer.fragments.MainViewFragment;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltycustomer.models.services.DiscoverPeersOnBackgroundService;
import ph.com.gs3.loyaltycustomer.models.services.DownloadUpdatesFromWebIntentService;
import ph.com.gs3.loyaltycustomer.models.services.DownloadUpdatesFromWebService;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProduct;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProductDao;
import ph.com.gs3.loyaltycustomer.models.tasks.AcquirePurchaseInfoTask;
import ph.com.gs3.loyaltycustomer.models.values.Announcement;
import ph.com.gs3.loyaltycustomer.presenters.WifiDirectConnectivityDataPresenter;


public class MainActivity extends Activity implements MainViewFragment.MainViewFragmentEventListener,
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener,
        AcquirePurchaseInfoTask.AcquirePurchaseInfoListener,
        Animation.AnimationListener{

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DATA_TYPE_JSON_SALES = "sales";
    public static final String DATA_TYPE_JSON_SALES_PRODUCT = "sales_product";

    private Customer currentCustomer;

    private MainViewFragment mainViewFragment;

    private boolean viewReady;
    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private TextView tvAnnouncements;
    private int announcementCount = 0;
    private Announcement announcement;
    private String[] splittedAnnouncement;
    private Animation animationTextFromRightToLeft;

    private Intent discoverPeersOnBackgroundIntent;
    private Intent downloadUpdatesFromWebServiceIntent;

    private WifiManager wifiManager;
    private ProgressDialog progressDialog;

    private StoreDao storeDao;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "EEE MMM d HH:mm:ss zzz yyyy");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentCustomer = Customer.getDeviceRetailerFromSharedPreferences(this);
        announcement = Announcement.getAnnouncementFromSharedPreference(this);

        if ( announcement.getCurrentAnnouncement().equals("") ) {
            announcement.setCurrentAnnouncement("Welcome to Don Benito's Cassava Cake and Pichi-pichi.");
        }

        announcement.save(this);

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

        storeDao = LoyaltyCustomerApplication.getInstance().getSession().getStoreDao();

    }


    private void startBackgroundService() {

        setServiceIntent();

        if (!isServiceRunning(DiscoverPeersOnBackgroundService.class)) {
            this.startService(discoverPeersOnBackgroundIntent);

        } else {
            Log.d(TAG, "DiscoverPeersOnBackgroundService SERVICE ALREADY RUNNING!");
        }

        if ( !isServiceRunning(DownloadUpdatesFromWebService.class) ) {
            this.startService(downloadUpdatesFromWebServiceIntent);

        } else {
            Log.d(TAG, "DownloadUpdatesFromWebService SERVICE ALREADY RUNNING!");
        }

    }

    private void setServiceIntent() {

        discoverPeersOnBackgroundIntent = new Intent(this, DiscoverPeersOnBackgroundService.class);
        downloadUpdatesFromWebServiceIntent = new Intent(this, DownloadUpdatesFromWebIntentService.class);

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

        Log.d(TAG,"CUSTOMER INFO : " + currentCustomer.toString());

        /*if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiDirectConnectivityDataPresenter.onDestroy();

    }

    @Override
    public void onViewReady() {
        viewReady = true;

        /*Store store = new Store();
        store.setDevice_id("26:00:ba:16:08:59");
        store.setMac_address("26:00:ba:16:08:59");
        store.setIs_active(1);
        store.setName("Don Benitos Manila");
        store.setCreated_at(new Date());
        store.setUpdated_at(new Date());

        storeDao.insert(store);*/

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

        wifiDirectConnectivityDataPresenter.discoverPeers();

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
    public void onSetAnnouncement(TextView tvAnnouncements) {

        this.tvAnnouncements = tvAnnouncements;
        splittedAnnouncement = announcement.getCurrentAnnouncement().split(";");
        this.tvAnnouncements.setText(splittedAnnouncement[0]);

        this.tvAnnouncements.measure(0, 0);

        int textSize = this.tvAnnouncements.getMeasuredWidth();
        setAnimation(textSize);

        this.tvAnnouncements.startAnimation(animationTextFromRightToLeft);
        announcementCount++;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.Main_hsvAnnouncement);
        horizontalScrollView.scrollTo(0, 0);

    }

    @Override
    public void onAnimationEnd(Animation animation) {


        if ( announcementCount < splittedAnnouncement.length ) {
            tvAnnouncements.setText(splittedAnnouncement[announcementCount]);
        } else {
            tvAnnouncements.setText(splittedAnnouncement[0]);
            announcementCount = 0;
        }

        Announcement checkAnnouncement = Announcement.getAnnouncementFromSharedPreference(this);

        if ( !checkAnnouncement.getCurrentAnnouncement().equals(announcement.getCurrentAnnouncement()) ) {
            announcement.setCurrentAnnouncement(checkAnnouncement.getCurrentAnnouncement());
            splittedAnnouncement = announcement.getCurrentAnnouncement().split(";");
            tvAnnouncements.setText(splittedAnnouncement[0]);
            announcementCount = 0;
        }

        announcementCount++;

        tvAnnouncements.measure(0, 0);
        int textSize = tvAnnouncements.getMeasuredWidth();
        setAnimation(textSize);

        tvAnnouncements.startAnimation(animationTextFromRightToLeft);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void setAnimation(int textSize) {
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        animationTextFromRightToLeft = new TranslateAnimation(width, (textSize * -1), 0, 0);
        animationTextFromRightToLeft.setDuration(15000);
        animationTextFromRightToLeft.setRepeatMode(Animation.RESTART);
        animationTextFromRightToLeft.setRepeatCount(Animation.ABSOLUTE);
        animationTextFromRightToLeft.setAnimationListener(this);


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

        Intent intent= new Intent(this,RewardsActivity.class);
        startActivity(intent);
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
                    transactionProduct.setSales_transaction_number(transactionProductJsonObject.getString("sales_transaction_number"));
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
                transaction.setTransaction_number(transactionJsonObject.getString("transaction_number"));
                transaction.setStore_id(transactionJsonObject.getLong("store_id"));
                transaction.setStore_name(transactionJsonObject.getString("store_name"));
                transaction.setAmount(Float.valueOf(transactionJsonObject.get("amount").toString()));
                transaction.setTransaction_date(
                        formatter.parse(transactionJsonObject.get("transaction_date").toString())
                );

                transactionDao.insert(transaction);

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

        }

        if ( !WifiDirectConnectivityState.getInstance().isServer() ) {

            wifiDirectConnectivityDataPresenter.disconnect(new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Disconnected.");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "Failed to disconnect.");
                }
            });
        }

    }


}
