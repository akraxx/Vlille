package rizomm.vlille.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rizomm.vlille.R;
import rizomm.vlille.managers.ToasterManager;
import rizomm.vlille.ui.activities.base.BaseBarActivity;

/**
 * Created by Maximilien on 19/05/2015.
 */
public class SettingsActivity extends BaseBarActivity {

    public static int DEFAULT_NUMBER_ELEMENTS_LIST = 50;
    public static int DEFAULT_PRECISION = 20;
    public static int DEFAULT_LOW_LIMIT = 0;
    public static int DEFAULT_MIDDLE_LIMIT = 5;

    public static final String SETTINGS_NUMBER_ELEMENTS_LIST = "settings_num_elm_list";
    public static final String SETTINGS_PRECISION = "settings_precision";
    public static final String SETTINGS_LOW_LIMIT = "settings_low_limit";
    public static final String SETTINGS_MIDDLE_LIMIT = "settings_middle_limit";

    @InjectView(R.id.settings_number_items_list)
    EditText numberItemsEditText;

    @InjectView(R.id.settings_precision)
    EditText precisionEditText;

    @InjectView(R.id.settings_indicator_low)
    EditText indLowEditText;

    @InjectView(R.id.settings_indicator_middle)
    EditText indMidEditText;

    @InjectView(R.id.settings_indicator_high)
    TextView indHighTextView;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ToasterManager toasterManager;

    private void updateValues() {
        int numElementsList = sharedPreferences.getInt(SETTINGS_NUMBER_ELEMENTS_LIST, DEFAULT_NUMBER_ELEMENTS_LIST);
        int precision = sharedPreferences.getInt(SETTINGS_PRECISION, DEFAULT_PRECISION);
        int lowLimit = sharedPreferences.getInt(SETTINGS_LOW_LIMIT, DEFAULT_LOW_LIMIT);
        int middleLimit = sharedPreferences.getInt(SETTINGS_MIDDLE_LIMIT, DEFAULT_MIDDLE_LIMIT);

        numberItemsEditText.setText(String.valueOf(numElementsList));
        precisionEditText.setText(String.valueOf(precision));
        indLowEditText.setText(String.valueOf(lowLimit));
        indMidEditText.setText(String.valueOf(middleLimit));
        indHighTextView.setText(String.valueOf(middleLimit));
    }

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        updateValues();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_settings;
    }

    @OnClick(R.id.settings_save)
    public void onClick() {
        try {
            Integer newNumberItems = Integer.valueOf(numberItemsEditText.getText().toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(newNumberItems > 0) {
                editor.putInt(SETTINGS_NUMBER_ELEMENTS_LIST, newNumberItems);
            } else {
                throw new IllegalArgumentException(getString(R.string.settings_error_number_elements));
            }

            Integer newPrecision = Integer.valueOf(precisionEditText.getText().toString());
            if(newPrecision > 10) {
                editor.putInt(SETTINGS_PRECISION, newPrecision);
            } else {
                throw new IllegalArgumentException(getString(R.string.settings_error_precision));
            }


            Integer newLowLimit = Integer.valueOf(indLowEditText.getText().toString());
            Integer newMiddleLimit = Integer.valueOf(indMidEditText.getText().toString());

            if(newLowLimit >= 0 && (newMiddleLimit > newLowLimit)) {
                editor.putInt(SETTINGS_LOW_LIMIT, newLowLimit);
                editor.putInt(SETTINGS_MIDDLE_LIMIT, newMiddleLimit);
            } else {
                throw new IllegalArgumentException(getString(R.string.settings_error_limit));
            }

            editor.apply();

            toasterManager.pop(getString(R.string.settings_change_done));
        } catch (Exception e) {
            toasterManager.pop(getString(R.string.settings_change_error));
        }

        updateValues();
    }
}
