package cc.wanko.karin.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import cc.wanko.karin.app.client.HomeTimelineSource;
import cc.wanko.karin.app.client.UserStatusSource;
import cc.wanko.karin.app.fragments.StatusListFragment;
import cc.wanko.karin.app.fragments.UserListListFragment;

/**
 * Created by eagletmt on 14/05/05.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private final FragmentActivity activity;

    public MainPagerAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                StatusListFragment fragment = new StatusListFragment();
                fragment.setStatusSource(new HomeTimelineSource(activity));
                return fragment;
            }
            case 1:
                return new UserListListFragment();
            case 2: {
                StatusListFragment fragment = new StatusListFragment();
                fragment.setStatusSource(new UserStatusSource(activity, -1));
                return fragment;
            }
        }
        throw new IllegalStateException("not reached");
    }

    @Override
    public int getCount() {
        return 3;
    }

    private static final String[] PAGE_TITLES = {
            "Home Timeline",
            "Lists",
            "Me",
    };

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }
}
