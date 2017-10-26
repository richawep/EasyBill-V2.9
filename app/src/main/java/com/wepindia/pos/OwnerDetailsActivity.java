package com.wepindia.pos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mswipetech.wisepad.sdktest.view.ApplicationData;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.GSTINValidation;

import java.util.Date;

public class OwnerDetailsActivity extends WepBaseActivity {

    private com.wep.common.app.views.WepButton btnAdd, btnClear, btnClose;
    private EditText Name, Gstin, Phone, Email, Address, RefernceNo, BillNoPrefix;
    private DatabaseHandler dbHelper;
    private Toolbar toolbar;
    MessageDialog MsgBox;
    Context myContext;
    String strUserName;
    Spinner spinner1, spinner2;

    private boolean mFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_owner_details
        setContentView(R.layout.activity_owner_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myContext = this;
        MsgBox = new MessageDialog(myContext);
        strUserName = ApplicationData.getUserName(this);

        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        com.wep.common.app.ActionBarUtils.setupToolbar(OwnerDetailsActivity.this, toolbar, getSupportActionBar(), "Owner Details", strUserName, " Date:" + s.toString());

        dbHelper = new DatabaseHandler(OwnerDetailsActivity.this);
        InitialseViewVariables();
        clickEvents();
        loadOwnerDetail();

        checkEditTextTest();


    }

