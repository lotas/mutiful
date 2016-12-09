package space.eya.mutiful;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = (TextView) findViewById(R.id.aboutTextView);
        String aboutString = getResources().getString(R.string.about_text).replace("\n", "<br>");

        Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
                Html.fromHtml(aboutString));

        aboutText.setText(spannedText);
        Linkify.addLinks(aboutText, Linkify.ALL);

        aboutText.setText(
            stripUnderlines((Spannable) aboutText.getText())
        );
    }

    private Spannable stripUnderlines(Spannable s) {
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        Log.i("WhereAreYou", "spans: " + spans.length);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        return s;
    }
}
