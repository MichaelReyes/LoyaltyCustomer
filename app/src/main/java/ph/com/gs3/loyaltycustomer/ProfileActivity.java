package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.Menu;

import java.util.HashMap;
import java.util.List;

import ph.com.gs3.loyaltycustomer.fragments.ProfileViewFragment;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.presenters.WifiDirectConnectivityDataPresenter;

/**
 * Created by GS3-MREYES on 10/1/2015.
 */
public class ProfileActivity extends Activity implements ProfileViewFragment.ProfileViewFragmentEventListener,
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    private ProfileViewFragment profileViewFragment;
    private Customer currentCustomer;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;


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

        if(mapProfile.get("NAME") != ""){
            currentCustomer.setDisplayName(mapProfile.get("NAME"));
            wifiDirectConnectivityDataPresenter.resetDeviceInfo(
                    currentCustomer.getDeviceInfo()
            );
        }
        if(mapProfile.get("EMAIL") != ""){
            currentCustomer.setProfileEmail(mapProfile.get("EMAIL"));
        }

        if(mapProfile.get("BIRTHDATE") !=""){
            currentCustomer.setProfileBirthDate(mapProfile.get("BIRTHDATE"));
        }

        if(mapProfile.get("GENDER") != ""){
            currentCustomer.setProfileGender(mapProfile.get("GENDER"));
        }

        if(mapProfile.get("ADDRESS") != ""){
            currentCustomer.setProfileAddress(mapProfile.get("ADDRESS"));
        }

        if(mapProfile.get("PASSWORD") !=""){
            currentCustomer.setProfilePassword(mapProfile.get("PASSWORD"));
        }

        currentCustomer.save(this);

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
}
