package cc.wanko.karin.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cc.wanko.karin.app.fragments.HomeTimelineFragment;
import cc.wanko.karin.app.fragments.UserListListFragment;

/**
 * Created by eagletmt on 14/05/05.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeTimelineFragment();
            case 1:
                return new UserListListFragment();
        }
        return new HomeTimelineFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    private static final String[] PAGE_TITLES = {
            "Home Timeline",
            "Lists",
    };

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }
}
