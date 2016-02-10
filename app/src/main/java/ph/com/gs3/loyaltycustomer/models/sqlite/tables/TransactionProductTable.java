package ph.com.gs3.loyaltycustomer.models.sqlite.tables;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class TransactionProductTable {

    public static final String TABLE_NAME = "tblTransactionProducts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WEB_ID = "web_id";
    public static final String COLUMN_SALES_ID = "sales_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_SUB_TOTAL = "sub_total";
    public static final String COLUMN_SALE_TYPE = "sale_type";

    public static final String CREATION_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID			                + " INTEGER PRIMARY KEY AUTOINCREMENT, "

            + COLUMN_WEB_ID			            + " INTEGER,"
            + COLUMN_SALES_ID 		            + " INTEGER, "
            + COLUMN_PRODUCT_ID 	            + " INTEGER, "
            + COLUMN_QUANTITY 	                + " INTEGER DEFAULT 0, "

            + COLUMN_SUB_TOTAL 	                + " REAL DEFAULT 0, "

            + COLUMN_SALE_TYPE 	                + " TEXT "

            + ");";
}
