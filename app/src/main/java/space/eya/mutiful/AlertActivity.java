package space.eya.mutiful;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class AlertActivity extends Activity {

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        // show on top of lockscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        ctx = getApplicationContext();

        Intent intent = getIntent();

        String alertType   = intent.hasExtra("ALERT_TYPE")   ? intent.getStringExtra("ALERT_TYPE")   : "loud";
        String phoneNumber = intent.hasExtra("PHONE_NUMBER") ? intent.getStringExtra("PHONE_NUMBER") : "?";
        String smsContent  = intent.hasExtra("SMS_MESSAGE")  ? intent.getStringExtra("SMS_MESSAGE")  : "?";

        String message = String.format(
            getResources().getString(
                alertType.equals("mute") ? R.string.alert_info_mute_format
                                         : R.string.alert_info_loud_format
            ),
            phoneNumber,
            smsContent
        );

        TextView txt = (TextView) findViewById(R.id.alert_info);

        Log.i("WhereAreYou", alertType + " Alert for: " + message);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (alertType.equals("mute")) {
            boolean muteRing = sharedPref.getBoolean(Preferences.KEY_MUTE_RING, true);
            boolean muteMusic = sharedPref.getBoolean(Preferences.KEY_MUTE_MUSIC, true);
            boolean muteAlarm = sharedPref.getBoolean(Preferences.KEY_MUTE_ALARM, false);
            boolean muteOther = sharedPref.getBoolean(Preferences.KEY_MUTE_OTHER, false);

            AlertUtils.MuteAll(ctx, muteRing, muteMusic, muteAlarm, muteOther);
            txt.setText(message);
        } else {
            boolean allowVibrate = sharedPref.getBoolean(Preferences.KEY_ALLOW_VIBRATE, true);
            boolean allowAudio = sharedPref.getBoolean(Preferences.KEY_ALLOW_AUDIO, true);
            AlertUtils.InvokeAlert(ctx, allowVibrate, allowAudio);
            txt.setText(message);
        }

        Linkify.addLinks(txt, Linkify.PHONE_NUMBERS);

        txt.setText(
            stripUnderlines(new SpannableString(txt.getText()))
        );
    }

    private Spannable stripUnderlines(Spannable s) {
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        return s;
    }

    /**
     * Shut up and close the window
     *
     * @param view
     */
    public void closeAction(View view) {
        AlertUtils.StopAlert(ctx);
        finish();
    }
}
