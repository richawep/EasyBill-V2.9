/****************************************************************************
 * Project Name		:	VAJRA
 * <p/>
 * File Name		:	ItemManagementActivity
 * <p/>
 * Purpose			:	Represents Item creation activity, takes care of all
 * UI back end operations in this activity, such as event
 * handling data read from or display in views.
 * <p/>
 * DateOfCreation	:	20-November-2012
 * <p/>
 * Author			:	Balasubramanya Bharadwaj B S
 ****************************************************************************/
package com.wepindia.pos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.ItemOutward;
import com.wep.common.app.views.WepButton;
import com.wep.common.app.views.WepEditText;
import com.wepindia.pos.GenericClasses.EditTextInputHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.adapters.ItemOutwardAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.StockOutwardMaintain;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ItemManagementActivity extends WepBaseActivity  implements  TextWatcher{
    String tx = "";
    Context myContext;
    BufferedReader buffer ;
    DatabaseHandler dbItems = new DatabaseHandler(ItemManagementActivity.this);
    MessageDialog MsgBox;
    int ROWCLICKEVENT = 0;
    EditText /*txtLongName,*/ txtShortName,  txtDineIn1, txtDineIn2, txtDineIn3, txtStock;
    WepEditText txtLongName;
    AutoCompleteTextView AutoCompleteItemLongName,AutoCompleteItemBarcode;
    Spinner spnrDepartment, spnrCategory, spnrKitchen,  spnrOptionalTax1, spnrOptionalTax2;
    CheckBox chkPriceChange, chkDiscountEnable, chkBillWithStock;
    RadioButton rbForwardTax, rbReverseTax;
    WepButton btnGenerateCSV, btnAdd, btnEdit, btnUploadExcel, btnSaveExcel,btnClearItem,btnCloseItem,btnImageBrowse;

    private String CSV_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeP_FnB_CSVs/";
    private String FILENAME = "Sample_OutwardItem.csv";

    private ArrayList<Integer> mDepartmentCodeList = new ArrayList<>();
    private Map<Integer, Integer> mCategoryCodeList = new LinkedHashMap<>();

    ImageView imgItemImage;
    TableLayout tblItems;
    TextView tvDineIn1Caption, tvDineIn2Caption, tvDineIn3Caption, tvFileName;
    Spinner spnrG_S, spnrMOU, spnrtaxationtype;
    EditText etRate ,etQuantity ,etHSN ,etGstTax ,etItemDiscount ;
    FrameLayout frame_rate_nongst, frame_rate_gst, frame_serviceTax, frame_salesTax, frame_GSTTax;
    EditText edtMenuCode, edtItemCGSTTax, edtItemSGSTTax, edtIGSTTax,edtcessTax;
    //TextView tvCaptionSNo,tvCaptionLongName,tvCaptionDineInPrice1,tvCaptionDineInPrice2,tvCaptionDineInPrice3,tvCaptionStock, tvCaptionRate,tvCaptionQuanity,tvCaptionMOU,tvCaptionGSTRate,tvCaptionImage,tvCaptionDelete;
    float fRate =0, fQuantity =0, fGSTTax = 0, fDiscount = 0;
    String txtHSNCode = "";
    ArrayAdapter<String> adapDeptCode, adapCategCode, adapKitCode, adapTax, adapDiscount;
    ArrayAdapter<CharSequence> supplytypeAdapter,MOUAdapter, taxationtypeAdapter;
    String strItemId, strImageUri = "";
    String strUploadFilepath = "", strUserName = "";
    private List<String> labelsDept;
    private List<String> labelsCateg;
    String GSTEnable="0", HSNEnable_out="0";
    int iMenuCode = 0;
    Cursor crsrSettings = null;
    private Toolbar toolbar;
    String businessDate = "";
    String itemName_beforeChange_in_update="";
    String itembarCode_beforeChange_in_update="";
    ItemOutwardAdapter itemListAdapter = null;
    private ListView listViewItems;
    ArrayList<ItemOutward> dataList = null;

    private String mUserCSVInvalidValue = "";
    private boolean mFlag;
    private int mCheckCSVValueType;


    private int mMenuCode;
    private String mItemName;
    private String mSupplyType;
    private double mRate1;
    private double mRate2;
    private double mRate3;
    private double mQuantity;
    private String mUOM;
    private double mCGSTRate;
    private double mSGSTRate;
    private double mIGSTRate;
    private double mCESSRate;
    private double mDiscount;

    private String mCurrentTime;
    private final int CHECK_INTEGER_VALUE = 0;
    private final int CHECK_DOUBLE_VALUE = 1;
    private final int CHECK_STRING_VALUE = 2;


    private double IGSTAmt = 0;
    private double CGSTAmt = 0;
    private double SGSTAmt = 0;
    private double cessAmt = 0;


    private String mLongName = "Long Name";
    private int mDeptCode = 0;
    private int mCategCode = 0;
    private int mKitchenCode = 0;
    private String mbarCode = "";
    private String mImageUri = "";
    private int mItemId = 0;
    private String mHSN = "";
    private String mtaxationType = "";

    private String mCategoryNameByDept;


    //  private Map<CheckCSVResponse,ItemOutward> mHashMap= new HashMap<>();
    private Map<Integer, ItemOutward> mHashMapItemCode = new TreeMap<>();
    private Map<String, ItemOutward> mHashMapItemName = new LinkedHashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemmaster);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myContext = this;
        MsgBox = new MessageDialog(myContext);

        strUserName = getIntent().getStringExtra("USER_NAME");
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        com.wep.common.app.ActionBarUtils.setupToolbar(ItemManagementActivity.this,toolbar,getSupportActionBar(),"Outward Items Master",strUserName," Date:"+s.toString());
        try {

            dbItems.CreateDatabase();
            dbItems.OpenDatabase();
            InitializeViewVariables();
            clickEvent();
            setCVSFile();
            parseCVSFile();
            setAllDepartmentCode();
            ResetItem();

            crsrSettings = dbItems.getBillSetting();
            if (crsrSettings.moveToFirst()) {
                tvDineIn1Caption.setText(crsrSettings.getString(crsrSettings.getColumnIndex("DineIn1Caption")));
                tvDineIn2Caption.setText(crsrSettings.getString(crsrSettings.getColumnIndex("DineIn2Caption")));
                tvDineIn3Caption.setText(crsrSettings.getString(crsrSettings.getColumnIndex("DineIn3Caption")));
                businessDate = crsrSettings.getString(crsrSettings.getColumnIndex("BusinessDate"));
                GSTEnable = crsrSettings.getString(crsrSettings.getColumnIndex("GSTEnable"));
                HSNEnable_out =  crsrSettings.getString(crsrSettings.getColumnIndex("HSNCode_Out"));

                if(crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("1"))
                {
                    edtMenuCode.setEnabled(true);
                } else {
                    edtMenuCode.setEnabled(false);
                }

            } else {
                Toast.makeText(myContext, "Error: Reading settings", Toast.LENGTH_SHORT).show();
            }
            InitializeAdapters();
            //DisplayItems();

            loadSpinnerData();
            loadSpinnerData1();
            loadAutoCompleteData_ItemNames();



            spnrtaxationtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //@Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String choice = spnrtaxationtype.getItemAtPosition(position).toString();
                    if (choice.equalsIgnoreCase("nonGST") || (choice.equalsIgnoreCase("NilRate")) ||
                            (choice.equalsIgnoreCase("Exempt"))) {
                        MsgBox.Show("Note", "For NilRate, nonGST, Exempt, tax will be 0");
                        edtItemCGSTTax.setText("0.00");
                        edtItemCGSTTax.setEnabled(false);
                        edtItemSGSTTax.setText("0.00");
                        edtItemSGSTTax.setEnabled(false);
                        edtIGSTTax.setText("0.00");
                        //edtIGSTTax.setEnabled(false);
                    }
                    else
                    {
                        edtItemCGSTTax.setText("0.00");
                        edtItemCGSTTax.setEnabled(true);
                        edtItemSGSTTax.setText("0.00");
                        edtItemSGSTTax.setEnabled(true);
                        edtIGSTTax.setText("0.00");
                        //edtIGSTTax.setEnabled(true);
                    }
                }

                //@Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            // Upload Excel data to Database


            /*btnUploadExcel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //startActivityForResult(new Intent(myContext, UploadFilePickerActivity.class), 1);
                    Intent intent = new Intent(myContext, FilePickerActivity.class);
                    intent.putExtra("contentType","csv");
                    startActivityForResult(intent, FilePickerActivity.FILE_PICKER_CODE);
                }
            });

            btnSaveExcel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    try {
                        AssetManager manager = myContext.getAssets();
                        //Environment.getExternalStorageDirectory() + "/stock1.xls";
                        //InputStream inputStream = getResources().openRawResource(R.raw.itemdb);
                        if (strUploadFilepath.equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "No File Found", Toast.LENGTH_SHORT).show();
                        } else {
                            //tvFileName.setText(strUploadFilepath);
                            //String path = Environment.getExternalStorageDirectory() + "/itemdb.csv";
                            String path = strUploadFilepath;
                            FileInputStream inputStream = new FileInputStream(path);
                            buffer = new BufferedReader(new InputStreamReader(inputStream));
                            Cursor cursor = dbItems.getCurrentDate();
//                            String currentdate = "";
//                            if(cursor.moveToNext())
//                                currentdate = cursor.getString(cursor.getColumnIndex("BusinessDate"));
                            if (dataList.size()>0)
                            {
                                final String current_date = businessDate;
                                AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                                        .setTitle("Replace Item")
                                        .setMessage(" Are you sure you want to Replace all the existing Items, if any")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                int deleted = dbItems.clearOutwardItemdatabase();
                                                Log.d("ItemManagement"," Items deleted before uploading excel :"+deleted);
                                                deleted = dbItems.clearOutwardStock(current_date);
                                                Log.d("ItemManagement"," Outward Stock deleted before uploading excel :"+deleted);
                                                new AsyncTask<Void,Void,Void>(){
                                                    ProgressDialog pd;

                                                    @Override
                                                    protected void onPreExecute() {
                                                        super.onPreExecute();
                                                        pd = new ProgressDialog(ItemManagementActivity.this);
                                                        pd.setMessage("Loading...");
                                                        pd.setCancelable(false);
                                                        pd.show();
                                                    }

                                                    @Override
                                                    protected Void doInBackground(Void... params) {

                                                        try {
                                                            String line = "";
                                                            int iteration = 0;
                                                            int i =1;
                                                            while ((line = buffer.readLine()) != null) {
                                                                final String[] colums = line.split(",");
                                                                if (colums.length != 9) {
                                                                    Log.d("CSVParser", "Skipping Bad CSV Row");
                                                                    //Toast.makeText(myContext, "Skipping Bad CSV Row", Toast.LENGTH_LONG).show();
                                                                    continue;
                                                                }
                                                                if (iteration == 0) {
                                                                    iteration++;
                                                                    continue;
                                                                }

                                                                ItemOutward item_add = new ItemOutward(i,colums[1].trim(), Double.parseDouble(colums[2].trim()),
                                                                        Double.parseDouble(colums[3].trim()), Double.parseDouble(colums[4].trim()),Double.parseDouble(colums[5].trim()),
                                                                        0, 0, 0, "","",i, Double.parseDouble(colums[6].trim()),Double.parseDouble(colums[7].trim()),
                                                                        Double.parseDouble(colums[6].trim())+ Double.parseDouble(colums[7].trim()),0,
                                                                        colums[8].trim(), "HSN_"+i,"GST", "G" ,0) ;
                                                                long lRowId = dbItems.addItem(item_add);


                                                                    /*InsertItem(colums[1].trim(), colums[1].trim(), Float.parseFloat(colums[2].trim()),
                                                                        Float.parseFloat(colums[3].trim()), Float.parseFloat(colums[4].trim()),
                                                                        0, 0, 0, Float.parseFloat(colums[5].trim()), 0, 0, 0, 0, 0, 0, 0, 1,
                                                                        2, 0, 0, 0, "", "",0f,0f,"HSN_"+i,
                                                                        Float.parseFloat(colums[6].trim())+ Float.parseFloat(colums[7].trim()),
                                                                        Float.parseFloat(colums[6].trim()),Float.parseFloat(colums[7].trim()),
                                                                        "G",colums[8].trim(),"", Float.parseFloat(colums[6].trim()),
                                                                        Float.parseFloat(colums[7].trim()), Integer.valueOf(colums[0].trim()));
            i++;
        }
        StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
        stock_outward.saveOpeningStock_Outward(current_date);

    } catch (Exception exp) {
        exp.printStackTrace();
        //Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
    }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try{
            ResetItem();
            //ClearItemTable();
            DisplayItemList();
            Toast.makeText(getApplicationContext(), "Items Imported Successfully", Toast.LENGTH_LONG).show();
            pd.dismiss();
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}.execute();



        dialog.dismiss();
        }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        //ClearItemTable();
        DisplayItemList();
        }
        });
        AlertDialog alert = builder.create();
        alert.show();
        }
        else
        {
        new AsyncTask<Void,Void,Void>(){
        ProgressDialog pd;

@Override
protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(ItemManagementActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        }

@Override
protected Void doInBackground(Void... params) {

        try {
        String line = "";
        int iteration = 0;
        int i = 1;

        while ((line = buffer.readLine()) != null) {
final String[] colums = line.split(",");
        if (colums.length != 9) {
        Log.d("CSVParser", "Skipping Bad CSV Row");
        //Toast.makeText(myContext, "Skipping Bad CSV Row", Toast.LENGTH_LONG).show();
        continue;
        }
        if (iteration == 0) {
        iteration++;
        continue;
        }
//                                                InsertItem(colums[1].trim(), colums[1].trim(), Float.parseFloat(colums[2].trim()),
//                                                        Float.parseFloat(colums[3].trim()), Float.parseFloat(colums[4].trim()),
//                                                        0, 0, 0, Float.parseFloat(colums[5].trim()), 0, 0, 0, 0, 0, 0, 0, 1,
//                                                        2, 0, 0, 0, "", "",0f,0f,"HSN_"+i,
//                                                        Float.parseFloat(colums[6].trim())+ Float.parseFloat(colums[7].trim()),
//                                                        Float.parseFloat(colums[6].trim()),Float.parseFloat(colums[7].trim()),
//                                                        "G",colums[8].trim(),"", Float.parseFloat(colums[6].trim()),
//                                                        Float.parseFloat(colums[7].trim()), Integer.valueOf(colums[0].trim()));
        ItemOutward item_add = new ItemOutward(i,colums[1].trim(), Double.parseDouble(colums[2].trim()),
        Double.parseDouble(colums[3].trim()), Double.parseDouble(colums[4].trim()),Double.parseDouble(colums[5].trim()),
        0, 0, 0, "","",i,Double.parseDouble(colums[6].trim()),
        Double.parseDouble(colums[7].trim()),Double.parseDouble(colums[6].trim())+ Double.parseDouble(colums[7].trim()),0,
        colums[8].trim(), "HSN_"+i,"GST", "G",0 ) ;
        long lRowId = dbItems.addItem(item_add);
        i++;
        }
final String current_date = businessDate;
        StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
        stock_outward.saveOpeningStock_Outward(current_date);
        } catch (Exception exp) {
        Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
        exp.printStackTrace();
        }
        return null;
        }

@Override
protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try{
        ResetItem();
        //ClearItemTable();
        DisplayItemList();
        Toast.makeText(myContext, "Items Imported Successfully", Toast.LENGTH_SHORT).show();
        pd.dismiss();
        }catch (Exception e){
        Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
        }

        }
        }.execute();

        }
        }
        } catch (IOException e) {
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
        }
        finally {
        tvFileName.setText("Select FileName");
        strUploadFilepath="";
        }
        }
        });*/

            SetGSTView();
            DisplayItemList();

            tvFileName.setPaintFlags(tvFileName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        } catch (Exception exp) {
            Toast.makeText(myContext, "OnCreate:" + exp.getMessage(), Toast.LENGTH_SHORT).show();
            exp.printStackTrace();
        }
    }



    private void setAllDepartmentCode() {
        try {
            Cursor crsrDept = dbItems.getAllDepartments();
            while (crsrDept != null && crsrDept.moveToNext()) {
                mDepartmentCodeList.add(crsrDept.getInt(crsrDept.getColumnIndex("DeptCode")));
            }

            Cursor crsrCategory = dbItems.getAllCategory();
            while (crsrCategory != null && crsrCategory.moveToNext()) {
                mCategoryCodeList.put(crsrCategory.getInt(crsrCategory.getColumnIndex("CategCode")),
                        crsrCategory.getInt(crsrCategory.getColumnIndex("DeptCode")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void SetGSTView()
    {
        Cursor crsrSettings = dbItems.getBillSetting();
        LinearLayout linear_HSN_OUT = (LinearLayout) findViewById(R.id.linear_HSN_OUT);
        if (crsrSettings.moveToFirst()) {
            if(HSNEnable_out == null ) {
                linear_HSN_OUT.setVisibility(View.INVISIBLE);
            }
            else if(HSNEnable_out.equalsIgnoreCase("0") ) {
                linear_HSN_OUT.setVisibility(View.INVISIBLE);
            }
            else {
                linear_HSN_OUT.setVisibility(View.VISIBLE);
            }
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String str = charSequence.toString();
        View view = getCurrentFocus();
        if(view== null)
            return;
        switch(view.getId()){
            case R.id.etItemLongName:
                tx = txtLongName.getText().toString();
                // tx = AutoCompleteItemLongName.getText().toString();
                break;
            case R.id.AutoCompleteItemLongName:
                // tx = txtLongName.getText().toString();
                tx = AutoCompleteItemLongName.getText().toString();
                break;
            case R.id.etItemDineInPrice1:
                tx = txtDineIn1.getText().toString();
                break;
            case R.id.etItemDineInPrice2:
                tx = txtDineIn2.getText().toString();
                break;
            case R.id.etItemDineInPrice3:
                tx = txtDineIn3.getText().toString();
                break;
            case R.id.etItemStock:
                tx = txtStock.getText().toString();
                break;
            case R.id.etHSNCode:
                tx = etHSN.getText().toString();
                break;
            case R.id.edtMenuCode:
                tx = edtMenuCode.getText().toString();
                break;
            case R.id.edtItemCGSTTax:
                tx = edtItemCGSTTax.getText().toString();
                break;
            case R.id.edtItemSGSTTax:
                tx = edtItemSGSTTax.getText().toString();
                break;
            case R.id.edtIGSTTax:
                tx = edtIGSTTax.getText().toString();
                break;
            case R.id.etItemDiscount:
                tx = etItemDiscount.getText().toString();
                break;

        }
    }
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    public void afterTextChanged(Editable editable) {

    }


    private  void clickEvent()
    {
        AutoCompleteItemLongName.setOnTouchListener(new View.OnTouchListener(){
            //@Override
            public boolean onTouch(View v, MotionEvent event){
                AutoCompleteItemLongName.showDropDown();
                return false;
            }
        });
        AutoCompleteItemLongName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = AutoCompleteItemLongName.getText().toString();
                Cursor cursorItem = dbItems.getItemDetail(itemName);
                if(cursorItem!=null && cursorItem.moveToFirst())
                {
                    ItemOutward item = new ItemOutward();
                    item.setMenuCode(cursorItem.getInt(cursorItem.getColumnIndex("MenuCode")));
                    item.setItemName(cursorItem.getString(cursorItem.getColumnIndex("ItemName")));
                    item.setDineIn1(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice1")))));
                    item.setDineIn2(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice2")))));
                    item.setDineIn3(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice3")))));
                    item.setStock(  Float.parseFloat(String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("Quantity")))));
                    item.setDeptCode(cursorItem.getInt(cursorItem.getColumnIndex("DeptCode")));
                    item.setCategCode(cursorItem.getInt(cursorItem.getColumnIndex("CategCode")));
                    item.setKitchenCode(cursorItem.getInt(cursorItem.getColumnIndex("KitchenCode")));
                    item.setBarCode(cursorItem.getString(cursorItem.getColumnIndex("ItemBarcode")));
                    item.setImageUri(cursorItem.getString(cursorItem.getColumnIndex("ImageUri")));
                    item.setUOM(cursorItem.getString(cursorItem.getColumnIndex("UOM")));
                    item.setCGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("CGSTRate")))));
                    item.setSGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("SGSTRate")))));
                    item.setIGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("IGSTRate")))));
                    item.setCessRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("cessRate")))));
                    item.setItemId(cursorItem.getInt(cursorItem.getColumnIndex("ItemId")));
                    item.setHSN(cursorItem.getString(cursorItem.getColumnIndex("HSNCode")));
                    item.setTaxationType(cursorItem.getString(cursorItem.getColumnIndex("TaxationType")));
                    item.setItemDiscount(Float.parseFloat(String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("DiscountPercent")))));
                    listItemClickEvent(item);
                }else
                {
                    Log.d("ItemManagement","No Such item present");
                }
            }
        });

        AutoCompleteItemBarcode.setOnTouchListener(new View.OnTouchListener(){
            //@Override
            public boolean onTouch(View v, MotionEvent event){
                AutoCompleteItemBarcode.showDropDown();
                return false;
            }
        });
        AutoCompleteItemBarcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemBarCode = AutoCompleteItemBarcode.getText().toString();
                Cursor cursorItem = dbItems.getItemDetail_ByBarCode(itemBarCode);
                if(cursorItem!=null && cursorItem.moveToFirst())
                {
                    ItemOutward item = new ItemOutward();
                    item.setMenuCode(cursorItem.getInt(cursorItem.getColumnIndex("MenuCode")));
                    item.setItemName(cursorItem.getString(cursorItem.getColumnIndex("ItemName")));
                    item.setDineIn1(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice1")))));
                    item.setDineIn2(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice2")))));
                    item.setDineIn3(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice3")))));
                    item.setStock(  Float.parseFloat(String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("Quantity")))));
                    item.setDeptCode(cursorItem.getInt(cursorItem.getColumnIndex("DeptCode")));
                    item.setCategCode(cursorItem.getInt(cursorItem.getColumnIndex("CategCode")));
                    item.setKitchenCode(cursorItem.getInt(cursorItem.getColumnIndex("KitchenCode")));
                    item.setBarCode(cursorItem.getString(cursorItem.getColumnIndex("ItemBarcode")));
                    item.setImageUri(cursorItem.getString(cursorItem.getColumnIndex("ImageUri")));
                    item.setUOM(cursorItem.getString(cursorItem.getColumnIndex("UOM")));
                    item.setCGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("CGSTRate")))));
                    item.setSGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("SGSTRate")))));
                    item.setIGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("IGSTRate")))));
                    item.setCessRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("cessRate")))));
                    item.setItemId(cursorItem.getInt(cursorItem.getColumnIndex("ItemId")));
                    item.setHSN(cursorItem.getString(cursorItem.getColumnIndex("HSNCode")));
                    item.setTaxationType(cursorItem.getString(cursorItem.getColumnIndex("TaxationType")));
                    item.setItemDiscount(Float.parseFloat(String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("DiscountPercent")))));
                    listItemClickEvent(item);
                }else
                {
                    Log.d("ItemManagement","No Such item present");
                }
            }
        });
    }
    private void InitializeViewVariables() {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();

        txtLongName = (WepEditText) findViewById(R.id.etItemLongName);
        AutoCompleteItemLongName = (AutoCompleteTextView) findViewById(R.id.AutoCompleteItemLongName);
        AutoCompleteItemBarcode = (AutoCompleteTextView) findViewById(R.id.AutoCompleteItemBarcode);
        txtDineIn1 = (EditText) findViewById(R.id.etItemDineInPrice1);
        etInputValidate.ValidateDecimalInput(txtDineIn1);
        txtDineIn2 = (EditText) findViewById(R.id.etItemDineInPrice2);
        etInputValidate.ValidateDecimalInput(txtDineIn2);
        txtDineIn3 = (EditText) findViewById(R.id.etItemDineInPrice3);
        etInputValidate.ValidateDecimalInput(txtDineIn3);
        txtStock = (EditText) findViewById(R.id.etItemStock);
        tvFileName = (TextView) findViewById(R.id.tvFileName);

        txtStock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(btnEdit.isEnabled()==false)
                    return false;
                try{
                    Date d = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
                    int invoiceno = dbItems.getLastBillNoforDate(String.valueOf(d.getTime()));
                    if(invoiceno > 0)
                    {
                        // since already billing done for this businessdate, hence stock cannot be updated from here.
                        // to update stock , goto Price & Stock module
                        MsgBox.Show("Restriction", "You cannot update quantity after making bill for the day. \n\nTo update quantity , " +
                                "please goto Price & Stock module \n\n Or make Day End  to update from here ");
                        txtStock.setEnabled(false);
                        //ResetItem();
                        return false;
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        /*txtStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnEdit.isEnabled()==false)
                    return;
                try{
                    Date d = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
                    int invoiceno = dbItems.getLastBillNoforDate(String.valueOf(d.getTime()));
                    if(invoiceno > 0)
                    {
                        // since already billing done for this businessdate, hence stock cannot be updated from here.
                        // to update stock , goto Price & Stock module
                        MsgBox.Show("Restriction", "You cannot update quantity after making bill for the day. \n\nTo update quantity , " +
                                "please goto Price & Stock module \n\n Or make Day End  to update from here ");
                        ResetItem();
                        return;
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        /*txtStock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
@Override
public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus && btnEdit.isEnabled() ){
        try{
        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
        int invoiceno = dbItems.getLastBillNoforDate(String.valueOf(d.getTime()));
        if(invoiceno > 0)
        {
        // since already billing done for this businessdate, hence stock cannot be updated from here.
        // to update stock , goto Price & Stock module
        MsgBox.Show("Restriction", "You cannot update quantity after making bill for the day. \n\nTo update quantity , " +
        "please goto Price & Stock module \n\n Or make Day End  to update from here ");
        ResetItem();
        return;
        }
        }catch (Exception e)
        {
        e.printStackTrace();
        Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }}
        }
        });*/

        tvDineIn1Caption = (TextView) findViewById(R.id.tvDineInPrice1);
        tvDineIn2Caption = (TextView) findViewById(R.id.tvDineInPrice2);
        tvDineIn3Caption = (TextView) findViewById(R.id.tvDineInPrice3);

        btnUploadExcel = (WepButton) findViewById(R.id.buttonUploadExcel);
        btnSaveExcel = (WepButton) findViewById(R.id.buttonSaveExcel);

        spnrDepartment = (Spinner) findViewById(R.id.spnrItemDeptCode);
        spnrDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (ROWCLICKEVENT == 0)
                {
                    if(!spnrDepartment.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        int deptid = dbItems.getDepartmentIdByName(labelsDept.get(spnrDepartment.getSelectedItemPosition()));
                        ArrayList<String> categName = dbItems.getCategoryNameByDeptCode(String.valueOf(deptid));
                    /*int categid = getIndexCateg(categName + "");
                    spnrCategory.setSelection(categid);*/
                        loadSpinnerData_cat(categName);

                        if(mCategoryNameByDept != null && mCategoryNameByDept.length() > 0) {
                            int categid = getIndexCategByDept(mCategoryNameByDept ,categName);
                            spnrCategory.setSelection(categid);
                            mCategoryNameByDept = "";
                        }
                    }
                    else
                    {
                        loadSpinnerData1();
                    }
                }
                else
                {
                    // setting it to 0;
                    ROWCLICKEVENT =0;
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        spnrCategory = (Spinner) findViewById(R.id.spnrItemCategCode);
        spnrCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int p = position;
                String item = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnrKitchen = (Spinner) findViewById(R.id.spnrItemKitchenCode);
        spnrOptionalTax1 = (Spinner) findViewById(R.id.spnrItemOptionalTax1);
        spnrOptionalTax2 = (Spinner) findViewById(R.id.spnrItemOptionalTax2);


        //chkPriceChange = (CheckBox) findViewById(R.id.chkPriceChange);
        //chkDiscountEnable = (CheckBox) findViewById(R.id.chkDiscount);
        //chkBillWithStock = (CheckBox) findViewById(R.id.chkBillwithStock);

        rbForwardTax = (RadioButton) findViewById(R.id.rbForwardTax);
        rbReverseTax =
                (RadioButton) findViewById(R.id.rbReverseTax);

        btnGenerateCSV = (WepButton) findViewById(R.id.btnGenerateCSV);
        btnAdd = (WepButton) findViewById(R.id.btnAddItem);
        btnEdit = (WepButton) findViewById(R.id.btnEditItem);
        btnClearItem = (WepButton) findViewById(R.id.btnClearItem);
        btnCloseItem = (WepButton) findViewById(R.id.btnCloseItem);
        btnImageBrowse = (WepButton) findViewById(R.id.btnImageBrowse);

        btnImageBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browse(v);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItem(v);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItem(v);
            }
        });
        btnClearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearItem(v);
            }
        });
        btnCloseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseItem(v);
            }
        });
        btnGenerateCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCSV();
            }
        });

        tblItems = (TableLayout) findViewById(R.id.tblItem);

        imgItemImage = (ImageView) findViewById(R.id.imgItemImage);
        imgItemImage.setImageResource(R.drawable.img_noimage);



        spnrG_S = (Spinner) findViewById(R.id.spnr_g_s);
        spnrMOU = (Spinner) findViewById(R.id.spnr_UOM);
        spnrtaxationtype = (Spinner) findViewById(R.id.spnr_taxationtype);
        etRate = (EditText) findViewById(R.id.etRate);
        etQuantity = (EditText) findViewById(R.id.et_quantity);
        etHSN = (EditText) findViewById(R.id.etHSNCode);
        etGstTax = (EditText)findViewById(R.id.etGSTTax);
        etItemDiscount = (EditText)findViewById(R.id.etItemDiscount);


        /*frame_rate_gst = (FrameLayout) findViewById(R.id.frame_rate_gst);
        frame_rate_nongst  = (FrameLayout) findViewById(R.id.frame_rate_nongst);
        frame_salesTax = (FrameLayout) findViewById(R.id.frame_salesTax);
        frame_serviceTax = (FrameLayout) findViewById(R.id.frame_serviceTax);
        frame_GSTTax = (FrameLayout) findViewById(R.id.frame_GSTTax);*/

        edtMenuCode = (EditText) findViewById(R.id.edtMenuCode);
        edtcessTax = (EditText) findViewById(R.id.edtcessTax);
        edtItemCGSTTax = (EditText) findViewById(R.id.edtItemCGSTTax);
        edtItemSGSTTax = (EditText) findViewById(R.id.edtItemSGSTTax);
        edtIGSTTax = (EditText) findViewById(R.id.edtIGSTTax);

        listViewItems = (ListView) findViewById(R.id.listViewItems);
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemClickEvent(itemListAdapter.getItems(position));
            }});

        txtDineIn1.addTextChangedListener(this);
        txtDineIn2.addTextChangedListener(this);
        txtDineIn3.addTextChangedListener(this);
        txtStock.addTextChangedListener(this);
        txtLongName.addTextChangedListener(this);
        AutoCompleteItemLongName.addTextChangedListener(this);
        edtMenuCode.addTextChangedListener(this);
        etHSN.addTextChangedListener(this);
        edtItemCGSTTax.addTextChangedListener(this);
        edtItemSGSTTax.addTextChangedListener(this);
        edtIGSTTax.addTextChangedListener(this);
        etItemDiscount.addTextChangedListener(this);

        edtItemSGSTTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String SGST = edtItemSGSTTax.getText().toString();
                String CGST = edtItemCGSTTax.getText().toString();
                if(SGST.equals("")){
                    //edtItemSGSTTax.setText("0");
                    SGST = ("0");
                }
                if(CGST.equals("")){
                    //edtItemCGSTTax.setText("0");
                    CGST = ("0");}

                double sgst_d = Double.parseDouble(SGST);
                double cgst_d = Double.parseDouble(CGST);
                double igst_d = sgst_d+cgst_d;
                edtIGSTTax.setText(String.format("%.2f",igst_d));

            }
        });
        edtItemCGSTTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String SGST = edtItemSGSTTax.getText().toString();
                String CGST = edtItemCGSTTax.getText().toString();
                if(CGST.equals("")) {
                    //edtItemCGSTTax.setText("0");
                    CGST = ("0");
                }
                if(SGST.equals("")) {
                    //edtItemSGSTTax.setText("0");
                    SGST = ("0");
                }

                double sgst_d = Double.parseDouble(SGST);
                double cgst_d = Double.parseDouble(CGST);
                double igst_d = sgst_d+cgst_d;
                edtIGSTTax.setText(String.format("%.2f",igst_d));

            }
        });
    }



    class GenerateCSV extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd = new ProgressDialog(ItemManagementActivity.this);
        int progress = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = 0;
            pd.setMessage("Generating sample csv...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            progress = 1;

            try {
                File directory = new File(CSV_GENERATE_PATH);
                if (!directory.exists())
                    directory.mkdirs();

                InputStream isAssetDbFile = myContext.getAssets().open("Sample_OutwardItem.csv");
                OutputStream osNewDbFile = new FileOutputStream(CSV_GENERATE_PATH + FILENAME);
                byte[] bFileBuffer = new byte[1024];
                int iBytesRead = 0;

                while ((iBytesRead = isAssetDbFile.read(bFileBuffer)) > 0) {
                    osNewDbFile.write(bFileBuffer, 0, iBytesRead);
                }

                osNewDbFile.flush();
                osNewDbFile.close();
                isAssetDbFile.close();
                pd.dismiss();
                publishProgress();

            } catch (FileNotFoundException e) {
                pd.dismiss();
                progress = 3;
                publishProgress();
                e.printStackTrace();
            } catch (IOException e) {
                pd.dismiss();
                progress = 4;
                publishProgress();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (progress == 1) {
                Toast.makeText(ItemManagementActivity.this, "CSV generated successfully! Path:" + CSV_GENERATE_PATH + FILENAME, Toast.LENGTH_LONG).show();
            } else if (progress == 3 || progress == 4){
                Toast.makeText(ItemManagementActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress = 2;
            pd.dismiss();
        }
    }

    public void generateCSV(){
        File temp = new File(CSV_GENERATE_PATH + FILENAME);

        AlertDialog.Builder builder = new AlertDialog.Builder(ItemManagementActivity.this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Overwrite Alert")
                .setMessage("There already exists a CSV file, regenerating a " +
                        "CSV file will overwrite the existing one. " +
                        "Are you sure you want to overwrite the file?")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new GenerateCSV().execute();
                    }
                });
        AlertDialog alert = builder.create();

        if (!temp.exists())
            new GenerateCSV().execute();
        else
            alert.show();
    }

    /**
     * performing button onclick upload csv file from external or SDCard
     */

    private void setCVSFile() {
        btnUploadExcel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(myContext, FilePickerActivity.class);
                intent.putExtra("contentType", "csv");
                startActivityForResult(intent, FilePickerActivity.FILE_PICKER_CODE);
            }
        });
    }

    /**
     * performing button onclick save csv file to data base and dispaly on UI
     */

    private void parseCVSFile() {
        btnSaveExcel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    AssetManager manager = myContext.getAssets();
                    if (strUploadFilepath.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "No File Found", Toast.LENGTH_SHORT).show();
                    } else {
                        String path = strUploadFilepath;
                       /* FileInputStream inputStream = new FileInputStream(path);
                        buffer = new BufferedReader(new InputStreamReader(inputStream));*/
                        buffer  = new BufferedReader(
                                new InputStreamReader(new FileInputStream(path),"ISO-8859-1"));
                        setCSVFileToDB(dataList);
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } finally {
                    tvFileName.setText(getResources().getString(R.string.select_filename));
                    strUploadFilepath = "";
                }
            }
        });
    }

    /**
     * Display exiting data as well as new data from data base
     *
     * @param inwardItemList - List of ItemInward
     */

    private void setCSVFileToDB(ArrayList<ItemOutward> inwardItemList) {

        if (inwardItemList.size() > 0) {
            showCSVAlertMessage();
        } else {
            downloadCSVData();
        }
    }

    /**
     * Ask User permission to dispaly old data or new data from CSV file
     */

    private void showCSVAlertMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                .setTitle("Replace Item")
                .setIcon(R.drawable.ic_launcher)
                .setMessage(" Are you sure you want to Replace all the existing Items, if any")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        downloadCSVData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //ClearItemTable();
                        DisplayItemList();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Download all data in Background then it will be return to UI
     */

    private void downloadCSVData() {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(ItemManagementActivity.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                readCSVValue(mCurrentTime);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if (mFlag) {
                        MsgBox.Show("Note", mUserCSVInvalidValue);
                    }else if(mUserCSVInvalidValue.equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "Items Imported Successfully", Toast.LENGTH_LONG).show();
                    }
                    DisplayItemList();
                    loadAutoCompleteData_ItemNames();
                    ResetItem();
                    //ClearItemTable();

                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * Read response from CSV file and set data to in Data base
     * checking All possible validation
     *
     * @param currentDate - need to current date
     */

    private void readCSVValue(String currentDate) {

        mUserCSVInvalidValue = "";
        String checkUOMTypye = "Pk Lt Ml Gm Kg Bg Bx No Mt Dz Sa St Bt Pl Pc";
        String[] checkSupplyType = {"G", "S"};
        String csvHeading = "MENU CODE,ITEM NAME,SUPPLY TYPE,RATE 1,RATE 2,RATE 3,QUANTITY,UOM,CGST RATE,SGST RATE,IGST RATE,cess RATE,DISCOUNT PERCENT,DEPARTMENT CODE,CATEGORY CODE,HSN,BAR CODE";
        boolean flag;
        boolean   mCSVHashCheckflag = false;
        try {
            String line;
            String chechCSVHeaderLine;
            chechCSVHeaderLine = buffer.readLine();


            mFlag = false;

            flag = csvHeading.equalsIgnoreCase(chechCSVHeaderLine);

            if (!flag) {
                mFlag = true;
                mUserCSVInvalidValue = getResources().getString(R.string.header_value_empty) + "\n"+csvHeading;
                //+ "MENU CODE,ITEM NAME,SUPPLY TYPE,RATE 1,RATE 2,RATE 3,QUANTITY,UOM,CGST RATE,SGST RATE,IGST RATE,cess RATE,DISCOUNT PERCENT";
                return;
            }

            //dataList.clear();
            mHashMapItemCode.clear();

            while ((line = buffer.readLine()) != null) {
                if (line.length() == 16)
                    continue;

                final String[] colums = line.split(",", -1);
                // final String[] colums = line.split("\\r\\n\\r\\n", 15);
                if (colums.length == 0)
                    continue;
                else if (colums.length != 17) {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.insufficient_information);
                    break;
                }
                if (colums[0] != null&& colums[0].trim().length() > 0  ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[0],"Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        mMenuCode = Integer.parseInt(colums[0]);
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.menu_code_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    if (colums[0].trim().length() > 0) {
                        mUserCSVInvalidValue = getResources().getString(R.string.menu_code_empty) + colums[1];
                        break;
                    } else {
                        mUserCSVInvalidValue = getResources().getString(R.string.please_enter_item_name);
                        break;
                    }
                }
                if ( colums[1] != null && colums[1].length() > 0 && colums[1].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[1],"String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {
                        if(colums[1].length()>20)
                            mItemName = colums[1].substring(0,21).toUpperCase();
                        else
                            mItemName = colums[1].toUpperCase();
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.item_name_invalid) + colums[0];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.item_name_empty) + colums[0];
                    break;
                }

                if (colums[2] != null && colums[2].length() > 0 && colums[2].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[2],"String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {

                        if (checkSupplyType[0].equals(colums[2])) {
                            mSupplyType = colums[2];
                        } else if (checkSupplyType[1].equals(colums[2])) {
                            mSupplyType = colums[2];
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.supply_type_invalid) + colums[1] + "e.g " + checkSupplyType;
                            break;
                        }

                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.supply_type_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.supply_type_empty) + colums[1];
                    break;
                }

                if (colums[3] != null && colums[3].length() > 0 && colums[3].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[3],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[3],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mRate1 = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[3])));
                        if(!(mRate1> 0 && mRate1 <999999998)){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter rate1 between 0 and 999999998 for item " +colums[1];
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.rate_1_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.rate_1_empty) + colums[1];
                    break;
                }

                if (colums[4] != null && colums[4].length() > 0 && colums[4].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[4],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[4],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mRate2 = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[4])));
                        if(!(mRate2> 0 && mRate2 <999999998)){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter rate2 between 0 and 999999998 for item "+ colums[1];
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.rate_2_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.rate_2_empty) + colums[1];
                    break;
                }

                if (colums[5] != null && colums[5].length() > 0 && colums[5].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[5],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[5],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mRate3 = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[5])));
                        if(!(mRate3> 0 && mRate3 <999999998)){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter rate3 between 0 and 999999998 for item " +colums[1];
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.rate_3_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.rate_3_empty) + colums[1];
                    break;
                }

                if (colums[6] != null && colums[6].length() > 0 && colums[6].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[6],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[5],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mQuantity = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[6])));
                        if(mQuantity>9999.99 || mQuantity<0){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter quantity between 0 and 9999.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.quantity_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.quantity_empty) + colums[1];
                    break;
                }

                if (colums[7] != null && colums[7].length() > 0 && colums[7].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[7],"String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {

                        if (colums[7].trim().length() == 2 && checkUOMTypye.contains(colums[7])) {
                            mUOM = colums[7];
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.uom_invalid) + colums[1] + "e.g " + checkUOMTypye;
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.uom_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.uom_empty) + colums[1];
                    break;
                }

                if (colums[8] != null && colums[8].length() > 0 && colums[8].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[8],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[8],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1==CHECK_INTEGER_VALUE) {
                        mCGSTRate = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[8])));
                        if(mCGSTRate>99.99 || mCGSTRate<0){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter CGST Rate between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.cgst_rate_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.cgst_rate_empty) + colums[1];
                    break;
                }

                if (colums[9] != null && colums[9].length() > 0 && colums[9].trim().length() > 0 ) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[9],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[9],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mSGSTRate =Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[9])));
                        if(mSGSTRate>99.99 || mSGSTRate<0){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter SGST Rate between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.sgst_rate_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.sgst_rate_empty) + colums[1];
                    break;
                }
                if (mCGSTRate+mSGSTRate <0 || mCGSTRate+mSGSTRate >99.99)
                {
                    mFlag = true;
                    mUserCSVInvalidValue = "Please note sum of SGST Rate and CGST Rate should between 0 and 99.99 for item " + colums[1];
                    return;
                }
                if (colums[10] != null && colums[10].length() > 0 && colums[10].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[10],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[10],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mIGSTRate = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[10])));
                        if(mIGSTRate>99.99 || mIGSTRate<0){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter IGST Rate between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.igst_rate_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.igst_rate_empty) + colums[1];
                    break;
                }

                if (mCGSTRate+mSGSTRate !=mIGSTRate)
                {
                    mFlag = true;
                    mUserCSVInvalidValue = "Please note sum of SGST Rate and CGST Rate should between equal to IGST Rate for item " + colums[1];
                    return;
                }
                if (colums[11] != null && colums[11].length() > 0 && colums[11].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[11],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[11],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mCESSRate = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[11])));
                        if(mCESSRate>99.99 || mCESSRate<0){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter cess Rate between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.cess_rate_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.cess_rate_empty) + colums[1];
                    break;
                }

                if (colums[12] != null && colums[12].length() > 0 && colums[12].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[12],"Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue1(colums[12],"Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mDiscount = Double.parseDouble(String.format("%.2f",Double.parseDouble(colums[12])));
                        if(mDiscount>99.99 || mDiscount<0){
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter discount between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.discount_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.discount_empty) + colums[1];
                    break;
                }
                if (colums[13] != null && colums[13].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[13], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        if (mDepartmentCodeList != null && mDepartmentCodeList.size() > 0) {
                            if (mDepartmentCodeList.contains(Integer.parseInt(colums[13]))) {
                                mDeptCode = Integer.parseInt(colums[13]);
                            } else {
                                mFlag = true;
                                mUserCSVInvalidValue = "This department code " + colums[13] + " is not present in the database for Item Name " + colums[1];
                                break;
                            }
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.database_department_null);
                            break;
                        }

                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.department_code_invalid) + " " + colums[1];
                        break;
                    }
                } else {
                    mDeptCode = 0;
                    mCategCode = 0;
                }

                boolean categoryFlag = false;

                if (colums[14] != null && colums[14].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue1(colums[14], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {

                        if (mCategoryCodeList != null && mCategoryCodeList.size() > 0) {

                            if (mDeptCode == 0) {
                                mFlag = true;
                                mUserCSVInvalidValue = "Item Name " + mItemName + " can not have category code " + colums[14] + " without department";
                                break;
                            }

                            if (mCategoryCodeList.containsKey(Integer.parseInt(colums[14]))) {
                                categoryFlag = true;
                            } else {
                                mFlag = true;
                                mUserCSVInvalidValue = "This category code " + colums[14] + " is not present in the database for Item Name " + colums[1];
                                break;
                            }

                            if (categoryFlag) {

                                if(checkDeptandCategoryTogether(mDeptCode ,Integer.parseInt(colums[14])) != 0) {
                                    mCategCode = checkDeptandCategoryTogether(mDeptCode ,Integer.parseInt(colums[14]));
                                } else  {
                                    mFlag = true;
                                    mUserCSVInvalidValue = "Category code " + colums[14] + " is not linked to department code " + mDeptCode + " for Item Name " + mItemName
                                            + "  in category tab of configurations feature";
                                    break;
                                }

                            }
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.database_category_null);
                            break;
                        }

                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.category_code_invalid) + " " + colums[1];
                        break;
                    }

                } else {
                    mCategCode = 0;
                }

                mHSN = "";
                if (colums[15] != null && colums[15].trim().length() > 0) {
                    mHSN = colums[15];
                }

                mbarCode = "";
                if (colums[16] != null && colums[16].trim().length() > 0) {
                    mbarCode = colums[16];
                }


                ItemOutward item_add = new ItemOutward(mMenuCode, mItemName, mRate1, mRate2, mRate3, mQuantity,
                        mDeptCode, mCategCode, mKitchenCode, mbarCode, mImageUri, mMenuCode,
                        mCGSTRate, mSGSTRate, mIGSTRate, mCESSRate, mUOM, mHSN, mtaxationType, mSupplyType,mDiscount);


                for (Map.Entry<Integer, ItemOutward> entry : mHashMapItemCode.entrySet()) {
                    //  String key = entry.getKey();
                    //  ItemOutward itemOutward = new ItemOutward()
                    ItemOutward entryValue = entry.getValue();
                    if (mHashMapItemCode.containsKey(mMenuCode) ) {
                        mCSVHashCheckflag = true;
                        break;

                    }else if (entryValue.getItemName().equalsIgnoreCase(mItemName)) {
                        mCSVHashCheckflag = true;
                        break;
                    }
                }
                if(!mCSVHashCheckflag)
                {
                    mHashMapItemCode.put(mMenuCode, item_add);
                }else
                {
                    //MsgBox.Show("Error","Either itemName or MenuCode is already present in CSV file");
                    mFlag = true;
                    mUserCSVInvalidValue = "Either itemName "+item_add.getItemName()+" or MenuCode "+mMenuCode+" is already present in CSV file";
                    mHashMapItemCode.clear();
                    return;
                }
                //Log.d("welcome", "readCSVValue: " + dataList);
            }
        /*if(mUserCSVInvalidValue.equals(""))
            mCSVHashCheckflag = true;*/

            if(!mCSVHashCheckflag && !mFlag)
            {
                saveCSVinDatabase();

            }

        } catch (Exception exp) {
            mCSVHashCheckflag = true;
            exp.printStackTrace();
        }
    }

    private int checkDeptandCategoryTogether(int departmentCSVValue , int categoryCSVValue) {
        int categoryCode = 0;

        for (Map.Entry entry : mCategoryCodeList.entrySet()) {

            if (entry.getKey().equals(categoryCSVValue) && entry.getValue().equals(departmentCSVValue)) {
                categoryCode = categoryCSVValue;
                return categoryCode;
            }
        }
        return categoryCode;
    }



    private void saveCSVinDatabase() {

        dataList = new ArrayList<>(mHashMapItemCode.values());

        //   Log.d("welcome", " hash map"+mHashMap);
        Log.d("welcome", " hash map" + mHashMapItemCode);


        if (dataList.size() > 0) {
            int deleted = dbItems.clearOutwardItemdatabase();
            Log.d("ItemManagement", " Items deleted before uploading excel :" + deleted);
            deleted = dbItems.clearOutwardStock(businessDate);
            Log.d("ItemManagement", " Outward Stock deleted before uploading excel :" + deleted);
            for (int i = 0; i < dataList.size(); i++) {
                dbItems.addItem(dataList.get(i));
            }
            final String current_date = businessDate;
            StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
            stock_outward.saveOpeningStock_Outward(current_date);
        }

    }


    private void checkCSVItemName() {

        /*ItemOutward itemOutward1 = new ItemOutward(mMenuCode, mItemName, mRate1, mRate2, mRate3, mQuantity,
                mDeptCode, mCategCode, mKitchenCode, mbarCode, mImageUri, mItemId,
                mCGSTRate, mSGSTRate, mIGSTRate, mCESSRate, mUOM, mHSN, mtaxationType, mSupplyType);

        List<ItemOutward> itemCodeList = new ArrayList<>(mHashMapItemCode.values());

        for (int i = 0; i < itemCodeList.size(); i++) {

            ItemOutward itemOutward = new ItemOutward(itemCodeList.get(i).getMenuCode(),
                    itemCodeList.get(i).getItemName(),
                    itemCodeList.get(i).getDineIn1(),
                    itemCodeList.get(i).getDineIn2(),
                    itemCodeList.get(i).getDineIn3(),
                    itemCodeList.get(i).getStock(),
                    itemCodeList.get(i).getDeptCode(),
                    itemCodeList.get(i).getCategCode(),
                    itemCodeList.get(i).getKitchenCode(),
                    itemCodeList.get(i).getBarCode(),
                    itemCodeList.get(i).getImageUri(),
                    itemCodeList.get(i).getItemId(),
                    itemCodeList.get(i).getCGSTRate(),
                    itemCodeList.get(i).getSGSTRate(),
                    itemCodeList.get(i).getIGSTRate(),
                    itemCodeList.get(i).getCessRate(),
                    itemCodeList.get(i).getUOM(),
                    itemCodeList.get(i).getHSN(),
                    itemCodeList.get(i).getTaxationType(),
                    itemCodeList.get(i).getSupplyType());

            mHashMapItemName.put(itemCodeList.get(i).getItemName(), itemOutward);
        }*/

        ArrayList<ItemOutward> finalValidCSVList = new ArrayList<>(mHashMapItemName.values());


    }

    /**
     * checking types of data validation(Integer , Double , String)
     *
     * @param value - csv value
     */

    public static int checkCSVTypeValue(String value) {
        int flag;
        try {
            Integer.parseInt(value);
            flag = 0;
            return flag;
        } catch (NumberFormatException nfe) {
            // nfe.printStackTrace();
        }
        try {
            Double.parseDouble(value);
            flag = 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            flag = 2;
        }
        return flag;
    }
    public static int checkCSVTypeValue1(String value,String dataType) {
        int flag =2;
        try {
            switch(dataType)
            {
                case "Int" : Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double" :  Double.parseDouble(value);
                    flag = 1;
                    break;
                default: flag = 2;
            }
        } catch (Exception nfe) {
            // nfe.printStackTrace();
        }
        finally
        {return flag;}
    }


    private void listItemClickEvent(ItemOutward item) {
        edtMenuCode.setText(String.valueOf(item.getMenuCode()));
        txtLongName.setText(item.getItemName());
        AutoCompleteItemLongName.setText(item.getItemName());
        itemName_beforeChange_in_update = item.getItemName();
        itembarCode_beforeChange_in_update = item.getBarCode();
        AutoCompleteItemBarcode.setText(item.getBarCode());
        txtDineIn1.setText(String.format("%.2f",item.getDineIn1()));
        txtDineIn2.setText(String.format("%.2f",item.getDineIn2()));
        txtDineIn3.setText(String.format("%.2f",item.getDineIn3()));
        txtStock.setText(String.format("%.2f",item.getStock()));

        strImageUri = item.getImageUri();

        Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(item.getDeptCode()));
        String deptName = "Select";
        int deptid = 0;
        if (crsrDept.moveToFirst()) {
            deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
            deptid = getIndexDept(deptName + "");
            spnrDepartment.setSelection(deptid);
        } else {

            deptid = getIndexDept(deptName + "");
            spnrDepartment.setSelection(deptid);
        }

        ArrayList<String> listCateg = dbItems.getCategoryNameByDeptCode(String.valueOf(deptid));
        loadSpinnerData_cat(listCateg);
        if (deptid ==0) {
            spnrCategory.setSelection(0);
        }else {
            int categoryId = Integer.valueOf(item.getCategCode());
            Cursor crsrCateg = dbItems.getCategory(categoryId);
            String categName = "";
            mCategoryNameByDept = "";
            if (crsrCateg.moveToFirst()) {
                categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
            }

            mCategoryNameByDept = categName;
            boolean bool = false;
            int i =0;
            for( i =0; !categName.equals("")&&bool == false && i<listCateg.size();i++)
            {
                if(categName.equalsIgnoreCase(listCateg.get(i)))
                    bool = true;
            }
            if(bool) // true
            {
                spnrCategory.setSelection(i-1);
            }
            else
                spnrCategory.setSelection(0);
        }

        spnrKitchen.setSelection((item.getKitchenCode()) );


        imgItemImage.setImageURI(null);
        if (!strImageUri.equalsIgnoreCase("")) { // &&
            // strImageUri.contains("\\")){
            imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
        } else {
            imgItemImage.setImageResource(R.drawable.img_noimage);
        }
        edtItemCGSTTax.setText(String.format("%.2f",item.getCGSTRate()));
        edtItemSGSTTax.setText(String.format("%.2f",item.getSGSTRate()));
        edtIGSTTax.setText(String.format("%.2f",item.getIGSTRate()));
        edtcessTax.setText(String.format("%.2f",item.getCessRate()));
        etItemDiscount.setText(String.format("%.2f",item.getItemDiscount()));

        String uom_temp = item.getUOM();
        String uom = "("+uom_temp+")";
        int index = getIndex_uom(uom);
        spnrMOU.setSelection(index);
        strItemId = String.valueOf(item.getItemId());
        etHSN.setText(item.getHSN());
        spnrtaxationtype.setSelection(getIndex_taxationType(item.getTaxationType()));

        btnAdd.setEnabled(false);
        btnEdit.setEnabled(true);


    }
    private void InitializeAdapters() {
        Log.d("Functions", "InitializeAdapters");
        // Cursor variable
        Cursor crsrAdapterData;

        // Initialize adapters
        adapDeptCode = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapDeptCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapCategCode = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapCategCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapKitCode = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapKitCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapTax = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapTax.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapDiscount = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapDiscount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        supplytypeAdapter = ArrayAdapter.createFromResource(this, R.array.g_s, android.R.layout.simple_spinner_item);
        supplytypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrG_S.setAdapter(supplytypeAdapter);

        MOUAdapter = ArrayAdapter.createFromResource(this, R.array.UOM, android.R.layout.simple_spinner_item);
        MOUAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrMOU.setAdapter(MOUAdapter);

        taxationtypeAdapter = ArrayAdapter.createFromResource(this, R.array.taxationtype, android.R.layout.simple_spinner_item);
        taxationtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrtaxationtype.setAdapter(taxationtypeAdapter);

        // Assign adapters to spinners
        spnrDepartment.setAdapter(adapDeptCode);
        //spnrCategory.setAdapter(adapCategCode);
        spnrKitchen.setAdapter(adapKitCode);
        spnrOptionalTax1.setAdapter(adapTax);
        spnrOptionalTax2.setAdapter(adapTax);


        // Add Kitchen to adapter
        crsrAdapterData = null;
        crsrAdapterData = dbItems.getAllKitchen();
        Log.d("Kitchen", "Rows:" + String.valueOf(crsrAdapterData.getCount()));
        if (crsrAdapterData.moveToFirst()) {
            adapKitCode.add("Select");
            do {
                adapKitCode.add(crsrAdapterData.getString(1));
            } while (crsrAdapterData.moveToNext());
        }else
        {
            adapKitCode.add("Select");
        }

        // Add Tax to adapter
        crsrAdapterData = null;
        crsrAdapterData = dbItems.getAllTaxConfig();
        Log.d("Tax", "Rows:" + String.valueOf(crsrAdapterData.getCount()));
        if (crsrAdapterData.moveToFirst()) {
            do {
                adapTax.add(crsrAdapterData.getString(1));
            } while (crsrAdapterData.moveToNext());
        }

        // Add Tax to adapter
        crsrAdapterData = null;
        crsrAdapterData = dbItems.getAllDiscountConfig();
        Log.d("Discount", "Rows:" + String.valueOf(crsrAdapterData.getCount()));
        if (crsrAdapterData.moveToFirst()) {
            do {
                adapDiscount.add(crsrAdapterData.getString(1));
            } while (crsrAdapterData.moveToNext());
        }
    }


    void DisplayItemList()
    {
        dataList = new ArrayList<ItemOutward>();
        Cursor cursorItem = dbItems.getAllItems();

        while(cursorItem!=null && cursorItem.moveToNext())
        {
            ItemOutward item = new ItemOutward();
            item.setMenuCode(cursorItem.getInt(cursorItem.getColumnIndex("MenuCode")));
            item.setItemName(cursorItem.getString(cursorItem.getColumnIndex("ItemName")));
            item.setDineIn1(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice1")))));
            item.setDineIn2(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice2")))));
            item.setDineIn3(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice3")))));
            item.setStock(  Float.parseFloat(String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("Quantity")))));
            item.setDeptCode(cursorItem.getInt(cursorItem.getColumnIndex("DeptCode")));
            item.setCategCode(cursorItem.getInt(cursorItem.getColumnIndex("CategCode")));
            item.setKitchenCode(cursorItem.getInt(cursorItem.getColumnIndex("KitchenCode")));
            item.setBarCode(cursorItem.getString(cursorItem.getColumnIndex("ItemBarcode")));
            item.setImageUri(cursorItem.getString(cursorItem.getColumnIndex("ImageUri")));
            item.setUOM(cursorItem.getString(cursorItem.getColumnIndex("UOM")));
            item.setCGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("CGSTRate")))));
            item.setSGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("SGSTRate")))));
            item.setIGSTRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("IGSTRate")))));
            item.setCessRate(Float.parseFloat(String.format("%.2f",cursorItem.getFloat(cursorItem.getColumnIndex("cessRate")))));
            item.setItemId(cursorItem.getInt(cursorItem.getColumnIndex("ItemId")));
            item.setHSN(cursorItem.getString(cursorItem.getColumnIndex("HSNCode")));
            item.setTaxationType(cursorItem.getString(cursorItem.getColumnIndex("TaxationType")));
            item.setItemDiscount(Float.parseFloat(String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("DiscountPercent")))));

            dataList.add(item);
        }
        if (itemListAdapter == null) {
            itemListAdapter = new ItemOutwardAdapter(ItemManagementActivity.this, dataList,dbItems, AutoCompleteItemLongName, AutoCompleteItemBarcode);
            listViewItems.setAdapter(itemListAdapter);
        } else {
            itemListAdapter.notifyNewDataAdded(dataList);
        }


    }

    @SuppressWarnings("deprecation")
