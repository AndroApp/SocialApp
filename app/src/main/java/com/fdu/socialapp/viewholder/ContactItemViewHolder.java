package com.fdu.socialapp.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fdu.socialapp.R;
import com.fdu.socialapp.avobject.SortUser;
import com.fdu.socialapp.model.MsnaUser;

import butterknife.Bind;

/**
 * Created by mh on 2015/12/8.
 */
public class ContactItemViewHolder extends AVCommonViewHolder {

    @Bind(R.id.alpha)
    TextView alpha;

    @Bind(R.id.img_friend_avatar)
    ImageView imageView;

    @Bind(R.id.tv_friend_name)
    TextView friendName;

    private boolean visible;

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public ContactItemViewHolder(ViewGroup root){
        super(root.getContext(), root, R.layout.contact_item);
    }

    @Override
    public void bindData(Object o) {
        final SortUser sortUser = (SortUser)o;
        friendName.setText(sortUser.getInnerUser().getUsername());
        if (visible){
            alpha.setVisibility(View.VISIBLE);
            alpha.setText(sortUser.getSortLetters());
        }else {
            alpha.setVisibility(View.GONE);
        }
    }
}
