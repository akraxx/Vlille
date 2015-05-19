package rizomm.vlille.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import rizomm.vlille.R;
import rizomm.vlille.adapters.VlilleListAdaptater;
import rizomm.vlille.managers.ToasterManager;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.model.Station;
import rizomm.vlille.ui.activities.StationDetailsActivity;
import rizomm.vlille.ui.fragments.base.TitledFragment;

/**
 * Created by Maximilien on 22/02/2015.
 */
public class VlilleListFragment extends TitledFragment implements VlilleManager.OnStationDetailsReceivedListener,
        VlilleManager.OnStationDistanceChangedListener{

    @Inject
    VlilleManager vlilleManager;

    @Inject
    ToasterManager toasterManager;

    @Inject
    SharedPreferences preferences;

    @Inject
    @Named("current")
    Provider<Location> currentLocationProvider;

    @InjectView(R.id.list_vlille)
    ListView vlilleListView;

    VlilleListAdaptater vlilleListAdaptater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        vlilleManager.addOnStationDetailsReceivedListeners(this);
        vlilleManager.addOnStationDistanceChangedListeners(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        vlilleManager.removeOnStationDetailsReceivedListeners(this);
        vlilleManager.removeOnStationDistanceChangedListeners(this);
    }

    @Override
    public View onViewInflated(View view, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vlilleListAdaptater = new VlilleListAdaptater(getActivity(), currentLocationProvider, vlilleManager, preferences);

        vlilleListView.setAdapter(vlilleListAdaptater);
        return view;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_list;
    }

    @Override
    public String getTitle() {
        return "LISTE";
    }

    @Override
    public void onStationDetailsReceived(Station station) {
        if(vlilleListAdaptater != null) {
            vlilleListAdaptater.notifyDataSetChanged();
        }
    }

    @OnItemClick(R.id.list_vlille)
    public void onContactListItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), StationDetailsActivity.class);
        Station station = vlilleListAdaptater.getItem(position);
        intent.putExtra("stationId", station.getId());
        startActivity(intent);
    }

    @OnTextChanged(value = R.id.vlille_search_bar, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(CharSequence text) {
        vlilleListAdaptater.setFilter(String.valueOf(text));
        vlilleListAdaptater.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.toolbar_button_refresh) {
            toasterManager.pop(getString(R.string.refresh_in_progress), Toast.LENGTH_SHORT);
            vlilleManager.refreshStationDistance(currentLocationProvider.get());
            vlilleManager.refreshStationsDetails(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStationDistanceChanged() {
        if(vlilleListAdaptater != null) {
            vlilleListAdaptater.notifyDataSetChanged();
        }
    }
}
