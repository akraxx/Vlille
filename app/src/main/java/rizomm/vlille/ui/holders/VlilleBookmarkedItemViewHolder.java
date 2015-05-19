package rizomm.vlille.ui.holders;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rizomm.vlille.R;
import rizomm.vlille.model.Station;
import rizomm.vlille.model.StationDetails;
import rizomm.vlille.utils.ColorConverter;
import rizomm.vlille.utils.DistanceConverter;
import rizomm.vlille.utils.TimeAgo;


public class VlilleBookmarkedItemViewHolder {

    private final Context context;

    private final SharedPreferences sharedPreferences;

    private final TimeAgo timeAgo;

    private View vlilleItemView;

    @InjectView(R.id.vlille_list_item_name)
    TextView vlilleNameTextView;

    @InjectView(R.id.vlille_list_item_distance)
    TextView vlilleDistanceTextView;

    @InjectView(R.id.marker_icon_parking)
    ImageView iconParkingImageView;

    @InjectView(R.id.marker_icon_vlille)
    ImageView iconVlilleImageView;

    @InjectView(R.id.vlille_list_item_paiement)
    ImageView iconPaiementImageView;

    @InjectView(R.id.vlille_list_item_status_offline)
    View statusView;

    @InjectView(R.id.marker_number_free_attachs)
    TextView numberFreeAttachTextView;

    @InjectView(R.id.marker_number_free_vlille)
    TextView numberFreeVlilleTextView;

    public VlilleBookmarkedItemViewHolder(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.timeAgo = new TimeAgo(context);
        createView();
    }

    private void createView() {
        vlilleItemView = LayoutInflater.from(context).inflate(R.layout.vlille_bookmarked_list_item, null);
        ButterKnife.inject(this, vlilleItemView);
    }

    public void updateView(Station station) {
        vlilleNameTextView.setText(station.getName());
        vlilleDistanceTextView.setText(DistanceConverter.computeDistance(station.getDistance()));


        StationDetails details = station.getDetails();

        if(station.isCbAllowed()) {
            iconPaiementImageView.setVisibility(View.VISIBLE);
        } else {
            iconPaiementImageView.setVisibility(View.INVISIBLE);
        }

        if(details != null) {
            numberFreeAttachTextView.setText(details.getAttachs().toString());
            numberFreeVlilleTextView.setText(details.getBikes().toString());

            iconVlilleImageView.setColorFilter(ColorConverter.getColorByNumber(details.getBikes(), sharedPreferences));
            iconParkingImageView.setColorFilter(ColorConverter.getColorByNumber(details.getAttachs(), sharedPreferences));

            if(details.getStatus()) {
                statusView.setVisibility(View.VISIBLE);
            } else {
                statusView.setVisibility(View.GONE);
            }
        }
    }

    public View getView() {
        return vlilleItemView;
    }

}
