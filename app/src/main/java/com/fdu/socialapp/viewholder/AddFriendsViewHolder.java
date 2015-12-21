package com.fdu.socialapp.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fdu.socialapp.R;
import com.fdu.socialapp.adapter.AddFriendsAdapter;
import com.fdu.socialapp.event.AddButtonClickEvent;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.service.AddRequestManager;
import com.fdu.socialapp.utils.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by mh on 2015/12/1.
 */
public class AddFriendsViewHolder extends AVCommonViewHolder {

    @Bind(R.id.avatar)
    ImageView avatarView;

    @Bind(R.id.name)
    TextView nameView;

    @Bind(R.id.add)
    Button addButton;

    public AddFriendsViewHolder(ViewGroup root){
        super(root.getContext(),root,R.layout.add_friends_item);
    }

    @Override
    public void bindData(Object o) {
        final MsnaUser user = (MsnaUser)o;

        nameView.setText(user.getUsername());

        addButton.setText(R.string.chat_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddButtonClickEvent event = new AddButtonClickEvent();
                event.user = user;
                EventBus.getDefault().post(event);

            }
        });
        ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);

    }


}
