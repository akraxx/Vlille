package rizomm.vlille.ui.activities.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import butterknife.ButterKnife;
import rizomm.vlille.R;
import rizomm.vlille.VlilleApplication;
import rizomm.vlille.ui.InjectableResource;
import rizomm.vlille.ui.activities.SettingsActivity;

/**
 * Created by Maximilien on 20/02/2015.
 */
public abstract class BaseActivity extends ActionBarActivity implements InjectableResource {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Perform injection so that when this call returns all dependencies will be available for use.
        ((VlilleApplication) getApplication()).inject(this);

        setContentView(getContentView());
        ButterKnife.inject(this);

        onBaseActivityCreate(savedInstanceState);
    }

    protected abstract void onBaseActivityCreate(Bundle savedInstanceState);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.toolbar_button_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
