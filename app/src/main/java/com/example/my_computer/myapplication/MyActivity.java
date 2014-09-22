package com.example.my_computer.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ClassLib.DataBaseHelper;
import ClassLib.LetterImageView;
import ClassLib.SampleData;


public class MyActivity extends ActionBarActivity implements ActionBar.TabListener {


    static FullscreenActivity.Custum_Class Loc_custum_class = FullscreenActivity.custum_class;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


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
                            .setTabListener(this)
            );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public static class Contact_Frag extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public DataBaseHelper dataBaseHelper;
        Contact_Adapter myArrayAdapter = null;

        public static Contact_Frag newInstance(int sectionNumber) {
            Contact_Frag fragment = new Contact_Frag();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //  dataBaseHelper = new DataBaseHelper(getActivity());


            View rootView = inflater.inflate(R.layout.list, container, false);

            final SwipeListView swipelistview = (SwipeListView) rootView.findViewById(R.id.example_swipe_lv_list);
            if (Loc_custum_class.getRead_contactses() != null) {


                Contact_Adapter adapter = new Contact_Adapter(getActivity(), Loc_custum_class.getRead_contactses());
                swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
                    @Override
                    public void onOpened(int position, boolean toRight) {


                    }

                    @Override
                    public void onClosed(int position, boolean fromRight) {
                    }

                    @Override
                    public void onListChanged() {


                    }

                    @Override
                    public void onMove(int position, float x) {
                    }

                    @Override
                    public void onStartOpen(int position, int action, boolean right) {
                        Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                    }

                    @Override
                    public void onStartClose(int position, boolean right) {
                        Log.d("swipe", String.format("onStartClose %d", position));
                    }

                    @Override
                    public void onClickFrontView(int position) {
                        Log.d("swipe", String.format("onClickFrontView %d", position));


                        swipelistview.openAnimate(position); //when you touch front view it will open


                    }

                    @Override
                    public void onClickBackView(int position) {
                        Log.d("swipe", String.format("onClickBackView %d", position));

                        swipelistview.closeAnimate(position);//when you touch back view it will close
                    }

                    @Override
                    public void onDismiss(int[] reverseSortedPositions) {

                    }

                });
                swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT);
                swipelistview.setAnimationTime(300);
                swipelistview.setAdapter(adapter);
            }

            return rootView;


        }

        public int convertDpToPixel(float dp) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return (int) px;
        }

        class Contact_Adapter extends ArrayAdapter<Read_contacts> {
            private final Activity context;
            private final List<Read_contacts> names;


            public Contact_Adapter(Activity context, List<Read_contacts> names) {

                super(context, android.R.layout.simple_list_item_1, names);
                this.context = context;
                this.names = names;

            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater;

                View rowView = convertView;
                if (rowView == null) {
                    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.custom_row, null);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                    viewHolder.letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);
                    viewHolder.letterImageView.setOval(true);
                    viewHolder.button1 = (ImageView) rowView.findViewById(R.id.swipe_button1);
                    viewHolder.button2 = (ImageView) rowView.findViewById(R.id.swipe_button2);
                    viewHolder.button3 = (ImageView) rowView.findViewById(R.id.swipe_button3);
                    rowView.setTag(viewHolder);

                }


                Read_contacts read_contacts = names.get(position);
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                viewHolder.name.setText(read_contacts.getName());
                viewHolder.letterImageView.setLetter(read_contacts.getName().charAt(0));
                viewHolder.button1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 1 Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.button2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 2 Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.button3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 3 Clicked", Toast.LENGTH_SHORT);
                    }
                });
                return rowView;
            }

            class ViewHolder {
                ImageView button1;
                ImageView button2;
                ImageView button3;
                TextView name;
                TextView number;
                LetterImageView letterImageView;

            }


        }
    }

    public static class Call_log_Frag extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Call_log_Adapter myArrayAdapter = null;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Call_log_Frag newInstance(int sectionNumber) {
            Call_log_Frag fragment = new Call_log_Frag();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.list, container, false);
            final SwipeListView swipelistview = (SwipeListView) rootView.findViewById(R.id.example_swipe_lv_list);

            if (Loc_custum_class.getRead_call_logs() != null) {
                myArrayAdapter = new Call_log_Adapter(getActivity(), Loc_custum_class.getRead_call_logs());


                swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
                    @Override
                    public void onOpened(int position, boolean toRight) {


                    }

                    @Override
                    public void onClosed(int position, boolean fromRight) {
                    }

                    @Override
                    public void onListChanged() {


                    }

                    @Override
                    public void onMove(int position, float x) {
                    }

                    @Override
                    public void onStartOpen(int position, int action, boolean right) {
                        Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                    }

                    @Override
                    public void onStartClose(int position, boolean right) {
                        Log.d("swipe", String.format("onStartClose %d", position));
                    }

                    @Override
                    public void onClickFrontView(int position) {
                        Log.d("swipe", String.format("onClickFrontView %d", position));


                        swipelistview.openAnimate(position); //when you touch front view it will open


                    }

                    @Override
                    public void onClickBackView(int position) {
                        Log.d("swipe", String.format("onClickBackView %d", position));

                        swipelistview.closeAnimate(position);//when you touch back view it will close
                    }

                    @Override
                    public void onDismiss(int[] reverseSortedPositions) {

                    }

                });
                swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there are five swiping modes

