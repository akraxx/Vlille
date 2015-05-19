package rizomm.vlille.managers;

import android.content.Context;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Maximilien on 19/03/2015.
 */
@Singleton
public class ToasterManager {

    @Inject
    Context context;

    private final HashMap<String, Date> messagesHistory = new HashMap<>();

    public void pop(String title) {
        pop(title, Toast.LENGTH_LONG);
    }

    public void pop(String title, int toastLength) {
        Date lastRegisteredMessage = messagesHistory.get(title);
        Date currentDate = new Date();

        // If the message has already been registered less than 1 second ago we do no display it
        if(!(lastRegisteredMessage != null && (currentDate.getTime()-lastRegisteredMessage.getTime())/1000 <= 1)) {
            messagesHistory.put(title, currentDate);
            Toast.makeText(context, title, toastLength).show();
        }

    }
}
