package space.eya.mutiful;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements
        ShareActionProvider.OnShareTargetSelectedListener, ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * Sharing app
     */
    private ShareActionProvider share = null;
    private Intent shareIntent = new Intent(Intent.ACTION_SEND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        actionBar.setLogo(R.drawable.logo);

        shareIntent.setAction(Intent.ACTION_SEND)
                   .putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.share_extra))
                   .putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.share_this_app));
        shareIntent.setType("text/plain");




        // pre 4.4 devices with hard settings btn don't show overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception e) {
            // presumably, not relevant
        }
    }

    /**
     * save checkbox state
     * @param view
     */
    public void onCheckboxClick(View view)
    {
        String cbName = "";
        switch (view.getId()) {
            case R.id.getLoud_vibrate:
                cbName = Preferences.KEY_ALLOW_VIBRATE;
                break;
            case R.id.getLoud_playMusic:
                cbName = Preferences.KEY_ALLOW_AUDIO;
                break;
            case R.id.mute_ring:
                cbName = Preferences.KEY_MUTE_RING;
                break;
            case R.id.mute_music:
                cbName = Preferences.KEY_MUTE_MUSIC;
                break;
            case R.id.mute_alarm:
                cbName = Preferences.KEY_MUTE_ALARM;
                break;
            case R.id.mute_other:
                cbName = Preferences.KEY_MUTE_OTHER;
                break;
        }

        if (cbName.isEmpty()) {
            return;
        }

        saveSharedPreference(this, cbName, ((CheckBox) view).isChecked());
    }

    private static void saveSharedPreference(Context ctx, String key, String value)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Preferences.SHARE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.apply();
        edit.putString(key, value);
        edit.commit();

        Log.d("WhereAreYou", "Save :: " + key + " :: " + value);
    }

    private static void saveSharedPreference(Context ctx, String key, Boolean value)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Preferences.SHARE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.apply();
        edit.putBoolean(key, value);
        edit.commit();

        Log.d("WhereAreYou", "Save :: " + key + " :: " + value.toString());
    }

    // click handlers
    /**
     * Launch test alert
     *
     * @param view
     */
    public void testAlertAction(View view) {
        Intent alertIntent = new Intent(this, AlertActivity.class);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        alertIntent.putExtra("ALERT_TYPE", "loud");
        alertIntent.putExtra("PHONE_NUMBER", "+4912345678901");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        alertIntent.putExtra("SMS_MESSAGE",  sharedPref.getString(Preferences.KEY_TRIGGER1,
                getResources().getString(R.string.trigger_text1)));

        startActivity(alertIntent);
    }

    /**
     * Launch test mute
     *
     * @param view
     */
    public void testMuteAction(View view) {
        Intent alertIntent = new Intent(this, AlertActivity.class);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        alertIntent.putExtra("ALERT_TYPE", "mute");
        alertIntent.putExtra("PHONE_NUMBER", "+4912345678901");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        alertIntent.putExtra("SMS_MESSAGE",  sharedPref.getString(Preferences.KEY_TRIGGER2,
                getResources().getString(R.string.trigger_text2)));

        startActivity(alertIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item=menu.findItem(R.id.action_share);
        share = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        share.setOnShareTargetSelectedListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        if (id == R.id.action_help) {
            Intent helpIntent = new Intent(this, HelpActivity.class);
            startActivity(helpIntent);
            return true;
        }


        if (id == R.id.action_share) {
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source,
                                         Intent intent) {
        Toast.makeText(this,
                intent.getComponent().toString(),
                Toast.LENGTH_LONG
        ).show();

        return(false);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return GetLoudFragment.newInstance();
            }
            return GetMuteFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_tab1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_tab2).toUpperCase(l);
            }
            return null;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class GetLoudFragment extends Fragment {
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GetLoudFragment newInstance() {
            GetLoudFragment fragment = new GetLoudFragment();
            return fragment;
        }

        public GetLoudFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_get_loud, container, false);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String trigger1 = sharedPref.getString(Preferences.KEY_TRIGGER1, getResources().getString(R.string.trigger_text1));
            Boolean allowVibrate = sharedPref.getBoolean(Preferences.KEY_ALLOW_VIBRATE, true);
            Boolean allowAudio = sharedPref.getBoolean(Preferences.KEY_ALLOW_AUDIO, true);

            final TextView tvGetLoudTrigger = (TextView) rootView.findViewById(R.id.getLoudTrigger);
            if (tvGetLoudTrigger != null) {
                tvGetLoudTrigger.setText(trigger1);
                tvGetLoudTrigger.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        saveSharedPreference(getActivity(), Preferences.KEY_TRIGGER1, charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            }

            CheckBox cbGetLoudVibrate = (CheckBox) rootView.findViewById(R.id.getLoud_vibrate);
            if (cbGetLoudVibrate != null) {
                cbGetLoudVibrate.setChecked(allowVibrate);
            }
            CheckBox cbGetLoudAudio = (CheckBox) rootView.findViewById(R.id.getLoud_playMusic);
            if (cbGetLoudAudio != null) {
                cbGetLoudAudio.setChecked(allowAudio);
            }

            Log.i("WhereAreYou", "trigger1=" + trigger1);
            Log.i("WhereAreYou", "allowVibrate=" + allowVibrate.toString());
            Log.i("WhereAreYou", "allowAudio=" + allowAudio.toString());


            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (view != null &&
                            view.getId() != R.id.getLoudTrigger &&
                            tvGetLoudTrigger != null) {
                        tvGetLoudTrigger.clearFocus();

                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(tvGetLoudTrigger.getWindowToken(), 0);
                    }
                    return false;
                }
            });


            return rootView;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class GetMuteFragment extends Fragment {
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GetMuteFragment newInstance() {
            GetMuteFragment fragment = new GetMuteFragment();
            return fragment;
        }

        public GetMuteFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mute, container, false);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String trigger2 = sharedPref.getString(Preferences.KEY_TRIGGER2, getResources().getString(R.string.trigger_text2));


            final TextView tvGetMuteTrigger = (TextView) rootView.findViewById(R.id.muteTrigger);
            if (tvGetMuteTrigger != null) {
                tvGetMuteTrigger.setText(trigger2);
                tvGetMuteTrigger.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        saveSharedPreference(getActivity(), Preferences.KEY_TRIGGER2, charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            }

            Map<Integer, Boolean> checkboxes = new HashMap<>();
            checkboxes.put(R.id.mute_ring, sharedPref.getBoolean(Preferences.KEY_MUTE_RING, true));
            checkboxes.put(R.id.mute_music, sharedPref.getBoolean(Preferences.KEY_MUTE_MUSIC, true));
            checkboxes.put(R.id.mute_alarm, sharedPref.getBoolean(Preferences.KEY_MUTE_ALARM, false));
            checkboxes.put(R.id.mute_other, sharedPref.getBoolean(Preferences.KEY_MUTE_OTHER, false));

            for (Integer elmId: checkboxes.keySet()) {
                CheckBox cbx = (CheckBox) rootView.findViewById(elmId);
                if (cbx != null) {
                    cbx.setChecked(checkboxes.get(elmId));
                }
            }

            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (view != null &&view.getId() != R.id.muteTrigger && tvGetMuteTrigger != null) {
                        tvGetMuteTrigger.clearFocus();
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(tvGetMuteTrigger.getWindowToken(), 0);
                    }
                    return false;
                }
            });

            Log.i("WhereAreYou", "trigger2=" + trigger2);

            return rootView;
        }

    }
}
