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
import rizomm.vlille.ui.holders.VlilleBookmarkedItemViewHolder;
import rizomm.vlille.utils.StationFilter;

/**
 * Created by Maximilien on 22/02/2015.
 */
public class VlilleBookmarkedListAdaptater extends BaseAdapter {

    private final VlilleManager vlilleManager;

    private final Context context;

    private final SharedPreferences sharedPreferences;

    private final Provider<Location> currentLocationProvider;

    @Setter
    private String filter = "";

    public VlilleBookmarkedListAdaptater(Context context,
                                         Provider<Location> currentLocationProvider,
                                         VlilleManager vlilleManager,
                                         SharedPreferences sharedPreferences) {
        this.context = context;
        this.vlilleManager = vlilleManager;
        this.currentLocationProvider = currentLocationProvider;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public int getCount() {
        return StationFilter.filterByName(vlilleManager.getBookmarkedStations(currentLocationProvider.get()), filter).size();
    }

    @Override
    public Station getItem(int position) {
        return StationFilter.filterByName(vlilleManager.getBookmarkedStations(currentLocationProvider.get()), filter).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        Station person = getItem(position);
        VlilleBookmarkedItemViewHolder viewHolder;

        if(cellView == null) {
            viewHolder = new VlilleBookmarkedItemViewHolder(context, sharedPreferences);
            cellView = viewHolder.getView();
            cellView.setTag(viewHolder);
        }

        viewHolder = (VlilleBookmarkedItemViewHolder) cellView.getTag();
        viewHolder.updateView(person);
        return cellView;
    }
}
