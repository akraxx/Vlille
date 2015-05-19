package rizomm.vlille.model;

import com.j256.ormlite.field.DatabaseField;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Created by Maximilien on 19/02/2015.
 */
@Data
@Builder
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
@Root(name = "marker")
public class Station {

    public static String CB_ALLOWED = "AVEC_TPE";

    @DatabaseField(generatedId = true, index = true)
    private int _id;

    @Attribute(name = "id")
    @DatabaseField(generatedId = false, index = true)
    private int id;

    @DatabaseField(index = true)
    @Attribute(name = "name")
    private String name;

    @DatabaseField
    @Attribute(name = "lat")
    private double latitude;

    @DatabaseField
    @Attribute(name = "lng")
    private double longitude;

    @DatabaseField(index = true)
    private boolean bookmarked = false;

    private Float distance = null;

    private StationDetails details;

    public boolean isCbAllowed() {
        return details != null && CB_ALLOWED.equals(details.getPayment());
    }

}
