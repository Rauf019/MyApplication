package com.example.my_computer.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import ClassLib.Contact;
import ClassLib.DataBaseHelper;
import ClassLib.LetterImageView;
import ClassLib.SampleAdapter;


public class MyActivity extends ActionBarActivity implements ActionBar.TabListener {


    public static FullscreenActivity.Custum_Class Loc_custum_class;
    static FragmentManager supportFragmentManager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Loc_custum_class = FullscreenActivity.custum_class;
        supportFragmentManager = getSupportFragmentManager();
        if (Loc_custum_class == null) {

            startActivity(new Intent(this, FullscreenActivity.class));

        } else if (Loc_custum_class.is_intialize) {
            setContentView(R.layout.activity_my);


            final ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            //   getApplicationContext().deleteDatabase("Call_blocker");
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {

                    actionBar.setSelectedNavigationItem(position);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                }
            });


            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this)
                );
            }


        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

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
        static int pre_loc;
        static boolean pre_loc_val;
        Adapter myArrayAdapter = null;
        DataBaseHelper dataBaseHelper;

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


            View rootView = inflater.inflate(R.layout.list, container, false);
            dataBaseHelper = new DataBaseHelper(getActivity());
//            final SwipeListView swipelistview = (SwipeListView) rootView.findViewById(R.id.example_swipe_lv_list);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);

            if (Loc_custum_class.getRead_contactses() != null) {


                Adapter adapter = new Adapter(getActivity(), Loc_custum_class.getRead_contactses());

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        final Read_contacts itemAtPosition = (Read_contacts) parent.getItemAtPosition(position);

                        Contact contact = dataBaseHelper.getContact(itemAtPosition.getNumber());

                        if (contact.get_phoneNumber() == null) {

                            dataBaseHelper.addContact(new Contact(itemAtPosition.getNumber(), itemAtPosition.getName()));
                        }

                        final Contact contact1 = dataBaseHelper.getContact(itemAtPosition.getNumber());

                        final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>(); // Where we track the selected items
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final boolean is_call = false,
                                is_msg = false,
                                is_both = false;
                        String[] toppings = {"Call", "Message", "Both"};
                        builder.setTitle("Block")

                                .setMultiChoiceItems(toppings, null,
                                        new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which,
                                                                boolean isChecked) {


                                                switch (which) {

                                                    case 0:

                                                        if (isChecked) {

                                                            hashMap.put("Call", true);
                                                        }

                                                        break;
                                                    case 1:
                                                        if (isChecked) {
                                                            hashMap.put("Msg", true);
                                                        }
                                                        break;
                                                    case 2:

                                                        if (isChecked) {
                                                            hashMap.put("Both", true);
                                                        }
                                                        break;
                                                }


                                            }
                                        })
                                        // Set the action buttons
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                        Boolean both = hashMap.get("Both") != null ? hashMap.get("Both") : false;
                                        Boolean call = hashMap.get("Call") != null ? hashMap.get("Call") : false;
                                        Boolean msg = hashMap.get("Msg") != null ? hashMap.get("Msg") : false;

                                        if (both) {

                                            dataBaseHelper.updateContact(new Contact(itemAtPosition.getNumber(),
                                                    itemAtPosition.getName(), true, true, itemAtPosition.getPhoto()));
                                            List<Contact> allContacts = dataBaseHelper.getAllContacts();
                                        } else {
                                            dataBaseHelper.updateContact(new Contact(itemAtPosition.getNumber(),
                                                    itemAtPosition.getName(), call, msg, itemAtPosition.getPhoto()));
                                            List<Contact> allContacts = dataBaseHelper.getAllContacts();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                    }
                                });
                        builder.show();


//
                    }
                });


                listView.setAdapter(adapter);


            }

            return rootView;


        }


    }

    public static class Call_log_Frag extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        Adapter adapter = null;
        DataBaseHelper dataBaseHelper;

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

            dataBaseHelper = new DataBaseHelper(getActivity());
            View rootView = inflater.inflate(R.layout.list, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);

            if (Loc_custum_class.getRead_call_logs() != null) {
                adapter = new Adapter(getActivity(), Loc_custum_class.getRead_call_logs());


            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    final Read_contacts itemAtPosition = (Read_contacts) parent.getItemAtPosition(position);


                    Contact contact = dataBaseHelper.getContact(itemAtPosition.getNumber());

                    if (contact.get_phoneNumber() == null) {

                        dataBaseHelper.addContact(new Contact(itemAtPosition.getNumber(), itemAtPosition.getName()));
                    }


                    final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>(); // Where we track the selected items
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    String[] toppings = {"Call", "Message", "Both"};
                    builder.setTitle("Block")

                            .setMultiChoiceItems(toppings, null,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which,
                                                            boolean isChecked) {


                                            switch (which) {

                                                case 0:

                                                    if (isChecked) {

                                                        hashMap.put("Call", true);
                                                    }

                                                    break;
                                                case 1:
                                                    if (isChecked) {
                                                        hashMap.put("Msg", true);
                                                    }
                                                    break;
                                                case 2:

                                                    if (isChecked) {
                                                        hashMap.put("Both", true);
                                                    }
                                                    break;
                                            }


                                        }
                                    })
                                    // Set the action buttons
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {


                                    Boolean both = hashMap.get("Both") != null ? hashMap.get("Both") : false;
                                    Boolean call = hashMap.get("Call") != null ? hashMap.get("Call") : false;
                                    Boolean msg = hashMap.get("Msg") != null ? hashMap.get("Msg") : false;

                                    if (both) {

                                        dataBaseHelper.updateContact(new Contact(itemAtPosition.getNumber(),
                                                itemAtPosition.getName(), true, true, itemAtPosition.getPhoto()));

                                    } else {
                                        dataBaseHelper.updateContact(new Contact(itemAtPosition.getNumber(),
                                                itemAtPosition.getName(), call, msg, itemAtPosition.getPhoto()));

                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {


                                }
                            });
                    builder.show();


