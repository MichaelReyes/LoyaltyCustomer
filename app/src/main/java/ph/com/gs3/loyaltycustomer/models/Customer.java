package ph.com.gs3.loyaltycustomer.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/**
 * Created by Ervinne Sodusta on 8/18/2015.
 */
public class Customer {

    public static final String TAG = Customer.class.getSimpleName();

    private String deviceId;
    private String displayName;
    private String profileMobileNumber;
    private String profileEmail;
    private String profileGender;
    private String profileAddress;
    private String profilePassword;
    private String profileBirthDate;
    private int currentPoints;

    public static Customer getDeviceRetailerFromSharedPreferences(Context context) {
        Customer customer = new Customer();

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        customer.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        customer.displayName = settings.getString("DISPLAY_NAME", "");
        customer.currentPoints = settings.getInt("CURRENT_POINTS", 0);

        customer.profileMobileNumber = settings.getString("MOBILE_NUMBER", "");
        customer.profileEmail = settings.getString("EMAIL", "");
        customer.profileGender = settings.getString("GENDER", "");
        customer.profileAddress = settings.getString("ADDRESS", "");
        customer.profilePassword = settings.getString("PASSWORD","");
        customer.profileBirthDate = settings.getString("BIRTHDATE", "");

        return customer;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public void save(Context context) {

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("DISPLAY_NAME", displayName);
        editor.putInt("CURRENT_POINTS", currentPoints);

        editor.putString("MOBILE_NUMBER", profileMobileNumber);
        editor.putString("EMAIL", profileEmail);
        editor.putString("GENDER", profileGender);
        editor.putString("ADDRESS", profileAddress);
        editor.putString("PASSWORD", profilePassword);
        editor.putString("BIRTHDATE", profileBirthDate);

        editor.commit();
    }

    public DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setOwnerDisplayName(displayName);
        deviceInfo.setType(DeviceInfo.Type.CUSTOMER);

        return deviceInfo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public String getProfileGender() {
        return profileGender;
    }

    public void setProfileGender(String profileGender) {
        this.profileGender = profileGender;
    }

    public String getProfileAddress() {
        return profileAddress;
    }

    public void setProfileAddress(String profileAddress) {
        this.profileAddress = profileAddress;
    }

    public String getProfilePassword() {
        return profilePassword;
    }

    public void setProfilePassword(String profilePassword) {
        this.profilePassword = profilePassword;

    }

    public String getProfileBirthDate() {
        return profileBirthDate;
    }

    public void setProfileBirthDate(String profileBirthDate) {
        this.profileBirthDate = profileBirthDate;
    }

    public String getProfileMobileNumber() {
        return profileMobileNumber;
    }

    public void setProfileMobileNumber(String profileMobileNumber) {
        this.profileMobileNumber = profileMobileNumber;
    }
}
