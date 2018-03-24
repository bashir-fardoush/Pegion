package com.example.dell.pegion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by DELL on 3/24/2018.
 */

class SectionPagerAdapter extends FragmentPagerAdapter {
    private int numberOfFragments = 3;

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new ChatFragment();
                break;
            case 1:
                fragment = new ReuestsFrgment();
                break;
            case 2:
                fragment = new FriendsFragment();
                break;
                default:
                    return null;
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position){
            case 0:
                title = "Chats";
                break;
            case 1:
                title = "Requests";
                break;
            case 2:
                title = "Friends";
                break;
            default: title ="title";

        }

        return title;
    }

    @Override
    public int getCount() {
        return numberOfFragments;
    }
}
