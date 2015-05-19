package rizomm.vlille.ui.activities.base;

import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import lombok.Getter;
import rizomm.vlille.R;
import rizomm.vlille.managers.VlilleManager;

public abstract class DrawerActivity extends BaseBarActivity {

    @Getter
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Inject
    VlilleManager vlilleManager;

    @Inject
    @Named("current")
    Provider<Location> currentLocationProvider;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                vlilleManager.refreshStationDistance(currentLocationProvider.get());
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.END|Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
