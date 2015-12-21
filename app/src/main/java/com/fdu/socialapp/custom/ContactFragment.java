package com.fdu.socialapp.custom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.activities.NewFriends;
import com.fdu.socialapp.adapter.ContactAdapter;
import com.fdu.socialapp.avobject.SortUser;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.service.CacheService;
import com.fdu.socialapp.utils.AVUserCacheUtils;
import com.fdu.socialapp.utils.CharacterParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mh on 2015/12/8.
 */
public class ContactFragment extends BaseFragment{

    private RecyclerView recyclerView;
    private TextView dialog;
    private EnLetterView rightLetter;
    private CharacterParser characterParser;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout newFriend;
    private ContactAdapter<SortUser> contactAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.list_friends);
        dialog = (TextView)view.findViewById(R.id.dialog);
        rightLetter = (EnLetterView)view.findViewById(R.id.right_letter);
        newFriend = (LinearLayout)view.findViewById(R.id.layout_new);
        characterParser = CharacterParser.getInstance();
        initRecyclerView();
        initEnLetterView();
        initNewFriend();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            findFriends();
        }catch (Exception e){
            Log.e("MSNA",e.getMessage());
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initRecyclerView(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        contactAdapter = new ContactAdapter<>();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contactAdapter);
    }

    private void initEnLetterView(){
        rightLetter.setTextView(dialog);
        rightLetter.setOnTouchingLetterChangedListener(new EnLetterView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = contactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    recyclerView.smoothScrollToPosition(position);
                }
            }
        });
    }

    private void initNewFriend(){
        newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), NewFriends.class);
                startActivity(intent);
            }
        });
    }

    private void findFriends() throws Exception{
        final List<MsnaUser> friends = new ArrayList<>();
        MsnaUser.getCurrentUser(MsnaUser.class).findFriendsWithCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, new FindCallback<MsnaUser>() {
            @Override
            public void done(List<MsnaUser> msnaUsers, AVException e) {
                if (e != null) {
                    Log.e("MSNA",e.getMessage());
                } else {
                    try {
                        friends.addAll(msnaUsers);
                        List<String> userIds = new ArrayList<String>();
                        for (AVUser user : friends) {
                            userIds.add(user.getObjectId());
                        }
                        CacheService.setFriendIds(userIds);
                        AVUserCacheUtils.cacheUsers(userIds);
                        List<MsnaUser> newFriends = new ArrayList<>();
                        for (AVUser user : friends) {
                            newFriends.add(CacheService.lookupUser(user.getObjectId()));
                        }
                        contactAdapter.setDataList(sortUsers(newFriends));
                        contactAdapter.notifyDataSetChanged();
                    }catch (Exception ce){
                        Log.e("MSNA",ce.getMessage());
                    }
                }
            }
        });

    }

    private List<SortUser> sortUsers(List<MsnaUser> users){
        List<SortUser> sortUsers = new ArrayList<>();
        for (int i = 0;i < users.size();i++){
            MsnaUser msnaUser = users.get(i);
            SortUser sortUser = new SortUser();
            sortUser.setInnerUser(msnaUser);
            String username = msnaUser.getUsername();
            if (username != null){
                String pinyin = characterParser.getSelling(username);
                String sortString = pinyin.substring(0,1).toUpperCase();
                if (sortString.matches("[A-Z]")){
                    sortUser.setSortLetters(sortString);
                }else {
                    sortUser.setSortLetters("#");
                }
            }else {
                sortUser.setSortLetters("#");
            }
            sortUsers.add(sortUser);
        }
        Collections.sort(sortUsers, new Comparator<SortUser>() {
            @Override
            public int compare(SortUser lhs, SortUser rhs) {
                if (lhs.getSortLetters().equals("@") || rhs.getSortLetters().equals("#")){
                    return -1;
                }else if (lhs.getSortLetters().equals("#") || rhs.getSortLetters().equals("@")){
                    return 1;
                }else {
                    return lhs.getSortLetters().compareTo(rhs.getSortLetters());
                }
            }
        });
        return sortUsers;
    }

}
