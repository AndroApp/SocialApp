package com.fdu.socialapp.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.adapter.AddFriendsAdapter;
import com.fdu.socialapp.event.AddButtonClickEvent;
import com.fdu.socialapp.event.EmptyEvent;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.service.AddRequestManager;
import com.fdu.socialapp.service.CacheService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by mh on 2015/12/1.
 */
public class AddFriends extends BaseActivity{
    private static final String TAG = "AddFriends";

    @Bind(R.id.searchNameEdit)
    EditText searchNameEdit;

    @Bind(R.id.add_friends_search_rv)
    RecyclerView recyclerView;

    private AddFriendsAdapter<MsnaUser> adapter;

    private String searchName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        Toolbar toolbar = (Toolbar)findViewById(R.id.id_toolbar_add_friends);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_36dp);
        setActionBar(toolbar);
        initRecyclerView();

    }

    private void initRecyclerView(){
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new AddFriendsAdapter<>();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchUser(String searchName, int skip) throws AVException {
        AVQuery<MsnaUser> q = AVUser.getQuery(MsnaUser.class);
        q.whereContains(MsnaUser.USERNAME, searchName);
        q.limit(Constants.PAGE_SIZE);
        q.skip(skip);
        MsnaUser user = (MsnaUser)AVUser.getCurrentUser();
        List<String> friendIds = new ArrayList<String>(CacheService.getFriendIds());
        friendIds.add(user.getObjectId());
        q.whereNotContainedIn(Constants.OBJECT_ID, friendIds);
        q.orderByDescending(Constants.UPDATED_AT);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        q.findInBackground(new FindCallback<MsnaUser>() {
            @Override
            public void done(List<MsnaUser> list, AVException e) {
                adapter.setDataList(list);
                adapter.notifyDataSetChanged();
                CacheService.registerUsers(list);
            }
        });
    }

    @OnClick(R.id.searchBtn)
    public void search(){
        searchName = searchNameEdit.getText().toString();
        try {
            searchUser(searchName, adapter.getItemCount());
        }catch (Exception e){

        }
    }

    public void onEvent(AddButtonClickEvent event){
        AddRequestManager.getInstance().createAddRequestInBackground(this,event.user);
    }
}
