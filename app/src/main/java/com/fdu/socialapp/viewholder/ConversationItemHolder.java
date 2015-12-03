package com.fdu.socialapp.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.fdu.socialapp.controller.ConversationHelper;
import com.fdu.socialapp.R;
import com.fdu.socialapp.controller.MessageHelper;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.service.ConversationManager;
import com.fdu.socialapp.event.ConversationItemClickEvent;
import com.fdu.socialapp.model.ConversationType;
import com.fdu.socialapp.model.Room;
import com.fdu.socialapp.utils.AVUserCacheUtils;
import com.fdu.socialapp.utils.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by mao on 2015/11/14 0014.
 * 聊天列表项的View holder
 */
public class ConversationItemHolder extends AVCommonViewHolder {
    @Bind(R.id.avatar)
    protected ImageView avatarView;

    @Bind(R.id.unread)
    protected View unreadView;

    @Bind(R.id.time_text)
    protected TextView timeView;

    @Bind(R.id.msg_text)
    protected TextView msgView;

    @Bind(R.id.name_text)
    protected TextView nameView;

    public ConversationItemHolder(ViewGroup root) {
        super(root.getContext(), root, R.layout.conversation_item);
    }

    @Override
    public void bindData(Object o) {
        final Room room = (Room) o;
        AVIMConversation avimConversation = room.getConversation();
        if (ConversationHelper.isValidConversation(avimConversation)) {
            if (ConversationHelper.typeOfConversation(avimConversation) == ConversationType.Single) {
                MsnaUser user =  AVUserCacheUtils.getCachedUser(ConversationHelper.otherIdOfConversation(avimConversation));
                if (user != null) {
                    ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
                }
            } else {
                avatarView.setImageBitmap(ConversationManager.getConversationIcon(avimConversation));
            }
            nameView.setText(ConversationHelper.nameOfConversation(avimConversation));

            if (room.getUnreadCount() > 0) {
                unreadView.setVisibility(View.VISIBLE);
            } else {
                unreadView.setVisibility(View.GONE);
            }

            AVIMMessage lastMessage = room.getLastMessage();
            if (lastMessage != null) {
                SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
                timeView.setText(format.format(new Date(lastMessage.getTimestamp())));
                msgView.setText(MessageHelper.outlineOfMsg((AVIMTypedMessage) room.getLastMessage()));;
            }
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationItemClickEvent itemClickEvent =  new ConversationItemClickEvent();
                itemClickEvent.conversationId = room.getConversationId();
                EventBus.getDefault().post(itemClickEvent);
            }
        });


    }
}
