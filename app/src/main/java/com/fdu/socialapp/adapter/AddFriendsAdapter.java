package com.fdu.socialapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.viewholder.AddFriendsViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mh on 2015/12/1.
 */
public class AddFriendsAdapter<T> extends RecyclerView.Adapter<AddFriendsViewHolder> {

    private List<T> dataList = new ArrayList<T>();

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> datas) {
        dataList.clear();
        if (null != datas) {
            dataList.addAll(datas);
        }
    }

    public void addDataList(List<T> datas) {
        dataList.addAll(0, datas);
    }

    @Override
    public AddFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AddFriendsViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(AddFriendsViewHolder holder, int position) {
        if(position >= 0 && position < dataList.size()){
            holder.bindData(dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
