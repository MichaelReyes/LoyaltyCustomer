package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltycustomer.adapters.TransactionListAdapter;
import ph.com.gs3.loyaltycustomer.fragments.TransactionViewFragment;
import ph.com.gs3.loyaltycustomer.models.values.Transaction;


/**
 * Created by GS3-MREYES on 10/4/2015.
 */
public class TransactionActivity extends Activity implements TransactionViewFragment.TransactionViewFragmentListener {

    public static final String TAG = TransactionActivity.class.getSimpleName();

    private TransactionViewFragment transactionViewFragment;

    private List<Transaction> transactions;

    private TransactionListAdapter transactionListAdapter;

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


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onViewReady() {



        transactionListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackButton() {
        finish();
    }
}
