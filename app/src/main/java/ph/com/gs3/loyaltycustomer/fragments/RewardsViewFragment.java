package ph.com.gs3.loyaltycustomer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;

import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.adapters.RewardsListAdapter;
import ph.com.gs3.loyaltycustomer.globals.Constants;
import ph.com.gs3.loyaltycustomer.models.services.manager.ImageLoader;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoImages;

/**
 * Created by Michael Reyes on 02/11/2016.
 */
public class RewardsViewFragment extends Fragment {

    public static final String TAG = RewardsViewFragment.class.getSimpleName();

    private ViewPromoViewFragmentEventListener viewPromoViewFragmentEventListener;

    private ListView lvRewardsList;
    private RewardsListAdapter rewardsListAdapter;

    private Button bBack;

    private ImageView ivPromo;
    private ImageLoader imageLoader;

    private Activity viewPromoActivity;

    private Constants constants;

    public static RewardsViewFragment createInstance(RewardsListAdapter rewardsListAdapter) {
        RewardsViewFragment rewardsViewFragment = new RewardsViewFragment();
        rewardsViewFragment.rewardsListAdapter = rewardsListAdapter;
        return rewardsViewFragment;
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        viewPromoActivity = activity;

        try {
            viewPromoViewFragmentEventListener = (ViewPromoViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement ViewPromoViewFragmentEventListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_rewards, container, false);

        imageLoader = new ImageLoader(viewPromoActivity);
        constants= new Constants();

        lvRewardsList = (ListView) rootView.findViewById(R.id.VR_lvRewards);
        lvRewardsList.setAdapter(rewardsListAdapter);
       
        ivPromo = (ImageView) rootView.findViewById(R.id.VR_ivPromo);

        bBack = (Button) rootView.findViewById(R.id.VR_bGoBack);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPromoViewFragmentEventListener.onGoBack();
            }
        });

        try {
            viewPromoViewFragmentEventListener.onViewReady();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rootView;
    }

    public void setPromoImage(PromoImages promoImage){

        String imageDirectory = promoImage.getImage_file();

        imageLoader.setDefaultServer(constants.SERVER_ADDRESS);

        imageLoader.displayImage(imageDirectory,ivPromo,0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;


    }

    public interface ViewPromoViewFragmentEventListener {

        void onViewReady() throws JSONException;

        void onGoBack();

    }

}
