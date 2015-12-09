package ru.aldi_service.courier;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static ru.aldi_service.courier.GlobalData.getEmployee;
import static ru.aldi_service.courier.GlobalData.getEmployeeName;
import static ru.aldi_service.courier.PGutils.dbConnect;

public class MainActivity extends AppCompatActivity {
    static SQLiteDatabase db;
    static Cursor c, c2, c3;
    static String selection;
    static LinearLayout linearLayout1;
    static int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    DBHelper dbHelper;
    // String[] selectionArgs = new String[1];
    String[] columnsDeliveries = {"waybill", "addressee", "contact_person",
            "geography", "address", "phone", "cost_of_delivery", "addressee_payment", "additional_payment", "info",
// urgency must be converted to int
//            "urgency",
            "comment", "delivery_date"};
    String[] columnsDeliveriesInt = {"id", "delivery_list_id", "n_items"};
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private exchangeDB exchangeDBTask = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setTitle(getEmployeeName());
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        exchangeDBTask = new exchangeDB();
        exchangeDBTask.execute((Void) null);
        //String employee="'"+String.valueOf(getEmployee())+"'";
        //selectionArgs[0]=employee;
        selection = "employee_id = ";
        c = db.query("delivery_lists", null, selection + "'" + String.valueOf(getEmployee()) + "'", null, null, null, null);
        Log.d("delivery lists SQLite", " N = " + String.valueOf(c.getCount()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

//          Build screen content

            linearLayout1 = (LinearLayout) rootView.findViewById(R.id.linearLayout1);

            selection = "employee_id ='" + String.valueOf(getEmployee()) + "'";
            String LOG_TAG = "Build screen";
            String[] cols = {"id"};
            String orderBy = "urgency desc";
            c = db.query("delivery_lists", cols, selection, null, null, null, null);
            Log.d(LOG_TAG, "delivery list num = " + String.valueOf(c.getCount()));
            if (c != null && c.getCount() > 0) {
                String lists = "";
                int i = 0;
                if (c.moveToFirst()) {
                    do {
                        if (i > 0) lists += ",";
                        lists += "'" + String.valueOf(c.getInt(c.getColumnIndex("id"))) + "'";
                        i++;
                    } while (c.moveToNext());
                }
                selection = "delivery_list_id IN (" + lists + ")";
                c2 = db.query("deliveries", null, selection, null, null, null, orderBy);
                Log.d(LOG_TAG, "deliveries num = " + String.valueOf(c2.getCount()));
                int j = 0;
//                String waybill=" ",addressee=" ",address=" ";
                if (c2 != null && c2.getCount() > 0) {
                    if (c2.moveToFirst()) {
                        do {
                            /*
                            Waybill wb= new Waybill(getContext());
                            wb.setAcceptedBy(c2.getString(c2.getColumnIndex("accepted_by")));
                            wb.setAddress(c2.getString(c2.getColumnIndex("address")));
                            wb.setAddressee(c2.getString(c2.getColumnIndex("addressee")));
                            wb.setComment(c2.getString(c2.getColumnIndex("comment")));
                            wb.setContactPerson(c2.getString(c2.getColumnIndex("contact_person")));
                            wb.setCostOfDelivery(c2.getFloat(c2.getColumnIndex("cost_of_delivery")));
                            wb.setAdditionalPayment(c2.getFloat(c2.getColumnIndex("additional_payment")));
                            wb.setAddresseePayment(c2.getFloat(c2.getColumnIndex("addressee_payment")));
                            wb.setWeight(c2.getFloat(c2.getColumnIndex("weight")));
                            wb.setGeography(c2.getString(c2.getColumnIndex("geography")));
                            wb.setPhone(c2.getString(c2.getColumnIndex("phone")));
                            wb.setDeliveryDate(c2.getString(c2.getColumnIndex("delivery_date")));
                            wb.setDeliveryListId(c2.getInt(c2.getColumnIndex("delivery_list_id")));
                            wb.setnItems(c2.getInt(c2.getColumnIndex("n_items")));
                            wb.setWaybill(c2.getString(c2.getColumnIndex("waybill")));
                            wb.setId(c2.getInt(c2.getColumnIndex("id")));
                            wb.setInfo(c2.getString(c2.getColumnIndex("info")));
                            wb.setStatus(c2.getInt(c2.getColumnIndex("status")));
                            wb.setUrgency(c2.getInt(c2.getColumnIndex("urgency")));

                            linearLayout1.addView(wb.waybillView(inflater)); */
                            j++;
                        } while (c2.moveToNext());
                    }
                }
            }
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
            switch (position) {
                case 0:
                    return "Search";
                case 1:
                    return "Deliveries";
                case 2:
                    return "Pickups";
            }
            return null;
        }
    }

    public class exchangeDB extends AsyncTask<Void, Void, Boolean> {
        private final int PREPARED = 1;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        private Connection connection;
        private String from, to;
        private Date now;

