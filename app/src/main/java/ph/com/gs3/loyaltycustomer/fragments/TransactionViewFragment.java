package ph.com.gs3.loyaltycustomer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.adapters.TransactionListAdapter;

/**
 * Created by Michael Reyes on 10/27/2015.
 */
public class TransactionViewFragment extends Fragment {

    public static final String TAG = TransactionViewFragment.class.getSimpleName();

    private ListView lvTransactions;

    private Activity mActivity;

    private TransactionListAdapter transactionListAdapter;
    private TransactionViewFragmentListener transactionViewFragmentListener;

    public static TransactionViewFragment createInstance(TransactionListAdapter transactionListAdapter) {
        TransactionViewFragment transactionViewFragment = new TransactionViewFragment();
        transactionViewFragment.transactionListAdapter = transactionListAdapter;
        return transactionViewFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            transactionViewFragmentListener = (TransactionViewFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement TransactionViewFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);

        lvTransactions = (ListView) rootView.findViewById(R.id.Transaction_lvTransaction);
        lvTransactions.setAdapter(transactionListAdapter);
        lvTransactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        Button bGoBack = (Button) rootView.findViewById(R.id.Transaction_bGoBack);
        bGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                transactionViewFragmentListener.onBackButton();

            }
        });

        transactionViewFragmentListener.onViewReady();

        return rootView;
    }

    public interface TransactionViewFragmentListener {

        void onViewReady();

        void onBackButton();
    }

}
