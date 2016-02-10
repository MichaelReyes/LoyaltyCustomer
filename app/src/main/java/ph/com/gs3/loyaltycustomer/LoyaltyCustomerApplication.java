package ph.com.gs3.loyaltycustomer;

import android.app.Application;

import ph.com.gs3.loyaltycustomer.models.sqlite.dao.DBHelper;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.DaoSession;

/**
 * Created by Bryan-PC on 09/02/2016.
 */
public class LoyaltyCustomerApplication extends Application {

    private static LoyaltyCustomerApplication instance = null;
    private DBHelper dbHelper = new DBHelper(this);

    public static LoyaltyCustomerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static DaoSession getNewSession() {
        return getInstance().dbHelper.getSession(true);
    }

    public static DaoSession getSession() {
        return getInstance().dbHelper.getSession(false);
    }


}
