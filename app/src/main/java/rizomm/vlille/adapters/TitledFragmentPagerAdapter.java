package rizomm.vlille.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import rizomm.vlille.ui.fragments.base.TitledFragment;


/**
 * Created by Maximilien on 05/02/2015.
 */
public class TitledFragmentPagerAdapter extends FragmentPagerAdapter {
    private final List<TitledFragment> fragments;

    public TitledFragmentPagerAdapter(FragmentManager fragmentManager, List<TitledFragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.fragments.get(position).getTitle();
    }
}
