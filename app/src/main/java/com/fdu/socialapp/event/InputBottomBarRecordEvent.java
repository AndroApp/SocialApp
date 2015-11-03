package com.fdu.socialapp.event;

/**
 * Created by mao on 2015/11/3 0003.
 */
public class InputBottomBarRecordEvent extends InputBottomBarEvent {
    /**
     * 录音本地路径
     */
    public String audioPath;

    /**
     * 录音长度
     */
    public int audioDuration;

    public InputBottomBarRecordEvent(int action, String path, int duration, Object tag) {
        super(action, tag);
        audioDuration = duration;
        audioPath = path;
    }
}
