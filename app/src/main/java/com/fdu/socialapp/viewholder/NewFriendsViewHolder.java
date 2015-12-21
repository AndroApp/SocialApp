package com.fdu.socialapp.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.avobject.AddRequest;
import com.fdu.socialapp.event.AgreeAddFriendEvent;
import com.fdu.socialapp.event.DeleteAddRequestEvent;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.service.AddRequestManager;
import com.fdu.socialapp.service.ConversationManager;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by mh on 2015/12/21.
 */
public class NewFriendsViewHolder extends AVCommonViewHolder {

    @Bind(R.id.avatar)
    ImageView avatarView;

    @Bind(R.id.name)
    TextView nameView;

    @Bind(R.id.add)
    Button addButton;

    @Bind(R.id.agreedView)
    TextView agreedView;

    public NewFriendsViewHolder(ViewGroup root){
        super(root.getContext(),root,R.layout.add_friends_item);
    }

    @Override
    public void bindData(Object o) {
        final AddRequest addRequest = (AddRequest)o;
        MsnaUser from = (MsnaUser)addRequest.getFromUser();
        if (from != null){
            nameView.setText(from.getUsername());
        }
        int status = addRequest.getStatus();
        if (status == AddRequest.STATUS_WAIT){
            addButton.setVisibility(View.VISIBLE);
            agreedView.setVisibility(View.GONE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"正在处理，请稍后...",Toast.LENGTH_SHORT).show();
                    AddRequestManager.getInstance().agreeAddRequest(addRequest, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                if(addRequest.getFromUser() != null){
                                    ConversationManager.getInstance().sendWelcomeMessage(addRequest.getFromUser().getObjectId());
                                }
                                AgreeAddFriendEvent event = new AgreeAddFriendEvent();
                                EventBus.getDefault().post(event);
                            }
                        }
                    });
                }
            });
        }else if (status == AddRequest.STATUS_DONE){
            addButton.setVisibility(View.GONE);
            agreedView.setVisibility(View.VISIBLE);
        }

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DeleteAddRequestEvent event = new DeleteAddRequestEvent();
                event.addRequest = addRequest;
                EventBus.getDefault().post(event);
                return false;
            }
        });
    }
}
