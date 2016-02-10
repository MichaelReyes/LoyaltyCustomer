package ph.com.gs3.loyaltycustomer.models.sqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ph.com.gs3.loyaltycustomer.models.sqlite.DBDAO.LoyaltyDBDAO;
import ph.com.gs3.loyaltycustomer.models.sqlite.tables.TransactionHasRewardTable;
import ph.com.gs3.loyaltycustomer.models.values.TransactionHasReward;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class TransactionHasRewardDAO extends LoyaltyDBDAO {

    private static final String WHERE_ID_EQUALS = TransactionHasRewardTable.COLUMN_SALES_ID
            + " =?";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    public TransactionHasRewardDAO(Context context) throws SQLException {
        super(context);
    }

    public long save(TransactionHasReward transactionHasRewards) {

        ContentValues values = new ContentValues();
        values.put(TransactionHasRewardTable.COLUMN_SALES_ID, transactionHasRewards.getSales_id());
        values.put(TransactionHasRewardTable.COLUMN_REWARDS_ID, transactionHasRewards.getRewards_id());
        return database.insert(TransactionHasRewardTable.TABLE_NAME, null, values);

    }

    public void saveMultipleData(ArrayList<TransactionHasReward> transactionHasRewards) {

        for (int i = 0; i < transactionHasRewards.size(); i++) {

            TransactionHasReward transactionHasReward = transactionHasRewards.get(i);

            ContentValues values = new ContentValues();
            values.put(TransactionHasRewardTable.COLUMN_SALES_ID, transactionHasReward.getSales_id());
            values.put(TransactionHasRewardTable.COLUMN_REWARDS_ID, transactionHasReward.getRewards_id());
            database.insert(TransactionHasRewardTable.TABLE_NAME, null, values);
        }

    }

    public boolean recordExists(int transaction_id) {

        String query = "SELECT * FROM " + TransactionHasRewardTable.TABLE_NAME +
                " WHERE " + TransactionHasRewardTable.COLUMN_ID + "=?";

        Cursor cursor = database.rawQuery(query, new String[]{transaction_id + ""});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public long update(TransactionHasReward transactionHasReward) {
        ContentValues values = new ContentValues();
        values.put(TransactionHasRewardTable.COLUMN_SALES_ID, transactionHasReward.getSales_id());
        values.put(TransactionHasRewardTable.COLUMN_REWARDS_ID, transactionHasReward.getRewards_id());

        long result = database.update(TransactionHasRewardTable.TABLE_NAME, values,
                WHERE_ID_EQUALS,
                new String[]{String.valueOf(transactionHasReward.getId())});
        Log.d("Update Result:", "=" + result);
        return result;

    }

    public int delete(TransactionHasReward transactionHasReward) {
        return database.delete(TransactionHasRewardTable.TABLE_NAME, WHERE_ID_EQUALS,
                new String[]{transactionHasReward.getId() + ""});
    }

    public void clearRecords() {
        database.execSQL("delete from " + TransactionHasRewardTable.TABLE_NAME);
    }

    //USING query() method
    public ArrayList<TransactionHasReward> getTransactionRewardsBySalesId(int salesId) {
        ArrayList<TransactionHasReward> transactionHasRewards = new ArrayList<TransactionHasReward>();

        String sql = "SELECT * FROM " + TransactionHasRewardTable.TABLE_NAME
                + " WHERE " + TransactionHasRewardTable.COLUMN_SALES_ID + " = ?";

        Cursor cursor = database.rawQuery(sql, new String[]{salesId + ""});

        while (cursor.moveToNext()) {
            TransactionHasReward transactionHasReward = new TransactionHasReward();
            transactionHasReward.setId(cursor.getInt(0));
            transactionHasReward.setSales_id(cursor.getInt(1));
            transactionHasReward.setRewards_id(cursor.getInt(2));

            transactionHasRewards.add(transactionHasReward);
        }
        return transactionHasRewards;
    }

}
