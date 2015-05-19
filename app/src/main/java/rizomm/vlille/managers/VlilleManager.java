package rizomm.vlille.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.CameraPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rizomm.vlille.R;
import rizomm.vlille.model.Station;
import rizomm.vlille.model.StationDetails;
import rizomm.vlille.model.Vlille;
import rizomm.vlille.services.VlilleService;
import rizomm.vlille.ui.activities.SettingsActivity;
import rizomm.vlille.utils.StationsComparator;

/**
 * Created by Maximilien on 08/03/2015.
 */
@Singleton
public class VlilleManager {
    public static int LIMIT_NEAR_STATION;

    private final VlilleService vlilleService;

    private final ToasterManager toasterManager;

    private final Context context;

    private final SharedPreferences preferences;

    @Getter
    @Setter
    private Vlille vlille;

    private List<OnStationDetailsReceivedListener> onStationDetailsReceivedListeners = new ArrayList<>();

    private List<OnStationDistanceChangedListener> onStationDistanceChangedListeners = new ArrayList<>();

    public List<Station> lastOrderedStationsByDistance;

    private Location lastPosition;

    @Getter
    @Setter
    private CameraPosition cameraPosition;

    @Inject
    public VlilleManager(VlilleService vlilleService, ToasterManager toasterManager, Context context, SharedPreferences preferences) {
        this.vlilleService = vlilleService;
        this.toasterManager = toasterManager;
        this.context = context;
        this.preferences = preferences;
    }

    public void addOnStationDetailsReceivedListeners(@NonNull OnStationDetailsReceivedListener onStationDetailsReceivedListener) {
        onStationDetailsReceivedListeners.add(onStationDetailsReceivedListener);
    }

    public void removeOnStationDetailsReceivedListeners(@NonNull OnStationDetailsReceivedListener onStationDetailsReceivedListener) {
        if(onStationDetailsReceivedListeners.contains(onStationDetailsReceivedListener)) {
            onStationDetailsReceivedListeners.remove(onStationDetailsReceivedListener);
        }
    }

    public void addOnStationDistanceChangedListeners(@NonNull OnStationDistanceChangedListener onStationDistanceChangedListener) {
        onStationDistanceChangedListeners.add(onStationDistanceChangedListener);
    }

    public void removeOnStationDistanceChangedListeners(@NonNull OnStationDistanceChangedListener onStationDistanceChangedListener) {
        if(onStationDistanceChangedListeners.contains(onStationDistanceChangedListener)) {
            onStationDistanceChangedListeners.remove(onStationDistanceChangedListener);
        }
    }

    public int getDistancePrecision() {
        return preferences.getInt(SettingsActivity.SETTINGS_PRECISION, SettingsActivity.DEFAULT_PRECISION);
    }

