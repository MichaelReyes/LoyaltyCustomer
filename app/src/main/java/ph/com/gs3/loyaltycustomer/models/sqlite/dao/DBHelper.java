package ph.com.gs3.loyaltycustomer.models.sqlite.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Bryan-PC on 09/02/2016.
 */
public class DBHelper {

    public static final String TAG = DBHelper.class.getSimpleName();
    private static final String DB_NAME = "loyaltydb";

    private SQLiteDatabase db = null;
    private DaoSession session = null;
    private Context context;

    public DBHelper(Context context) {
        this.context = context;
    }

    private DaoMaster getMaster() {
        if (db == null) {
            db = getDatabase(DB_NAME, false);
        }
        return new DaoMaster(db);
    }

    public DaoSession getSession(boolean newSession) {
        if (newSession) {
            return getMaster().newSession();
        }
        if (session == null) {
            session = getMaster().newSession();
        }
        return session;
    }

    private synchronized SQLiteDatabase getDatabase(String name, boolean readOnly) {
        String s = "getDB(" + name + ",readonly=" + (readOnly ? "true" : "false") + ")";
        try {
            readOnly = false;
            Log.i(TAG, s);
            SQLiteOpenHelper helper = new MyOpenHelper(context, name, null);
            if (readOnly) {
                return helper.getReadableDatabase();
            } else {
                return helper.getWritableDatabase();
            }
        } catch (Exception ex) {
            Log.e(TAG, s, ex);
            return null;
        } catch (Error err) {
            Log.e(TAG, s, err);
            return null;
        }
    }

    private class MyOpenHelper extends DaoMaster.OpenHelper {
        public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Create DB-Schema (version " + Integer.toString(DaoMaster.SCHEMA_VERSION) + ")");
            super.onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Update DB-Schema to version: " + Integer.toString(oldVersion) + "->" + Integer.toString(newVersion));
            switch (oldVersion) {
                case 1:
//                    db.execSQL("Excecute for migration here");
                default:
                    break;
            }
        }
    }

}
