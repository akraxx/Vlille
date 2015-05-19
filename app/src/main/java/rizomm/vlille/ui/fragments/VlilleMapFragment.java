package rizomm.vlille.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import rizomm.vlille.R;
import rizomm.vlille.managers.ToasterManager;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.model.Station;
import rizomm.vlille.model.StationDetails;
import rizomm.vlille.model.Vlille;
import rizomm.vlille.ui.activities.StationDetailsActivity;
import rizomm.vlille.ui.fragments.base.TitledFragment;
import rizomm.vlille.ui.holders.MarkerHolder;

/**
 * Created by Maximilien on 22/02/2015.
 */
public class VlilleMapFragment extends TitledFragment implements VlilleManager.OnStationDetailsReceivedListener {

    private GoogleMap map;

    private Map<Integer, MarkerHolder> storedMarkers = new HashMap<>();

    private boolean mapZoomed = false;

    @Inject
    VlilleManager vlilleManager;

    @Inject
    ToasterManager toasterManager;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    @Named("current")
    Provider<Location> currentLocationProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        vlilleManager.addOnStationDetailsReceivedListeners(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        vlilleManager.removeOnStationDetailsReceivedListeners(this);
    }

    @Override
    public View onViewInflated(View view, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        map = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_vlille)).getMap();

        if(!mapZoomed) {
            toasterManager.pop(getString(R.string.bad_location));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(vlilleManager.getVlille().getCenterLatitude(),
                    vlilleManager.getVlille().getCenterLongitude()), 15));
            mapZoomed = true;
        }

        // Move the camera to the current location
        if (map!=null){
            map.setMyLocationEnabled(true);
            Location myLocation = currentLocationProvider.get();
            if(myLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
                mapZoomed = true;
            }

            map.setOnMarkerClickListener(getMarkerClickListener());

            map.setOnCameraChangeListener(getCameraChangeListener());
        }

        vlilleManager.refreshStationsDetails(this);

        return view;
    }

    /**
     * Start the {@link rizomm.vlille.ui.activities.StationDetailsActivity} when click on a marker.
     *
     * @return the listener
     */
    private GoogleMap.OnMarkerClickListener getMarkerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (Map.Entry<Integer, MarkerHolder> entry : storedMarkers.entrySet()) {
                    if(entry.getValue().getMarker().equals(marker)) {
                        Intent intent = new Intent(getActivity(), StationDetailsActivity.class);
                        intent.putExtra("stationId", entry.getKey());
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * When the camera changed, we need to update/create the stations marker which are now visible to the users
     * @return
     */
    private GoogleMap.OnCameraChangeListener getCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                // Update the camera position of the manager
                vlilleManager.setCameraPosition(map.getCameraPosition());

                Vlille vlille = vlilleManager.getVlille();
                VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();

                if(vlille != null) {
                    List<Station> stations = vlille.getStations();
                    for (Station station : stations) {
                        // If the station is on the current view, update it
                        if(visibleRegion.latLngBounds.contains(new LatLng(station.getLatitude(), station.getLongitude()))) {
                            // Create the marker if needed
                            if(!storedMarkers.containsKey(station.getId())) {
                                processMapMarker(station);
                            }

                            // Refresh data
                            vlilleManager.refreshStationsDetails(station.getId(), VlilleMapFragment.this);
                        }
                    }
                }
            }
        };
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_map;
    }

    @Override
    public String getTitle() {
        return "CARTE";
    }

    /**
     * When a station details changed (bikes/attachs), we update it on the map if the station is visible.
     *
     * @param station
     */
    @Override
    public void onStationDetailsReceived(Station station) {
        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();

        // If the station is not on the current view, no need to create / update the marker
        if(!visibleRegion.latLngBounds.contains(new LatLng(station.getLatitude(), station.getLongitude()))) {
            return;
        }

        processMapMarker(station);
    }

    private void processMapMarker(Station station) {
        StationDetails stationDetails = station.getDetails();

        if(stationDetails == null) {
            Log.e(getClass().getSimpleName(), "Details of the station " + station.toString() + " are null");
        }

        Log.i(getClass().getSimpleName(), station.toString());

        MarkerHolder markerHolder;

        // Create the mark if it does not exists
        if(!storedMarkers.containsKey(station.getId())) {

            markerHolder = new MarkerHolder(getActivity(), sharedPreferences);

            storedMarkers.put(station.getId(), markerHolder);

            markerHolder.setMarker(map.addMarker(markerHolder.createView(station)));
        } else {
            markerHolder = storedMarkers.get(station.getId());

            Log.i(getClass().getSimpleName(), markerHolder.getMarker().getTitle());
        }

        // Update it
        markerHolder.updateView(station);
    }

    /**
     * When press the refresh button, refresh all the displayed markers.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.toolbar_button_refresh) {
            toasterManager.pop(getString(R.string.refresh_in_progress), Toast.LENGTH_SHORT);
            VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();

            for (Map.Entry<Integer, MarkerHolder> markerEntry : storedMarkers.entrySet()) {
                LatLng markerPosition = markerEntry.getValue().getMarker().getPosition();
                if(visibleRegion.latLngBounds.contains(markerPosition)) {
                    vlilleManager.refreshStationsDetails(markerEntry.getKey(), this);
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
