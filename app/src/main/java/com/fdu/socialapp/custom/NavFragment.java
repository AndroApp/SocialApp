package com.fdu.socialapp.custom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fdu.socialapp.R;
import com.fdu.socialapp.model.MsnaUser;

/**
 * Created by mao on 2015/10/16 0016.
 *
 */
public class NavFragment extends BaseFragment {
    public static final String ARG_POSITION = "layout_id";
    private static final String TAG="NavFragment";
    public NavFragment() {
        // Empty constructor required for fragment subclasses
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Created");
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int position = getArguments().getInt(ARG_POSITION);
        View view;
        switch (position){
            case R.id.contacts_layout:
                Log.i(TAG, "CreateView: contacts");
                view = inflater.inflate(R.layout.fragment_contacts, container,false);
                break;

            case R.id.aboutme_layout:
                Log.i(TAG, "CreateView: aboutMe");
                view = inflater.inflate(R.layout.fragment_aboutme, container,false);
                ((TextView)view.findViewById(R.id.userInfoName)).setText(MsnaUser.getCurrentUser().getUsername());
                break;

            case R.id.etc_layout:
                Log.i(TAG, "CreateView: etc");
                view = inflater.inflate(R.layout.fragment_etc, container,false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_etc, container,false);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
