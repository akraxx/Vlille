package rizomm.vlille.ui.activities.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.InjectView;
import rizomm.vlille.R;

/**
 * Created by Maximilien on 20/02/2015.
 */
public abstract class BaseBarActivity extends BaseActivity {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {

    }
}
