package nl.compra.compraapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class AwesomeBobScroller extends ScrollView
{

    OnBottomReachedListener mListener;

    public AwesomeBobScroller (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AwesomeBobScroller (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AwesomeBobScroller (Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount()-1);
        int diff = (view.getBottom()-(getHeight()+getScrollY()));

        if (diff == 0 && mListener != null) {
            mListener.onBottomReached();

            System.out.println ();

        }

        super.onScrollChanged(l, t, oldl, oldt);
    }


    // Getters & Setters

    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListener;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }


    /**
     * Event listener.
     */
    public interface OnBottomReachedListener{
        public void onBottomReached();
    }

}
