package com.fdu.socialapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.fdu.socialapp.avobject.SortUser;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.viewholder.ContactItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mh on 2015/12/8.
 *
 */
public class ContactAdapter<T> extends RecyclerView.Adapter<ContactItemViewHolder> {

    private List<T> dataList = new ArrayList<>();

    public void setDataList(List<T> datas){
        dataList.clear();
        if (datas != null){
            dataList.addAll(datas);
        }
    }

    public void addDataList(List<T> datas){
        if (datas != null) {
            for (T data:datas){
                if (!dataListContains(data)){
                    dataList.add(data);
                }
            }
        }
    }

    private boolean dataListContains(T d){
        SortUser user = (SortUser)d;
        for (T data:dataList){
            SortUser user0 = (SortUser)data;
            if (user.getInnerUser().getObjectId().equals(user0.getInnerUser().getObjectId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ContactItemViewHolder holder, int position) {
        if (position >=0 && position < dataList.size()){
            char section = getSectionForPosition(position);
            if (position == getPositionForSection(section)){
                holder.setVisible(true);
            }else {
                holder.setVisible(false);
            }
            holder.bindData(dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public char getSectionForPosition(int position) {
        return ((SortUser)dataList.get(position)).getSortLetters().charAt(0);
    }

    public int getPositionForSection(char section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = ((SortUser)dataList.get(i)).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
