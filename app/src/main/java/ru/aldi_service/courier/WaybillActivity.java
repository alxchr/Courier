package ru.aldi_service.courier;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WaybillActivity extends Activity {
    DBHelper dbHelper;
    TextView tvWaybill, tvAddressee, tvGeography, tvAddress, tvPhone, tvInfo, tvComment, tvItems, tvAccepted;
    RadioButton rbAccept, rbDecline, rbDone;
    Button execute;
    ContentValues cv;
    long ddts;
    String waybill, addressee, address, cp, geo, phone, accepted, info, comment, dd;
    byte[] sign = {0};
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
                    startActivityForResult(intent, 1);
                    break;
                case R.id.rbDecline:
                    newstatus = 4;
                    break;
                case R.id.bExecute:
                    intent = new Intent();
                    intent.putExtra("id", id);
                    if (newstatus > 0 && newstatus != status) {
                        if (newstatus == 3) {
                            ddts = System.currentTimeMillis() / 1000L;
                        }
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
                        cv.put("delivery_date", ddts);
                        cv.put("status", newstatus);
                        if (sign.length > 1) {
                            Log.d("Sign accepted", "length = " + sign.length);
                            cv.put("sign", sign);
                        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            sign = data.getByteArrayExtra("sign");
            accepted = data.getStringExtra("accepted");
        }
    }

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
        tvAccepted = (TextView) findViewById(R.id.accepted);
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
        tvAccepted.setText(accepted);
        cp = c1.getString(c1.getColumnIndex("contact_person"));
        ddts = c1.getLong(c1.getColumnIndex("delivery_date"));
        status = c1.getInt(c1.getColumnIndex("status"));
        dli = c1.getInt(c1.getColumnIndex("delivery_list_id"));
        nItems = c1.getInt(c1.getColumnIndex("n_items"));
        urgency = c1.getInt(c1.getColumnIndex("urgency"));
        sign = c1.getBlob(c1.getColumnIndex("sign"));
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
        if (sign != null && sign.length > 1) {
            LinearLayout llSign = (LinearLayout) findViewById(R.id.llSign);
            llSign.addView(new RenderView(this));
        }
    }

    class RenderView extends View {
        Bitmap bitmap1;
        Rect dst = new Rect();
        Rect src;

        public RenderView(Context context) {
            super(context);
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                ByteArrayInputStream is = new ByteArrayInputStream(sign);
                bitmap1 = BitmapFactory.decodeStream(is, null, options);
                is.close();
                src = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
                Log.d("Sign", "Config =" + bitmap1.getConfig() + " Size = " + bitmap1.getByteCount());
            } catch (IOException e) {
                Log.d("Sign", "Failed");
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            dst.set(0, 0, 300, 150);
            canvas.drawColor(Color.LTGRAY);
            canvas.drawBitmap(bitmap1, src, dst, null);
        }

    }
}
