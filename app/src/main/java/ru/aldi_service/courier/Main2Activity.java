package ru.aldi_service.courier;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static ru.aldi_service.courier.GlobalData.getEmployee;
import static ru.aldi_service.courier.GlobalData.getEmployeeName;
import static ru.aldi_service.courier.PGutils.dbConnect;

public class Main2Activity extends AppCompatActivity {
    static SQLiteDatabase db;
    static Cursor c1, c2;
    static String selection;
    DBHelper dbHelper;
    TabHost tabHost;
    String[] columnsDeliveries = {"waybill", "addressee", "contact_person",
            "geography", "address", "phone", "cost_of_delivery", "addressee_payment", "additional_payment", "info",
// urgency must be converted to int
//            "urgency",
            "comment", "delivery_date"};
    String[] columnsDeliveriesInt = {"id", "delivery_list_id", "n_items"};
    ArrayList<Waybill> prepared = new ArrayList<>();
    ArrayList<Waybill> done = new ArrayList<>();
    ArrayList<Waybill> in_work = new ArrayList<>();
    ArrayList<Waybill> problems = new ArrayList<>();
    ListView lvPrepared, lvInWork, lvDone, lvProblems;
    TabHost.TabContentFactory TabFactory = new TabHost.TabContentFactory() {

        @Override
        public View createTabContent(String tag) {
            if (tag.equals(getString(R.string.text_tab1))) {
                WaybillAdapter wAdapter = new WaybillAdapter(Main2Activity.this, prepared);
                lvPrepared.setAdapter(wAdapter);
                return lvPrepared;

            } else if (tag.equals(getString(R.string.text_tab2))) {
                WaybillAdapter sAdapter = new WaybillAdapter(Main2Activity.this, in_work);
                lvInWork.setAdapter(sAdapter);
                return lvInWork;

            } else if (tag.equals(getString(R.string.text_tab3))) {
                WaybillAdapter sAdapter = new WaybillAdapter(Main2Activity.this, done);
                lvDone.setAdapter(sAdapter);
                return lvDone;

            } else if (tag.equals(getString(R.string.text_tab4))) {
                WaybillAdapter sAdapter = new WaybillAdapter(Main2Activity.this, problems);
                lvProblems.setAdapter(sAdapter);
                return lvProblems;

            }
            return null;
        }
    };
    private exchangeDB exchangeDBTask = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int id, oldstatus, newstatus, j;
        Waybill wdel = null;
        boolean inserted;
        String currentTag;
        String LOG_TAG = "Waybill result =";
        if (resultCode == RESULT_OK) {
            id = data.getIntExtra("id", 0);
            currentTag = tabHost.getCurrentTabTag();
            oldstatus = data.getIntExtra("oldstatus", 0);
            newstatus = data.getIntExtra("newstatus", 0);
            Log.d(LOG_TAG, resultCode + " request = " + requestCode + " id = " + id + " old = " + oldstatus + " new = " + newstatus);
            if (oldstatus == 1 && newstatus == 2) {
                for (Waybill w : prepared) {
                    j = w.getId();
                    if (j == id) {
                        inserted = false;
                        for (Waybill w1 : in_work) {
                            int i = in_work.indexOf(w1);
                            if (w.getDeliveryDate().compareToIgnoreCase(w1.getDeliveryDate()) < 0) {
                                in_work.add(i, w);
                                inserted = true;
                                break;
                            }
                        }
                        if (!inserted) {
                            in_work.add(w);
                        }
                        wdel = w;
                        break;
                    }
                }
                if (wdel != null) {
                    prepared.remove(wdel);
                }
            }
            if (oldstatus == 1 && newstatus == 4) {
                for (Waybill w : prepared) {
                    j = w.getId();
                    if (j == id) {
                        inserted = false;
                        for (Waybill w1 : problems) {
                            int i = problems.indexOf(w1);
                            if (w.getDeliveryDate().compareToIgnoreCase(w1.getDeliveryDate()) < 0) {
                                problems.add(i, w);
                                inserted = true;
                                break;
                            }
                        }
                        if (!inserted) {
                            problems.add(w);
                        }
                        wdel = w;
                        break;
                    }
                }
                if (wdel != null) {
                    prepared.remove(wdel);
                }
            }
            if (oldstatus == 4 && newstatus == 2) {
                for (Waybill w : problems) {
                    j = w.getId();
                    if (j == id) {
                        inserted = false;
                        for (Waybill w1 : in_work) {
                            int i = in_work.indexOf(w1);
                            if (w.getDeliveryDate().compareToIgnoreCase(w1.getDeliveryDate()) < 0) {
                                in_work.add(i, w);
                                inserted = true;
                                break;
                            }
                        }
                        if (!inserted) {
                            in_work.add(w);
                        }
                        wdel = w;
                        break;
                    }
                }
                if (wdel != null) {
                    problems.remove(wdel);
                }
            }
            if (oldstatus == 2 && newstatus == 4) {
                for (Waybill w : in_work) {
                    j = w.getId();
                    if (j == id) {
                        inserted = false;
                        for (Waybill w1 : problems) {
                            int i = problems.indexOf(w1);
                            if (w.getDeliveryDate().compareToIgnoreCase(w1.getDeliveryDate()) < 0) {
                                problems.add(i, w);
                                inserted = true;
                                break;
                            }
                        }
                        if (!inserted) {
                            problems.add(w);
                        }
                        wdel = w;
                        break;
                    }
                }
                if (wdel != null) {
                    in_work.remove(wdel);
                }
            }

            tabHost.clearAllTabs();

            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(getString(R.string.text_tab1));
            tabSpec.setContent(TabFactory);
            tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_prepared));
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec(getString(R.string.text_tab2));
            tabSpec.setContent(TabFactory);
            tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_in_work));
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec(getString(R.string.text_tab3));
            tabSpec.setContent(TabFactory);
            tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_done));
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec(getString(R.string.text_tab4));
            tabSpec.setContent(TabFactory);
            tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_problem));
            tabHost.addTab(tabSpec);

            tabHost.setCurrentTabByTag(currentTag);
        }

    }

    //    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        exchangeDBTask = new exchangeDB();
        exchangeDBTask.execute((Void) null);

        selection = "employee_id = ";
        c1 = db.query("delivery_lists", null, selection + "'" + String.valueOf(getEmployee()) + "'", null, null, null, null);
        Log.d("delivery lists SQLite", " N = " + String.valueOf(c1.getCount()));
        if (c1 != null && c1.getCount() > 0) {
            String lists = "", w, dd, adr, adrs;
            int i = 0, s, u, id;
            if (c1.moveToFirst()) {
                do {
                    if (i > 0) lists += ",";
                    lists += "'" + String.valueOf(c1.getInt(c1.getColumnIndex("id"))) + "'";
                    i++;
                } while (c1.moveToNext());
            }
            selection = "delivery_list_id IN (" + lists + ")";
            c2 = db.query("deliveries", null, selection, null, null, null, "urgency desc, delivery_date asc");
            Log.d("deliveries", "deliveries num = " + String.valueOf(c2.getCount()));
            if (c2 != null && c2.getCount() > 0) {
                if (c2.moveToFirst()) {
                    do {
                        adr = c2.getString(c2.getColumnIndex("address"));
                        adrs = c2.getString(c2.getColumnIndex("addressee"));
                        dd = c2.getString(c2.getColumnIndex("delivery_date"));
                        id = c2.getInt(c2.getColumnIndex("id"));
                        s = c2.getInt(c2.getColumnIndex("status"));
                        u = c2.getInt(c2.getColumnIndex("urgency"));
                        w = c2.getString(c2.getColumnIndex("waybill"));
                        switch (s) {
                            case 1:
                                prepared.add(new Waybill(this, id, w, adrs, adr, u, dd));
                                break;
                            case 2:
                                in_work.add(new Waybill(this, id, w, adrs, adr, u, dd));
                                break;
                            case 3:
                                done.add(new Waybill(this, id, w, adrs, adr, u, dd));
                                break;
                            case 4:
                                problems.add(new Waybill(this, id, w, adrs, adr, u, dd));
                                break;
                        }

                    } while (c2.moveToNext());
                }
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setTitle(getEmployeeName());
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        lvPrepared = new ListView(this);
        lvInWork = new ListView(this);
        lvDone = new ListView(this);
        lvProblems = new ListView(this);

        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec(getString(R.string.text_tab1));
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_prepared));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(getString(R.string.text_tab2));
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_in_work));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(getString(R.string.text_tab3));
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_done));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(getString(R.string.text_tab4));
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_tab_problem));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag(getString(R.string.text_tab2));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Toast.makeText(getBaseContext(), tabId, Toast.LENGTH_SHORT).show();
            }
        });
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
//                            long rowID = db.replace("delivery_lists", null, cv);
//                            Log.d(LOG_TAG, "row replaced, ID = " + rowID);
                        }
                        i++;
                    }
                    delivery_lists += ")";
                    Log.d(LOG_TAG, delivery_lists);
                    ps = connection.prepareStatement("SELECT * FROM deliveries "
                                    + " WHERE delivery_list_id IN "
                                    + delivery_lists
                                    + " ORDER BY delivery_list_id",
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

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
                        LOG_TAG = "Insert delivery";
                        try {
                            long rowID = db.insertOrThrow("deliveries", null, cv);
                            Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                        } catch (SQLiteConstraintException e) {
//                            long rowID = db.replace("deliveries", null, cv);
//                            Log.d(LOG_TAG, "row replaced, ID = " + rowID);
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
//                            long rowID = db.replace("delivery_items", null, cv);
//                            Log.d(LOG_TAG, "row replaced, ID = " + rowID);
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
