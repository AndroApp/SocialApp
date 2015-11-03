package com.fdu.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fdu.socialapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mao on 2015/10/19 0019.
 * Session list adapter
 */
public class MyListAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    class ViewHolder{
        public TextView mainTitle;
        public TextView subTitke;
        public ImageView sessionIcon;
        public ImageView newmessage;
    }
    public MyListAdapter(Context context) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i < 6; i++){
            Map<String, Object> map = new HashMap<>();
            map.put("mainTitle", "Test_User" + i);
            map.put("subTitle", "title" + i);
            map.put("sessionIcon", R.mipmap.ic_launcher);
            map.put("newMessage", true);
            list.add(map);
        }
        return list;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }



}



