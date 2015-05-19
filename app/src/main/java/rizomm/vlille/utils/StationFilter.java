package rizomm.vlille.utils;

import java.util.ArrayList;
import java.util.List;

import rizomm.vlille.model.Station;

/**
 * Created by Maximilien on 17/05/2015.
 */
public class StationFilter {

    public static List<Station> filterByName(List<Station> stations, String chain) {
        if(chain == null || chain.isEmpty()) {
            return stations;
        } else {
            List<Station> matchingStations = new ArrayList<>();
            for (Station station : stations) {
                if(station.getName().toLowerCase().contains(chain.toLowerCase())) {
                    matchingStations.add(station);
                }
            }

            return matchingStations;
        }
    }
}
