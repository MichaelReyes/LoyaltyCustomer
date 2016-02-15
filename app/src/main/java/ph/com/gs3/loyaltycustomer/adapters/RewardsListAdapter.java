package ph.com.gs3.loyaltycustomer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Reward;


/**
 * Created by Michael Reyes on 10/26/2015.
 */
public class RewardsListAdapter extends BaseAdapter {

    private Context context;
    private List<Reward> rewardList;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    public RewardsListAdapter(Context context, List<Reward> rewardList) {
        this.context = context;
        this.rewardList = rewardList;
    }

    @Override
    public int getCount() {
        return rewardList.size();
    }

    @Override
    public Object getItem(int position) {
        return rewardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RewardViewHolder viewHolder;

        Reward reward = (Reward) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_rewards, parent, false);

            viewHolder = new RewardViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (RewardViewHolder) row.getTag();

        viewHolder.tvReward.setText(reward.getReward());
        viewHolder.tvValidFrom.setText(formatter.format(reward.getValid_from()));
        viewHolder.tvValidUntil.setText(formatter.format(reward.getValid_until()));

        return row;
    }

    private static class RewardViewHolder {

        final TextView tvReward;
        final TextView tvValidFrom;
        final TextView tvValidUntil;

        public RewardViewHolder(View view) {
            tvReward = (TextView) view.findViewById(R.id.VR_tvReward);
            tvValidFrom = (TextView) view.findViewById(R.id.VR_tvValidFrom);
            tvValidUntil= (TextView) view.findViewById(R.id.VR_tvValidUntil);
        }

    }

}
