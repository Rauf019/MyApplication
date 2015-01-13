package com.example.my_computer.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.CallLog;
import android.provider.ContactsContract;
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
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class MyActivity extends ActionBarActivity implements ActionBar.TabListener {

    static ViewPager mViewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ActionBar actionBar;
    private boolean doubleBackToExitPressedOnce;

    public static void startMyTask(AsyncTask asyncTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        } else {

            asyncTask.execute();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.view_pager);
            actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            try {
                ViewConfiguration config = ViewConfiguration.get(this);
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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

//      }
        } catch (Exception a) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, getString(R.string.FlurryAgent));
        FlurryAgent.logEvent("In Main Activity");
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogEvents(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = pref.edit();

        int id = item.getItemId();

        if (id == R.id.action_settings) {


            int key_name = pref.getInt("key_name", 0);

            builder.setTitle("Block Mode").setSingleChoiceItems(getResources().getStringArray(R.array.blockmode),
                    key_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {

                                case 0:
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    dialog.dismiss();
                                    break;
                                case 2:
                                    dialog.dismiss();
                                    break;
                                case 3:
                                    dialog.dismiss();
                                    break;
                                case 4:
                                    dialog.dismiss();
                                    break;

                            }

                            editor.putInt("key_name", which);
                            editor.commit();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();


            return true;
        } else if (id == R.id.action_settings1) {

            startActivity(new Intent(this, SettingsActivity.class));
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
        private static List<Read_contacts> contacts;
        public ListView listView;
        DataBaseHelper dataBaseHelper;
        View rootView;
        Adapter adapter;


        public Contact_Frag() {

        }

        public static Contact_Frag newInstance(int sectionNumber) {
            Contact_Frag fragment = new Contact_Frag();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            dataBaseHelper = new DataBaseHelper(getActivity());

            if (contacts == null) {

                contacts = new ArrayList<>();
                startMyTask(new Contact_task());

            }

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.list, container, false);
            listView = (ListView) rootView.findViewById(R.id.listView);

            if (contacts.size() != 0) {

                adapter = new Adapter(getActivity(), contacts);
                listView.setAdapter(adapter);

            } else {

                adapter = new Adapter(getActivity(), new ArrayList<Read_contacts>());
                listView.setAdapter(adapter);

            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    final Read_contacts itemAtPosition = (Read_contacts) parent.getItemAtPosition(position);

                    Contact contact = dataBaseHelper.getContact(itemAtPosition.getNumber());

                    if (contact.get_phoneNumber() == null) {

                        dataBaseHelper.addContact(new Contact(itemAtPosition.getNumber(), itemAtPosition.getName()));

                    }


                    final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    String[] toppings = {"Call", "Message", "Both"};
                    builder.setTitle(String.format("%s\n%s ( %s )", "Block ", ((itemAtPosition.getName() != null) ? itemAtPosition.getName() : ""), itemAtPosition.getNumber()))

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

            return rootView;
        }


        class Contact_task extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {


                Cursor c = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.PHOTO_URI}, null, null, null);

                if (c.moveToFirst()) {


                    do {

                        String nameCol = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        if (!(Character.isLetter(nameCol.codePointAt(0)))) {
                            nameCol = null;
                        }

                        int numCol = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int typeCol = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);

                        contacts.add(new Read_contacts(nameCol, c.getString(numCol), c.getString(typeCol)));
                    }


                    while (c.moveToNext());


                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (contacts.size() != 0) {

                    adapter.UpdateList(contacts);
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
        private static List<Read_contacts> msg;
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

        private static Data get_lookup(Context context, String Number) {
            Cursor c = null;
            try {
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(Number));
                c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.PHOTO_URI}, null, null, null);

                if (c.moveToFirst()) {
                    if (c.getString(0) != null) {
                        return new Data(c.getString(1), c.getString(0));
                    }
                } else {
                    return new Data(null, null);
                }

            } catch (Exception e) {
                e.getMessage();
            } finally {
                c.close();
            }
            return null;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            dataBaseHelper = new DataBaseHelper(getActivity());
            if (msg == null) {
                msg = new ArrayList<>();

                startMyTask(new Message_task());

            }


        }

        public String remove_plus(String phoneNumber) {
            try {
                if (phoneNumber.charAt(0) == '+') {
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "");
                    return "0" + String.valueOf(numberProto.getNationalNumber());
                } else {
                    return phoneNumber;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.list, container, false);
            final ListView listView = (ListView) rootView.findViewById(R.id.listView);


            if (msg.size() != 0) {

                adapter = new Adapter(getActivity(), msg);
                listView.setAdapter(adapter);

            } else {

                adapter = new Adapter(getActivity(), new ArrayList<Read_contacts>());
                listView.setAdapter(adapter);

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
                    builder.setTitle(String.format("%s\n%s ( %s )", "Block ", (itemAtPosition.getName() != null) ? itemAtPosition.getName() : "", itemAtPosition.getNumber()))

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


                }
            });

            listView.setAdapter(adapter);
            return rootView;

        }

        class Message_task extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    HashSet<String> hashSet = null;
                    Cursor c = getActivity().getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address"}, null, null, null);
                    msg = new ArrayList<>();

                    if (c.moveToFirst()) {


                        try {
                            hashSet = new HashSet<>();
                            do {

                                String string = remove_plus(c.getString(0));
                                hashSet.add(string);

                            }
                            while (c.moveToNext());


                            Iterator itr = hashSet.iterator();

                            while (itr.hasNext())

                            {
                                String next = (String) itr.next();

                                Data lookup = get_lookup(getActivity(), next);

                                msg.add(new Read_contacts(lookup.getName(),
                                        next,
                                        lookup.getPhoto_url()));


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


                if (msg.size() != 0) {

                    adapter.UpdateList(msg);
                }

            }

        }


    }

    public static class Calllog_Frag extends Fragment {


        private static final String ARG_SECTION_NUMBER = "section_number";
        private static ArrayList<Read_contacts> calllog;
        Adapter adapter = null;
        DataBaseHelper dataBaseHelper;

        public static Calllog_Frag newInstance(int sectionNumber) {
            Calllog_Frag fragment = new Calllog_Frag();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            dataBaseHelper = new DataBaseHelper(getActivity());
            if (calllog == null) {
                calllog = new ArrayList<>();
                startMyTask(new Calllog_task());

            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.list, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);

            if (calllog.size() != 0) {


                adapter = new Adapter(getActivity(), calllog);
                listView.setAdapter(adapter);

            } else {

                adapter = new Adapter(getActivity(), new ArrayList<Read_contacts>());
                listView.setAdapter(adapter);

            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    final Read_contacts itemAtPosition = (Read_contacts) parent.getItemAtPosition(position);


                    Contact contact = dataBaseHelper.getContact(itemAtPosition.getNumber());

                    if (contact.get_phoneNumber() == null) {

                        dataBaseHelper.addContact(new Contact(itemAtPosition.getNumber(), itemAtPosition.getName()));
                    }


                    final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    String[] toppings = {"Call", "Message", "Both"};
                    builder.setTitle(String.format("%s\n%s ( %s )", "Block ", (itemAtPosition.getName() != null) ? itemAtPosition.getName() : "", itemAtPosition.getNumber()))
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
                }
            });


            return rootView;

        }

        class Calllog_task extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    Cursor c = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"number", "name"}
                            , null, null, null);
                    HashSet<String> hashSet = null;

                    if (c.moveToFirst()) {

                        try {
                            hashSet = new HashSet<>();
                            do {

                                String string = remove_plus(c.getString(c.getColumnIndex(CallLog.Calls.NUMBER)));
                                hashSet.add(string);

                            }
                            while (c.moveToNext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {

                            for (String s : hashSet) {

                                Data lookup = get_lookup(getActivity(), s);

                                calllog.add(new Read_contacts(lookup.getName(),
                                        s,
                                        lookup.getPhoto_url()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            public String remove_plus(String phoneNumber) {
                try {
                    if (phoneNumber.charAt(0) == '+') {
                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "");
                        return "0" + String.valueOf(numberProto.getNationalNumber());
                    } else {
                        return phoneNumber;
                    }
                } catch (Exception e) {
                    return null;
                }
            }

            private Data get_lookup(Context context, String Number) {
                Cursor c = null;
                try {
                    Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(Number));
                    c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.PHOTO_URI}, null, null, null);

                    if (c.moveToFirst()) {
                        if (c.getString(0) != null) {
                            return new Data(c.getString(1), c.getString(0));
                        }
                    } else {
                        return new Data(null, null);
                    }

                } catch (Exception e) {
                    e.getMessage();
                } finally {
                    c.close();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (calllog.size() != 0) {

                    adapter.UpdateList(calllog);
                }

            }
        }

    }

    public static class StaggeredGridFragment extends Fragment implements View.OnClickListener {

        private static final String ARG_SECTION_NUMBER = "section_number";

        DataBaseHelper dataBaseHelper;
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

            dataBaseHelper = new DataBaseHelper(getActivity());

        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

            View inflate = inflater.inflate(R.layout.grid_view, container, false);
            GridView gridView = (GridView) inflate.findViewById(R.id.grid_view);
            List<Contact> allContacts_true = dataBaseHelper.getAllContacts();

            if (allContacts_true.size() <= 0) {

                View inflate1 = inflater.inflate(R.layout.emptylist, container, false);
                Button button = (Button) inflate1.findViewById(R.id.add_btn);
                button.setOnClickListener(this);

                return inflate1;

            } else {

                mAdapter = new SampleAdapter(getActivity(), allContacts_true);
                gridView.setAdapter(mAdapter);
            }

            return inflate;
        }


        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.add_btn) {
                mViewPager.setCurrentItem(1);
            }

        }
    }

    static class Adapter extends ArrayAdapter<Read_contacts> {

        DataBaseHelper dataBaseHelper;

        private Activity context = null;
        private List<Read_contacts> names = null;

        public Adapter(Activity context, List<Read_contacts> names) {

            super(context, android.R.layout.simple_list_item_1, names);

            try {

                dataBaseHelper = new DataBaseHelper(getContext());
                this.context = context;
                this.names = names;


            } catch (Exception e) {
                e.printStackTrace();
            }


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

//                if (rowView == null) {
                inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.custom_row, null);
//                    ViewHolder viewHolder = new ViewHolder();
                TextView number = (TextView) rowView.findViewById(R.id.textView4);
                TextView name = (TextView) rowView.findViewById(R.id.name);
                LetterImageView letterImageView = (LetterImageView) rowView.findViewById(R.id.iv_avatar);
//                    rowView.setTag(viewHolder);

//                }

//                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                final Read_contacts read_contacts = names.get(position);


                if (read_contacts.getName() == null) {
                    name.setText(read_contacts.getNumber());
                    letterImageView.setLetter(read_contacts.getNumber().charAt(0));
//                    number.setVisibility(View.GONE);


                } else {
                    name.setText(read_contacts.getName());
                    letterImageView.setLetter(read_contacts.getName().charAt(0));
                    number.setText(read_contacts.getNumber());
                }


                return rowView;
            } catch (Exception e) {
                e.printStackTrace();
                return rowView;
            }
        }

        void UpdateList(List<Read_contacts> msg) {

//            names.clear();
            names.addAll(msg);
            notifyDataSetChanged();

        }

        class ViewHolder {

            TextView name, number, date;

            LetterImageView letterImageView;

        }

    }

    public static class Data {
        String Photo_url;
        String Name;

        Data(String photo_url, String name) {

            if (photo_url != null) {

                Photo_url = photo_url;

            } else {

                Photo_url = null;

            }
            if (name != null) {

                Name = name;

            } else {

                Name = null;

            }


        }

        public String getName() {
            return Name;
        }

        public String getPhoto_url() {
            return Photo_url;
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

                    return StaggeredGridFragment.newInstance(position + 1);

                case 1:

                    return Contact_Frag.newInstance(position + 1);

                case 2:

                    return Message_Frag.newInstance(position + 1);

                case 3:
                    //            return Contact_Frag.newInstance(position + 1);
                    return Calllog_Frag.newInstance(position + 1);


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
            return 4;
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
