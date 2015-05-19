package rizomm.vlille.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import javax.inject.Provider;

import lombok.Setter;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.model.Station;
import rizomm.vlille.ui.activities.SettingsActivity;
import rizomm.vlille.ui.holders.VlilleItemViewHolder;
import rizomm.vlille.utils.StationFilter;

/**
 * Created by Maximilien on 22/02/2015.
 */
public class VlilleListAdaptater extends BaseAdapter {

    private final VlilleManager vlilleManager;

    private final Context context;

    private final Provider<Location> currentLocationProvider;

    private final SharedPreferences preferences;

    @Setter
    private String filter = "";

    public VlilleListAdaptater(Context context,
                               Provider<Location> currentLocationProvider,
                               VlilleManager vlilleManager,
                               SharedPreferences preferences) {
        this.context = context;
        this.vlilleManager = vlilleManager;
        this.currentLocationProvider = currentLocationProvider;
        this.preferences = preferences;
    }

    @Override
    public int getCount() {
        int numElements = preferences.getInt(SettingsActivity.SETTINGS_NUMBER_ELEMENTS_LIST, SettingsActivity.DEFAULT_NUMBER_ELEMENTS_LIST);
        return StationFilter.filterByName(vlilleManager.getStationsNear(currentLocationProvider.get(), numElements), filter).size();
    }

    @Override
    public Station getItem(int position) {
        int numElements = preferences.getInt(SettingsActivity.SETTINGS_NUMBER_ELEMENTS_LIST, SettingsActivity.DEFAULT_NUMBER_ELEMENTS_LIST);
        return StationFilter.filterByName(vlilleManager.getStationsNear(currentLocationProvider.get(), numElements), filter).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        Station person = getItem(position);
        VlilleItemViewHolder viewHolder;

        if(cellView == null) {
            viewHolder = new VlilleItemViewHolder(context, preferences);
            cellView = viewHolder.getView();
            cellView.setTag(viewHolder);
        }

        viewHolder = (VlilleItemViewHolder) cellView.getTag();
        viewHolder.updateView(person);
        return cellView;
    }
}
