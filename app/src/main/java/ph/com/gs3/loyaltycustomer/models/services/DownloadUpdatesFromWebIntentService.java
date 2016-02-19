package ph.com.gs3.loyaltycustomer.models.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
import ph.com.gs3.loyaltycustomer.models.values.Announcement;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;

/**
 * Created by Bryan-PC on 15/02/2016.
 */
public class DownloadUpdatesFromWebIntentService extends IntentService {

    public static final String TAG = DownloadUpdatesFromWebIntentService.class.getSimpleName();

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

    private Announcement announcement;


    public DownloadUpdatesFromWebIntentService() {
        super(DownloadUpdatesFromWebIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        context = this;

        announcement = Announcement.getAnnouncementFromSharedPreference(context);

        constants = new Constants();

        SERVER_URL = constants.SERVER_ADDRESS;

        imageLoader = new ImageLoader(context);

        Log.d(TAG, "Download Updates From Web Service on background.");

        promoImagesDao = LoyaltyCustomerApplication.getInstance().getSession().getPromoImagesDao();
        rewardDao = LoyaltyCustomerApplication.getInstance().getSession().getRewardDao();

        initializeApiCommunicator();

        Thread connectToServerThread = new Thread(new connectToServerThread());
        connectToServerThread.start();


    }

    private void initializeApiCommunicator() {
        httpCommunicator = new HttpCommunicator();
        retrofit = httpCommunicator.getRetrofit();
        downloadUpdatesAPI = retrofit.create(DownloadUpdatesAPI.class);
    }


    private class connectToServerThread extends Thread {

        @Override
        public void run() {

            try {
                Log.d(TAG, "Download Updates From Web Service on background.");

                Call<PromoImages> promoLogoCall = downloadUpdatesAPI.getPromoLogo();
                promoLogoCall.enqueue(new Callback<PromoImages>() {
                    @Override
                    public void onResponse(Response<PromoImages> response, Retrofit retrofit) {

                        try {
                            promoImages = response.body();

                            Log.d(TAG, "PROMO IMAGE RESPONSE BODY : " + promoImages.getImage_file());

                            if (promoImages != null) {

                                promoImagesDao.deleteAll();

                                imageDirectory = promoImages.getImage_file();
                                promoImagesDao.insert(promoImages);

                                announcement.setCurrentAnnouncement(promoImages.getDescription());
                                announcement.save(context);

                            } else {
                                Log.d(TAG, "No promo available.");
                            }
                        } catch (NullPointerException e) {

                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });

                Call<List<Reward>> promoCall = downloadUpdatesAPI.getRewards();
                promoCall.enqueue(new Callback<List<Reward>>() {
                    @Override
                    public void onResponse(Response<List<Reward>> response, Retrofit retrofit) {

                        try {

                            Log.d(TAG, "REWARDS RESPONSE BODY : " + response.body().toString());

                            rewards = response.body();

                            if (rewards != null) {
                                if (!rewards.isEmpty()) {

                                    rewardDao.deleteAll();

                                    for (int i = 0; i < rewards.size(); i++) {
                                        reward = rewards.get(i);
                                        rewardDao.insert(reward);
                                    }
                                }

                            } else {
                                Log.d(TAG, "No rewards available.");
                            }
                        } catch (NullPointerException e) {

                        }


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });

            } catch (JsonSyntaxException | NullPointerException e) {

                Log.e(TAG, "DOWNLOAD UPDATES FROM WEB EXCEPTION : ", e);
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