//           swipelistview.setOffsetLeft(convertDpToPixel(0f));
//            swipelistview.setOffsetRight(convertDpToPixel(80f));
                swipelistview.setAnimationTime(300); // Animation time
                swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

//                swipelistview.notifyDataSetChanged();
                swipelistview.setAdapter(myArrayAdapter);
            }


            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }


        class Call_log_Adapter extends ArrayAdapter<Read_call_log> {
            private final Activity context;
            private final List<Read_call_log> names;

            public Call_log_Adapter(Activity context, List<Read_call_log> names) {

                super(context, android.R.layout.simple_list_item_1, names);
                this.context = context;
                this.names = names;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater;

                View rowView = convertView;
                if (rowView == null) {
                    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.custom_row, null);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                    viewHolder.letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);

                    viewHolder.button1 = (ImageView) rowView.findViewById(R.id.swipe_button1);
                    viewHolder.button2 = (ImageView) rowView.findViewById(R.id.swipe_button2);
                    viewHolder.button3 = (ImageView) rowView.findViewById(R.id.swipe_button3);
                    rowView.setTag(viewHolder);
                }

                Read_call_log s = names.get(position);
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                viewHolder.name.setText(s.getName());
                viewHolder.letterImageView.setLetter(s.getName().charAt(0));
                viewHolder.button1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 1 Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.button2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 2 Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.button3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 3 Clicked", Toast.LENGTH_SHORT);
                    }
                });
                return rowView;
            }


            class ViewHolder {
                ImageView button1;
                ImageView button2;
                ImageView button3;
                TextView name;
                TextView number;
                LetterImageView letterImageView;

            }

        }

    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            return rootView;
        }
    }

    public static class Message_Frag extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Message_Frag_Adapter myArrayAdapter = null;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Message_Frag newInstance(int sectionNumber) {
            Message_Frag fragment = new Message_Frag();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.list, container, false);
            final SwipeListView swipelistview = (SwipeListView) rootView.findViewById(R.id.example_swipe_lv_list);

            if (Loc_custum_class.getRead_smses() != null) {
                myArrayAdapter = new Message_Frag_Adapter(getActivity(), Loc_custum_class.getRead_smses());
                swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
                    @Override
                    public void onOpened(int position, boolean toRight) {


                    }

                    @Override
                    public void onClosed(int position, boolean fromRight) {
                    }

                    @Override
                    public void onListChanged() {


                    }

                    @Override
                    public void onMove(int position, float x) {
                    }

                    @Override
                    public void onStartOpen(int position, int action, boolean right) {
                        Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                    }

                    @Override
                    public void onStartClose(int position, boolean right) {
                        Log.d("swipe", String.format("onStartClose %d", position));
                    }

                    @Override
                    public void onClickFrontView(int position) {
                        Log.d("swipe", String.format("onClickFrontView %d", position));


                        swipelistview.openAnimate(position); //when you touch front view it will open


                    }

                    @Override
                    public void onClickBackView(int position) {
                        Log.d("swipe", String.format("onClickBackView %d", position));

                        swipelistview.closeAnimate(position);//when you touch back view it will close
                    }

                    @Override
                    public void onDismiss(int[] reverseSortedPositions) {

                    }

                });
                swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there are five swiping modes
                swipelistview.setAnimationTime(300);
                swipelistview.setAdapter(myArrayAdapter);
            }


            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }


        class Message_Frag_Adapter extends ArrayAdapter<Read_sms> {
            private final Activity context;
            private final List<Read_sms> names;

            public Message_Frag_Adapter(Activity context, List<Read_sms> names) {

                super(context, android.R.layout.simple_list_item_1, names);
                this.context = context;
                this.names = names;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater;

                View rowView = convertView;
                if (rowView == null) {
                    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.custom_row, null);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                    viewHolder.letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);

                    viewHolder.button1 = (ImageView) rowView.findViewById(R.id.swipe_button1);
                    viewHolder.button2 = (ImageView) rowView.findViewById(R.id.swipe_button2);
                    viewHolder.button3 = (ImageView) rowView.findViewById(R.id.swipe_button3);
                    rowView.setTag(viewHolder);
                }

                Read_sms s = names.get(position);
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                viewHolder.name.setText(s.getName());
                viewHolder.letterImageView.setLetter(s.getName().charAt(0));
                viewHolder.button1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 1 Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.button2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 2 Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.button3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Button 3 Clicked", Toast.LENGTH_SHORT);
                    }
                });
                return rowView;
            }


            class ViewHolder {
                ImageView button1;
                ImageView button2;
                ImageView button3;
                TextView name;
                TextView number;
                LetterImageView letterImageView;

            }

        }

    }

    public static class StaggeredGridFragment extends Fragment implements
            AbsListView.OnScrollListener, AbsListView.OnItemClickListener {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private StaggeredGridView mGridView;
        private boolean mHasRequestedMore;
        private SampleAdapter mAdapter;

        private ArrayList<String> mData;


        public static StaggeredGridFragment newInstance(int sectionNumber) {
            StaggeredGridFragment fragment = new StaggeredGridFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

//        @Override
//        public void onCreate(final Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setRetainInstance(true);
//        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_sgv, container, false);
        }

        @Override
        public void onActivityCreated(final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mGridView = (StaggeredGridView) getView().findViewById(R.id.grid_view);

            if (savedInstanceState == null) {
                final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

                View header = layoutInflater.inflate(R.layout.list_item_header_footer, null);
                View footer = layoutInflater.inflate(R.layout.list_item_header_footer, null);
                TextView txtHeaderTitle = (TextView) header.findViewById(R.id.txt_title);
                TextView txtFooterTitle = (TextView) footer.findViewById(R.id.txt_title);
                txtHeaderTitle.setText("THE HEADER!");
                txtFooterTitle.setText("THE FOOTER!");

                mGridView.addHeaderView(header);
                mGridView.addFooterView(footer);
            }

            if (mAdapter == null) {
                mAdapter = new SampleAdapter(getActivity(), R.id.txt_line1);
            }

            if (mData == null) {
                mData = SampleData.generateSampleData();
            }

            for (String data : mData) {
                mAdapter.add(data);
            }

            mGridView.setAdapter(mAdapter);
            mGridView.setOnScrollListener(this);
            mGridView.setOnItemClickListener(this);
        }

        @Override
        public void onScrollStateChanged(final AbsListView view, final int scrollState) {

        }

        @Override
        public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {

            // our handling
            if (!mHasRequestedMore) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen >= totalItemCount) {

                    mHasRequestedMore = true;
                    onLoadMoreItems();
                }
            }
        }

        private void onLoadMoreItems() {
            final ArrayList<String> sampleData = SampleData.generateSampleData();
            for (String data : sampleData) {
                mAdapter.add(data);
            }
            // stash all the data in our backing store
            mData.addAll(sampleData);
            // notify the adapter that we can update now
            mAdapter.notifyDataSetChanged();
            mHasRequestedMore = false;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Toast.makeText(getActivity(), "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return StaggeredGridFragment.newInstance(position + 1);

                case 1:
                    return Contact_Frag.newInstance(position + 1);
//                return PlaceholderFragment.newInstance(position + 1);
                case 2:
                    //   return PlaceholderFragment.newInstance(position + 1);


                    return Message_Frag.newInstance(position + 1);

                case 3:
                    //  return PlaceholderFragment.newInstance(position + 1);
                    return Call_log_Frag.newInstance(position + 1);

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
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
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }


}
