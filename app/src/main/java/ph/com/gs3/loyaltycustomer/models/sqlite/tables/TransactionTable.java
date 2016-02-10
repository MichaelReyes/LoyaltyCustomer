package ph.com.gs3.loyaltycustomer.models.sqlite.tables;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class TransactionTable {

    public static final String TABLE_NAME = "tblTransaction";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STORE_ID = "store_id";
    public static final String COLUMN_CUSTOMER_ID = "customer_id";
    public static final String COLUMN_AMOUNT= "amount";
    public static final String COLUMN_TOTAL_DISCOUNT = "total_discount";
    public static final String COLUMN_TRANSACTION_DATE = "transaction_date";
    public static final String COLUMN_IS_SYNCED = "is_synced";

    public static final String CREATION_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID			                + " INTEGER PRIMARY KEY AUTOINCREMENT, "

            + COLUMN_STORE_ID			        + " INTEGER,"
            + COLUMN_CUSTOMER_ID 		        + " INTEGER, "

            + COLUMN_AMOUNT 	                + " REAL, "
            + COLUMN_TOTAL_DISCOUNT 	        + " REAL DEFAULT 0, "

            + COLUMN_TRANSACTION_DATE 	        + " TEXT NOT NULL, "

            + COLUMN_IS_SYNCED 	                + " INTEGER DEFAULT 0 "

            + ");";
}
