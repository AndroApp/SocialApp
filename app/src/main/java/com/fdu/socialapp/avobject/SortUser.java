package com.fdu.socialapp.avobject;

import com.fdu.socialapp.model.MsnaUser;

/**
 * Created by mh on 2015/12/8.
 */
public class SortUser {
    private MsnaUser innerUser;
    private String sortLetters;

    public MsnaUser getInnerUser(){
        return innerUser;
    }

    public String getSortLetters(){
        return sortLetters;
    }

    public void setInnerUser(MsnaUser innerUser){
        this.innerUser = innerUser;
    }

    public void setSortLetters(String sortLetters){
        this.sortLetters = sortLetters;
    }

}
