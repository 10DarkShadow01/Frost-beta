package com.example.hp.chatlive;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by HP on 23-Dec-17.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                KnownUsers4 FriendsFragment=new KnownUsers4();
                return  FriendsFragment;

            case 1:
               RequestsFragment requestsFragment=new RequestsFragment();
                return  requestsFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "FRIENDS";

            case 1:
                return "REQUESTS";

            default:
                return null;
        }
    }
}
