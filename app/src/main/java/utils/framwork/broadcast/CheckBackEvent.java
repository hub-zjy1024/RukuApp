package utils.framwork.broadcast;

import android.content.Context;
import android.content.Intent;

/**
 * Created by 张建宇 on 2019/11/30.
 */
public class CheckBackEvent {
    Context mContext;
    private String isBack = "isback";
    EventUtil.Listener mListener;

    public CheckBackEvent(Context mContext, EventUtil.Listener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
        setListener(mListener);
    }
    public CheckBackEvent(Context mContext) {
        this.mContext = mContext;
    }

    public void sendEvent(){
        EventUtil env = new EventUtil(mContext);
        Intent intent = new Intent();
        intent.setAction(EventUtil.action);
        intent.putExtra(isBack, true);
        env.sendBroadcast(mContext,intent  );
    }

    public void setListener(EventUtil.Listener mListener) {
        EventUtil env = new EventUtil(mContext);
        env.regist(mListener);
    }
}
