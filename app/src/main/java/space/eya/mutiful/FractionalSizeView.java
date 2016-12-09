package space.eya.mutiful;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


/**
 *
 */
public class FractionalSizeView extends View {
    public FractionalSizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FractionalSizeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width * 57 / 100, 0);
    }
}