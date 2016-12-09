package space.eya.mutiful;

import java.util.Locale;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class HelpActivity extends ActionBarActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final ImageView pb1 = (ImageView) findViewById(R.id.progressBar1);
        final ImageView pb2 = (ImageView) findViewById(R.id.progressBar2);
        final ImageView pb3 = (ImageView) findViewById(R.id.progressBar3);
        final Drawable active = getResources().getDrawable(R.drawable.slider_tab_active);
        final Drawable inactive = getResources().getDrawable(R.drawable.slider_tab_inactive);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pb1.setImageDrawable(inactive);
                pb2.setImageDrawable(inactive);
                pb3.setImageDrawable(inactive);

                switch (position) {
                    case 2:
                        pb3.setImageDrawable(active);
                        break;
                    case 1:
                        pb2.setImageDrawable(active);
                        break;
                    case 0:
                        pb1.setImageDrawable(active);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_help_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_help_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_help_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView;

            switch (tabNumber) {
                case 3: /* Alert */
                    rootView = inflater.inflate(R.layout.fragment_help_alert, container, false);
                    break;

                case 2: /* MUTE */
                    rootView = inflater.inflate(R.layout.fragment_help_mute, container, false);
                    break;

                case 1: /* GET LOUD */
                default:
                    rootView = inflater.inflate(R.layout.fragment_help_getloud, container, false);
                    break;
            }

            return rootView;
        }
    }

}
