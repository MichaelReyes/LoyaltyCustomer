package ph.com.gs3.loyaltycustomer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltycustomer.LoyaltyCustomerApplication;
import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionHasReward;

/**
 * Created by Bryan-PC on 16/03/2016.
 */
public class ClaimRewardListAdapter extends BaseAdapter {

    private Context context;
    private List<TransactionHasReward> transactionHasRewardList;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    public ClaimRewardListAdapter(Context context, List<TransactionHasReward> transactionHasRewardList) {
        this.context = context;
        this.transactionHasRewardList = transactionHasRewardList;
    }

    @Override
    public int getCount() {
        return transactionHasRewardList.size();

    }

    @Override
    public Object getItem(int position) {
        return transactionHasRewardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RewardViewHolder viewHolder;

        TransactionHasReward transactionHasReward = (TransactionHasReward) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_claim_rewards, parent, false);

            viewHolder = new RewardViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (RewardViewHolder) row.getTag();

        RewardDao rewardDao = LoyaltyCustomerApplication.getSession().getRewardDao();

        List<Reward> rewardList =
                rewardDao
                        .queryBuilder()
                        .where(
                                RewardDao.Properties.Id.eq(
                                        transactionHasReward.getReward_id()
                                )
                        ).list();

        for(Reward reward : rewardList){

            viewHolder.tvRewardId.setText(String.valueOf(reward.getId()));
            viewHolder.tvSalesTransactionNumber.setText(transactionHasReward.getSales_transaction_number());
            viewHolder.ctvReward.setText(reward.getReward());

        }

        return row;
    }

    private static class RewardViewHolder {

        final TextView tvRewardId;
        final TextView tvSalesTransactionNumber;
        final CheckedTextView ctvReward;

        public RewardViewHolder(View view) {

            tvRewardId = (TextView) view.findViewById(R.id.VCR_tvRewardId);
            tvSalesTransactionNumber = (TextView) view.findViewById(R.id.VCR_tvSalesTransactionNumber);
            ctvReward = (CheckedTextView) view.findViewById(R.id.VCR_ctvReward);
        }

    }

}