//    private void DisplayItems() {
//        Cursor crsrItems = null;
//
//        crsrItems = dbItems.getAllItems();
//        //crsrItems = dbItems.getAllItemswithDeptCategName();
//        TableRow rowItems = null;
//
//        TextView tvSno, tvMenuCode, tvLongName, tvShortName, tvDineIn1, tvDineIn2, tvDineIn3, tvTakeAway, tvPickUp, tvDelivery,
//                tvStock, tvPriceChange, tvDiscountEnable, tvBillWithStock, tvTaxType, tvDeptCode, tvCategCode,
//                tvKitchenCode, tvSalesTaxId, tvAdditionalTaxId, tvOptionalTaxId1, tvOptionalTaxId2, tvDiscountId,
//                tvItemBarcode, tvImageUri, tvSpace, tvDeptName, tvCategName, tvItemId;
//        TextView tvHSNCode_out,tvMOU,tvRate,tvGSTRate,tvSupplyType, tvSalesTaxPercent, tvServiceTaxPercent;
//        ImageView imgIcon;
//        ImageButton btnItemDelete;
//        int i = 1;
//
//        if (crsrItems.moveToFirst()) {
//            do {
//                rowItems = new TableRow(myContext);
//                rowItems.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//                rowItems.setBackgroundResource(R.drawable.border_itemdatabase);
//
//                tvSno = new TextView(myContext);
//                tvSno.setTextSize(15);
//                tvSno.setGravity(1);
//                tvSno.setWidth(105);
//                tvSno.setTextColor(getResources().getColor(R.color.black));
//                tvSno.setText(String.valueOf(i));
//                //rowItems.addView(tvSno);
//
//                tvMenuCode = new TextView(myContext);
//                tvMenuCode.setTextSize(18);
//                tvMenuCode.setGravity(1);
//                tvMenuCode.setWidth(160);
//                tvMenuCode.setText(crsrItems.getString(crsrItems.getColumnIndex("MenuCode")));
//                //rowItems.addView(tvMenuCode);
//
//                tvLongName = new TextView(myContext);
//                tvLongName.setTextSize(15);
//                tvLongName.setWidth(360);
//                //tvLongName.setTypeface(Typeface.DEFAULT_BOLD);
//                tvLongName.setTextColor(getResources().getColor(R.color.black));
//                tvLongName.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemName")));
//                //rowItems.addView(tvLongName);
//
//
//
//                tvDineIn1 = new TextView(myContext);
//                //tvDineIn1.setGravity(1);
//                tvDineIn1.setTextSize(15);
//                tvDineIn1.setGravity(1);
//                tvDineIn1.setWidth(110);
//                //tvDineIn1.setBackgroundResource(R.drawable.border);
//                tvDineIn1.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice1")));
//                //rowItems.addView(tvDineIn1);
//
//                tvDineIn2 = new TextView(myContext);
//                tvDineIn2.setGravity(1);
//                tvDineIn2.setTextSize(15);
//                tvDineIn2.setWidth(110);
//                tvDineIn2.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice2")));
//                //rowItems.addView(tvDineIn2);
//
//                tvDineIn3 = new TextView(myContext);
//                tvDineIn3.setGravity(1);
//                tvDineIn3.setWidth(110);
//                tvDineIn3.setTextSize(15);
//                tvDineIn3.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice3")));
//                //rowItems.addView(tvDineIn3);
//
//
//
//                tvStock = new TextView(myContext);
//                tvStock.setGravity(1);
//                tvStock.setWidth(110);
//                tvStock.setTextSize(15);
//                //tvStock.setTypeface(Typeface.DEFAULT_BOLD);
//                tvStock.setTextColor(getResources().getColor(R.color.black));
//                //tvStock.setBackgroundResource(R.drawable.border);
//                tvStock.setText(crsrItems.getString(crsrItems.getColumnIndex("Quantity")));
//                //rowItems.addView(tvStock);
//
//
//
//
//
//                tvDeptCode = new TextView(myContext);
//                tvDeptCode.setText(crsrItems.getString(crsrItems.getColumnIndex("DeptCode")));
//                //rowItems.addView(tvDeptCode);
//
//                tvCategCode = new TextView(myContext);
//                tvCategCode.setText(crsrItems.getString(crsrItems.getColumnIndex("CategCode")));
//                //rowItems.addView(tvCategCode);
//
//                tvKitchenCode = new TextView(myContext);
//                tvKitchenCode.setText(crsrItems.getString(crsrItems.getColumnIndex("KitchenCode")));
//                //rowItems.addView(tvKitchenCode);
//
//
//
//                tvItemBarcode = new TextView(myContext);
//                tvItemBarcode.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemBarcode")));
//                //rowItems.addView(tvItemBarcode);
//
//                tvImageUri = new TextView(myContext);
//                tvImageUri.setText(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")));
//                //rowItems.addView(tvImageUri);
//
//                imgIcon = new ImageView(myContext);
//                TableRow.LayoutParams rowparams = new TableRow.LayoutParams(60, 40);
//                rowparams.gravity = Gravity.CENTER;
//                imgIcon.setLayoutParams(rowparams);
//                imgIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                imgIcon.setImageURI(null);
//                // imgIcon.setBackgroundResource(R.drawable.border);
//                if (!crsrItems.getString(crsrItems.getColumnIndex("ImageUri")).equalsIgnoreCase("")) { // &&
//                    // strImageUri.contains("\\")){
//                    imgIcon.setImageURI(Uri.fromFile(new File(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")))));
//                } else {
//                    imgIcon.setImageResource(R.drawable.img_noimage);
//                }
//                //rowItems.addView(imgIcon);
//
//                tvSupplyType = new TextView(myContext);
//                tvSupplyType.setGravity(1);
//                tvSupplyType.setWidth(110);
//                tvSupplyType.setText(crsrItems.getString(crsrItems.getColumnIndex("SupplyType")));
//                //rowItems.addView(tvSupplyType);
//
//
//                tvMOU = new TextView(myContext);
//                tvMOU.setGravity(Gravity.RIGHT|Gravity.END);
//                tvMOU.setWidth(120);
//                tvMOU.setTextSize(15);
//                //tvMOU.setTypeface(Typeface.DEFAULT_BOLD);
//                tvMOU.setTextColor(getResources().getColor(R.color.black));
//                //tvMOU.setBackgroundResource(R.drawable.border);
//                tvMOU.setText(crsrItems.getString(crsrItems.getColumnIndex("UOM")));
//                //rowItems.addView(tvMOU);
//
//
//
//                // For Space purpose
//                tvSpace = new TextView(myContext);
//                tvSpace.setWidth(49);
//                tvSpace.setText("");
//                //tvSpace.setBackgroundResource(R.drawable.border);
//                //rowItems.addView(tvSpace);
//
//                TextView tvSpace1 = new TextView(myContext);
//                tvSpace1.setText("");
//                tvSpace1.setWidth(22);
//
//                // Delete
//                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
//                btnItemDelete = new ImageButton(myContext);
//
//                TableRow.LayoutParams rowparams1 = new TableRow.LayoutParams(40, 35);
//                rowparams1.gravity = Gravity.CENTER;
//                btnItemDelete.setBackground(getResources().getDrawable(R.drawable.delete_icon_border));
//                //btnItemDelete.setLayoutParams(rowparams1);
//                btnItemDelete.setLayoutParams(new TableRow.LayoutParams(40, 35));
//                //btnItemDelete.setOnClickListener(mListener);
//
//
//
//
//                TextView tvTaxationType = new TextView(myContext);
//                tvTaxationType.setText(spnrtaxationtype.getSelectedItem().toString());
//
//                tvItemId = new TextView(myContext);
//                tvItemId.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemId")));
//
//                tvSalesTaxPercent = new TextView(myContext);
//                tvSalesTaxPercent.setText(crsrItems.getString(crsrItems.getColumnIndex("SalesTaxPercent")));
//
//                tvServiceTaxPercent = new TextView(myContext);
//                tvServiceTaxPercent.setText(crsrItems.getString(crsrItems.getColumnIndex("ServiceTaxPercent")));
//
//                if(GSTEnable.equals("0")) {
//
//                    rowItems.addView(tvSno);//0
//                    rowItems.addView(tvMenuCode);//1
//                    rowItems.addView(tvLongName);//2
//
//                    rowItems.addView(tvDineIn1);//3
//                    rowItems.addView(tvDineIn2);//4
//                    rowItems.addView(tvDineIn3);//5
//
//                    rowItems.addView(tvStock);//6
//
//                    rowItems.addView(tvDeptCode);//7
//                    rowItems.addView(tvCategCode);//8
//                    rowItems.addView(tvKitchenCode);//9
//
//                    rowItems.addView(tvItemBarcode);//10
//                    rowItems.addView(tvImageUri);//11
//                    rowItems.addView(tvSpace1);//12
//                    rowItems.addView(imgIcon);//13
//                    rowItems.addView(tvSpace);//14
//                    rowItems.addView(btnItemDelete);//15
//
//
//                    rowItems.addView(tvItemId);//16
//                    rowItems.addView(tvSalesTaxPercent);//17
//                    rowItems.addView(tvServiceTaxPercent);//18
//                    rowItems.addView(tvMOU);//19
//
//                    rowItems.setOnClickListener(new View.OnClickListener() {
//
//                        public void onClick(View v) {
//                            // TODO Auto-generated method stub
//
//                            if (String.valueOf(v.getTag()) == "TAG") {
//                                TableRow Row = (TableRow) v;
//                                ROWCLICKEVENT = 1;
//                                TextView MenuCode = (TextView) Row.getChildAt(1);
//                                TextView LongName = (TextView) Row.getChildAt(2);
//                                itemName_beforeChange_in_update = LongName.getText().toString();
//                                Log.d("Richa",itemName_beforeChange_in_update);
//                                TextView DineIn1 = (TextView) Row.getChildAt(3);
//                                TextView DineIn2 = (TextView) Row.getChildAt(4);
//                                TextView DineIn3 = (TextView) Row.getChildAt(5);
//                                TextView Stock = (TextView) Row.getChildAt(6);
//                                TextView DeptCode = (TextView) Row.getChildAt(7);
//                                TextView CategCode = (TextView) Row.getChildAt(8);
//                                TextView KitchenCode = (TextView) Row.getChildAt(9);
//                                TextView Barcode = (TextView) Row.getChildAt(10);
//                                TextView ImageUri = (TextView) Row.getChildAt(11);
//                                TextView ItemId = (TextView) Row.getChildAt(16);
//                                TextView SalesTaxPercent = (TextView) Row.getChildAt(17);
//                                TextView ServiceTaxPercent = (TextView) Row.getChildAt(18);
//                                TextView MOU = (TextView) Row.getChildAt(19);
//
//                                strItemId = ItemId.getText().toString();
//                                edtMenuCode.setText(MenuCode.getText().toString());
//                                txtLongName.setText(LongName.getText());
//                                itemName_beforeChange_in_update = txtLongName.getText().toString();
//                                Log.d("Richa1",itemName_beforeChange_in_update);
//                                //txtShortName.setText(ShortName.getText());
//                                AutoCompleteItemBarcode.setText(Barcode.getText());
//                                txtDineIn1.setText(DineIn1.getText());
//                                txtDineIn2.setText(DineIn2.getText());
//                                txtDineIn3.setText(DineIn3.getText());
//                                txtStock.setText(Stock.getText());
//                                strImageUri = ImageUri.getText().toString();
//
//                                Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(DeptCode.getText().toString()));
//                                String deptName = "Select";
//                                int deptid = 0;
//                                if (crsrDept.moveToFirst()) {
//                                    deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
//                                    deptid = getIndexDept(deptName + "");
//                                    spnrDepartment.setSelection(deptid);
//                                } else {
//
//                                    deptid = getIndexDept(deptName + "");
//                                    spnrDepartment.setSelection(deptid);
//                                }
//                                //spnrDepartment.setSelection(Integer.parseInt(DeptCode.getText().toString()));
//                                ArrayList<String> listCateg = dbItems.getCategoryNameByDeptCode(String.valueOf(deptid));
//                                loadSpinnerData_cat(listCateg);
//                                int  deptCode =Integer.parseInt(DeptCode.getText().toString());
//                                if (deptCode ==0) {
//                                    spnrCategory.setSelection(0);
//                                }else {
//                                    Cursor crsrCateg = dbItems.getCategory(Integer.valueOf(CategCode.getText().toString()));
//                                    String categName = "";
//                                    if (crsrCateg.moveToFirst()) {
//                                        categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
//                                    }
//                                    boolean bool = false;
//                                    int i =0;
//                                    for( i =0; !categName.equals("")&&bool == false && i<listCateg.size();i++)
//                                    {
//                                        if(categName.equalsIgnoreCase(listCateg.get(i)))
//                                            bool = true;
//                                    }
//                                    /*ArrayAdapter<String> dataAdapter = (ArrayAdapter<String>) spnrCategory.getAdapter();
//                                    int spinnerPosition = dataAdapter.getPosition(categName);*/
//                                    if(bool) // true
//                                    {
//                                        spnrCategory.setSelection(i-1);
//                                    }
//                                    else
//                                        spnrCategory.setSelection(0);
//                                }
//
//
//
//
//                                spnrKitchen.setSelection(Integer.parseInt(KitchenCode.getText().toString()) );
//
//
//                                imgItemImage.setImageURI(null);
//                                if (!strImageUri.equalsIgnoreCase("")) { // &&
//                                    // strImageUri.contains("\\")){
//                                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
//                                } else {
//                                    imgItemImage.setImageResource(R.drawable.img_noimage);
//                                }
//                                edtItemSalesTax.setText(SalesTaxPercent.getText().toString());
//                                edtItemServiceTax.setText(ServiceTaxPercent.getText().toString());
//                                String uom_temp = MOU.getText().toString();
//                                String uom = "("+uom_temp+")";
//                                int index = getIndex(uom);
//                                spnrMOU.setSelection(index);
//                                btnLinkItem.setEnabled(false);
//                                btnDelinkItem.setEnabled(true);
//                            }
//                        }
//                    });
//
//                    rowItems.setTag("TAG");
//
//                    tblItems.addView(rowItems, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//                    i++;
//                }
//                else
//                {
//                    // gst enabled
//
//                    /*rowItems.addView(tvSno);
//                    rowItems.addView(tvMenuCode);
//                    rowItems.addView(tvLongName);
//                    rowItems.addView(tvShortName);
//                    rowItems.addView(tvRate);
//                    rowItems.addView(tvStock);
//                    rowItems.addView(tvMOU);
//                    rowItems.addView(tvTakeAway);
//                    rowItems.addView(tvPickUp);
//                    rowItems.addView(tvDelivery);
//                    rowItems.addView(tvGSTRate);
//                    rowItems.addView(tvPriceChange);
//                    rowItems.addView(tvDiscountEnable);
//                    rowItems.addView(tvBillWithStock);
//                    rowItems.addView(tvTaxType);
//                    rowItems.addView(tvDeptCode);
//                    rowItems.addView(tvCategCode);
//                    rowItems.addView(tvKitchenCode);
//                    rowItems.addView(tvSalesTaxId);
//                    rowItems.addView(tvAdditionalTaxId);
//                    rowItems.addView(tvOptionalTaxId1);
//                    rowItems.addView(tvOptionalTaxId2);
//                    rowItems.addView(tvDiscountId);
//                    rowItems.addView(tvItemBarcode);
//                    rowItems.addView(tvImageUri);//24
//                    rowItems.addView(tvSpace1);//25
//                    rowItems.addView(imgIcon);//26
//                    rowItems.addView(tvSpace);//27
//                    rowItems.addView(btnItemDelete);//28
//                    rowItems.addView(tvHSNCode_out);//29
//                    rowItems.addView(tvSupplyType);
//                    rowItems.addView(tvTaxationType);
//
//                    // For Space purpose
//                    tvSpace = new TextView(myContext);
//                    tvSpace.setText("              ");
//                    rowItems.addView(tvSpace);
//                    rowItems.addView(tvItemId);//33
//                    rowItems.addView(tvSalesTaxPercent);//34
//                    rowItems.addView(tvServiceTaxPercent);//35
//
//                    rowItems.setOnClickListener(new View.OnClickListener() {
//
//                        public void onClick(View v) {
//                            // TODO Auto-generated method stub
//
//                            if (String.valueOf(v.getTag()) == "TAG") {
//                                TableRow Row = (TableRow) v;
//
//                                TextView MenuCode = (TextView) Row.getChildAt(1);
//                                TextView LongName = (TextView) Row.getChildAt(2);
//                                TextView ShortName = (TextView) Row.getChildAt(3);
//                                TextView Rate = (TextView) Row.getChildAt(4);
//                                TextView Quantity = (TextView) Row.getChildAt(5);
//                                TextView MOU = (TextView) Row.getChildAt(6);
//                                TextView TakeAway = (TextView) Row.getChildAt(7);
//                                TextView PickUp = (TextView) Row.getChildAt(8);
//                                TextView Delivery = (TextView) Row.getChildAt(9);
//                                TextView GSTRate = (TextView) Row.getChildAt(10);
//                                TextView PriceChange = (TextView) Row.getChildAt(11);
//                                TextView DiscountEnable = (TextView) Row.getChildAt(12);
//                                TextView BillWithStock = (TextView) Row.getChildAt(13);
//                                TextView TaxType = (TextView) Row.getChildAt(14);
//                                TextView DeptCode = (TextView) Row.getChildAt(15);
//                                TextView CategCode = (TextView) Row.getChildAt(16);
//                                TextView KitchenCode = (TextView) Row.getChildAt(17);
//                                TextView SalesTaxId = (TextView) Row.getChildAt(18);
//                                TextView AdditionalTaxId = (TextView) Row.getChildAt(19);
//                                TextView OptionalTaxId1 = (TextView) Row.getChildAt(20);
//                                TextView OptionalTaxId2 = (TextView) Row.getChildAt(21);
//                                TextView DiscountId = (TextView) Row.getChildAt(22);
//                                TextView Barcode = (TextView) Row.getChildAt(23);
//                                TextView ImageUri = (TextView) Row.getChildAt(24);
//                                TextView SupplyType = (TextView) Row.getChildAt(30);
//                                TextView HSNCode_out = (TextView) Row.getChildAt(29);
//                                TextView taxationtype =  (TextView) Row.getChildAt(31);
//                                TextView ItemId = (TextView) Row.getChildAt(33);
//                                TextView SalesTaxPercent = (TextView) Row.getChildAt(34);
//                                TextView ServiceTaxPercent = (TextView) Row.getChildAt(35);
//
//                                strItemId = ItemId.getText().toString();
//                                edtMenuCode.setText(MenuCode.getText());
//                                txtLongName.setText(LongName.getText());
//                                //txtShortName.setText(ShortName.getText());
//                                AutoCompleteItemBarcode.setText(Barcode.getText());
//                                *//*txtDineIn1.setText(DineIn1.getText());
//                                txtDineIn2.setText(DineIn2.getText());
//                                txtDineIn3.setText(DineIn3.getText());
//                                txtStock.setText(Stock.getText());*//*
//                                strImageUri = ImageUri.getText().toString();
//
//                                // gst
//                                etGstTax.setText(GSTRate.getText());
//                                etRate.setText(Rate.getText());
//                                etQuantity.setText(Quantity.getText());
//                                etHSN.setText(HSNCode_out.getText());
//                                etDiscount.setText(DiscountId.getText());
//                                // richa to do
//                                spnrG_S.setSelection(supplytypeAdapter.getPosition(SupplyType.getText().toString()));
//                                spnrMOU.setSelection(MOUAdapter.getPosition(MOU.getText().toString()));
//                                spnrtaxationtype.setSelection(taxationtypeAdapter.getPosition(taxationtype.getText().toString()));
//
//                                // gst end
//                                Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(DeptCode.getText().toString()));
//                                if(crsrDept.moveToFirst()) {
//                                    String deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
//                                    int deptid = getIndexDept(deptName + "");
//                                    spnrDepartment.setSelection(deptid);
//                                } else {
//                                    String deptName = "Select";
//                                    int deptid = getIndexDept(deptName + "");
//                                    spnrDepartment.setSelection(deptid);
//                                }
//                                //spnrDepartment.setSelection(Integer.parseInt(DeptCode.getText().toString()));
//                                Cursor crsrCateg = dbItems.getCategory(Integer.valueOf(CategCode.getText().toString()));
//                                if(crsrCateg.moveToFirst()) {
//                                    String categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
//                                    int categid = getIndexCateg(categName + "");
//                                    spnrCategory.setSelection(categid);
//                                } else {
//                                    String categName = "Select";
//                                    int categid = getIndexCateg(categName + "");
//                                    spnrCategory.setSelection(categid);
//                                }
//                                //spnrCategory.setSelection(Integer.parseInt(CategCode.getText().toString()));
//                                spnrKitchen.setSelection(Integer.parseInt(KitchenCode.getText().toString()) - 1);
//                                spnrSalesTax.setSelection(Integer.parseInt(SalesTaxId.getText().toString()) - 1);
//                                spnrAdditionalTax.setSelection(Integer.parseInt(AdditionalTaxId.getText().toString()) - 1);
//                                spnrOptionalTax1.setSelection(Integer.parseInt(OptionalTaxId1.getText().toString()) - 1);
//                                spnrOptionalTax2.setSelection(Integer.parseInt(OptionalTaxId2.getText().toString()) - 1);
//                                spnrDiscount.setSelection(Integer.parseInt(DiscountId.getText().toString()) - 1);
//
//                                if (PriceChange.getText().toString().equalsIgnoreCase("Yes")) {
//                                    chkPriceChange.setChecked(true);
//                                } else {
//                                    chkPriceChange.setChecked(false);
//                                }
//                                if (DiscountEnable.getText().toString().equalsIgnoreCase("Yes")) {
//                                    chkDiscountEnable.setChecked(true);
//                                } else {
//                                    chkDiscountEnable.setChecked(false);
//                                }
//                                if (BillWithStock.getText().toString().equalsIgnoreCase("Yes")) {
//                                    chkBillWithStock.setChecked(true);
//                                } else {
//                                    chkBillWithStock.setChecked(false);
//                                }
//
//                                if (TaxType.getText().toString().equalsIgnoreCase("Forward Tax")) {
//                                    rbForwardTax.setChecked(true);
//                                } else if (TaxType.getText().toString().equalsIgnoreCase("Reverse Tax")) {
//                                    rbReverseTax.setChecked(true);
//                                }
//
//                                imgItemImage.setImageURI(null);
//                                if (!strImageUri.equalsIgnoreCase("")) { // &&
//                                    // strImageUri.contains("\\")){
//                                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
//                                } else {
//                                    imgItemImage.setImageResource(R.drawable.img_noimage);
//                                }
//                                edtItemSalesTax.setText(SalesTaxPercent.getText().toString());
//                                edtItemServiceTax.setText(ServiceTaxPercent.getText().toString());
//                                btnLinkItem.setEnabled(false);
//                                btnDelinkItem.setEnabled(true);
//                            }
//                        }
//                    });
//
//                    rowItems.setTag("TAG");
//
//
//                    tblItems.addView(rowItems, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//                    i++;*/
//                }
//            } while (crsrItems.moveToNext());
//        } else {
//            Log.d("DisplayItem", "No Item found");
//        }
//
//    }

    private int getIndex_uom(String substring){

        int index = 0;
        for (int i = 0; index==0 && i < spnrMOU.getCount(); i++){

            if (spnrMOU.getItemAtPosition(i).toString().contains(substring)){
                index = i;
            }

        }

        return index;

    }

    private int getIndex_taxationType(String substring){

        int index = 0;
        for (int i = 0; i < spnrtaxationtype.getCount(); i++){

            if (spnrtaxationtype.getItemAtPosition(i).toString().contains(substring)){
                index = i;
                break;
            }

        }

        return index;

    }

    public int getIndexDept(String item)
    {
        for (int i = 0; i < labelsDept.size(); i++)
        {
            String auction = labelsDept.get(i);
            if (item.equals(auction))
            {
                return i;
            }
        }
        return -1;
    }

    public int getIndexCateg(String item)
    {
        for (int i = 0; i < labelsCateg.size(); i++)
        {
            String auction = labelsCateg.get(i);
            if (item.equals(auction))
            {
                return i;
            }
        }
        return -1;
    }

    public int getIndexCategByDept(String item , List<String> listCategory)
    {
        for (int i = 0; i < listCategory.size(); i++)
        {
            String auction = listCategory.get(i);
            if (item.equals(auction))
            {
                return i;
            }
        }
        return -1;
    }




    private boolean IsItemExists(String ItemFullName, int MenuCode, String barcode, int type) {
        boolean isItemExists = false;

        if(type==1){
            for (ItemOutward item : dataList) {
                if ( item.getMenuCode() == (MenuCode) && !item.getItemName().equalsIgnoreCase(ItemFullName.toUpperCase())){
                    MsgBox = new MessageDialog(myContext);
                    MsgBox.Show("Inconsistent"," Menu Code "+MenuCode+" already present for item "+item.getItemName() );
                    isItemExists = true;
                    break;
                }
                else if (item.getItemName().equalsIgnoreCase(ItemFullName.toUpperCase()) && item.getMenuCode() != (MenuCode)){
                    MsgBox = new MessageDialog(myContext);
                    MsgBox.Show("Inconsistent"," Item "+ItemFullName +" already present with Menu Code "+item.getMenuCode());
                    isItemExists = true;
                    break;
                }else if (item.getItemName().equalsIgnoreCase(ItemFullName.toUpperCase()) && item.getMenuCode() == (MenuCode)){
                    MsgBox = new MessageDialog(myContext);
                    MsgBox.Show("Inconsistent"," Item "+ItemFullName +" already present with Menu Code "+item.getMenuCode()+
                            "\n Therefore you cannot add it again");
                    isItemExists = true;
                    break;
                }else if (!barcode.equals("") && item.getBarCode().equalsIgnoreCase(barcode)&& item.getItemName() != (ItemFullName))
                {
                    //System.out.println("Richa : Duplicate Barcode ");
                    MsgBox = new MessageDialog(myContext);
                    MsgBox.Show("Inconsistent"," Item "+item.getItemName() +" already present with BarCode "+barcode);
                    isItemExists = true;
                    break;
                }
                else
                {
                    // System.out.println("Richa : No duplicacy ");
                }
            }
        }else if(type ==2)
        {
            List<String> itemlist = dbItems.getAllItemsNames();
            List<String> menuCodelist = dbItems.getAllMenuCode();
            for (ItemOutward item : dataList) {
                if(itemName_beforeChange_in_update.equalsIgnoreCase(ItemFullName))
                {
                    if(item.getMenuCode()==MenuCode && !item.getItemName().equalsIgnoreCase(ItemFullName)){
                        MsgBox = new MessageDialog(myContext);
                        MsgBox.Show("Inconsistent"," Menu Code "+MenuCode+" already present for item "+item.getItemName() );
                        isItemExists = true;
                        break;
                    }
                }
                else
                {
                    // itemname is either the new name or existing name
                    if(itemlist.contains(ItemFullName))
                    {
                        MsgBox = new MessageDialog(myContext);
                        MsgBox.Show("Inconsistent"," Item "+ItemFullName +" already present with Menu Code "+item.getMenuCode());
                        isItemExists = true;
                        break;
                    }/*else if ( menuCodelist.contains(String.valueOf(MenuCode))){
                        MsgBox = new MessageDialog(myContext);
                        MsgBox.Show("Inconsistent"," Menu Code "+MenuCode+" already present for item "+item.getItemName() );
                        isItemExists = true;
                        break;
                    }*/
                    /*else if ( item.getMenuCode() == (MenuCode) && !item.getItemName().equalsIgnoreCase(ItemFullName.toUpperCase())){
                        MsgBox = new MessageDialog(myContext);
                        MsgBox.Show("Inconsistent"," Menu Code "+MenuCode+" already present for item "+item.getItemName() );
                        isItemExists = true;
                        break;
                    }*/

                }
                if(itembarCode_beforeChange_in_update.equalsIgnoreCase(barcode))
                {
                    if((item.getMenuCode()==MenuCode && !item.getBarCode().equalsIgnoreCase(barcode))|| (item.getItemName()== ItemFullName && !item.getBarCode().equalsIgnoreCase(barcode))){
                        MsgBox = new MessageDialog(myContext);
                        MsgBox.Show("Inconsistent","This Barcode has been already used " );
                        isItemExists = true;
                        break;
                    }
                }
                else
                {
                    // itemname is either the new name or existing name
                    itemlist = dbItems.getAllItemsBarCode();
                    if(itemlist.contains(barcode))
                    {
                        MsgBox = new MessageDialog(myContext);
                        MsgBox.Show("Inconsistent",barcode+"  already present for Item "+item.getItemName());
                        isItemExists = true;
                        break;
                    }
                }
            }}
        return isItemExists;
    }

    private void InsertItem(String LongName, String ShortName, float DineInPrice1, float DineInPrice2,
                            float DineInPrice3, float TakeAwayPrice, float PickUpPrice, float DeliveryPrice, float Stock,
                            int PriceChange, int DiscountEnable, int BillWithStock, int TaxType, int DeptCode, int CategCode,
                            int KitchenCode, int SalesTaxId, int AdditionalTaxId, int OptionalTaxId1, int OptionalTaxId2, int DiscId,
                            String ItemBarcode, String ImageUri , Float frate, Float fquantity,String hsnCode, float IGSTRate,
                            float CGSTRate, float SGSTRate,
                            String g_s, String MOU_str, String taxationtype, float SalesTaxPercent, float ServiceTaxPercent, int MenuCode) {

        long lRowId = 0;
        float cgsttax=0,sgsttax=0;

        // gst
        /*if (taxationtype.equalsIgnoreCase("GST")) {
            if (fgsttax == 0) {
                cgsttax =sgsttax = 0;
            } else {
                cgsttax = sgsttax = fgsttax / 2;
            }
        }if (taxationtype.equalsIgnoreCase("AdditionalTax"))
        {
            if (fgsttax == 0) {
                cgsttax = 0;
            } else {
                cgsttax = fgsttax;
            }
        }*/

        /*Item objItem = new Item(ItemBarcode, LongName, ShortName, AdditionalTaxId, BillWithStock, CategCode, DeptCode,
                DiscId, DiscountEnable, KitchenCode, OptionalTaxId1, OptionalTaxId2, PriceChange, SalesTaxId, TaxType,
                DeliveryPrice, DineInPrice1, DineInPrice2, DineInPrice3, PickUpPrice, Stock, TakeAwayPrice, ImageUri,hsnCode,
                IGSTRate,0,CGSTRate, 0,SGSTRate, 0 ,MOU_str, taxationtype,frate,g_s, SalesTaxPercent, ServiceTaxPercent, MenuCode,0,0);

        lRowId = dbItems.addItem(objItem);*/

        Log.d("Item", "Row Id:" + String.valueOf(lRowId));
    }

    private long ReadData(int Type) {

        long lRowId =0;

        String strMenuCode = "", strLongName = "", strShortName = "", strBarcode = "";
        int iDeptCode = 0, iCategCode = 0, iKitchenCode = 0, iSalesTaxId = 0, iAdditionalTaxId = 0, iOptionalTaxId1 = 0,
                iOptionalTaxId2 = 0, iDiscountId = 0, iPriceChange = 0, iDiscountEnable = 0, iBillWithStock = 0,iTaxType = 0;
        double fDineIn1 = 0, fDineIn2 = 0, fDineIn3 = 0, fTakeAway = 0, fPickUp = 0, fDelivery = 0, fStock = 0, itemDiscount;

        strMenuCode = edtMenuCode.getText().toString();
        strLongName = txtLongName.getText().toString().toUpperCase();
        strLongName = AutoCompleteItemLongName.getText().toString().toUpperCase();
        strBarcode = AutoCompleteItemBarcode.getText().toString();

        fDineIn1 = Double.parseDouble(String.format("%.2f",Double.parseDouble(txtDineIn1.getText().toString())));
        fDineIn2 = Double.parseDouble(String.format("%.2f",Double.parseDouble(txtDineIn2.getText().toString())));
        fDineIn3 = Double.parseDouble(String.format("%.2f",Double.parseDouble(txtDineIn3.getText().toString())));

        fPickUp = 0.00f;
        fTakeAway = 0.00f;
        fDelivery = 0.00f;
        fStock = Double.parseDouble(String.format("%.2f",Double.parseDouble((txtStock.getText().toString()))));

        if(!spnrDepartment.getSelectedItem().toString().equalsIgnoreCase("Select"))
            iDeptCode = dbItems.getDepartmentIdByName(labelsDept.get(spnrDepartment.getSelectedItemPosition()));
        if(!spnrCategory.getSelectedItem().toString().equalsIgnoreCase("Select department first"))
            iCategCode = dbItems.getCategoryIdByName(String.valueOf(spnrCategory.getSelectedItem()));
        iKitchenCode = spnrKitchen.getSelectedItemPosition() ;


        float frate = 0.0f, fquantity = 0.0f,fgsttax =0.0f;
        String hsnCode = "";

        iDiscountId = 0;
        hsnCode = etHSN.getText().toString();
        /*if(hsnCode.equals(""))
            hsnCode= "0";*/
        String g_s = spnrG_S.getItemAtPosition(spnrG_S.getSelectedItemPosition()).toString();
        //String MOU_str = spnrMOU.getItemAtPosition(spnrMOU.getSelectedItemPosition()).toString();
        String MOU_str_temp = spnrMOU.getSelectedItem().toString();
        int length  = MOU_str_temp.length();
        String MOU_str = MOU_str_temp.substring(length-3, length-1);

        String taxationtype_str = spnrtaxationtype.getItemAtPosition(spnrtaxationtype.getSelectedItemPosition()).toString();

        float fSalesTax = 0;
        float fServiceTax = 0;
        float fCGSTTax = Float.parseFloat(String.format("%.2f",Float.parseFloat(edtItemCGSTTax.getText().toString())));
        float fSGSTTax = Float.parseFloat(String.format("%.2f",Float.parseFloat(edtItemSGSTTax.getText().toString())));
        float fIGSTTax = Float.parseFloat(String.format("%.2f",Float.parseFloat(edtIGSTTax.getText().toString())));
        float fcessTax = Float.parseFloat(String.format("%.2f",Float.parseFloat(edtcessTax.getText().toString())));
        itemDiscount = Double.parseDouble((String.format("%.2f",Double.parseDouble(etItemDiscount.getText().toString()))));



        //Cursor crsrSettings = dbItems.getBillSetting();
        if(crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("0")) {
            iMenuCode = dbItems.getItemMenuCode();
            iMenuCode++;
        } else {
            iMenuCode = Integer.valueOf(edtMenuCode.getText().toString());

        }

        if (fgsttax >= 0 && fquantity >= 0) {
            // Type 1 - addItem, Type 2 - updateItem
            if (Type == 1) {

                /*InsertItem(strLongName, strLongName, fDineIn1, fDineIn2, fDineIn3, fTakeAway, fPickUp, fDelivery,
                        fStock, iPriceChange, iDiscountEnable, iBillWithStock, iTaxType, iDeptCode, iCategCode,
                        iKitchenCode, iSalesTaxId, iAdditionalTaxId, iOptionalTaxId1, iOptionalTaxId2, iDiscountId,
                        strBarcode, strImageUri, frate, fquantity, hsnCode, fCGSTTax+fSGSTTax, fCGSTTax, fSGSTTax, g_s,
                        MOU_str, taxationtype_str,
                        fSalesTax, fServiceTax, iMenuCode);*/

                ItemOutward item_add = new ItemOutward(iMenuCode,strLongName, fDineIn1, fDineIn2,fDineIn3,fStock,
                        iDeptCode,iCategCode,iKitchenCode, strBarcode,strImageUri,iMenuCode, fCGSTTax,fSGSTTax,fIGSTTax,fcessTax,
                        MOU_str, hsnCode,taxationtype_str, g_s ,itemDiscount) ;
                 lRowId = dbItems.addItem(item_add);
                if (lRowId>0)
                {
                    Log.d("Item Management : ", strLongName+" added sucessfully at Row Id:" + String.valueOf(lRowId));

                    // inserting new item stock in OutwardStock Table
                    double rate =0;
                    if(Double.parseDouble(txtDineIn1.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn1.getText().toString());
                    else if(Double.parseDouble(txtDineIn2.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn2.getText().toString());
                    else if(Double.parseDouble(txtDineIn3.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn3.getText().toString());

                    double quantity =Double.parseDouble(txtStock.getText().toString());

                    StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
                    stock_outward.addItemToStock_Outward( businessDate, iMenuCode,
                            strLongName,quantity, rate );
                }
                else
                    Log.d("Item Management : ", strLongName+" cannot be added in Item Outward database");
            }
            else if (Type == 2)
            {
                lRowId = dbItems.updateItem(Integer.parseInt(edtMenuCode.getText().toString()), strLongName, strShortName, strBarcode,
                        iDeptCode, iCategCode, iKitchenCode, fDineIn1, fDineIn2, fDineIn3, fTakeAway, fPickUp, fDelivery,
                        iSalesTaxId, iAdditionalTaxId, iOptionalTaxId1, iOptionalTaxId2, iDiscountId, fStock, iPriceChange,
                        iDiscountEnable, iBillWithStock, strImageUri, iTaxType, frate, hsnCode, g_s, MOU_str,
                        taxationtype_str, fIGSTTax, fCGSTTax, fSGSTTax,fcessTax,
                        fSalesTax, fServiceTax, Integer.valueOf(strItemId), itemDiscount);
                if (lRowId > 0)
                {
                    Log.d("Item Management : ", "Updated Rows: " + String.valueOf(lRowId));
                    // updating outwardStock table
                    double rate =0;
                    if(Double.parseDouble(txtDineIn1.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn1.getText().toString());
                    else if(Double.parseDouble(txtDineIn2.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn2.getText().toString());
                    else if(Double.parseDouble(txtDineIn3.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn3.getText().toString());

                    double quantity =Double.parseDouble(txtStock.getText().toString());

                    StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
                    stock_outward.updateOpeningStock_Outward( businessDate, Integer.parseInt(strMenuCode),
                            strLongName,quantity, rate );
                    stock_outward.updateClosingStock_Outward( businessDate, Integer.parseInt(strMenuCode),
                            strLongName,quantity);
                }else
                {
                    Log.d("Item Management : ", " Item data cannot be updated" );
                }

            }
        }
        else {
            ResetItem();
            Toast.makeText(myContext, " Negative Values not allowed for GST Tax and Quantity", Toast.LENGTH_SHORT).show();
        }
        return lRowId;
    }


    private void ResetItem() {
        tx = "";
        mFlag = false;
        mUserCSVInvalidValue = "";
        strItemId = "";
        strImageUri = "";
        txtLongName.setText("");
        AutoCompleteItemLongName.setText("");
        //txtShortName.setText("");
        AutoCompleteItemBarcode.setText("");
        txtDineIn1.setText("0.00");
        txtDineIn2.setText("0.00");
        txtDineIn3.setText("0.00");
        txtStock.setText("0.00");
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        tvFileName.setText("Select FileName");
        strUploadFilepath="";
        edtItemCGSTTax.setText("0.00");
        edtItemSGSTTax.setText("0.00");
        edtIGSTTax.setText("0.00");
        edtcessTax.setText("0.00");
        edtMenuCode.setText("");
        txtStock.setEnabled(true);
        etRate.setText("0.00");
        etQuantity.setText("0.00");
        etItemDiscount.setText("0.00");
        etHSN.setText("");
        etGstTax.setText("0.00");
        itemName_beforeChange_in_update="";
        itembarCode_beforeChange_in_update="";
        spnrMOU.setSelection(0);
        spnrG_S.setSelection(0);
        spnrCategory.setSelection(0);
        spnrDepartment.setSelection(0);
        spnrKitchen.setSelection(0);
        spnrtaxationtype.setSelection(0);
        fRate =0;
        fQuantity =0;
        txtHSNCode = "";
        fGSTTax = 0;
        fDiscount = 0;
        imgItemImage.setImageResource(R.drawable.img_noimage);

    }

    public void Browse(View v) {
        Intent intent = new Intent(myContext, FilePickerActivity.class);
        intent.putExtra("contentType","image");
        startActivityForResult(intent, FilePickerActivity.PICK_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FilePickerActivity.FILE_PICKER_CODE)
            {
                strImageUri = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                Log.d("FilePicker Result", "Path + FileName:" + strImageUri);
                // Toast.makeText(myContext, "Image Path:" + strImageUri,
                // Toast.LENGTH_LONG).show();
                if(!strImageUri.equalsIgnoreCase(""))
                {
                    imgItemImage.setImageURI(null);
                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                }
                else
                {
                    imgItemImage.setImageResource(R.drawable.img_noimage);
                }
                strUploadFilepath = data.getStringExtra(UploadFilePickerActivity.EXTRA_FILE_PATH);
                tvFileName.setText(strUploadFilepath.substring(strUploadFilepath.lastIndexOf("/")+1));
            }
            else if (requestCode == FilePickerActivity.PICK_IMAGE_CODE)
            {
                strImageUri = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                Log.d("FilePicker Result", "Path + FileName:" + strImageUri);
                // Toast.makeText(myContext, "Image Path:" + strImageUri,
                // Toast.LENGTH_LONG).show();
                if(!strImageUri.equalsIgnoreCase("")) {
                    imgItemImage.setImageURI(null);
                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                }else{
                    imgItemImage.setImageResource(R.drawable.img_noimage);
                }
                strUploadFilepath = data.getStringExtra(UploadFilePickerActivity.EXTRA_FILE_PATH);
                tvFileName.setText(strUploadFilepath.substring(strUploadFilepath.lastIndexOf("/")+1));
            }
        }
    }

    public void AddItem(View v) {

        if (txtLongName.getText().toString().equalsIgnoreCase("")) {
        /*MsgBox.Show("Warning", "Please Enter Item Name");
        return;*/
        }
        if (AutoCompleteItemLongName.getText().toString().equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please Enter Item Name");
            return;
        }

        if (spnrMOU.getSelectedItem().toString().equalsIgnoreCase("") ||spnrMOU.getSelectedItem().toString().equalsIgnoreCase("Select") ) {
            MsgBox.Show("Warning", "Please Select Item UOM");
            return;
        }


        if(crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("1")) {
            if (edtMenuCode.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Enter Item Code");
                return;
            }
            else {
                iMenuCode = Integer.valueOf(edtMenuCode.getText().toString());
                String ItemFullName = txtLongName.getText().toString().toUpperCase();
                ItemFullName = AutoCompleteItemLongName.getText().toString().toUpperCase();
                if(IsItemExists( ItemFullName,  iMenuCode,AutoCompleteItemBarcode.getText().toString().trim(),1))
                {
                    return;
                }
            }
        }else
        {
            String ItemFullName = txtLongName.getText().toString().toUpperCase();
            ItemFullName = AutoCompleteItemLongName.getText().toString().toUpperCase();
            List<String> itemlist = dbItems.getAllItemsNames();
            if(itemlist.contains(ItemFullName))
            {
                MsgBox = new MessageDialog(myContext);
                MsgBox.Show("Inconsistent"," Item "+ItemFullName +" already present ");
                return;
            }
            String ItemBarCode = AutoCompleteItemBarcode.getText().toString();
            List<String> itemlist_barcode = dbItems.getAllItemsBarCode();
            if(itemlist_barcode.contains(ItemBarCode))
            {
                MsgBox = new MessageDialog(myContext);
                MsgBox.Show("Inconsistent"," This barcode is already present for another item  ");
                return;
            }
        }
        /*else
        }

        /*if (spnrDepartment.getSelectedItem().toString().equals("Select")) {
            MsgBox.Show("Warning", "Please Select Department");
            return;
        }*/

        if (txtDineIn1.getText().toString().equalsIgnoreCase("")) {
            txtDineIn1.setText("0.00");
        }

        if (txtDineIn2.getText().toString().equalsIgnoreCase("")) {
            txtDineIn2.setText("0.00");
        }

        if (txtDineIn3.getText().toString().equalsIgnoreCase("")) {
            txtDineIn3.setText("0.00");
        }

        if (txtStock.getText().toString().equalsIgnoreCase("")) {
            txtStock.setText("0.00");
        }else
        {
            double stock = Double.parseDouble(txtStock.getText().toString());
            if(stock>9999.99){
                MsgBox.Show("Warning","Please enter quantity between 0 and 9999.99");
                return;
            }
        }

        if (etItemDiscount.getText().toString().equalsIgnoreCase("")) {
            etItemDiscount.setText("0.00");
        }else
        {
            double discount = Double.parseDouble(etItemDiscount.getText().toString());
            if(discount>99.99){
                MsgBox.Show("Warning","Please enter discount between 0 and 99.99");
                return;
            }
        }
        /*if (etRate.getText().toString().equals(""))
        {
            etRate.setText("0");
        }*/
        /*if (etQuantity.getText().toString().equals(""))
        {
            etQuantity.setText("0");
        }*/
        /*if (etHSN.getText().toString().equals(""))
        {
            etHSN.setText("0");
        }
        if (etGstTax.getText().toString().equals(""))
        {
            etGstTax.setText("0");
        }*/


        String salesTax_str = edtItemCGSTTax.getText().toString();
        if (salesTax_str.equalsIgnoreCase("")) {
            edtItemCGSTTax.setText("0");
        }else if (Double.parseDouble(salesTax_str)< 0 || Double.parseDouble(salesTax_str)>99.99)
        {
            MsgBox.Show("Warning","Please enter CGST Rate between 0 and 99.99");
            return;
        }

        String serviceTax_str = edtItemSGSTTax.getText().toString();
        if (serviceTax_str.equalsIgnoreCase("")) {
            edtItemSGSTTax.setText("0");
        }else if (Double.parseDouble(serviceTax_str) <0 || Double.parseDouble(serviceTax_str)> 99.99)
        {
            MsgBox.Show("Warning","Please enter SGST Rate between 0 and 99.99");
            return;
        }

        String IGST_str = edtIGSTTax.getText().toString();
        if (IGST_str.equalsIgnoreCase("")) {
            edtIGSTTax.setText("0");
        }else if (Double.parseDouble(IGST_str) <0 || Double.parseDouble(IGST_str)> 99.99)
        {
            MsgBox.Show("Warning","Please enter IGST Rate between 0 and 99.99");
            return;
        }

        String cess_str = edtcessTax.getText().toString();
        if (cess_str.equalsIgnoreCase("")) {
            edtIGSTTax.setText("0");
        }else if (Double.parseDouble(cess_str) <0 || Double.parseDouble(cess_str)> 99.99)
        {
            MsgBox.Show("Warning","Please enter cess Rate between 0 and 99.99");
            return;
        }


        new AsyncTask<Void,Void,Void>(){
            ProgressDialog pd;
            long lRowId=0;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(ItemManagementActivity.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                lRowId=0;
                try {
                     lRowId = ReadData(1);
                    lRowId ++;// 2 - updateItem
                    //ResetItem();
                    //ClearItemTable();
                    //DisplayItems();
                } catch (Exception exp) {
                    //Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
                    exp.printStackTrace();
                }
                return null;
            }



            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try{
                    if (lRowId > 0) {
                        Toast.makeText(myContext, AutoCompleteItemLongName.getText().toString().toUpperCase()+" added successfully", Toast.LENGTH_SHORT).show();
                    }
                    ResetItem();
                    loadAutoCompleteData_ItemNames();
                    //ClearItemTable();
                    DisplayItemList();

                    //Toast.makeText(myContext, "Item Added Successfully", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }catch (Exception e){
                    Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    public void EditItem(View v) {
        try {
            /*Date d = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
            int invoiceno = dbItems.getLastBillNoforDate(String.valueOf(d.getTime()));
            if(invoiceno > 0)
            {
                // since already billing done for this businessdate, hence stock cannot be updated from here.
                // to update stock , goto Price & Stock module
                MsgBox.Show("Restriction", "You cannot update stock after making bill for the day. \n\nTo update stock , " +
                        "please goto Price & Stock module \n\n Or make Day End  to update from here ");
                return;
            }*/


            if (txtLongName.getText().toString().equalsIgnoreCase("")) {
        /*MsgBox.Show("Warning", "Please Enter Item Name");
        return;*/
            }
            if (AutoCompleteItemLongName.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Enter Item Name");
                return;
            }

            if (spnrMOU.getSelectedItem().toString().equalsIgnoreCase("") || spnrMOU.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                MsgBox.Show("Warning", "Please Select Item UOM");
                return;
            }


            if (crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("1")) {
                if (edtMenuCode.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Please Enter Item Code");
                    return;
                } else {
                    iMenuCode = Integer.valueOf(edtMenuCode.getText().toString());
                    iMenuCode = Integer.valueOf(edtMenuCode.getText().toString());
                    String ItemFullName = txtLongName.getText().toString().toUpperCase();
                    ItemFullName = AutoCompleteItemLongName.getText().toString().toUpperCase();
                    if(IsItemExists( ItemFullName,  iMenuCode,AutoCompleteItemBarcode.getText().toString().trim(),2))
                    {
                        return;
                    }
                }
            }else
            {
                String ItemFullName = txtLongName.getText().toString().toUpperCase();
                ItemFullName = AutoCompleteItemLongName.getText().toString().toUpperCase();
                List<String> itemlist = dbItems.getAllItemsNames();
                if(itemlist.contains(ItemFullName) && !itemName_beforeChange_in_update.equalsIgnoreCase(ItemFullName))
                {
                    MsgBox = new MessageDialog(myContext);
                    MsgBox.Show("Inconsistent"," Item "+ItemFullName +" already present ");
                    return;
                }
                String ItemBarCode = AutoCompleteItemBarcode.getText().toString();
                itemlist = dbItems.getAllItemsBarCode();
                if(itemlist.contains(ItemBarCode) && !itembarCode_beforeChange_in_update.equalsIgnoreCase(ItemBarCode))
                {
                    MsgBox = new MessageDialog(myContext);
                    MsgBox.Show("Inconsistent"," This barcode is already present ");
                    return;
                }
            }

            if (txtDineIn1.getText().toString().equalsIgnoreCase("")) {
                txtDineIn1.setText("0.00");
            }

            if (txtDineIn2.getText().toString().equalsIgnoreCase("")) {
                txtDineIn2.setText("0.00");
            }

            if (txtDineIn3.getText().toString().equalsIgnoreCase("")) {
                txtDineIn3.setText("0.00");
            }

            if (txtStock.getText().toString().equalsIgnoreCase("")) {
                txtStock.setText("0.00");
            }else
            {
                double stock = Double.parseDouble(txtStock.getText().toString());
                if(stock>9999.99){
                    MsgBox.Show("Warning","Please enter quantity between 0 and 9999.99");
                    return;
                }
            }
            if (etItemDiscount.getText().toString().equalsIgnoreCase("")) {
                etItemDiscount.setText("0.00");
            }else
            {
                double discount = Double.parseDouble(etItemDiscount.getText().toString());
                if(discount>99.99){
                    MsgBox.Show("Warning","Please enter discount between 0 and 99.99");
                    return;
                }
            }


            String CGSTTax_str = edtItemCGSTTax.getText().toString();
            if (CGSTTax_str.equalsIgnoreCase("")) {
                edtItemCGSTTax.setText("0");
            }else if (Double.parseDouble(CGSTTax_str)< 0 || Double.parseDouble(CGSTTax_str)>99.99)
            {
                MsgBox.Show("Warning","Please enter CST Rate between 0 and 99.99");
                return;
            }

            String SGSTTax_str = edtItemSGSTTax.getText().toString();
            if (SGSTTax_str.equalsIgnoreCase("")) {
                edtItemSGSTTax.setText("0");
            }else if (Double.parseDouble(SGSTTax_str) <0 || Double.parseDouble(SGSTTax_str)> 99.99)
            {
                MsgBox.Show("Warning","Please enter SGST Rate between 0 and 99.99");
                return;
            }
            String IGSTTax_str = edtIGSTTax.getText().toString();
            if (IGSTTax_str.equalsIgnoreCase("")) {
                edtIGSTTax.setText("0.00");
            }else if (Double.parseDouble(IGSTTax_str) <0 || Double.parseDouble(IGSTTax_str)> 99.99)
            {
                MsgBox.Show("Warning","Please enter IGST Rate between 0 and 99.99");
                return;
            }

            String cess_str = edtcessTax.getText().toString();
            if (cess_str.equalsIgnoreCase("")) {
                edtIGSTTax.setText("0");
            }else if (Double.parseDouble(cess_str) <0 || Double.parseDouble(cess_str)> 99.99)
            {
                MsgBox.Show("Warning","Please enter cess Rate between 0 and 99.99");
                return;
            }


            new AsyncTask<Void, Void, Void>() {
                ProgressDialog pd;
                long lRowId =0;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pd = new ProgressDialog(ItemManagementActivity.this);
                    pd.setMessage("Loading...");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    lRowId =0;
                    try {
                        lRowId = ReadData(2); // 2 - updateItem
                        //ResetItem();
//                    ClearItemTable();
//                    DisplayItems();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                        //Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return null;
                }


                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try {
                        if(lRowId>0)
                        {
                            Toast.makeText(myContext, AutoCompleteItemLongName.getText().toString().toUpperCase()+" updated Successfully", Toast.LENGTH_LONG).show();
                        }
                        ResetItem();
                        loadAutoCompleteData_ItemNames();
                        //ClearItemTable();
                        DisplayItemList();
                        pd.dismiss();

                    } catch (Exception e) {
                        Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }.execute();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ClearItem(View v) {
        ResetItem();
        //loadAutoCompleteData();
        //loadSpinnerData1();
    }

    public void CloseItem(View v) {
        dbItems.CloseDatabase();
        this.finish();
    }

    private void loadAutoCompleteData_ItemNames() {
        List ItemName_list = dbItems.getAllItemsName();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, ItemName_list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        AutoCompleteItemLongName.setAdapter(dataAdapter);

        List ItemBarcode_list = dbItems.getAllItemsBarCode();
        ArrayAdapter<String> dataAdapter_barCode = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, ItemBarcode_list);
        dataAdapter_barCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AutoCompleteItemBarcode.setAdapter(dataAdapter_barCode);

    }
    private void loadSpinnerData() {
        labelsDept = dbItems.getAllDeptforCateg();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labelsDept);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrDepartment.setAdapter(dataAdapter);
    }

    private void loadSpinnerData1() {
        if (spnrDepartment.getSelectedItem().toString().equals("Select"))
        {
            labelsCateg = new ArrayList<String>();
            labelsCateg.add("Select department first");
        }
        else
        {
            labelsCateg = dbItems.getAllCategforDept();
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labelsCateg);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrCategory.setAdapter(dataAdapter);
    }
    private void loadSpinnerData_cat(ArrayList<String>categName) {
        if (categName.size() ==0)
        {
            return;
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categName);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrCategory.setAdapter(dataAdapter);
    }

    private void loadSpinnerData1_old() {
        labelsCateg = dbItems.getAllCategforDept();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labelsCateg);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrCategory.setAdapter(dataAdapter);
    }


    private void DisplayItems_old() {
        Cursor crsrItems = null;

        crsrItems = dbItems.getAllItems();
        //crsrItems = dbItems.getAllItemswithDeptCategName();
        TableRow rowItems = null;

        TextView tvSno, tvMenuCode, tvLongName, tvShortName, tvDineIn1, tvDineIn2, tvDineIn3, tvTakeAway, tvPickUp, tvDelivery,
                tvStock, tvPriceChange, tvDiscountEnable, tvBillWithStock, tvTaxType, tvDeptCode, tvCategCode,
                tvKitchenCode, tvSalesTaxId, tvAdditionalTaxId, tvOptionalTaxId1, tvOptionalTaxId2, tvDiscountId,
                tvItemBarcode, tvImageUri, tvSpace, tvDeptName, tvCategName, tvItemId;
        TextView tvHSNCode_out,tvMOU,tvRate,tvGSTRate,tvSupplyType;
        ImageView imgIcon;
        ImageButton btnItemDelete;
        int i = 1;
        if (crsrItems.moveToFirst()) {
            do {
                rowItems = new TableRow(myContext);
                rowItems.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                rowItems.setBackgroundResource(R.drawable.row_background);

                tvSno = new TextView(myContext);
                tvSno.setTextSize(18);
                tvSno.setGravity(1);
                tvSno.setWidth(80);
                tvSno.setText(String.valueOf(i));
                rowItems.addView(tvSno);

                tvMenuCode = new TextView(myContext);
                tvMenuCode.setTextSize(0);
                tvMenuCode.setText(crsrItems.getString(crsrItems.getColumnIndex("MenuCode")));
                rowItems.addView(tvMenuCode);

                tvLongName = new TextView(myContext);
                tvLongName.setTextSize(18);
                tvLongName.setWidth(300);
                tvLongName.setText(crsrItems.getString(crsrItems.getColumnIndex("LongName")).toUpperCase());
                rowItems.addView(tvLongName);

                tvShortName = new TextView(myContext);
                tvShortName.setTextSize(18);
                //tvShortName.setText(crsrItems.getString(crsrItems.getColumnIndex("ShortName")));
                rowItems.addView(tvShortName);

                tvDineIn1 = new TextView(myContext);
                tvDineIn1.setGravity(1);
                tvDineIn1.setWidth(80);
                tvDineIn1.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice1")));
                rowItems.addView(tvDineIn1);

                tvDineIn2 = new TextView(myContext);
                tvDineIn2.setGravity(1);
                tvDineIn2.setWidth(80);
                tvDineIn2.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice2")));
                rowItems.addView(tvDineIn2);

                tvDineIn3 = new TextView(myContext);
                tvDineIn3.setGravity(1);
                tvDineIn3.setWidth(80);
                tvDineIn3.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice3")));
                rowItems.addView(tvDineIn3);

                tvTakeAway = new TextView(myContext);
                tvTakeAway.setGravity(1);
                tvTakeAway.setText(crsrItems.getString(crsrItems.getColumnIndex("TakeAwayPrice")));
                rowItems.addView(tvTakeAway);

                tvPickUp = new TextView(myContext);
                tvPickUp.setGravity(1);
                tvPickUp.setText(crsrItems.getString(crsrItems.getColumnIndex("PickUpPrice")));
                rowItems.addView(tvPickUp);

                tvDelivery = new TextView(myContext);
                tvDelivery.setGravity(1);
                tvDelivery.setText(crsrItems.getString(crsrItems.getColumnIndex("DeliveryPrice")));
                rowItems.addView(tvDelivery);

                tvStock = new TextView(myContext);
                tvStock.setGravity(1);
                tvStock.setWidth(110);
                tvStock.setText(crsrItems.getString(crsrItems.getColumnIndex("Quantity")));
                rowItems.addView(tvStock);

                tvPriceChange = new TextView(myContext);
                tvPriceChange.setText(crsrItems.getString(crsrItems.getColumnIndex("PriceChange")).equalsIgnoreCase("1")
                        ? "Yes" : "No");
                rowItems.addView(tvPriceChange);

                tvDiscountEnable = new TextView(myContext);
                tvDiscountEnable
                        .setText(crsrItems.getString(crsrItems.getColumnIndex("DiscountEnable")).equalsIgnoreCase("1")
                                ? "Yes" : "No");
                rowItems.addView(tvDiscountEnable);

                tvBillWithStock = new TextView(myContext);
                tvBillWithStock
                        .setText(crsrItems.getString(crsrItems.getColumnIndex("BillWithStock")).equalsIgnoreCase("1")
                                ? "Yes" : "No");
                rowItems.addView(tvBillWithStock);

                tvTaxType = new TextView(myContext);
                tvTaxType.setText(crsrItems.getString(crsrItems.getColumnIndex("TaxType")).equalsIgnoreCase("1")
                        ? "Forward Tax" : "Reverse Tax");
                rowItems.addView(tvTaxType);

                tvDeptCode = new TextView(myContext);
                tvDeptCode.setText(crsrItems.getString(crsrItems.getColumnIndex("DeptCode")));
                rowItems.addView(tvDeptCode);

                tvCategCode = new TextView(myContext);
                tvCategCode.setText(crsrItems.getString(crsrItems.getColumnIndex("CategCode")));
                rowItems.addView(tvCategCode);

                tvKitchenCode = new TextView(myContext);
                tvKitchenCode.setText(crsrItems.getString(crsrItems.getColumnIndex("KitchenCode")));
                rowItems.addView(tvKitchenCode);

                tvSalesTaxId = new TextView(myContext);
                tvSalesTaxId.setText(crsrItems.getString(crsrItems.getColumnIndex("SalesTaxId")));
                rowItems.addView(tvSalesTaxId);

                tvAdditionalTaxId = new TextView(myContext);
                tvAdditionalTaxId.setText(crsrItems.getString(crsrItems.getColumnIndex("AdditionalTaxId")));
                rowItems.addView(tvAdditionalTaxId);

                tvOptionalTaxId1 = new TextView(myContext);
                tvOptionalTaxId1.setText(crsrItems.getString(crsrItems.getColumnIndex("OptionalTaxId1")));
                rowItems.addView(tvOptionalTaxId1);

                tvOptionalTaxId2 = new TextView(myContext);
                tvOptionalTaxId2.setText(crsrItems.getString(crsrItems.getColumnIndex("OptionalTaxId2")));
                rowItems.addView(tvOptionalTaxId2);

                tvDiscountId = new TextView(myContext);
                tvDiscountId.setText(crsrItems.getString(crsrItems.getColumnIndex("DiscId")));
                rowItems.addView(tvDiscountId);

                tvItemBarcode = new TextView(myContext);
                tvItemBarcode.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemBarcode")));
                rowItems.addView(tvItemBarcode);

                tvImageUri = new TextView(myContext);
                tvImageUri.setText(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")));
                rowItems.addView(tvImageUri);

                imgIcon = new ImageView(myContext);
                imgIcon.setLayoutParams(new TableRow.LayoutParams(50, 40));
                imgIcon.setImageURI(null);
                if (!crsrItems.getString(crsrItems.getColumnIndex("ImageUri")).equalsIgnoreCase("")) { // &&
                    // strImageUri.contains("\\")){
                    imgIcon.setImageURI(Uri.fromFile(new File(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")))));
                } else {
                    imgIcon.setImageResource(R.drawable.img_noimage);
                }
                rowItems.addView(imgIcon);

                tvSupplyType = new TextView(myContext);
                tvSupplyType.setGravity(1);
                tvSupplyType.setWidth(110);
                tvSupplyType.setText(crsrItems.getString(crsrItems.getColumnIndex("SupplyType")));
                rowItems.addView(tvSupplyType);

                tvGSTRate = new TextView(myContext);
                tvGSTRate.setGravity(1);
                tvGSTRate.setWidth(110);
                Float fGSTRate =0.0f;
                String IGSTRate_str = crsrItems.getString(crsrItems.getColumnIndex("IGSTRate"));
                if ((IGSTRate_str != null)&&Float.parseFloat(IGSTRate_str) == 0)
                {
                    String CGSTRate_str = crsrItems.getString(crsrItems.getColumnIndex("CGSTRate"));
                    if((CGSTRate_str!= null )&&(Float.parseFloat(IGSTRate_str)> 0)) {
                        Float fCgst = Float.parseFloat(CGSTRate_str);
                        fGSTRate = 2 * fCgst;
                    }
                    else
                    {
                        fGSTRate = Float.parseFloat(IGSTRate_str);
                    }
                }
                else
                {
                    fGSTRate = Float.parseFloat(IGSTRate_str);
                }
                tvGSTRate.setText(fGSTRate.toString());
                rowItems.addView(tvGSTRate);

                tvRate = new TextView(myContext);
                tvRate.setGravity(1);
                tvRate.setWidth(80);
                tvRate.setText(crsrItems.getString(crsrItems.getColumnIndex("Rate")));
                rowItems.addView(tvRate);

                tvMOU = new TextView(myContext);
                tvMOU.setGravity(1);
                tvMOU.setWidth(110);
                tvMOU.setText(crsrItems.getString(crsrItems.getColumnIndex("MOU")));
                rowItems.addView(tvMOU);

                tvHSNCode_out = new TextView(myContext);
                tvHSNCode_out.setGravity(1);
                tvHSNCode_out.setWidth(110);
                tvHSNCode_out.setText(crsrItems.getString(crsrItems.getColumnIndex("HSNCode")));
                rowItems.addView(tvHSNCode_out);

                // For Space purpose
                tvSpace = new TextView(myContext);
                tvSpace.setText("                ");
                rowItems.addView(tvSpace);

                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                btnItemDelete = new ImageButton(myContext);
                btnItemDelete.setImageResource(res);
                btnItemDelete.setLayoutParams(new TableRow.LayoutParams(60, 40));
                //btnItemDelete.setOnClickListener(mListener);
                rowItems.addView(btnItemDelete);

                // For Space purpose
                tvSpace = new TextView(myContext);
                tvSpace.setText("              ");
                rowItems.addView(tvSpace);

                rowItems.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        if (String.valueOf(v.getTag()) == "TAG") {
                            TableRow Row = (TableRow) v;

                            TextView MenuCode = (TextView) Row.getChildAt(1);
                            TextView LongName = (TextView) Row.getChildAt(2);
                            TextView ShortName = (TextView) Row.getChildAt(3);
                            TextView DineIn1 = (TextView) Row.getChildAt(4);
                            TextView DineIn2 = (TextView) Row.getChildAt(5);
                            TextView DineIn3 = (TextView) Row.getChildAt(6);
                            TextView TakeAway = (TextView) Row.getChildAt(7);
                            TextView PickUp = (TextView) Row.getChildAt(8);
                            TextView Delivery = (TextView) Row.getChildAt(9);
                            TextView Stock = (TextView) Row.getChildAt(10);
                            TextView PriceChange = (TextView) Row.getChildAt(11);
                            TextView DiscountEnable = (TextView) Row.getChildAt(12);
                            TextView BillWithStock = (TextView) Row.getChildAt(13);
                            TextView TaxType = (TextView) Row.getChildAt(14);
                            TextView DeptCode = (TextView) Row.getChildAt(15);
                            TextView CategCode = (TextView) Row.getChildAt(16);
                            TextView KitchenCode = (TextView) Row.getChildAt(17);
                            TextView SalesTaxId = (TextView) Row.getChildAt(18);
                            TextView AdditionalTaxId = (TextView) Row.getChildAt(19);
                            TextView OptionalTaxId1 = (TextView) Row.getChildAt(20);
                            TextView OptionalTaxId2 = (TextView) Row.getChildAt(21);
                            TextView DiscountId = (TextView) Row.getChildAt(22);
                            TextView Barcode = (TextView) Row.getChildAt(23);
                            TextView ImageUri = (TextView) Row.getChildAt(24);
                            TextView SupplyType = (TextView) Row.getChildAt(25);
                            TextView GSTRate = (TextView) Row.getChildAt(26);
                            TextView Rate = (TextView) Row.getChildAt(27);
                            TextView MOU = (TextView) Row.getChildAt(28);
                            TextView HSNCode_out = (TextView) Row.getChildAt(29);

                            edtMenuCode.setText(MenuCode.getText().toString());
                            txtLongName.setText(LongName.getText());
                            AutoCompleteItemLongName.setText(LongName.getText());
                            // txtShortName.setText(ShortName.getText());
                            AutoCompleteItemBarcode.setText(Barcode.getText());
                            txtDineIn1.setText(DineIn1.getText());
                            txtDineIn2.setText(DineIn2.getText());
                            txtDineIn3.setText(DineIn3.getText());
                            txtStock.setText(Stock.getText());
                            strImageUri = ImageUri.getText().toString();

                            // gst
                            etGstTax.setText(GSTRate.getText());
                            etRate.setText(Rate.getText());
                            etHSN.setText(HSNCode_out.getText());
                            spnrG_S.setSelection(Integer.parseInt(SupplyType.getText().toString()));
                            //spnrMOU.

                            // gst end
                            Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(DeptCode.getText().toString()));
                            if(crsrDept.moveToFirst()) {
                                String deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
                                int deptid = getIndexDept(deptName + "");
                                spnrDepartment.setSelection(deptid);
                            } else {
                                String deptName = "Select";
                                int deptid = getIndexDept(deptName + "");
                                spnrDepartment.setSelection(deptid);
                            }
                            //spnrDepartment.setSelection(Integer.parseInt(DeptCode.getText().toString()));
                            Cursor crsrCateg = dbItems.getCategory(Integer.valueOf(CategCode.getText().toString()));
                            if(crsrCateg.moveToFirst()) {
                                String categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
                                int categid = getIndexCateg(categName + "");
                                spnrCategory.setSelection(categid);
                            } else {
                                String categName = "Select";
                                int categid = getIndexCateg(categName + "");
                                spnrCategory.setSelection(categid);
                            }
                            //spnrCategory.setSelection(Integer.parseInt(CategCode.getText().toString()));
                            spnrKitchen.setSelection(Integer.parseInt(KitchenCode.getText().toString()) - 1);
                            spnrOptionalTax1.setSelection(Integer.parseInt(OptionalTaxId1.getText().toString()) - 1);
                            spnrOptionalTax2.setSelection(Integer.parseInt(OptionalTaxId2.getText().toString()) - 1);


                            if (PriceChange.getText().toString().equalsIgnoreCase("Yes")) {
                                chkPriceChange.setChecked(true);
                            } else {
                                chkPriceChange.setChecked(false);
                            }
                            if (DiscountEnable.getText().toString().equalsIgnoreCase("Yes")) {
                                chkDiscountEnable.setChecked(true);
                            } else {
                                chkDiscountEnable.setChecked(false);
                            }
                            if (BillWithStock.getText().toString().equalsIgnoreCase("Yes")) {
                                chkBillWithStock.setChecked(true);
                            } else {
                                chkBillWithStock.setChecked(false);
                            }

                            if (TaxType.getText().toString().equalsIgnoreCase("Forward Tax")) {
                                rbForwardTax.setChecked(true);
                            } else if (TaxType.getText().toString().equalsIgnoreCase("Reverse Tax")) {
                                rbReverseTax.setChecked(true);
                            }

                            imgItemImage.setImageURI(null);
                            if (!strImageUri.equalsIgnoreCase("")) { // &&
                                // strImageUri.contains("\\")){
                                imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                            } else {
                                imgItemImage.setImageResource(R.drawable.img_noimage);
                            }
                            btnAdd.setEnabled(false);
                            btnEdit.setEnabled(true);
                        }
                    }
                });

                rowItems.setTag("TAG");

                tblItems.addView(rowItems, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                i++;
            } while (crsrItems.moveToNext());
        } else {
            Log.d("DisplayItem", "No Item found");
        }
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
                            /*Intent returnIntent =new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);*/
                            dbItems.CloseDatabase();
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_delete, menu);
        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            finish();
        }
        else if (id == R.id.action_home)
        {
            onHomePressed();
        }
        else if (id == R.id.action_screen_shot)
        {
            com.wep.common.app.ActionBarUtils.takeScreenshot(this,findViewById(android.R.id.content).getRootView());
        }
        else if (id == R.id.action_clear)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setIcon(R.drawable.ic_launcher)
                    .setMessage("Are you sure to delete all the existing Items?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(myContext, "clear", Toast.LENGTH_SHORT).show();
                            long lResult = dbItems.deleteAllOutwardItem();
                            if(lResult>0)
                            {
                                itemListAdapter.notifyDataSetChanged(dbItems.getAllItem());
                                dataList.clear();
                                loadAutoCompleteData_ItemNames();
                                ResetItem();
                                Toast.makeText(myContext, "Items Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        txtLongName.clearFocus();
        txtLongName.setCursorVisible(false);
        AutoCompleteItemLongName.clearFocus();
        AutoCompleteItemLongName.setCursorVisible(false);
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /*char pressedKey = (char) event.getUnicodeChar();
        String Barcode = "" + pressedKey;
        Toast.makeText(getApplicationContext(), "barcode--->>>" + Barcode, Toast.LENGTH_SHORT).show();

        Toast.makeText(myContext, "keyUp:"+keyCode+" : "+event.toString(), Toast.LENGTH_SHORT).show();*/
        long dd = event.getEventTime()-event.getDownTime();
        /*long time1= System.currentTimeMillis();
        long time= SystemClock.uptimeMillis();*/
        //long dd = time - event.getEventTime();
        /*Log.d("TAG",String.valueOf(dd));
        Log.d("TAG1",String.valueOf(event.getEventTime()-event.getDownTime()));
        Log.d("TAG",String.valueOf(event));*/
        if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT ||event.getKeyCode() == KeyEvent.KEYCODE_SHIFT_LEFT  )
        {
            return true ;
        }
        if (dd<15 && dd >0)
        {
            View v = getCurrentFocus();
            //System.out.println(v);
            AutoCompleteTextView etbar = (AutoCompleteTextView)findViewById(R.id.AutoCompleteItemBarcode);
            //EditText ed = (WepEditText)findViewById(v.getId());

            if (v.getId()!= R.id.AutoCompleteItemBarcode)
            {

                switch (v.getId())
                {
                    case R.id.etItemLongName :txtLongName.setText(tx);
                        AutoCompleteItemLongName.setText(tx);
                        break;
                    case R.id.etHSNCode:  etHSN.setText(tx);
                        break;

                    case R.id.etItemDineInPrice1:
                    case R.id.etItemDineInPrice2:
                    case R.id.etItemDineInPrice3:
                    case R.id.etItemStock:
                    case R.id.et_hsn:
                    case R.id.edtMenuCode:
                    case R.id.edtItemCGSTTax:
                    case R.id.edtItemSGSTTax:
                    case R.id.edtIGSTTax:
                    case R.id.etItemDiscount:
                        EditText ed = (EditText)findViewById(v.getId());
                        //String ed_str = ed.getText().toString();
                        ed.setText(tx);
                }
                String bar_str = etbar.getText().toString();
                bar_str += (char)event.getUnicodeChar();
                etbar.setText(bar_str);
                etbar.showDropDown();

            }


        }
        /*Toast.makeText(myContext, "keyUp:"+keyCode+" : "+dd, Toast.LENGTH_SHORT).show();*/

        return true;
    }


}
