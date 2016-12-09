package space.eya.mutiful;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // try to get shared prefs
        SharedPreferences sharedPref = context.getSharedPreferences(Preferences.SHARE_NAME, context.MODE_PRIVATE);
        String getLoudText = sharedPref.getString(Preferences.KEY_TRIGGER1, context.getResources().getString(R.string.trigger_text1));
        String getMuteText = sharedPref.getString(Preferences.KEY_TRIGGER2, context.getResources().getString(R.string.trigger_text2));
        Log.i("WhereAreYou", "Trigger get loud: " + getLoudText);
        Log.i("WhereAreYou", "Trigger get mute: " + getMuteText);

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("WhereAreYou", "senderNum: " + phoneNumber + "; message: " + message);

                    Intent alertIntent = new Intent(context, AlertActivity.class);
                    alertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    alertIntent.putExtra("PHONE_NUMBER", phoneNumber);
                    alertIntent.putExtra("SMS_MESSAGE", message);


                    if (message.toLowerCase().contains(getLoudText.toLowerCase())) {
                        Log.i("WhereAreYou", "Starting get loud alert activity");
                        alertIntent.putExtra("ALERT_TYPE", "loud");
                        context.startActivity(alertIntent);
                        break;
                    } else if (message.toLowerCase().contains(getMuteText.toLowerCase())) {
                        Log.i("WhereAreYou", "Starting get mute alert activity");
                        alertIntent.putExtra("ALERT_TYPE", "mute");
                        context.startActivity(alertIntent);
                        break;
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("WhereAreYou", "Exception smsReceiver" +e);
        }
    }

}
