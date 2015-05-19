package rizomm.vlille.ui.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import rizomm.vlille.R;
import rizomm.vlille.adapters.TitledFragmentPagerAdapter;
import rizomm.vlille.managers.VlilleManager;
import rizomm.vlille.ui.fragments.base.BaseFragment;
import rizomm.vlille.ui.fragments.base.TitledFragment;
import rizomm.vlille.ui.slidingtab.SlidingTabLayout;

/**
 * Created by Maximilien on 22/02/2015.
 */
public class HomeFragment extends BaseFragment {

    @InjectView(R.id.view_pager)
    ViewPager contactListViewPager;

    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;

    @Inject
    VlilleManager vlilleManager;

    @Inject
    @Named("current")
    Provider<Location> currentLocationProvider;

    @Override
    public View onViewInflated(View view, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        List<TitledFragment> tabs = new ArrayList<>();

        tabs.add((TitledFragment) Fragment.instantiate(getActivity(), VlilleMapFragment.class.getName()));
        tabs.add((TitledFragment) Fragment.instantiate(getActivity(), VlilleListFragment.class.getName()));

        TitledFragmentPagerAdapter adapter = new TitledFragmentPagerAdapter(getChildFragmentManager(), tabs);

        contactListViewPager.setAdapter(adapter);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(contactListViewPager);

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1) {
                    vlilleManager.refreshStationDistance(currentLocationProvider.get());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });

        return view;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_home;
    }

}