//
                }
            });


            listView.setAdapter(adapter);


            return rootView;


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
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imageView.setBackgroundColor(Color.GREEN);

                }
            });
//            Bitmap bitmap
//                    = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.power);
            return rootView;
        }


        private void FloodFill(Bitmap bmp, Point pt, int targetColor, int replacementColor) {


            Queue<Point> q = new LinkedList<Point>();
            q.add(pt);
            while (q.size() > 0) {
                Point n = q.poll();
                if (bmp.getPixel(n.x, n.y) != targetColor)
                    continue;

                Point w = n, e = new Point(n.x + 1, n.y);
                while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                    bmp.setPixel(w.x, w.y, replacementColor);
                    if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
                        q.add(new Point(w.x, w.y - 1));
                    if ((w.y < bmp.getHeight() - 1)
                            && (bmp.getPixel(w.x, w.y + 1) == targetColor))
                        q.add(new Point(w.x, w.y + 1));
                    w.x--;
                }
                while ((e.x < bmp.getWidth() - 1)
                        && (bmp.getPixel(e.x, e.y) == targetColor)) {
                    bmp.setPixel(e.x, e.y, replacementColor);

                    if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
                        q.add(new Point(e.x, e.y - 1));
                    if ((e.y < bmp.getHeight() - 1)
                            && (bmp.getPixel(e.x, e.y + 1) == targetColor))
                        q.add(new Point(e.x, e.y + 1));
                    e.x++;
                }
            }
        }
    }

    public static class Message_Frag extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Adapter adapter = null;
        DataBaseHelper dataBaseHelper;

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

            dataBaseHelper = new DataBaseHelper(getActivity());
            View rootView = inflater.inflate(R.layout.list, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            if (Loc_custum_class.getRead_smses() != null) {
                adapter = new Adapter(getActivity(), Loc_custum_class.getRead_smses());

            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    try {
                        final Read_contacts itemAtPosition = (Read_contacts) parent.getItemAtPosition(position);


                        Contact contact = dataBaseHelper.getContact(itemAtPosition.getNumber());

                        if (contact.get_phoneNumber() == null) {

                            dataBaseHelper.addContact(new Contact(itemAtPosition.getNumber(), itemAtPosition.getName()));
                        }


                        final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>(); // Where we track the selected items
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        String[] toppings = {"Call", "Message", "Both"};
                        builder.setTitle("Block")

                                .setMultiChoiceItems(toppings, null,
                                        new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which,
                                                                boolean isChecked) {


                                                switch (which) {

                                                    case 0:

                                                        if (isChecked) {

                                                            hashMap.put("Call", true);
                                                        }

                                                        break;
                                                    case 1:
                                                        if (isChecked) {
                                                            hashMap.put("Msg", true);
                                                        }
                                                        break;
                                                    case 2:

                                                        if (isChecked) {
                                                            hashMap.put("Both", true);
                                                        }
                                                        break;
                                                }


                                            }
                                        })
                                        // Set the action buttons
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                        Boolean both = hashMap.get("Both") != null ? hashMap.get("Both") : false;
                                        Boolean call = hashMap.get("Call") != null ? hashMap.get("Call") : false;
                                        Boolean msg = hashMap.get("Msg") != null ? hashMap.get("Msg") : false;

                                        if (both) {

                                            dataBaseHelper.updateContact(new Contact(itemAtPosition.getNumber(),
                                                    itemAtPosition.getName(), true, true, itemAtPosition.getPhoto()));

                                        } else {
                                            dataBaseHelper.updateContact(new Contact(itemAtPosition.getNumber(),
                                                    itemAtPosition.getName(), call, msg, itemAtPosition.getPhoto()));

                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                    }
                                });
                        builder.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//
                }
            });


            listView.setAdapter(adapter);


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


    }

    public static class StaggeredGridFragment extends Fragment implements
            AbsListView.OnScrollListener, AbsListView.OnItemClickListener {

        private static final String ARG_SECTION_NUMBER = "section_number";
        List<Contact> contact1;
        TextView txtHeaderTitle;
        DataBaseHelper dataBaseHelper;
        private StaggeredGridView mGridView;
        private boolean mHasRequestedMore;
        private SampleAdapter mAdapter;

        public static StaggeredGridFragment newInstance(int sectionNumber) {
            StaggeredGridFragment fragment = new StaggeredGridFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            dataBaseHelper = new DataBaseHelper(getActivity());


        }


        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

            View inflate = inflater.inflate(R.layout.activity_sgv, container, false);

            mGridView = (StaggeredGridView) inflate.findViewById(R.id.grid_view);


            List<Contact> allContacts_true = dataBaseHelper.getAllContacts_true();
            List<Contact> allContacts_true1 = dataBaseHelper.getAllContacts();

            mAdapter = new SampleAdapter(getActivity(), dataBaseHelper.getAllContacts_true());


            mGridView.setAdapter(mAdapter);
            mGridView.setOnScrollListener(this);
            mGridView.setOnItemClickListener(this);

            return inflate;
        }

        @Override
        public void onActivityCreated(final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

//
//            List<Contact> contact1 = dataBaseHelper.Sort_By(" IS_CALL_BLOCK ");
//            List<Contact> contact2 = dataBaseHelper.Sort_By(" IS_MSG_BLOCK ");
//            mGridView = (StaggeredGridView) getView().findViewById(R.id.grid_view);
//
//            if (savedInstanceState == null) {
//
//                final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
//                View header = layoutInflater.inflate(R.layout.list_item_header_footer, null);
//                TextView txtHeaderTitle = (TextView) header.findViewById(R.id.txt_title);
//                txtHeaderTitle.setText("BLOCK BY CALLS");
//                mGridView.addHeaderView(header);
//
//            }
//
//            if (mAdapter == null) {
//                mAdapter = new SampleAdapter(getActivity(), R.id.txt_line1, contact1);
//            }
//
//            if (mData == null) {
//                mData = SampleData.generateSampleData();
//            }
//
//            for (String data : mData) {
//                mAdapter.add(data);
//            }
//
//            mGridView.setAdapter(mAdapter);
//            mGridView.setOnScrollListener(this);
//            mGridView.setOnItemClickListener(this);
        }

        @Override
        public void onScrollStateChanged(final AbsListView view, final int scrollState) {

        }

        @Override
        public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {


//            if (!mHasRequestedMore) {
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if (lastInScreen >= totalItemCount) {
//
//                    mHasRequestedMore = true;
//                    onLoadMoreItems();
//                }
//            }
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Toast.makeText(getActivity(), "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
        }


    }

    static class Adapter extends ArrayAdapter<Read_contacts> {
        private final Activity context;
        private final List<Read_contacts> names;
        DataBaseHelper dataBaseHelper;

        //            HashMap< Integer,Read_contacts> mIdMap ;
        public Adapter(Activity context, List<Read_contacts> names) {

            super(context, android.R.layout.simple_list_item_1, names);
//                mIdMap = new HashMap<Integer, Read_contacts>();
            dataBaseHelper = new DataBaseHelper(getContext());
            this.context = context;
            this.names = names;


        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater;
            View rowView = convertView;
            try {

                if (rowView == null) {
                    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.custom_row, null);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                    viewHolder.letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);
                    rowView.setTag(viewHolder);

                }
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                final Read_contacts read_contacts = names.get(position);
                if (read_contacts.getName() == null) {
                    viewHolder.name.setText(read_contacts.getNumber());
                    viewHolder.letterImageView.setLetter(read_contacts.getNumber().charAt(0));
                } else {
                    viewHolder.name.setText(read_contacts.getName());
                    viewHolder.letterImageView.setLetter(read_contacts.getName().charAt(0));
                }


                return rowView;
            } catch (Exception e) {
                e.printStackTrace();
                return rowView;
            }
        }

        class ViewHolder {

            TextView name;

            LetterImageView letterImageView;

        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ;
                    return PlaceholderFragment.newInstance(position + 1);

                case 1:

                    return StaggeredGridFragment.newInstance(position + 1);


//                return PlaceholderFragment.newInstance(position + 1);
                case 2:
                    //   return PlaceholderFragment.newInstance(position + 1);
                    return Contact_Frag.newInstance(position + 1);


                case 3:
                    return Message_Frag.newInstance(position + 1);

                case 4:
                    return Call_log_Frag.newInstance(position + 1);

            }
            return null;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }


        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }


        @Override
        public int getItemPosition(Object object) {


            if (object.getClass().getName().equals(StaggeredGridFragment.class.getName())) {
                return POSITION_NONE;

            } else {
                return POSITION_UNCHANGED;
            }


//           android.app.Fragment fragment = (android.app.Fragment) object;
//
//
//
//
//


        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {


                case 0:
                    return getString(R.string.title_section0).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }

}
