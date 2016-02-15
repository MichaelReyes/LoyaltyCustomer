package ph.com.gs3.loyaltycustomer.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import ph.com.gs3.loyaltycustomer.R;
import ph.com.gs3.loyaltycustomer.models.Customer;

/**
 * Created by GS3-MREYES on 10/2/2015.
 */
public class ProfileViewFragment extends Fragment {

    public static final String TAG = ProfileViewFragment.class.getSimpleName();

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvBirthDate;
    private TextView tvGender;
    private TextView tvAddress;

    private EditText etName;
    private EditText etEmail;
    private EditText etBirthDate;
    private Spinner sGender;
    private Spinner sCity;
    private EditText etPassword;
    private EditText etMobileNumber;

    private DatePickerDialog dpBirthDate;

    private Button bSave;
    private Button bCancel;

    private HashMap<String,String> mapProfile = new HashMap<String,String>();

    private Activity mActivity;
    private ProfileViewFragmentEventListener profileViewFragmentEventListener;
    private Customer currentCustomer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            profileViewFragmentEventListener = (ProfileViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement ProfileViewFragmentEventListener");
        }

        setDateTimeField();
        currentCustomer = Customer.getDeviceRetailerFromSharedPreferences(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        setFields(rootView);

        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpBirthDate.show();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapProfile.put("NAME", etName.getText().toString());
                mapProfile.put("EMAIL", etEmail.getText().toString());
                mapProfile.put("BIRTHDATE", etBirthDate.getText().toString());
                mapProfile.put("GENDER", sGender.getSelectedItem().toString());
                mapProfile.put("ADDRESS", sCity.getSelectedItem().toString());
                mapProfile.put("PASSWORD", etPassword.getText().toString());

                profileViewFragmentEventListener.onProfileSave(mapProfile);
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewFragmentEventListener.onCancel();
            }
        });

        return rootView;

    }

    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        dpBirthDate = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etBirthDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void setFields(View rootView){

        tvName =(TextView) rootView.findViewById(R.id.profile_tvName);
        etName = (EditText) rootView.findViewById(R.id.profile_etName);
        if(currentCustomer.getDisplayName() != ""){
            //etName.setEnabled(false);
        }
        etName.setText(currentCustomer.getDisplayName());

        etMobileNumber = (EditText) rootView.findViewById(R.id.profile_etMobileNumber);
        etMobileNumber.setText(currentCustomer.getProfileMobileNumber());

        tvEmail =(TextView) rootView.findViewById(R.id.profile_tvEmail);
        etEmail = (EditText) rootView.findViewById(R.id.profile_etEmail);

        etEmail.setText(currentCustomer.getProfileEmail());

        /*if(currentCustomer.getProfileEmail() != ""){
            setVisibility(tvEmail,etEmail,false);
        }*/

        tvBirthDate =(TextView) rootView.findViewById(R.id.profile_tvBirthDate);
        etBirthDate = (EditText) rootView.findViewById(R.id.profile_etBirthDate);

        etBirthDate.setText(currentCustomer.getProfileBirthDate());

        /*if (currentCustomer.getProfileBirthDate() != "") {
            setVisibility(tvBirthDate, etBirthDate, false);
        }*/

        tvGender =(TextView) rootView.findViewById(R.id.profile_tvGender);
        sGender = (Spinner) rootView.findViewById(R.id.profile_sGender);

        setSpinnerSelection(sGender,R.array.gender_array,currentCustomer.getProfileGender());

        /*if(currentCustomer.getProfileGender() != ""){
            tvGender.setVisibility(View.GONE);
            sGender.setVisibility(View.GONE);
        }*/

        tvAddress =(TextView) rootView.findViewById(R.id.profile_tvAddress);
        sCity  = (Spinner) rootView.findViewById(R.id.profile_sCity);

        setSpinnerSelection(sCity,R.array.city_array,currentCustomer.getProfileAddress());

        /*if(currentCustomer.getProfileAddress() != ""){
            tvAddress.setVisibility(View.GONE);
            sCity.setVisibility(View.GONE);
        }*/

        etPassword = (EditText) rootView.findViewById(R.id.profile_etPassword);
        etPassword.setText(currentCustomer.getProfilePassword());

        bSave = (Button) rootView.findViewById(R.id.profile_bSave);
        bCancel = (Button) rootView.findViewById(R.id.profile_bCancel);
    }

    private void setSpinnerSelection(Spinner spinner,int textArrayResId,String compareValue){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity,
                textArrayResId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            spinner.setSelection(spinnerPosition);
        }

    }

    private void setVisibility(TextView textView, EditText editText, Boolean display){

        textView.setVisibility(display ? View.VISIBLE : View.GONE);
        editText.setVisibility(display ? View.VISIBLE : View.GONE);

    }


    public interface ProfileViewFragmentEventListener {

        void onViewReady();

        void onCancel();

        void onProfileSave(HashMap<String, String> mapProfile);


    }
}


