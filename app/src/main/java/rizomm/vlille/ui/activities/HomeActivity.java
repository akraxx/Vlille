package rizomm.vlille.ui.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import rizomm.vlille.R;
import rizomm.vlille.ui.activities.base.DrawerActivity;

public class HomeActivity extends DrawerActivity {

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
    }

    @Override
    public int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.toolbar_button_bookmark_list) {
            DrawerLayout drawerLayout = getDrawerLayout();
            if(drawerLayout.isDrawerOpen(Gravity.END|Gravity.RIGHT)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(Gravity.END);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
