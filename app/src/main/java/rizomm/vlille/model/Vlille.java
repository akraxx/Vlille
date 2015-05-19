package rizomm.vlille.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Created by Maximilien on 19/02/2015.
 */
@Root(name = "markers")
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
@Builder
@NoArgsConstructor
public class Vlille {
    @Attribute(name = "center_lat")
    private Double centerLatitude;

    @Attribute(name = "center_lng")
    private Double centerLongitude;

    @Attribute(name = "zoom_level")
    private Double zoomLevel;

    @ElementList(inline=true, name = "marker")
    private List<Station> stations;
}
