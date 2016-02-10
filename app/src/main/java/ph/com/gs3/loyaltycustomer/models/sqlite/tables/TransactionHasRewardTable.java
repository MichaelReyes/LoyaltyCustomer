package ph.com.gs3.loyaltycustomer.models.sqlite.tables;

/**
 * Created by Michael Reyes on 10/30/2015.
 */
public class TransactionHasRewardTable {


    public static final String TABLE_NAME = "tblTransactionHasRewards";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SALES_ID = "sales_id";
    public static final String COLUMN_REWARDS_ID = "rewards_id";

    public static final String CREATION_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SALES_ID       + " INTEGER, "
            + COLUMN_REWARDS_ID     + " INTEGER "
            + ");";

}
