package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltycustomer.adapters.TransactionListAdapter;
import ph.com.gs3.loyaltycustomer.fragments.TransactionViewFragment;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;


/**
 * Created by GS3-MREYES on 10/4/2015.
 */
public class TransactionActivity extends Activity implements
        TransactionViewFragment.TransactionViewFragmentListener {

    public static final String TAG = TransactionActivity.class.getSimpleName();

    private TransactionViewFragment transactionViewFragment;

    private List<Transaction> transactions;

    private TransactionListAdapter transactionListAdapter;

    private TransactionDao transactionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        transactionViewFragment = (TransactionViewFragment)
                getFragmentManager().findFragmentByTag(TransactionViewFragment.TAG);

        transactions = new ArrayList<>();
        transactionListAdapter = new TransactionListAdapter(this, transactions);

        if (transactionViewFragment == null) {
            transactionViewFragment = new TransactionViewFragment();
            transactionViewFragment = TransactionViewFragment.createInstance(transactionListAdapter);
            getFragmentManager().beginTransaction().add(
                    R.id.container_transaction,
                    transactionViewFragment, TransactionViewFragment.TAG).commit();
        }

        transactionDao = LoyaltyCustomerApplication.getInstance().getSession().getTransactionDao();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onViewReady() {

        transactions = transactionDao.loadAll();

        for(Transaction transaction : transactions){

            Log.d(TAG,Float.toString(transaction.getAmount()));

        }

        Transaction transaction1 = new Transaction();
        transaction1.setTransaction_date(new Date());
        transaction1.setAmount((float) 200);
        transaction1.setStore_id((long) 1);
        transaction1.setStore_sales_id((long) 2);
        transaction1.setTotal_discount((float) 0);

        transactions.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setTransaction_date(new Date());
        transaction2.setAmount((float) 200);
        transaction2.setStore_id((long) 1);
        transaction2.setStore_sales_id((long) 2);
        transaction2.setTotal_discount((float) 0);

        transactions.add(transaction2);

        transactionListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackButton() {
        finish();
    }
}
