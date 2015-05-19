package rizomm.vlille;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import rizomm.vlille.modules.ServiceModule;

/**
 * Created by Maximilien on 20/02/2015.
 */
public class VlilleApplication extends Application {
    private ObjectGraph graph;

    @Override public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(
                new ServiceModule(this)
        );
    }

    public void inject(Object object) {
        graph.inject(object);
    }
}
