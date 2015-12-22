package com.fdu.socialapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.adapter.NewFriendsAdapter;
import com.fdu.socialapp.avobject.AddRequest;
import com.fdu.socialapp.event.AgreeAddFriendEvent;
import com.fdu.socialapp.event.DeleteAddRequestEvent;
import com.fdu.socialapp.event.EmptyEvent;
import com.fdu.socialapp.service.AddRequestManager;

import java.util.List;

import butterknife.Bind;

/**
 * Created by mh on 2015/12/21.
 */
public class NewFriends extends BaseActivity {
    private final static String TAG = "NewFriends";

    @Bind(R.id.new_friends_rv)
    RecyclerView recyclerView;

    private NewFriendsAdapter<AddRequest> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);

        Toolbar toolbar = (Toolbar)findViewById(R.id.id_toolbar_add_friends);
        setActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_36dp);
        toolbar.setTitle("新朋友");

        initRecyclerView();
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new NewFriendsAdapter<>();
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

    @Override
    protected void onResume() {
        getAddRequestInBackground(adapter.getItemCount(),Constants.PAGE_SIZE);
        super.onResume();
    }

    private void getAddRequestInBackground(int skip,int limit){
        try {
            AddRequestManager.getInstance().findAddRequestInBackground(skip, limit, new FindCallback<AddRequest>() {
                @Override
                public void done(List<AddRequest> list, AVException e) {
                    adapter.setDataList(list);
                    adapter.notifyDataSetChanged();
                    AddRequestManager.getInstance().markAddRequestsRead(list);
                }
            });
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    public void onEvent(AgreeAddFriendEvent event) {
        refresh();
    }

    public void onEvent(final DeleteAddRequestEvent event){
        new AlertDialog.Builder(this).setMessage("删除好友请求").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    event.addRequest.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(AVException e) {
                            if (filterException(e))
                                getAddRequestInBackground(adapter.getItemCount(),Constants.PAGE_SIZE);
                        }
                    });
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }
            }
        }).setNegativeButton("取消",null).show();
    }
}
