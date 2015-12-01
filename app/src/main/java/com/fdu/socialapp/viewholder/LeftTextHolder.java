package com.fdu.socialapp.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.fdu.socialapp.R;
import com.fdu.socialapp.event.LeftChatItemClickEvent;
import com.fdu.socialapp.utils.AVUserCacheUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by mao on 2015/11/3 0003.
 * 显示在左侧的对话框Holder
 */
public class LeftTextHolder extends AVCommonViewHolder {
    @Bind(R.id.chat_left_text_tv_time)
    protected TextView timeView;

    @Bind(R.id.chat_left_text_tv_content)
    protected TextView contentView;

    @Bind(R.id.chat_left_text_tv_name)
    protected TextView nameView;


    public LeftTextHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.chat_left_text_view);
    }

    @OnClick({R.id.chat_left_text_tv_content, R.id.chat_left_text_tv_name})
    public void onNameClick(View view) {
        LeftChatItemClickEvent clickEvent = new LeftChatItemClickEvent();
        clickEvent.userId = nameView.getText().toString();
        EventBus.getDefault().post(clickEvent);
    }

    @Override
    public void bindData(Object o) {
        AVIMMessage message = (AVIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        String time = dateFormat.format(message.getTimestamp());

        String content = getContext().getString(R.string.unspport_message_type);
        if (message instanceof AVIMTextMessage) {
            content = ((AVIMTextMessage) message).getText();
        }

        contentView.setText(content);
        timeView.setText(time);

        String otherId = message.getFrom();
        AVUser user = AVUserCacheUtils.getCachedUser(otherId);
        if (user != null) {
            nameView.setText(user.getUsername());
        } else {
            nameView.setText("对话");
        }
    }

    public void showTimeView(boolean isShow) {
        timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
