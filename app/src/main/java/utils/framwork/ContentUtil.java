package utils.framwork;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 在sdk 14+(native ActionBar),该显示区域指的是ActionBar下面的部分<br/>
 在Support Library revision lower than 19，使用AppCompat，则显示区域包含ActionBar<br/>
 在Support Library revision 19 (or greater)，使用AppCompat，则显示区域不包含ActionBar，即行为与第一种情况相同。<br/>
 所以如果不使用Support Library或使用Support Library的最新版本，则R.id.content所指的区域都是ActionBar以下<br/>
 链接：https://www.jianshu.com/p/077777fa28c9<br/><br/>
 Created by 张建宇 on 2019/5/10. */
public class ContentUtil {
    Context mContext;


    public ContentUtil(Context mContext) {
        this.mContext = mContext;
    }

    public int navibarHeight() {
        Activity mac = (Activity) mContext;
        globalView = mac.findViewById(android.R.id.content);
        int statusBarHeight = getStatuBarHeight();
        int contentTop = globalView.getTop();
        //statusBarHeight是上面所求的状态栏的高度
        int titleBarHeight = contentTop - statusBarHeight;
        return titleBarHeight;
    }

    private View globalView;
    private int firstHeight;
    private boolean isFirst = true;

    public void getKeyboardHeight() {
        Activity mac = (Activity) mContext;
        globalView = mac.findViewById(android.R.id.content);

        globalView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                globalView.getWindowVisibleDisplayFrame(rect);
                if (isFirst) {
                    isFirst = false;
                    firstHeight = rect.height();
                } else {
                    int height = rect.height();
                    if (height < firstHeight) {
                        System.out.println("键盘打开 " + (firstHeight - height));
                    } else {
                        System.out.println("键盘关闭 ");
                    }
                }
            }
        });
    }

    public int getStatuBarHeight() {

        Rect frame = new Rect();
        Activity mac = (Activity) mContext;
        //       获取程序显示的区域，包括标题栏，但不包括状态栏
        mac.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

}
