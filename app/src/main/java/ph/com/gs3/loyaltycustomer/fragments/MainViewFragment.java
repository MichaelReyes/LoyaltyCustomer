package ph.com.gs3.loyaltycustomer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ph.com.gs3.loyaltycustomer.R;

/**
 * Created by Michael Reyes on 02/09/2016.
 */
public class MainViewFragment extends Fragment {

    public static final String TAG = MainViewFragment.class.getSimpleName();

    private TextView tvViewPromo;
    private TextView tvAnnouncements;

    private Button bRefreshPresence;
    private Button bEditProfile;
    private Button bHistory;
    private Button bUsePoints;

    private MainViewFragmentEventListener mainViewFragmentEventListener;

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mainViewFragmentEventListener = (MainViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement MainViewFragmentEventListener");
        }

        mActivity = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setAnnouncements(rootView);

        tvViewPromo = (TextView) rootView.findViewById(R.id.Main_tvViewPromo);
        tvViewPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onViewPromo();

            }
        });

        bRefreshPresence = (Button) rootView.findViewById(R.id.Main_bRefreshPresence);
        bRefreshPresence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onRefreshPresence();
            }
        });

        bEditProfile = (Button) rootView.findViewById(R.id.Main_bProfile);
        bEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onEditProfile();
            }
        });

        bHistory = (Button) rootView.findViewById(R.id.Main_bHistory);
        bHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onCheckHistory();

            }
        });

        mainViewFragmentEventListener.onViewReady();

        return rootView;
    }

    private void setAnnouncements(View rootView){

        tvAnnouncements = (TextView) rootView.findViewById(R.id.Main_tvAnnouncements);
        mainViewFragmentEventListener.onSetAnnouncement(tvAnnouncements);
    }



    public interface MainViewFragmentEventListener {

        void onViewReady();

        void onRefreshPresence();

        void onEditProfile();

        void onCheckHistory();

        void onViewPromo();

        void onSetAnnouncement(TextView tvAnnouncements);

    }

}