    private void InitialseViewVariables()
    {
        btnAdd = (com.wep.common.app.views.WepButton) findViewById(R.id.btnAdd);
        btnClear = (com.wep.common.app.views.WepButton) findViewById(R.id.btnClear);
        btnClose = (com.wep.common.app.views.WepButton) findViewById(R.id.btnClose);
        Name = (EditText) findViewById(R.id.ownerName);
        Gstin = (EditText) findViewById(R.id.ownerGstin);
        RefernceNo = (EditText) findViewById(R.id.ownerReferenceNo);
        BillNoPrefix = (EditText) findViewById(R.id.ownerBillPrefix);
        Phone = (EditText) findViewById(R.id.ownerContact);
        Email = (EditText) findViewById(R.id.ownerEmail);
        spinner1 = (Spinner) findViewById(R.id.ownerPos);
        spinner2 = (Spinner) findViewById(R.id.ownerOffice);
        spinner2.setSelection(1);
        Address = (EditText) findViewById(R.id.ownerAddress);
    }
    private  void clickEvents()
    {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String GSTIN = Gstin.getText().toString().trim().toUpperCase();
                if (GSTIN == null) {
                    GSTIN = "";
                }


                if (Name.getText().toString().equalsIgnoreCase("") ||
                        Email.getText().toString().equalsIgnoreCase("") ||
                        Phone.getText().toString().equalsIgnoreCase("") ||
                        spinner1.getSelectedItem().toString().equalsIgnoreCase("") ||
                        spinner1.getSelectedItem().toString().equalsIgnoreCase("Select") ||
                        Address.getText().toString().equalsIgnoreCase("")) {
                        MsgBox.Show("Incomplete Information","Please fill required details");
                    //Toast.makeText(OwnerDetailsActivity.this, "detail not completed", Toast.LENGTH_SHORT).show();
                }else if (!Gstin.getText().toString().trim().toUpperCase().equals("") && Gstin.getText().toString().trim().toUpperCase().length()!=15)
                {
                     MsgBox.Show("Note", "GSTIN can either be empty or of 15 characters");
                }  else{
                    try {
                       // boolean cc = isValidEmailAddress(Email.getText().toString().trim());
                        mFlag =  GSTINValidation.checkGSTINValidation(GSTIN);
                        if (!isValidEmailAddress(Email.getText().toString().trim()))
                        {
                            MsgBox.Show("Invalid Information","Please Enter Valid Email id");
                            return;
                        }else if(Phone.getText().toString().trim().length() !=10)
                        {
                            MsgBox.Show("Invalid Information","Phone no cannot be less than 10 digits.");
                            return;
                        }
                        else if (mFlag) {
                            dbHelper.CreateDatabase();
                            dbHelper.OpenDatabase();
                            dbHelper.deleteOwnerDetails();
                            updateDetails();
                        } else {
                            MsgBox.Show("Invalid Information","Please Enter Valid GSTIN");
                            //Toast.makeText(OwnerDetailsActivity.this, "Please Enter Valid GSTIN", Toast.LENGTH_SHORT).show();
                        }

                        //dbHelper.close();
                    } catch (Exception ex) {
                        MsgBox.Show("Error", ex.getMessage());
                    }
                }

            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                close(v);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Name.setText("");
                Gstin.setText("");
                RefernceNo.setText("");
                Phone.setText("");
                Email.setText("");
                Address.setText("");
                spinner1.setSelection(0);
                spinner2.setSelection(1);
            }
        });

    }
    private void checkEditTextTest() {


      /*  Gstin.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String gst = Gstin.getText().toString();
                Log.d("welcome", " work " + gst);
                Log.d("welcome", " work " + s);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String gst1 = Gstin.getText().toString();
                Log.d("welcome", " work " + gst1);
                Log.d("welcome", " work " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String gst = Gstin.getText().toString();
                Log.d("welcome", " work " + gst);
                Log.d("welcome", " work " + s);
            }
        });*/

    }

    private int getIndex_pos(String substring) {

        int index = 0;
        for (int i = 0; index == 0 && i < spinner1.getCount(); i++) {

            if (spinner1.getItemAtPosition(i).toString().contains(substring)) {
                index = i;
            }

        }

        return index;

    }

    private void loadOwnerDetail() {
        dbHelper.CreateDatabase();
        dbHelper.OpenDatabase();
        Cursor cursor = dbHelper.getOwnerDetail();
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("OwnerName"));
            String gstin = cursor.getString(cursor.getColumnIndex("GSTIN"));
            String refernceNo = cursor.getString(cursor.getColumnIndex("refNo"));
            String phone = cursor.getString(cursor.getColumnIndex("Phone"));
            String email = cursor.getString(cursor.getColumnIndex("Email"));
            String address = cursor.getString(cursor.getColumnIndex("Address"));
            String pos = cursor.getString(cursor.getColumnIndex("POS"));
            String mainOffice = cursor.getString(cursor.getColumnIndex("IsMainOffice"));
            String bill_prefix = "";
            if (null != cursor.getString(cursor.getColumnIndex("BillNoPrefix")))
                bill_prefix = cursor.getString(cursor.getColumnIndex("BillNoPrefix"));
            Name.setText(name);
            Gstin.setText(gstin);
            RefernceNo.setText(refernceNo);
            Phone.setText(phone);
            Email.setText(email);
            Address.setText(address);
            BillNoPrefix.setText(bill_prefix);
            spinner1.setSelection(getIndex_pos(pos));
            if (mainOffice.equalsIgnoreCase("yes"))
                spinner2.setSelection(1);
            else
                spinner2.setSelection(2);
        }
        // dbHelper.close();
    }

    private void updateDetails() {
        long Status;
        String str = spinner1.getSelectedItem().toString();
        int length = str.length();
        String sub = "";
        if (length > 0) {
            sub = str.substring(length - 2, length);
        }

        Status = dbHelper.addOwnerDetails(Name.getText().toString(), Gstin.getText().toString().trim().toUpperCase(),
                Phone.getText().toString(), Email.getText().toString(),
                Address.getText().toString(), sub,
                spinner2.getSelectedItem().toString(), RefernceNo.getText().toString(), BillNoPrefix.getText().toString());
        if(Status>0){
            Cursor cursor = dbHelper.getOwnerDetail();
            if(cursor!=null && cursor.moveToFirst())
                Toast.makeText(OwnerDetailsActivity.this, "Details Successfully Updated", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(OwnerDetailsActivity.this, "Details Successfully Added", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(OwnerDetailsActivity.this, "Could Not Add Details", Toast.LENGTH_SHORT).show();
    }

    private void close(View v) {

        Cursor cc = dbHelper.getOwnerDetail();
        if (cc != null && cc.moveToFirst()) {
            dbHelper.close();
            Intent intentHomeScreen = new Intent(this, HomeActivity.class);
            startActivity(intentHomeScreen);
            this.finish();
        } else
            Toast.makeText(myContext, "Please fill and save owner details ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);
            AuthorizationDialog
                    .setTitle("Are you sure you want to exit ?")
                    .setIcon(R.drawable.ic_launcher)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            dbHelper.CloseDatabase();
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * checking types of data validation(Integer , Double , String)
     *
     * @param value - csv value
     */

    /*public static int checkDataypeValue(String value) {
        int flag;
        try {
            Integer.parseInt(value);
            flag = 0;
            return flag;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        try {
            Double.parseDouble(value);
            flag = 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            flag = 2;
        }
        return flag;
    }*/
    public static int checkDataypeValue(String value, String type) {
        int flag =0;
        try {
            switch(type) {
                case "Int":
                    Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double" : Double.parseDouble(value);
                    flag = 1;
                    break;
                default : flag =2;
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            flag = -1;
        }
        return flag;
    }
}
