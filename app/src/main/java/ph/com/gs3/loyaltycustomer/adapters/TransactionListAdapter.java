package ph.com.gs3.loyaltycustomer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;

/**
 * Created by Michael Reyes on 10/27/2015.
 */
public class TransactionListAdapter extends BaseAdapter {

    private Context context;
    private List<Transaction> transactions;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);


    public TransactionListAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        transactionViewHolder viewHolder;

        Log.d("TEST","WEAWEaweaw");

        Transaction transaction = (Transaction) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_transactions, parent, false);

            viewHolder = new transactionViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (transactionViewHolder) row.getTag();

        Log.d("TLISTADAPTER : " ,  formatter.format(transaction.getTransaction_date()));

        viewHolder.tvTransactionDate.setText(formatter.format(transaction.getTransaction_date()));
        viewHolder.tvStore.setText(Long.toString(transaction.getStore_id()));
        viewHolder.tvAmount.setText(Float.toString(transaction.getAmount()));

        return row;
    }

    private static class transactionViewHolder {

        final TextView tvTransactionDate;
        final TextView tvStore;
        final TextView tvAmount;

        public transactionViewHolder(View view) {

            tvTransactionDate = (TextView) view.findViewById(R.id.VT_tvTransactionDate);
            tvStore = (TextView) view.findViewById(R.id.VT_tvStore);
            tvAmount = (TextView) view.findViewById(R.id.VT_tvAmount);

        }

    }
}