        exchangeDB() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                now = new Date();
                GregorianCalendar calen = new GregorianCalendar();
                calen.add(Calendar.DAY_OF_YEAR, -14);
                to = format1.format(now);
                from = format1.format(calen.getTime());
                int i, j;
                connection = dbConnect(getResources().getString(R.string.database_url),
                        getResources().getString(R.string.database_user),
                        getResources().getString(R.string.database_password));
                if (connection == null) {
                    return false;
                } else {
                    PreparedStatement ps;
                    ps = connection.prepareStatement("SELECT * FROM delivery_lists WHERE employee_id= ? "
                                    + " AND deleted=false AND datetime BETWEEN '" + from + "' AND '" + to + "' ",
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ps.setInt(1, getEmployee());
                    ResultSet result1 = ps.executeQuery();
                    result1.last();
                    i = result1.getRow();
                    result1.beforeFirst();
                    Log.d("delivery lists", "N = " + String.valueOf(i));
                    i = 0;
                    String LOG_TAG = "Insert delivery list";
                    String delivery_lists = "(";
                    while (result1.next()) {
                        ContentValues cv = new ContentValues();
                        cv.put("id", result1.getInt("id"));
                        cv.put("employee_id", result1.getInt("employee_id"));
                        if (i > 0) delivery_lists += ",";
                        delivery_lists += "'" + String.valueOf(result1.getInt("id")) + "'";
                        cv.put("user_id", result1.getInt("user_id"));
                        cv.put("list_number", result1.getString("list_number"));
                        cv.put("datetime", result1.getString("datetime"));
                        try {
                            long rowID = db.insertOrThrow("delivery_lists", null, cv);
                            Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                        } catch (SQLiteConstraintException e) {
                            long rowID = db.replace("delivery_lists", null, cv);
                            Log.d(LOG_TAG, "row replaced, ID = " + rowID);
                        }
                        i++;
                    }
                    delivery_lists += ")";
                    Log.d(LOG_TAG, delivery_lists);
                    ps = connection.prepareStatement("SELECT * FROM deliveries "
                                    + " WHERE delivery_list_id IN "
                                    /*
                            + "( SELECT id FROM delivery_lists WHERE employee_id= ? "
                            + " AND deleted=false AND datetime BETWEEN '"+from+"' AND '"+to+"')"
                            */
                                    + delivery_lists
                                    + " ORDER BY delivery_list_id",
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    // ps.setInt(1, getEmployee());
                    ResultSet result2 = ps.executeQuery();
                    result2.last();
                    i = result2.getRow();
                    result2.beforeFirst();
                    Log.d("deliveries", "N = " + String.valueOf(i));
                    String deliveries = "(";
                    String sUrgency;

                    i = 0;
                    while (result2.next()) {
                        if (i > 0) deliveries += ",";
                        deliveries += "'" + String.valueOf(result2.getInt("id")) + "'";
                        ContentValues cv = new ContentValues();
                        for (String col : columnsDeliveriesInt) {
                            cv.put(col, result2.getInt(col));
                        }
                        cv.put("weight", result2.getFloat("weight"));
                        for (String col : columnsDeliveries) {
                            cv.put(col, result2.getString(col));
                        }
                        sUrgency = result2.getString("urgency");
                        int iUrgency = 0;
                        for (j = 0; j < getResources().getStringArray(R.array.urgencies).length; j++) {
                            if (sUrgency.equals(getResources().getStringArray(R.array.urgencies)[j]))
                                iUrgency = j;
                        }
                        cv.put("urgency", iUrgency);
                        cv.put("status", PREPARED);
                        /*
                        LOG_TAG="Convert urgency to ";
                        Log.d(LOG_TAG, String.valueOf(iUrgency));
                        */
                        LOG_TAG = "Insert delivery";
                        try {
                            long rowID = db.insertOrThrow("deliveries", null, cv);
                            Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                        } catch (SQLiteConstraintException e) {
                            long rowID = db.replace("deliveries", null, cv);
                            Log.d(LOG_TAG, "row replaced, ID = " + rowID);
                        }
                        i++;
                    }
                    deliveries += ")";
                    Log.d(LOG_TAG, deliveries);
                    ps = connection.prepareStatement("SELECT * FROM delivery_items "
                            + "WHERE delivery_id IN " + deliveries
                            , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet result3 = ps.executeQuery();
                    while (result3.next()) {
                        ContentValues cv = new ContentValues();
                        cv.put("id", result3.getInt("id"));
                        cv.put("delivery_id", result3.getInt("delivery_id"));
                        cv.put("item_number", result3.getString("item_number"));
                        LOG_TAG = "Insert item";
                        try {
                            long rowID = db.insertOrThrow("delivery_items", null, cv);
                            Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                        } catch (SQLiteConstraintException e) {
                            long rowID = db.replace("delivery_items", null, cv);
                            Log.d(LOG_TAG, "row replaced, ID = " + rowID);
                        }
                    }
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
