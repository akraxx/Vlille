package rizomm.vlille.utils;

import android.location.Location;

import java.util.Comparator;

import rizomm.vlille.model.Station;

/**
 * Created by Maximilien on 19/03/2015.
 */
public class StationsComparator implements Comparator<Station> {

    private final Location myLocation;

    public StationsComparator(Location myLocation) {
        this.myLocation = myLocation;
    }

    @Override
    public int compare(Station s1, Station s2) {
        float[] distance1 = new float[1];
        Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), s1.getLatitude(), s1.getLongitude(), distance1);

        s1.setDistance(distance1[0]);
        float[] distance2 = new float[1];

        Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), s2.getLatitude(), s2.getLongitude(), distance2);
        s2.setDistance(distance2[0]);

        return new Float(distance1[0]).compareTo(distance2[0]);
    }
}
