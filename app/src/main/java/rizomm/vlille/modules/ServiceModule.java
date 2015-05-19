package rizomm.vlille.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import rizomm.vlille.VlilleApplication;
import rizomm.vlille.adapters.VlilleListAdaptater;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.services.VlilleService;
import rizomm.vlille.ui.activities.HomeActivity;
import rizomm.vlille.ui.activities.LoadingActivity;
import rizomm.vlille.ui.activities.SettingsActivity;
import rizomm.vlille.ui.activities.StationDetailsActivity;
import rizomm.vlille.ui.fragments.DrawerFragment;
import rizomm.vlille.ui.fragments.HomeFragment;
import rizomm.vlille.ui.fragments.VlilleListFragment;
import rizomm.vlille.ui.fragments.VlilleMapFragment;
import rizomm.vlille.utils.VlilleConverter;

/**
 * Created by Maximilien on 20/02/2015.
 */
@Module(
        injects = {
                HomeActivity.class,
                LoadingActivity.class,
                StationDetailsActivity.class,
                SettingsActivity.class,
                DrawerFragment.class,
                HomeFragment.class,
                VlilleMapFragment.class,
                VlilleListFragment.class,
                VlilleManager.class,
                VlilleListAdaptater.class
        },

        complete = false
)
public class ServiceModule {

    public static final String PREFS_NAME = "vlille_pref_file";

    private VlilleApplication application;

    public ServiceModule(VlilleApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public LocationManager provideLocationManager() {
        return (LocationManager)application.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides
    @Named("current")
    public Location providesCurrentLocation(LocationManager locationManager) {
        Location lastKnownLocationByNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastKnownLocationByNetwork != null) {
            return lastKnownLocationByNetwork;
        } else {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Provides
    @Singleton
    public RestAdapter providesRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint("http://vlille.fr")
                .setConverter(new VlilleConverter())
                .build();
    }

    @Provides
    @Singleton
    public VlilleService providesVlilleService(RestAdapter restAdapter) {
        return restAdapter.create(VlilleService.class);
    }

    @Provides
    public SharedPreferences providesSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }
}
