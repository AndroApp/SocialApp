package com.fdu.socialapp.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.fdu.socialapp.R;
import com.fdu.socialapp.model.MyConversation;

import butterknife.Bind;

/**
 * Created by mao on 2015/11/14 0014.
 */
public class ConversationItemHolder extends AVCommonViewHolder {
    @Bind(R.id.avatar)
    protected ImageView avatarView;

    @Bind(R.id.unread)
    protected TextView unreadView;

    @Bind(R.id.time_text)
    protected TextView timeView;

    @Bind(R.id.msg_text)
    protected TextView msgView;

    @Bind(R.id.name_text)
    protected TextView nameView;

    public ConversationItemHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, R.layout.conversation_item);
    }

    @Override
    public void bindData(Object o) {
        final MyConversation myConversation = (MyConversation) o;
        AVIMConversation avimConversation = myConversation.getConversation();

    }
}
