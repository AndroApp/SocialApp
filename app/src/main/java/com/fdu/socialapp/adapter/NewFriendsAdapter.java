package com.fdu.socialapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.fdu.socialapp.avobject.AddRequest;
import com.fdu.socialapp.viewholder.NewFriendsViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mh on 2015/12/21.
 */
public class NewFriendsAdapter<T> extends RecyclerView.Adapter<NewFriendsViewHolder> {

    private List<T> dataList = new ArrayList<>();

    public void setDataList(List<T> dataList){
        this.dataList.clear();
        if (dataList != null){
            this.dataList.addAll(dataList);
        }
    }

    @Override
    public NewFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewFriendsViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(NewFriendsViewHolder holder, int position) {
        if (position >= 0 && position < dataList.size()){
            holder.bindData(dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
