package com.fdu.socialapp.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.fdu.socialapp.R;
import com.fdu.socialapp.custom.ConversationFragment;
import com.fdu.socialapp.custom.NavFragment;
import com.fdu.socialapp.custom.PagerSlidingTabStrip;
import com.fdu.socialapp.model.MsnaUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 2015/10/16 0016.
 * 页面导航适配器
 */
public class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, PagerSlidingTabStrip.IconTabProvider {
    private static final String TAG = "PagerAdapter";
    private final String[] TITLES = {"会话", "通讯录", "发现", "我"};
    private final int[] CONTENTS = {R.id.contacts_layout, R.id.etc_layout, R.id.aboutme_layout};
    private final int[] ICONS = {R.drawable.ic_chat_black_24dp, R.drawable.ic_contacts_black_24dp, R.drawable.ic_public_black_24dp, R.drawable.ic_perm_identity_black_24dp};
    List<Fragment> fragments = new ArrayList<>();

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new ConversationFragment());
        for (int content : CONTENTS) {
            NavFragment fragment = new NavFragment();
            Bundle args = new Bundle();
            args.putInt(NavFragment.ARG_POSITION, content);
            fragment.setArguments(args);
            fragments.add(fragment);
        }

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "Page " + position + " selected");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public int getPageIconResId(int position) {
        return ICONS[position];
    }
}
