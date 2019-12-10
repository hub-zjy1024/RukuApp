package utils.framwork.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

/**
 * Created by 张建宇 on 2019/11/30.
 */
public class EventUtil {
    public static final String action = EventUtil.class.getName() + ".event";
    private WeakReference<Context> mWkRefer;
    private String key = "";

    public EventUtil(Context mWkRefer) {
        this.mWkRefer =new WeakReference<Context> (mWkRefer) ;
    }

    public interface Listener{
        void onRecieve(Intent event);
    }

    public void sendBroadcast(Context mContext, Intent mIntent) {
        mIntent.setAction(action);
        mContext.sendBroadcast(mIntent);
    }

    public void regist(final Listener listener) {
        Context context = mWkRefer.get();
        if (context != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            BroadcastReceiver   reciever = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (listener != null) {
                        listener.onRecieve(intent);
                    }
                }
            };
            context.registerReceiver(reciever,filter);
        }
    }
}
