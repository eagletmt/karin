package cc.wanko.karin.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import cc.wanko.karin.app.client.HomeTimelineSource;
import cc.wanko.karin.app.fragments.StatusListFragment;
import cc.wanko.karin.app.fragments.UserListListFragment;

/**
 * Created by eagletmt on 14/05/05.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private FragmentActivity activity;

    public MainPagerAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StatusListFragment(new HomeTimelineSource(activity));
            case 1:
                return new UserListListFragment();
        }
        throw new IllegalStateException("not reached");
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
