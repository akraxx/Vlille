package rizomm.vlille.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Maximilien on 19/02/2015.
 */
@Root(name = "station")
@Data
@EqualsAndHashCode(exclude = {"lastUpdate", "lastDataReceived"})
public class StationDetails {
    @Element(name = "adress")
    private String address;
    @Element(name = "status")
    private Boolean status;
    @Element(name = "bikes")
    private Integer bikes;
    @Element(name = "attachs")
    private Integer attachs;
    @Element(name = "paiement")
    private String payment;
    @Element(name = "lastupd")
    private String lastUpdate;

    private Date lastDataReceived;
}
