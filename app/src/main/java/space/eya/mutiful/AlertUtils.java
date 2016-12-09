package space.eya.mutiful;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;


/**
 * Created by lotas on 4/12/15.
 */
public class AlertUtils
{
    private static Boolean isMute = true;
    private static Boolean muteRing = true;
    private static Boolean muteMusic = true;
    private static Boolean muteAlarm = false;
    private static Boolean muteOther = false;

    private static Ringtone r;
    private static Vibrator v;
    private static int maxVolume;
    private static int ringerMode;
    private static AudioManager audioManager;

    private static int baseVolRing;
    private static int baseVolAlarm;
    private static int baseVolDtmf;
    private static int baseVolMusic;
    private static int baseVolNotification;
    private static int baseVolSystem;

    /**
     * Turn volume up
     * Play something that looks like a ringtone couple of times
     * Vibrate
     * Act strange
     *
     * @param mContext
     */
    public static void InvokeAlert(Context mContext, boolean allowVibrate, boolean allowAudio)
    {
        isMute = false;
        Boolean finished = false;

        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        // remember to restore them
        // try to add grace -1
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) - 1;

        saveStreamVolumes();

        // Check & Turn Volume UP
        if (ringerMode == AudioManager.RINGER_MODE_SILENT || ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
        // volume up
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);

        // unmute all
        muteAllStreams(false);

        // Play something
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null ){  // I can't see this ever being null (as always have a default notification) but just in case
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        }

        if (allowAudio) {
            r = RingtoneManager.getRingtone(mContext, alert);
            // make settings for this : repeat X
            r.play();
        }


        if (allowVibrate) {
            // Start vibrating
            v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                v.vibrate(5000); // 5 sec
            }
        }
    }

    /**
     * Save current stream volumes
     */
    private static void saveStreamVolumes() {
        ringerMode = audioManager.getRingerMode();
        baseVolRing = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        baseVolAlarm = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        baseVolDtmf = audioManager.getStreamVolume(AudioManager.STREAM_DTMF);
        baseVolMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        baseVolNotification = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        baseVolSystem = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    /**
     * Restore current stream volumes
     */
    private static void restoreStreamVolumes() {
        audioManager.setRingerMode(ringerMode);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, baseVolRing, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, baseVolAlarm, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, baseVolDtmf, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, baseVolMusic, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, baseVolNotification, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, baseVolSystem, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * Mute everything
     *
     * @param mContext
     */
    public static void MuteAll(Context mContext, boolean ring, boolean music, boolean alarm, boolean other)
    {
        isMute = true;
        muteRing = ring;
        muteMusic = music;
        muteAlarm = alarm;
        muteOther = other;

        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        // remember to restore them
        baseVolRing = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        ringerMode = audioManager.getRingerMode();

        // Check & Turn Volume UP
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        // mute 'em all
        muteAllStreams(true);
    }

    private static void muteAllStreams(boolean state) {
        if (muteRing) {
            audioManager.setStreamMute(AudioManager.STREAM_RING, state);
        }
        if (muteMusic) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, state);
        }
        if (muteAlarm) {
            audioManager.setStreamMute(AudioManager.STREAM_ALARM, state);
        }
        if (muteOther) {
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, state);
            audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, state);
            audioManager.setStreamMute(AudioManager.STREAM_DTMF, state);
        }
    }

    /**
     * Stop activities and restore levels
     *
     * @param mContext
     */
    public static void StopAlert(Context mContext)
    {
        if (r != null && r.isPlaying()){
            r.stop();
        }
        if (v != null) {
            v.cancel();
        }

        if (isMute) {
            // unmute all
            muteAllStreams(false);
        }

        restoreStreamVolumes();
    }
}
