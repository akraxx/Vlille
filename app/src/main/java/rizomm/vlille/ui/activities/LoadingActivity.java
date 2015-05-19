package rizomm.vlille.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import rizomm.vlille.R;
import rizomm.vlille.database.DatabaseHelper;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.model.Station;
import rizomm.vlille.model.Vlille;
import rizomm.vlille.ui.activities.base.BaseActivity;

/**
 * Created by Maximilien on 13/05/2015.
 */
public class LoadingActivity extends BaseActivity implements VlilleManager.OnVlilleReceivedListener {
    public static final String FIRST_LAUNCH = "firstLaunch";
    public static final String VLILLE_DATA_CENTER_LAT = "vlille_data_center_lat";
    public static final String VLILLE_DATA_CENTER_LNG = "vlille_data_center_lng";
    public static final String VLILLE_DATA_CENTER_ZOOM = "vlille_data_center_zoom";

    private boolean isFirstLaunch = true;

    @Inject
    VlilleManager vlilleManager;

    @Inject
    SharedPreferences preferences;

    @InjectView(R.id.loading_text)
    TextView loadingTextView;

    private void initFromDatabase() throws SQLException {
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

        List<Station> stations = helper.getDao().queryForAll();

        Vlille vlille = Vlille.builder()
                .centerLatitude(Double.valueOf(preferences.getString(VLILLE_DATA_CENTER_LAT, "0")))
                .centerLongitude(Double.valueOf(preferences.getString(VLILLE_DATA_CENTER_LNG, "0")))
                .zoomLevel(Double.valueOf(preferences.getString(VLILLE_DATA_CENTER_ZOOM, "0")))
                .stations(stations)
                .build();

        vlilleManager.setVlille(vlille);
    }

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        isFirstLaunch = preferences.getBoolean(FIRST_LAUNCH, true);

        if(isFirstLaunch) {
            vlilleManager.initializeStations(this);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_LAUNCH, false);
            editor.apply();
        } else {
            try {
                initFromDatabase();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } catch (SQLException e) {
                Log.e(getClass().getSimpleName(), e.toString());
                vlilleManager.initializeStations(this);
            }

        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_home_loading;
    }

    @Override
    public void onVlilleReceived(Vlille vlille) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VLILLE_DATA_CENTER_LAT, vlille.getCenterLatitude().toString());
        editor.putString(VLILLE_DATA_CENTER_LNG, vlille.getCenterLongitude().toString());
        editor.putString(VLILLE_DATA_CENTER_ZOOM, vlille.getZoomLevel().toString());

        editor.apply();

        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

        for (Station station : vlille.getStations()) {
            try {
                helper.getDao().create(station);
            } catch (SQLException e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
        }

        loadingTextView.setText(getString(R.string.loading_stations));

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
