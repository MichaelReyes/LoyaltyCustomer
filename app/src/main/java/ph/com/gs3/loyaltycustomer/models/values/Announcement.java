package ph.com.gs3.loyaltycustomer.models.values;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Edgar Palmon on 10/19/2015.
 */
public class Announcement {

    public static final String TAG = Announcement.class.getSimpleName();
    private String currentAnnouncement;

    public static Announcement getAnnouncementFromSharedPreference(Context context) {
        Announcement announcement = new Announcement();

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        announcement.currentAnnouncement = settings.getString("ANNOUNCEMENT", "");

        return announcement;

    }

    public void save(Context context) {

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("ANNOUNCEMENT", currentAnnouncement);

        editor.commit();
    }

    public String getCurrentAnnouncement() {
        return currentAnnouncement;
    }

    public void setCurrentAnnouncement(String currentAnnouncement) {
        this.currentAnnouncement = currentAnnouncement;
    }
}
