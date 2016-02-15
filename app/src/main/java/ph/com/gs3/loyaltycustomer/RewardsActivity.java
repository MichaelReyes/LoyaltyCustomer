package ph.com.gs3.loyaltycustomer;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltycustomer.adapters.RewardsListAdapter;
import ph.com.gs3.loyaltycustomer.fragments.RewardsViewFragment;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoImages;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoImagesDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.RewardDao;


/**
 * Created by Michael Reyes on 02/11/2016.
 */
public class RewardsActivity extends Activity implements RewardsViewFragment.ViewPromoViewFragmentEventListener {

    public static final String TAG = RewardsActivity.class.getSimpleName();

    private RewardsViewFragment rewardsViewFragment;

    private List<Reward> rewards;
    private RewardsListAdapter rewardsListAdapter;
    private RewardDao rewardDao;

    private List<PromoImages> promoImagesList;
    private PromoImagesDao promoImagesDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rewards);

        rewardsViewFragment = (RewardsViewFragment)
                getFragmentManager().findFragmentByTag(RewardsViewFragment.TAG);


        rewards = new ArrayList<>();
        rewardsListAdapter = new RewardsListAdapter(this, rewards);

        promoImagesList = new ArrayList<>();


        if (rewardsViewFragment == null) {
            rewardsViewFragment = new RewardsViewFragment();
            rewardsViewFragment = RewardsViewFragment.createInstance(rewardsListAdapter);
            getFragmentManager().beginTransaction().add(R.id.view_promo_container, rewardsViewFragment, RewardsViewFragment.TAG).commit();
        }

        rewardDao = LoyaltyCustomerApplication.getInstance().getSession().getRewardDao();
        promoImagesDao = LoyaltyCustomerApplication.getInstance().getSession().getPromoImagesDao();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onViewReady() throws JSONException {

        List<Reward> rewardList = rewardDao.loadAll();

        for(Reward reward : rewardList){

            rewards.add(reward);

        }

        List<PromoImages> pImageList = promoImagesDao.loadAll();

        for(PromoImages promoImages : pImageList){

          rewardsViewFragment.setPromoImage(promoImages);

        }

        rewardsListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onGoBack() {

       finish();

    }

}
