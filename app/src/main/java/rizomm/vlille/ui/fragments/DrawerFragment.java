package rizomm.vlille.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import rizomm.vlille.R;
import rizomm.vlille.adapters.VlilleBookmarkedListAdaptater;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.model.Station;
import rizomm.vlille.ui.activities.StationDetailsActivity;
import rizomm.vlille.ui.fragments.base.BaseFragment;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DrawerFragment extends BaseFragment implements VlilleManager.OnStationDetailsReceivedListener,
        VlilleManager.OnStationDistanceChangedListener {

    @InjectView(R.id.list_vlille_bookmarked)
    ListView vlilleBookmarkedListView;

    @Inject
    VlilleManager vlilleManager;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    @Named("current")
    Provider<Location> currentLocationProvider;

    VlilleBookmarkedListAdaptater adaptater;

    public DrawerFragment() {
    }

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
        adaptater = new VlilleBookmarkedListAdaptater(getActivity(), currentLocationProvider, vlilleManager, sharedPreferences);

        vlilleBookmarkedListView.setAdapter(adaptater);

        return view;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_drawer;
    }

    @OnItemClick(R.id.list_vlille_bookmarked)
    public void onContactListItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), StationDetailsActivity.class);
        Station station = adaptater.getItem(position);
        intent.putExtra("stationId", station.getId());
        startActivity(intent);
    }

    @OnTextChanged(value = R.id.vlille_bookmark_search_bar, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(CharSequence text) {
        adaptater.setFilter(String.valueOf(text));
        adaptater.notifyDataSetChanged();
    }


    @Override
    public void onStationDetailsReceived(Station station) {
        if(adaptater != null) {
            adaptater.notifyDataSetChanged();
        }
    }

    @Override
    public void onStationDistanceChanged() {
        if(adaptater != null) {
            adaptater.notifyDataSetChanged();
        }
    }
}
