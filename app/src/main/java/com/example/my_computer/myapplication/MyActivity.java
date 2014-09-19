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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class MyActivity extends ActionBarActivity implements ActionBar.TabListener {


    SectionsPagerAdapter mSectionsPagerAdapter;


    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

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
            dataBaseHelper = new DataBaseHelper(getActivity());
            View rootView = inflater.inflate(R.layout.list, container, false);


            ListView listView = (ListView) rootView
                    .findViewById(R.id.listView);

            if (Sms.Read_Contact(getActivity()) != null) {

                myArrayAdapter = new Contact_Adapter(getActivity(), Sms.Read_Contact(getActivity()));
            }

            dataBaseHelper.getAllContacts();
            listView.setAdapter(myArrayAdapter);

            return rootView;
        }

        class Contact_Adapter extends ArrayAdapter<Sms.Read_contacts> {
            private final Activity context;
            private final List<Sms.Read_contacts> names;


            public Contact_Adapter(Activity context, List<Sms.Read_contacts> names) {

                super(context, android.R.layout.simple_list_item_1, names);
                this.context = context;
                this.names = names;

            }

            @Override
            public void sort(Comparator<? super Sms.Read_contacts> comparator) {
                super.sort(comparator);


            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater;

                View rowView = convertView;
                if (rowView == null) {
                    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.cus_list_contact, null);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                    viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.check);
                    viewHolder.letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);
                    viewHolder.letterImageView.setOval(true);
                    rowView.setTag(viewHolder);

                }

                Sms.Read_contacts s = names.get(position);
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                viewHolder.name.setText(s.getName());


                viewHolder.letterImageView.setLetter(s.getName().charAt(0));

                if (viewHolder.checkBox.isChecked()) {


                    viewHolder.checkBox.setChecked(true);

                } else {
                    viewHolder.checkBox.setChecked(false);
                }


//                viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//
//                        if (compoundButton.isChecked())
//                        {
//
//
//                        }
//
//                    }
//                });


                return rowView;
            }

            class ViewHolder {
                public TextView name;
                public TextView number;
                public CheckBox checkBox;
                public ImageView contact_img;
                public ImageView contact_type;
                public LetterImageView letterImageView;

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
            ListView listView = (ListView) rootView
                    .findViewById(R.id.listView);
            myArrayAdapter = new Call_log_Adapter(getActivity(), Sms.Read_Call_Log(getActivity()));

            //         getActivity().startActivity(new Intent(getActivity(), PinnedSectionListActivity.class));


            listView.setAdapter(myArrayAdapter);
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


        class Call_log_Adapter extends ArrayAdapter<Sms.Read_call_log> {
            private final Activity context;
            private final List<Sms.Read_call_log> names;

            public Call_log_Adapter(Activity context, List<Sms.Read_call_log> names) {

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
                    rowView = inflater.inflate(R.layout.cus_list_contact, null);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) rowView.findViewById(R.id.name);

                    viewHolder.letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);

                    rowView.setTag(viewHolder);
                }

                Sms.Read_call_log s = names.get(position);
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                viewHolder.name.setText(s.getName());
                //    viewHolder.number.setText(s.getNumber());

                viewHolder.letterImageView.isOval();
                viewHolder.letterImageView.setLetter('C');

                return rowView;

            }


            class ViewHolder {
                public TextView name;
                public TextView number;
                public ImageView contact_img;
                public ImageView contact_type;
                public LetterImageView letterImageView;
            }

        }

    }

    public static class Message_Frag extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";


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
            ListView listView = (ListView) rootView
                    .findViewById(R.id.listView);
            Message_Frag_Adapter myArrayAdapter = new Message_Frag_Adapter(getActivity(), Sms.Read_Sms(getActivity()));
            listView.setAdapter(myArrayAdapter);
            return rootView;
        }

        class Message_Frag_Adapter extends ArrayAdapter<Sms.Read_sms> {
            private final Activity context;
            private final List<Sms.Read_sms> names;

            public Message_Frag_Adapter(Activity context, List<Sms.Read_sms> names) {

                super(context, android.R.layout.simple_list_item_1, names);
                this.context = context;
                this.names = names;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater;

                View rowView = convertView;
                if (rowView == null) {

                    // reuse views

                    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.cus_list_contact, null);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                    //        viewHolder.number = (TextView) rowView.findViewById(R.id.number);

                    viewHolder.letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);
                    rowView.setTag(viewHolder);
                }

                Sms.Read_sms s = names.get(position);
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();

                viewHolder.name.setText(s.getName());

                viewHolder.letterImageView.setLetter('C');


                return rowView;

            }


            class ViewHolder {
                public TextView name;
                public TextView number;
                public ImageView contact_img;
                public ImageView contact_type;
                public LetterImageView letterImageView;
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
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);

                case 1:
                    return Contact_Frag.newInstance(position + 1);

                case 2:
                    return Message_Frag.newInstance(position + 1);

                case 3:

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
