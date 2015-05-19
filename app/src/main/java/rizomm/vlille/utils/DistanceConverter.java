package rizomm.vlille.utils;

/**
 * Created by Maximilien on 18/05/2015.
 */
public class DistanceConverter {
    public static String computeDistance(Float distance) {
        if(distance == null) {
            return "N/A";
        }

        String distanceText;
        if(distance >= 1000) {
            distance = distance / 1000;
            distanceText = String.format("%.2f km", distance);
        } else {
            distanceText = String.format("%.0f m", distance);
        }
        return distanceText;
    }
}
