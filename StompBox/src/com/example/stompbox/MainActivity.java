package com.example.stompbox;

import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
	private static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    // The {@link ViewPager} that will host the section contents.
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager); // pagerはactivity_main.xml内で定義されている。
        mViewPager.setAdapter(mSectionsPagerAdapter);	// Sectionsと言ってるケド実態はFragment。
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	Log.v(TAG, "onPageSelected:" + position);
                actionBar.setSelectedNavigationItem(position);
            }
        });
        actionBar.addTab(actionBar.newTab().setText("メイン").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("ダミー1").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("ダミー2").setTabListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_finish){
        	finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    	Log.v(TAG, "onTabSelected");
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    // A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.v(TAG, "FragmentPagerAdapter.Constructor");
        }
        @Override
        public Fragment getItem(int position) {
            Log.v(TAG, "FragmentPagerAdapter.getItem:" + position);
            if(position == 0){
            	return new MyFragment();
            }else{
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1);
            }
        }
        @Override
        public int getCount() {
            return 3;	// Show 3 total pages.
        }
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    // A placeholder fragment containing a simple view.
    public static class PlaceholderFragment extends Fragment {
    	// The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        	Log.v(TAG, "Fragment.Constructor");
        }
        // PagerAdapter側でgetItem()が呼ばれる度にここでFragmentのインスタンスを生成して返している。
        // Returns a new instance of this fragment for the given section number.
        public static PlaceholderFragment newInstance(int sectionNumber) {
        	Log.v(TAG, "Fragment.newInstance:" + sectionNumber);
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	Log.v(TAG, "Fragment.onCreateView");
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    public static class MyFragment extends Fragment {
    	private ListView activityList;
    	private ArrayAdapter<String> listAdapter;
    	private Class<?>[] pageClasses = {
    		com.example.stompbox.background.MainActivity.class, 
    		com.example.stompbox.alarm.MainActivity.class, 
    		com.example.stompbox.network.MainActivity.class, 
    		com.example.stompbox.storage.MainActivity.class, 
    		com.example.stompbox.barcode.ScanActivity.class, 
    		com.example.stompbox.barcode.MainActivity.class, 
    		com.example.stompbox.homeicon.MainActivity.class
    	};
    	public MyFragment(){
    		Log.v(TAG, "MyFragment.Constructor");
    	}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	Log.v(TAG, "MyFragment.onCreateView");
            View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
            listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
            listAdapter.add("background");
            listAdapter.add("alarm");
            listAdapter.add("network");
            listAdapter.add("storage");
            listAdapter.add("barcode scan");
            listAdapter.add("barcode gen");
            listAdapter.add("widget");
            activityList = (ListView)rootView.findViewById(R.id.listView1);
            activityList.setAdapter(listAdapter);
            activityList.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.v(TAG, "ListView.onItemClick:" + position + ", id:" + id);
					if(position < pageClasses.length){
						Intent intent = new Intent(getActivity(), pageClasses[position]);
						startActivity(intent);
					}
				}
            });
            return rootView;
        }
    }
}