    public void initializeStations(final OnVlilleReceivedListener listener) {
        vlilleService.stations(new Callback<Vlille>() {

            @Override
            public void success(Vlille vlilleCallback, Response response) {

                vlille = vlilleCallback;

                listener.onVlilleReceived(vlille);

                Log.i(getClass().getSimpleName(), vlilleCallback.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(getClass().getSimpleName(), error.toString());
                // Retry
                initializeStations(listener);
            }
        });
    }

    private void getStationDetails(final Station station, final OnStationDetailsReceivedListener listener) {
        vlilleService.station(station.getId(), new Callback<StationDetails>() {

            @Override
            public void success(StationDetails stationDetails, Response response) {
                if(station.getId() == 6) {
                    stationDetails.setStatus(true);
                }

                // Check if there are changes
                if(!(station.getDetails() != null && station.getDetails().equals(stationDetails))) {
                    station.setDetails(stationDetails);
                    stationDetails.setLastDataReceived(new Date());

                    // Notify every listeners
                    for (OnStationDetailsReceivedListener onStationDetailsReceivedListener : onStationDetailsReceivedListeners) {
                        if(onStationDetailsReceivedListener != listener) {
                            onStationDetailsReceivedListener.onStationDetailsReceived(station);
                        }
                    }

                    // Notify the given listener
                    if(listener != null) {
                        listener.onStationDetailsReceived(station);
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(getClass().getSimpleName(), error.toString());
                // Retry
                getStationDetails(station, listener);
            }
        });
    }

    /**
     * Check the location
     *
     * @param myLocation Current location
     * @return
     */
    public Location locationEnabled(Location myLocation) {
        if(myLocation != null) {
            return myLocation;
        } else if(cameraPosition != null) {
            myLocation = new Location("Map location");
            myLocation.setLatitude(cameraPosition.target.latitude);
            myLocation.setLongitude(cameraPosition.target.longitude);

            toasterManager.pop("Utilisation de la position centrale de la carte", Toast.LENGTH_LONG);
            return myLocation;
        } else {
            return null;
        }
    }

    @DebugLog
    public void refreshStationDistance(Location myLocation) {
        myLocation = locationEnabled(myLocation);

        if(myLocation == null) {
            toasterManager.pop(context.getString(R.string.bad_location));
            return;
        }

        float[] distanceWithLastLocation = new float[1];
        Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), lastPosition.getLatitude(), lastPosition.getLongitude(), distanceWithLastLocation);

        // Check if the position of the device change
        if(distanceWithLastLocation[0] < getDistancePrecision()) {
            return;
        }

        // Check if at least one distance changed
        boolean atleastOneDistanceChanged = false;
        for (Station station : vlille.getStations()) {

            float[] stationDistanceWithLastLocation = new float[1];
            Location.distanceBetween(station.getLatitude(), station.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude(), stationDistanceWithLastLocation);

            station.setDistance(stationDistanceWithLastLocation[0]);

            if(!atleastOneDistanceChanged && stationDistanceWithLastLocation[0] >= getDistancePrecision()) {
                atleastOneDistanceChanged = true;
            }
        }

        if(atleastOneDistanceChanged) {
            for (OnStationDistanceChangedListener listener : onStationDistanceChangedListeners) {
                listener.onStationDistanceChanged();
            }
        }

    }

    /**
     * Refresh a specific station
     * @param id id of the station
     * @param listener listener to call back
     */
    public void refreshStationsDetails(Integer id, final OnStationDetailsReceivedListener listener) {
        for (final Station station : vlille.getStations()) {

            if(station.getId() == id) {
                getStationDetails(station, listener);
                return;
            }
        }
    }

    /**
     * Get all stations near the {@code myLocation}. Specify the {@code distance} in meters.
     *
     *
     * @param myLocation
     *
     * @return
     */
    public List<Station> getStationsNear(Location myLocation, int limit) {
        final Location myFinalLocation = locationEnabled(myLocation);

        if(myFinalLocation == null) {
            if(vlille != null) {
                return vlille.getStations().subList(0, limit);
            } else {
                return new ArrayList<>();
            }
        }

        // If the position did not change
        if(lastOrderedStationsByDistance != null && lastPosition != null) {
            float[] distanceWithLastLocation = new float[1];
            Location.distanceBetween(myFinalLocation.getLatitude(), myFinalLocation.getLongitude(), lastPosition.getLatitude(), lastPosition.getLongitude(), distanceWithLastLocation);

            if(distanceWithLastLocation[0] < getDistancePrecision()) {
                return lastOrderedStationsByDistance.subList(0, limit);
            }
        }

        // If vlille has been loaded we can sort stations
        if(vlille != null) {
            List<Station> stations = new ArrayList<>(vlille.getStations());
            Collections.sort(stations, new StationsComparator(myFinalLocation));

            lastPosition = myFinalLocation;
            lastOrderedStationsByDistance = stations;

            return stations.subList(0, limit);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Station> getBookmarkedStations(Location myLocation) {
        List<Station> bookmarkedStations = new ArrayList<>();
        if(vlille != null) {
            for (Station station : vlille.getStations()) {
                if(station.isBookmarked()) {
                    bookmarkedStations.add(station);
                }
            }
        }

        Collections.sort(bookmarkedStations, new StationsComparator(locationEnabled(myLocation)));

        return bookmarkedStations;
    }

    public Station getStationById(int stationId) {
        Station stationById = null;
        if(vlille != null) {
            Iterator<Station> iterator = vlille.getStations().iterator();
            while (iterator.hasNext() && stationById == null) {
                Station station = iterator.next();
                if(station.getId() == stationId) {
                    stationById = station;
                }
            }
        }

        return stationById;
    }

    public void refreshStationsDetails(final OnStationDetailsReceivedListener listener) {
        for (final Station station : vlille.getStations()) {
           getStationDetails(station, listener);
        }
    }

    public Station bookmarkAction(Station station) {
        station.setBookmarked(!station.isBookmarked());
        // Notify every listeners
        for (OnStationDetailsReceivedListener onStationDetailsReceivedListener : onStationDetailsReceivedListeners) {
            onStationDetailsReceivedListener.onStationDetailsReceived(station);
        }
        return station;
    }

    public interface OnStationDetailsReceivedListener {
        public void onStationDetailsReceived(Station station);
    }

    public interface OnStationDistanceChangedListener {
        public void onStationDistanceChanged();
    }

    public interface OnVlilleReceivedListener {
        public void onVlilleReceived(Vlille vlille);
    }
}
