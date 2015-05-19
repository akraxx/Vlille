package rizomm.vlille.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import rizomm.vlille.R;
import rizomm.vlille.database.DatabaseHelper;
import rizomm.vlille.managers.ToasterManager;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.model.Station;
import rizomm.vlille.model.StationDetails;
import rizomm.vlille.ui.activities.base.BaseBarActivity;
import rizomm.vlille.utils.ColorConverter;
import rizomm.vlille.utils.DistanceConverter;

/**
 * Created by Maximilien on 09/03/2015.
 */
public class StationDetailsActivity extends BaseBarActivity implements VlilleManager.OnStationDetailsReceivedListener {

    @Inject
    VlilleManager vlilleManager;

    @Inject
    ToasterManager toasterManager;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    @Named("current")
    Provider<Location> currentLocationProvider;

    @InjectView(R.id.station_details_name)
    TextView stationNameTextView;

    @InjectView(R.id.station_details_address)
    TextView stationAddressTextView;

    @InjectView(R.id.station_details_attachs)
    TextView stationAttachsTextView;

    @InjectView(R.id.station_details_vlille)
    TextView stationVlilleTextView;

    @InjectView(R.id.station_details_distance)
    TextView stationDistanceTextView;

    @InjectView(R.id.station_details_cb)
    View stationCbView;

    @InjectView(R.id.vlille_list_item_status_offline)
    View statusView;

    private int stationId;

    private Menu menu;

    private void updateView(Station station) {
        stationNameTextView.setText(station.getName());
        StationDetails details = station.getDetails();
        if(station.isBookmarked()) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_bookmark_on));
        } else {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_bookmark_off));
        }

        if(station.isCbAllowed()) {
            stationCbView.setVisibility(View.VISIBLE);
        } else {
            stationCbView.setVisibility(View.INVISIBLE);
        }

        stationDistanceTextView.setText(DistanceConverter.computeDistance(station.getDistance()));

        if(details != null) {
            stationAddressTextView.setText(details.getAddress());
            stationAttachsTextView.setText(details.getAttachs().toString());
            stationAttachsTextView.setTextColor(ColorConverter.getColorByNumber(details.getAttachs(), sharedPreferences));
            stationVlilleTextView.setText(details.getBikes().toString());
            stationVlilleTextView.setTextColor(ColorConverter.getColorByNumber(details.getBikes(), sharedPreferences));

            if(details.getStatus()) {
                statusView.setVisibility(View.VISIBLE);
            } else {
                statusView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        stationId = getIntent().getIntExtra("stationId", 0);

        vlilleManager.refreshStationDistance(currentLocationProvider.get());
        vlilleManager.refreshStationsDetails(stationId, this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_station_details;
    }

    @Override
    public void onStationDetailsReceived(Station station) {
        updateView(station);
    }

    @OnClick(R.id.locate_station)
    public void onClick() {
        Station station = vlilleManager.getStationById(stationId);
        String uri = String.format(Locale.FRANCE, "http://maps.google.com/maps?daddr=%s",
                station.getDetails().getAddress());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.toolbar_button_refresh) {
            vlilleManager.refreshStationsDetails(stationId, this);
            toasterManager.pop(getString(R.string.refresh_in_progress), Toast.LENGTH_SHORT);
        } else if (item.getItemId() == R.id.toolbar_button_bookmark) {
            Station station = vlilleManager.getStationById(stationId);
            vlilleManager.bookmarkAction(station);

            try {
                OpenHelperManager.getHelper(this, DatabaseHelper.class).getDao().update(station);
            } catch (SQLException e) {
                Log.e(getClass().getSimpleName(), "Impossible to update database " + e.toString());
            }

            updateView(station);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_details_main, menu);

        updateView(vlilleManager.getStationById(stationId));
        return true;
    }
}
