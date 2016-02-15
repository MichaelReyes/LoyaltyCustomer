package ph.com.gs3.loyaltycustomer.models.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.util.List;

import ph.com.gs3.loyaltycustomer.LoyaltyCustomerApplication;
import ph.com.gs3.loyaltycustomer.globals.Constants;
import ph.com.gs3.loyaltycustomer.models.api.HttpCommunicator;
import ph.com.gs3.loyaltycustomer.models.services.manager.ImageLoader;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoImages;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoImagesDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.RewardDao;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;

/**
 * Created by Michael Reyes on 10/28/2015.
 */
public class DownloadUpdatesFromWebService extends Service {

    public static final String TAG = DownloadUpdatesFromWebService.class.getSimpleName();

    private ImageLoader imageLoader;

    private Constants constants;
    private String SERVER_URL;

    private String imageDirectory;

    private Context context;

    private List<Reward> rewards;
    private Reward reward;
    private RewardDao rewardDao;

    private PromoImages promoImages;
    private PromoImagesDao promoImagesDao;

    private HttpCommunicator httpCommunicator;
    private Retrofit retrofit;
    private DownloadUpdatesAPI downloadUpdatesAPI;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = this;
        constants = new Constants();

        SERVER_URL = constants.SERVER_ADDRESS;

        imageLoader = new ImageLoader(context);

        Log.d(TAG, "Download Updates From Web Service on background.");

        promoImagesDao = LoyaltyCustomerApplication.getInstance().getSession().getPromoImagesDao();
        rewardDao = LoyaltyCustomerApplication.getInstance().getSession().getRewardDao();

        initializeApiCommunicator();

        Thread connectToServerThread = new Thread(new connectToServerThread());
        connectToServerThread.start();

        //return START_STICKY;

        if (getState() == 0) {
            writeState(1);
            stopSelf();
        } else {
            writeState(0);
        }
        return START_NOT_STICKY;
    }

    private void initializeApiCommunicator(){
        httpCommunicator = new HttpCommunicator();
        retrofit = httpCommunicator.getRetrofit();
        downloadUpdatesAPI = retrofit.create(DownloadUpdatesAPI.class);
    }

    private void writeState(int state) {
        SharedPreferences.Editor editor = getSharedPreferences("serviceStart", MODE_MULTI_PROCESS)
                .edit();
        editor.clear();
        editor.putInt("normalStart", state);
        editor.commit();
    }

    private int getState() {
        return getApplicationContext().getSharedPreferences("serviceStart",
                MODE_MULTI_PROCESS).getInt("normalStart", 1);
    }

    private class connectToServerThread extends Thread {

        @Override
        public void run() {

            while (true) {

                try {
                    Log.d(TAG, "Download Updates From Web Service on background.");

                    Call<PromoImages> promoLogoCall = downloadUpdatesAPI.getPromoLogo();
                    promoLogoCall.enqueue(new Callback<PromoImages>() {
                        @Override
                        public void onResponse(Response<PromoImages> response, Retrofit retrofit) {


                            promoImages = response.body();

                            Log.d(TAG,"PROMO IMAGE RESPONSE BODY : " + promoImages.getImage_file());

                           if(promoImages != null) {

                               promoImagesDao.deleteAll();

                               imageDirectory = promoImages.getImage_file();
                               promoImagesDao.insert(promoImages);

                           }else{
                               Log.d(TAG, "No promo available.");
                           }

                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    });

                    Log.d(TAG,"dsadasda");

                    Call<List<Reward>> promoCall = downloadUpdatesAPI.getRewards();
                    promoCall.enqueue(new Callback<List<Reward>>() {
                        @Override
                        public void onResponse(Response<List<Reward>> response, Retrofit retrofit) {

                            Log.d(TAG,"REWARDS RESPONSE BODY : " + response.body().toString());

                           rewards = response.body();

                            if(rewards != null){
                                if(!rewards.isEmpty()){

                                    rewardDao.deleteAll();

                                    for(int i =0;i< rewards.size();i++){
                                        reward = rewards.get(i);

                                        rewardDao.insert(reward);

                                    }
                                }

                            }else{
                                Log.d(TAG,"No rewards available.");
                            }
                        }
                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    });

                    Thread.sleep(10000);
                } catch (InterruptedException | JsonSyntaxException e) {

                    Log.e(TAG, "DOWNLOAD UPDATES FROM WEB EXCEPTION : ", e);
                }
            }
        }
    }

    public interface DownloadUpdatesAPI {

        @GET("/promo/json")
        Call<PromoImages> getPromoLogo();

        //"/rewards/1"
        @GET("/rewards?dataType=json&lastUpdate=")
        Call<List<Reward>> getRewards();



    }

}
