package ph.com.gs3.loyaltycustomer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.models.values.Transaction;

/**
 * Created by Michael Reyes on 10/27/2015.
 */
public class TransactionListAdapter extends BaseAdapter {

    private Context context;
    private List<Transaction> transactionList;


    public TransactionListAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        historyViewHolder viewHolder;

        Transaction transaction = (Transaction) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_transactions, parent, false);

            viewHolder = new historyViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (historyViewHolder) row.getTag();

        viewHolder.tvTransactionDate.setText(transaction.getTransaction_date());
        //viewHolder.tvStore.setText();

        return row  ;
    }

    private static class historyViewHolder {

        final TextView tvTransactionDate;
        final TextView tvStore;
        final TextView tvAmount;

        public historyViewHolder(View view) {

            tvTransactionDate = (TextView) view.findViewById(R.id.VT_tvTransactionDate);
            tvStore = (TextView) view.findViewById(R.id.VT_tvStore);
            tvAmount = (TextView) view.findViewById(R.id.VT_tvAmount);

        }

    }
}
