package rizomm.vlille.ui.holders;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lombok.Getter;
import lombok.Setter;
import rizomm.vlille.R;
import rizomm.vlille.model.Station;
import rizomm.vlille.model.StationDetails;
import rizomm.vlille.utils.ColorConverter;

/**
 * Created by Maximilien on 08/03/2015.
 */
public class MarkerHolder {

    @Getter
    @Setter
    Marker marker;

    Context context;

    SharedPreferences sharedPreferences;

    private View markerView;

    private IconGenerator iconGenerator;

    @InjectView(R.id.marker_icon_parking)
    ImageView iconParkingImageView;

    @InjectView(R.id.marker_icon_vlille)
    ImageView iconVlilleImageView;

    @InjectView(R.id.marker_icon_bookmark)
    ImageView iconBookmarkImageView;

    @InjectView(R.id.marker_icon_status)
    ImageView iconStatusImageView;

    @InjectView(R.id.marker_number_free_attachs)
    TextView numberFreeAttachTextView;

    @InjectView(R.id.marker_number_free_vlille)
    TextView numberFreeVlilleTextView;

    public MarkerHolder(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public MarkerOptions createView(Station station) {
        markerView = LayoutInflater.from(context).inflate(R.layout.marker, null);
        ButterKnife.inject(this, markerView);

        iconGenerator = new IconGenerator(context);
        iconGenerator.setContentView(markerView);

        return new MarkerOptions().position(new LatLng(station.getLatitude(), station.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                .title(station.getName());
    }

    public void updateView(Station station) {

        StationDetails stationDetails = station.getDetails();

        if(stationDetails == null) {
            Log.e(getClass().getSimpleName(), "Details of the station " + station.toString() + " are null");
            return;
        }

        if(marker == null) {
            Log.e(getClass().getSimpleName(), "Marker has not been initialized");
        }

        numberFreeVlilleTextView.setText(stationDetails.getBikes().toString());
        numberFreeAttachTextView.setText(stationDetails.getAttachs().toString());

        iconVlilleImageView.setColorFilter(ColorConverter.getColorByNumber(stationDetails.getBikes(), sharedPreferences));
        iconParkingImageView.setColorFilter(ColorConverter.getColorByNumber(stationDetails.getAttachs(), sharedPreferences));

        if(station.isBookmarked()) {
            iconBookmarkImageView.setVisibility(View.VISIBLE);
        } else {
            iconBookmarkImageView.setVisibility(View.GONE);
        }

        if(station.getDetails().getStatus()) {
            iconStatusImageView.setVisibility(View.VISIBLE);
        } else {
            iconStatusImageView.setVisibility(View.GONE);
        }

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
    }

    public View getView() {
        return markerView;
    }

}
