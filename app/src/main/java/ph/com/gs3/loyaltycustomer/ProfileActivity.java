package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import java.util.HashMap;
import java.util.List;

import ph.com.gs3.loyaltycustomer.fragments.ProfileViewFragment;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.models.api.HttpCommunicator;
import ph.com.gs3.loyaltycustomer.presenters.WifiDirectConnectivityDataPresenter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by GS3-MREYES on 10/1/2015.
 */
public class ProfileActivity extends Activity implements ProfileViewFragment.ProfileViewFragmentEventListener,
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    private ProfileViewFragment profileViewFragment;
    private Customer currentCustomer;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private HttpCommunicator httpCommunicator;
    private Retrofit retrofit;
    private RegisterUserAPI registerUserAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        currentCustomer = Customer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, currentCustomer.getDeviceInfo()
        );

        profileViewFragment = (ProfileViewFragment) getFragmentManager().findFragmentByTag(ProfileViewFragment.TAG);

        if (profileViewFragment == null) {
            profileViewFragment = new ProfileViewFragment();
            getFragmentManager().beginTransaction().add(R.id.container_profile, profileViewFragment, ProfileViewFragment.TAG).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_activity_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onViewReady() {

    }

    @Override
    public void onCancel() {
        finish();
    }

    @Override
    public void onProfileSave(HashMap<String,String> mapProfile) {

        if(!mapProfile.get("NAME").equals("")){
            currentCustomer.setDisplayName(mapProfile.get("NAME"));
            wifiDirectConnectivityDataPresenter.resetDeviceInfo(
                    currentCustomer.getDeviceInfo()
            );
        }
        if(!mapProfile.get("EMAIL").equals("")){
            currentCustomer.setProfileEmail(mapProfile.get("EMAIL"));
        }

        if(!mapProfile.get("BIRTHDATE").equals("")){
            currentCustomer.setProfileBirthDate(mapProfile.get("BIRTHDATE"));
        }

        if(!mapProfile.get("GENDER").equals("")){
            currentCustomer.setProfileGender(mapProfile.get("GENDER"));
        }

        if(!mapProfile.get("ADDRESS").equals("")){
            currentCustomer.setProfileAddress(mapProfile.get("ADDRESS"));
        }

        if(!mapProfile.get("PASSWORD").equals("")){
            currentCustomer.setProfilePassword(mapProfile.get("PASSWORD"));
        }

        currentCustomer.save(this);

        initializeApiCommunicator();

        Log.d(TAG, " CUSTOMER : " + currentCustomer.getDisplayName() + " ~ " +
                currentCustomer.getDeviceId() + " ~ " +
                currentCustomer.getProfileMobileNumber() + " ~ " +
                currentCustomer.getProfileAddress() + " ~ " +
                currentCustomer.getProfileGender() + " ~ " +
                currentCustomer.getProfileBirthDate());

        if(isNetworkAvailable()){

            Call<String> registerUserCall = registerUserAPI.registerUser(
                    currentCustomer.getDisplayName(),
                    currentCustomer.getDeviceId(),
                    currentCustomer.getProfileMobileNumber(),
                    currentCustomer.getProfileAddress(),
                    currentCustomer.getProfileGender(),
                    currentCustomer.getProfileBirthDate());

            registerUserCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {
                    Log.d(TAG, "RESPONSE : " + response.body().toString());
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, "Unable to register user.");
                }
            });

        }

        finish();
    }


    @Override
    public void onCurrentDeviceInfoUpdated() {

    }

    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onConnectionTerminated() {

    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

    }

    private void initializeApiCommunicator(){
        httpCommunicator = new HttpCommunicator();
        retrofit = httpCommunicator.getRetrofit();
        registerUserAPI = retrofit.create(RegisterUserAPI.class);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public interface RegisterUserAPI{

        @FormUrlEncoded
        @POST("/customers")
        Call<String> registerUser(@Field("name") String name,
                                  @Field("device_id") String device_id,
                                  @Field("contact_number") String contact_number,
                                  @Field("location") String location,
                                  @Field("gender") String gender,
                                  @Field("birth_date") String birth_date);

    }
}
