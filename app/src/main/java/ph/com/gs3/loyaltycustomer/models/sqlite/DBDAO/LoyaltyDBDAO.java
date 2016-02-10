package ph.com.gs3.loyaltycustomer.models.sqlite.DBDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import ph.com.gs3.loyaltystore.models.sqlite.DatabaseHelperDefault;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class LoyaltyDBDAO {

    protected SQLiteDatabase database;
    private DatabaseHelperDefault databaseHelperDefault;
    private Context mContext;

    public LoyaltyDBDAO(Context context) throws SQLException {
        this.mContext = context;
        databaseHelperDefault = DatabaseHelperDefault.getHelper(mContext);
        open();

    }

    public void open() throws SQLException {
        if(databaseHelperDefault == null)
            databaseHelperDefault = DatabaseHelperDefault.getHelper(mContext);
        database = databaseHelperDefault.getWritableDatabase();
    }

	/*public void close() {
		dbHelper.close();
		database = null;
	}*/

}
