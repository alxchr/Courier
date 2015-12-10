package ru.aldi_service.courier;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class WaybillActivity extends Activity {
    DBHelper dbHelper;
    TextView tvWaybill, tvAddressee, tvGeography, tvAddress, tvPhone, tvInfo, tvComment, tvItems;
    RadioButton rbAccept, rbDecline, rbDone;
    Button execute;
    ContentValues cv;
    String waybill, addressee, address, cp, geo, phone, accepted, info, comment, dd;
    private int i, newstatus = 0, status, dli, nItems, urgency, id;
    private float weight, cod, addpay, addrpay;
    private String sId, sItems = "";
    private SQLiteDatabase db;
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.rbAccept:
                    newstatus = 2;
                    break;
                case R.id.rbDone:
                    newstatus = 3;
                    intent = new Intent(WaybillActivity.this,Sign.class);
                    startActivity(intent);
                    break;
                case R.id.rbDecline:
                    newstatus = 4;
                    break;
                case R.id.bExecute:
                    intent = new Intent();
                    intent.putExtra("id", id);
                    if (newstatus > 0 && newstatus != status) {
                        cv = new ContentValues();
                        cv.put("id", id);
                        cv.put("delivery_list_id", dli);
                        cv.put("waybill", waybill);
                        cv.put("weight", weight);
                        cv.put("n_items", nItems);
                        cv.put("addressee", addressee);
                        cv.put("contact_person", cp);
                        cv.put("geography", geo);
                        cv.put("address", address);
                        cv.put("phone", phone);
                        cv.put("cost_of_delivery", cod);
                        cv.put("addressee_payment", addrpay);
                        cv.put("additional_payment", addpay);
                        cv.put("accepted_by", accepted);
                        cv.put("urgency", urgency);
                        cv.put("info", info);
                        cv.put("comment", comment);
                        cv.put("delivery_date", dd);
                        cv.put("status", newstatus);
                        try {
                            long rowID = db.replaceOrThrow("deliveries", null, cv);
                            Log.d("New status = ", newstatus + " row ID = " + rowID);
                        } catch (SQLiteConstraintException e) {
//                            long rowID = db.replace("deliveries", null, cv);
//                            Log.d(LOG_TAG, "row replaced, ID = " + rowID);
                        }

                        intent.putExtra("oldstatus", status);
                        intent.putExtra("newstatus", newstatus);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "No status change", Toast.LENGTH_SHORT).show();
                        intent.putExtra("oldstatus", 0);
                        intent.putExtra("newstatus", 0);
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
            }

        }
    };
    private Cursor c1, c2;
    private String selection;
    private String[] columns = {"item_number"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waybill);
        Intent intent = getIntent();
        sId = intent.getStringExtra("delivery_id");
        selection = "id = '" + sId + "'";
        id = Integer.parseInt(sId);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        c1 = db.query("deliveries", null, selection, null, null, null, null);
        tvWaybill = (TextView) findViewById(R.id.waybill_number);
        tvAddressee = (TextView) findViewById(R.id.addressee);
        tvGeography = (TextView) findViewById(R.id.geography);
        tvAddress = (TextView) findViewById(R.id.address);
        tvInfo = (TextView) findViewById(R.id.info);
        tvPhone = (TextView) findViewById(R.id.phone);
        tvComment = (TextView) findViewById(R.id.comment);
        tvItems = (TextView) findViewById(R.id.items);
        c1.moveToFirst();
        waybill = c1.getString(c1.getColumnIndex("waybill"));
        tvWaybill.setText(waybill);
        addressee = c1.getString(c1.getColumnIndex("addressee"));
        tvAddressee.setText(addressee);
        geo = c1.getString(c1.getColumnIndex("geography"));
        tvGeography.setText(geo);
        address = c1.getString(c1.getColumnIndex("address"));
        tvAddress.setText(address);
        phone = c1.getString(c1.getColumnIndex("phone"));
        tvPhone.setText(phone);
        info = c1.getString(c1.getColumnIndex("info"));
        tvInfo.setText(info);
        comment = c1.getString(c1.getColumnIndex("comment"));
        tvComment.setText(comment);
        cod = c1.getFloat(c1.getColumnIndex("cost_of_delivery"));
        addrpay = c1.getFloat(c1.getColumnIndex("addressee_payment"));
        addpay = c1.getFloat(c1.getColumnIndex("additional_payment"));
        weight = c1.getFloat(c1.getColumnIndex("weight"));
        accepted = c1.getString(c1.getColumnIndex("accepted_by"));
        cp = c1.getString(c1.getColumnIndex("contact_person"));
        dd = c1.getString(c1.getColumnIndex("delivery_date"));
        status = c1.getInt(c1.getColumnIndex("status"));
        dli = c1.getInt(c1.getColumnIndex("delivery_list_id"));
        nItems = c1.getInt(c1.getColumnIndex("n_items"));
        urgency = c1.getInt(c1.getColumnIndex("urgency"));
        selection = "delivery_id = '" + sId + "'";
        c2 = db.query("delivery_items", columns, selection, null, null, null, null);
        if (c2 != null && c2.getCount() > 0) {
            if (c2.moveToFirst()) {
                i = 0;
                do {
                    if (i > 0) {
                        sItems += ", ";
                    }
                    sItems += c2.getString(c2.getColumnIndex("item_number"));
                    i++;
                } while (c2.moveToNext());
                if (i > 0) {
                    tvItems.setText(sItems);
                }
            }
        }
        rbAccept = (RadioButton) findViewById(R.id.rbAccept);
        rbDecline = (RadioButton) findViewById(R.id.rbDecline);
        rbDone = (RadioButton) findViewById(R.id.rbDone);
        execute = (Button) findViewById(R.id.bExecute);
        execute.setOnClickListener(ocl);
        rbAccept.setOnClickListener(ocl);
        rbDone.setOnClickListener(ocl);
        rbDecline.setOnClickListener(ocl);
    }
}
