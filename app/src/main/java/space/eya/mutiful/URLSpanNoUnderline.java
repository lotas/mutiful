package space.eya.mutiful;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by lotas on 5/27/15.
 */

class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}