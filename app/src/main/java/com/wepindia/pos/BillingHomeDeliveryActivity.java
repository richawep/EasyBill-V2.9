package com.wepindia.pos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mswipetech.wisepad.sdktest.view.ApplicationData;
import com.wep.common.app.Database.BillDetail;
import com.wep.common.app.Database.BillItem;
import com.wep.common.app.Database.Category;
import com.wep.common.app.Database.ComplimentaryBillDetail;
import com.wep.common.app.Database.Customer;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.DeletedKOT;
import com.wep.common.app.Database.Department;
import com.wep.common.app.Database.PendingKOT;
import com.wep.common.app.models.Items;
import com.wep.common.app.print.BillKotItem;
import com.wep.common.app.print.BillServiceTaxItem;
import com.wep.common.app.print.BillSubTaxItem;
import com.wep.common.app.print.BillTaxItem;
import com.wep.common.app.print.BillTaxSlab;
import com.wep.common.app.print.PrintKotBillItem;
import com.wep.common.app.utils.Preferences;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.GenericClasses.DateTime;
import com.wepindia.pos.GenericClasses.DecimalDigitsInputFilter;
import com.wepindia.pos.GenericClasses.EditTextInputHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.RecyclerDirectory.TestItemsAdapter;
import com.wepindia.pos.adapters.CategoryAdapter;
import com.wepindia.pos.adapters.DepartmentAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.AddedItemsToOrderTableClass;
import com.wepindia.pos.utils.GSTINValidation;
import com.wepindia.pos.utils.StockOutwardMaintain;
import com.wepindia.printers.WepPrinterBaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BillingHomeDeliveryActivity extends WepPrinterBaseActivity implements TextWatcher {

    String linefeed = "";
    String tx ="";
    DecimalFormat df_2, df_3;
    Pattern p = Pattern.compile("^(-?[0-9]+[\\.\\,][0-9]{1,2})?[0-9]*$");
    int CUSTOMER_FOUND =0;
    int PRINTOWNERDETAIL = 0, BOLDHEADER = 0, PRINTSERVICE = 0, BILLAMOUNTROUNDOFF = 0;
    int AMOUNTPRINTINNEXTLINE = 0;


    private TextView tvServiceTax_text;
    private int UTGSTENABLED = 0,HSNPRINTENABLED=0;

    private static final String TAG = BillingHomeDeliveryActivity.class.getSimpleName();
    Context myContext;
    DatabaseHandler dbBillScreen = new DatabaseHandler(BillingHomeDeliveryActivity.this);
    private DatabaseHandler db;
    MessageDialog MsgBox;
    EditText   tvTableNumber, tvTableSplitNo, tvBillNumber;
    ListView listViewDept, listViewCateg, lstvwKOTModifiers;
    private DepartmentAdapter departmentAdapter;
    private CategoryAdapter categoryAdapter;
    RelativeLayout boxDept, boxCat,boxItem;
    //private GridView gridViewItems;
    //private ItemsAdapter itemsAdapter;
    TableLayout tblOrderItems;
    TextView tvSalesTax;
    TextView tvServiceTax, tvDiscountAmount,  tvDiscountPercentage;
    WepButton btnSplitBill, btnSaveKOT, btnPayBill, btnDeleteKOT,btnDeleteBill, btnDeliveryStatus, btnPrintKOT, btnPrintBill,
            btnClear, btnReprint;
    TextView tvDate, tvSubTotal, tvIGSTValue,tvTaxTotal, tvServiceTaxTotal, tvBillAmount, tvSubUdfValue,txtOthercharges,tvcessValue;
    LinearLayout relative_Interstate, relative_pos;
    private LinearLayout idd_date;
    EditText edtCustId, edtCustName, edtCustPhoneNo, edtCustAddress, etCustGSTIN;
    Button btnAddCustomer;
    CheckBox chk_interstate = null;
    EditText et_pos = null;
    boolean CustomerDetailsFilled = false;
    Calendar Time; // Time variable
    Cursor crsrSettings = null, crsrCustomerDetails = null;

    String[] Name;
    int REPRINT_KOT =0;
    String[] ImageUri;
    String strUserId = "", strUserName = "", strDate = "";

    int[] MenuCode;
    byte jBillingMode = 0, jWeighScale = 0;
    int iTaxType = 0, iTotalItems = 0, iCustId = 0, iTokenNumber = 0;
    float fTotalDiscount = 0, fCashPayment = 0, fCardPayment = 0, fCouponPayment = 0, fPettCashPayment = 0, fPaidTotalPayment = 0;
    double dPettCashPayment = 0;
    float dDiscPercent = 0;
    float fWalletPayment = 0;
    double dChangePayment = 0;
    float fRoundOfValue =0;
    double dFinalBillValue=0;
    double dServiceTaxPercent = 0, dOtherChrgs = 0;
    String strPaymentStatus = "", strMakeOrder = "";
    boolean strOrderDelivered= false;
    Date d;
    int PrintBillPayment = 0;
    AutoCompleteTextView aTViewSearchItem, aTViewSearchMenuCode,autoCompleteTextViewSearchItemBarcode;
    Spinner spnr_pos;
    ArrayAdapter<CharSequence> POS_LIST;
    SimpleCursorAdapter deptdataAdapter, categdataAdapter;
    String GSTEnable = "", HSNEnable_out = "", POSEnable = "";
    TextView tvHSNCode_out;
    Date strDate_date;
    float fTotalsubTaxPercent = 0;
    Button btndepart, btncateg, btnitem;

    TextView tvdeptline, tvcategline;
    String HomeDeliveryCaption="", TakeAwayCaption="", DineInCaption = "", CounterSalesCaption = "";
    int iKOTNo = -1;
    int iPrintKOTStatus = 0;
    public boolean isPrinterAvailable = false;
    private Toolbar toolbar;
    private AppCompatDelegate delegate;
    String businessDate="";
    int BillwithStock = 0;
    String ownerPos= "";
    int reprintBillingMode =0;
    boolean isReprint = false;
    String FASTBILLINGMODE = "1"; // by default setting to items only mode
    int ItemwiseDiscountEnabled =0;

    private float mHeadingTextSize;
    private float mDataMiniDeviceTextsize;
    private float mSamsungTab3VDeviceTextsize;
    private float mSamsungT561DeviceTextsize;

    private int mItemNameWidth;
    private int mHSNWidth;
    private int mQuantityWidth;
    private int mRateWidth;
    private int mAmountWidth;

    private final static int mSamsungTab3VScreenResolutionWidth = 600;
    private final static int mSamsungT561ScreenResolutionWidth = 800;
    private final static int mDataMiniScreenResolutionWidth = 752;

    private TextView mItemNameTextView;
    private TextView mHSNTextView;
    private TextView mQuantityTextView;
    private TextView mRateTextView;
    private TextView mAmountTextView;
    private TextView mDeleteTextView;

    private RecyclerView mRecyclerGridView;
    private TestItemsAdapter mTestItemsAdapter;
    private GridLayoutManager mGridLayoutManager;

    public void onConfigurationRequired() {

    }

    public void onPrinterAvailable(int flag) {

        //Toast.makeText(BillingCounterSalesActivity.this, "Bill Printer Status : " + flag, Toast.LENGTH_SHORT).show();
        //isPrinterAvailable = flag;
        if(flag == 2)
        {
            btnPrintBill.setEnabled(false);
            btnReprint.setEnabled(false);
            btnPrintKOT.setEnabled(false);
            SetPrinterAvailable(false);
        }
        else if(flag == 5)
        {
            //btnPrintBill.setEnabled(true);
            btnReprint.setEnabled(true);
            btnPrintKOT.setEnabled(true);
            SetPrinterAvailable(true);
        }
        else if(flag == 0)
        {
            //btnPrintBill.setEnabled(true);
            btnReprint.setEnabled(true);
            btnPrintKOT.setEnabled(true);
            SetPrinterAvailable(false);
        }
    }
    public void SetPrinterAvailable(boolean flag) {
        String status="Offline";
        if(flag)
            status = "Available";
        Toast.makeText(this, "Bill Printer Status : " + status, Toast.LENGTH_SHORT).show();
        isPrinterAvailable = flag;
        //btn_PrintBill.setEnabled(true);
        //btn_Reprint.setEnabled(true);
    }


    boolean REVERSETAX = false;

    /************************************************************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_home_delivery);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewCenter = (TextView) toolbar.findViewById(com.wep.common.app.R.id.textViewCenter);
        tvSalesTax = (TextView) findViewById(R.id.tvTaxTotal);
        tvServiceTax = (TextView) findViewById(R.id.tvServiceTax);

        myContext = this;
        dbBillScreen.OpenDatabase();
        try {
            df_2 = new DecimalFormat("0.##");
            df_2.setRoundingMode(RoundingMode.FLOOR);

            df_3 = new DecimalFormat("0.###");
            df_3.setRoundingMode(RoundingMode.FLOOR);

            db = new DatabaseHandler(this);
            MsgBox = new MessageDialog(myContext);
            //MsgBox.setIcon(R.drawable.ic_launcher);
            String strBillMode = getIntent().getStringExtra("BILLING_MODE");
            jBillingMode = Byte.parseByte(strBillMode);
            strUserId = ApplicationData.getUserId(this);//ApplicationData.USER_ID;
            strUserName = ApplicationData.getUserName(this);//ApplicationData.USER_NAME;
            iCustId = getIntent().getIntExtra("CUST_ID", 0);
            strPaymentStatus = getIntent().getStringExtra("Payment_Status");
            strMakeOrder = getIntent().getStringExtra("MAKE_ORDER");
            d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            com.wep.common.app.ActionBarUtils.setupToolbar(BillingHomeDeliveryActivity.this, toolbar, getSupportActionBar(), "", strUserName, " Date:" + s.toString());
            IntializeViewVariables();
            getScreenResolutionWidthType(checkScreenResolutionWidthType(this));
            ClearAll();
            crsrSettings = db.getBillSettings();
            if (crsrSettings.moveToFirst()) {
                DineInCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeDineInCaption"));
                CounterSalesCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeCounterSalesCaption"));
                HomeDeliveryCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeHomeDeliveryCaption"));
                TakeAwayCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeTakeAwayCaption"));
                ownerPos = crsrSettings.getString(crsrSettings.getColumnIndex("POSNumber"));
                if (!(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1)) { // reverse tax
                    REVERSETAX = true;
                }else
                {
                    REVERSETAX = false;
                }

                iTaxType = crsrSettings.getInt(crsrSettings.getColumnIndex("TaxType"));
                FASTBILLINGMODE = crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode"));
                HSNPRINTENABLED = crsrSettings.getInt(crsrSettings.getColumnIndex("HSNPrintEnabled_out"));
                UTGSTENABLED = crsrSettings.getInt(crsrSettings.getColumnIndex("UTGSTEnabled"));
                if(UTGSTENABLED ==1)
                    tvServiceTax_text.setText("UTGST-Tax :");
                else
                    tvServiceTax_text.setText("SGST-Tax :");

                BillwithStock = crsrSettings.getInt(crsrSettings.getColumnIndex("BillwithStock"));
                businessDate = crsrSettings.getString(crsrSettings.getColumnIndex("BusinessDate"));
                ItemwiseDiscountEnabled = crsrSettings.getInt(crsrSettings.getColumnIndex("DiscountType"));
                // GSt
                HSNEnable_out = crsrSettings.getString(crsrSettings.getColumnIndex("HSNCode_Out"));
                if (HSNEnable_out == null || HSNEnable_out.equals("0")) {
                    HSNEnable_out = "0";
                    tvHSNCode_out.setVisibility(View.INVISIBLE);
                }
                else
                {tvHSNCode_out.setVisibility(View.VISIBLE);}

                POSEnable = crsrSettings.getString(crsrSettings.getColumnIndex("POS_Out"));
                if (POSEnable == null || POSEnable.equals("0")) {
                    POSEnable = "0";
                    relative_Interstate.setVisibility(View.INVISIBLE);
                }else {
                    relative_Interstate.setVisibility(View.VISIBLE);
                }

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintOwnerDetail")) == 1)) { // print owner detail
                    PRINTOWNERDETAIL = 1;
                }else
                {
                    PRINTOWNERDETAIL = 0;
                }

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("BoldHeader")) == 1)) { // bold header
                    BOLDHEADER = 1;
                }else
                {
                    BOLDHEADER = 0;
                }

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintService")) == 1)) { // Service Name print
                    PRINTSERVICE = 1;
                }else
                {
                    PRINTSERVICE = 0;
                }

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("BillAmountRoundOff")) == 1)) { // Bill Amount Round Off
                    BILLAMOUNTROUNDOFF = 1;
                }else
                {
                    BILLAMOUNTROUNDOFF = 0;
                }
            }
            GSTEnable = "1";
            Cursor crssOtherChrg = null;
            if (jBillingMode == 4) {
                crssOtherChrg = db.getKOTModifierByModes_new(HomeDeliveryCaption);
                textViewCenter.setText(HomeDeliveryCaption);
            } else if (jBillingMode == 3) {
                crssOtherChrg = db.getKOTModifierByModes_new(TakeAwayCaption);
                textViewCenter.setText(TakeAwayCaption);
                btnDeliveryStatus.setText("PickUp Status");
            }

            if (crssOtherChrg.moveToFirst()) {
                do {
                    dOtherChrgs += crssOtherChrg.getDouble(crssOtherChrg.getColumnIndex("ModifierAmount"));
                } while (crssOtherChrg.moveToNext());
                txtOthercharges.setText(String.format("%.2f", dOtherChrgs));
            }

            ControlsSetEnabled();
            loadAutoCompleteData();
            loadItems(0);
            // Get bill number
            //Log.d("Richa_init : ", String.valueOf(dbBillScreen.getNewBillNumber()));
            int iBillNumber = db.getNewBillNumber();
            tvBillNumber.setText(String.valueOf(iBillNumber));
            if(FASTBILLINGMODE !=null) {
                if (FASTBILLINGMODE.equalsIgnoreCase("1")) {
                    // setting visibility of buttons
                    boxDept.setVisibility(View.GONE);
                    boxCat.setVisibility(View.GONE);
                    // btnitem.setVisibility(View.VISIBLE);

                    // setting visibility of grids/listviews
                    //gridViewItems.setNumColumns(6);
                    mGridLayoutManager = new GridLayoutManager(BillingHomeDeliveryActivity.this, 6);
                    mRecyclerGridView.setLayoutManager(mGridLayoutManager);
                    loadItems(0);
                    /*tvdeptline.setVisibility(View.GONE);
                    tvcategline.setVisibility(View.GONE);*/
                    /*listViewDept.setVisibility(View.GONE);
                    listViewCateg.setVisibility(View.GONE);*/

                } else if (FASTBILLINGMODE.equalsIgnoreCase("2")) {
                    boxDept.setVisibility(View.VISIBLE);
                    boxCat.setVisibility(View.GONE);
                    //btnitem.setVisibility(View.VISIBLE);

                    //gridViewItems.setNumColumns(4);
                    mGridLayoutManager = new GridLayoutManager(BillingHomeDeliveryActivity.this, 4);
                    mRecyclerGridView.setLayoutManager(mGridLayoutManager);

                    loadItems(0);
                    //tvcategline.setVisibility(View.GONE);
                    //listViewCateg.setVisibility(View.GONE);

                } else if (FASTBILLINGMODE.equalsIgnoreCase("3")) {
                    boxDept.setVisibility(View.VISIBLE);
                    boxCat.setVisibility(View.VISIBLE);

                    mGridLayoutManager = new GridLayoutManager(BillingHomeDeliveryActivity.this, 2);
                    mRecyclerGridView.setLayoutManager(mGridLayoutManager);

                    //btnitem.setVisibility(View.VISIBLE);
                    loadItems(0);
                }
            }

            crsrCustomerDetails = db.getCustomerById(iCustId);
            if (crsrCustomerDetails == null || !crsrCustomerDetails.moveToFirst()) {
                //MsgBox.Show("Warning", "Customer details not found");
            }

            Cursor BillItems = db.getKOTItems_new(iCustId, String.valueOf(jBillingMode));
            if (BillItems.moveToFirst()) {

                LoadKOTItems(BillItems);
                if (!strMakeOrder.equalsIgnoreCase("YES") && !strMakeOrder.equalsIgnoreCase("")) {
                    //Tender();
                }

            }


           /* if ((crsrSettings != null) && crsrSettings.moveToFirst()) {
                GSTEnable = crsrSettings.getString(crsrSettings.getColumnIndex("GSTEnable"));
                HSNEnable_out = crsrSettings.getString(crsrSettings.getColumnIndex("HSNCode_Out"));
                if (HSNEnable_out == null) {
                    HSNEnable_out = "0";
                }
                POSEnable = crsrSettings.getString(crsrSettings.getColumnIndex("POS_Out"));
                if (POSEnable == null) {
                    POSEnable = "0";
                }

            }
            if ((GSTEnable != null) && (GSTEnable.equals("1"))) {
                // setting TAX name
                tvSalesTax.setText(NAME_CGST_RATE);
                tvServiceTax.setText(NAME_SGST_RATE);
                etCustGSTIN.setVisibility(View.VISIBLE);


            } else {
                tvSalesTax.setText(NAME_SALES_RATE);
                tvServiceTax.setText(NAME_SERVICE_RATE);
                etCustGSTIN.setVisibility(View.GONE);
            }*/

            //gst
            /*if (GSTEnable.equalsIgnoreCase("1")) {
                if ((HSNEnable_out != null) && (HSNEnable_out.equalsIgnoreCase("1"))) {
                    tvHSNCode_out.setVisibility(View.VISIBLE);
                } else {
                    tvHSNCode_out.setVisibility(View.GONE);
                }
            } else { // no gst
                tvHSNCode_out.setVisibility(View.GONE);
            }*/

            chk_interstate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == false) {
                        //et_pos.setBackgroundColor(Color.WHITE);
                        spnr_pos.setSelection(0);
                        spnr_pos.setEnabled(false);
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                        /*float taxAmount = Float.parseFloat(tvTaxTotal.getText().toString());
                        if (taxAmount <= 0) {
                            tvTaxTotal.setText("0");
                            tvServiceTaxTotal.setText("0");
                        } else {
                            tvTaxTotal.setText(String.valueOf(taxAmount / 2));
                            tvTaxTotal.setText(String.valueOf(taxAmount / 2));
                        }
                        tvServiceTaxTotal.setVisibility(View.VISIBLE);
                        tvServiceTax.setVisibility(View.VISIBLE);
                        */
                    } else {
                        // interstate
                        //et_pos.setBackground(Color.GRAY);
                        spnr_pos.setSelection(0);
                        spnr_pos.setEnabled(true);
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

                        /*tvSalesTax.setText("IGST Tax");
                        tvServiceTax.setText("");
                        float taxAmount = Float.parseFloat(tvTaxTotal.getText().toString());
                        if (taxAmount <= 0) {
                            tvTaxTotal.setText("0");
                            tvServiceTaxTotal.setText("");
                        } else {
                            tvTaxTotal.setText(String.valueOf(taxAmount * 2));
                            tvServiceTaxTotal.setText("");

                        }
                        tvServiceTaxTotal.setVisibility(View.INVISIBLE);
                        tvServiceTax.setVisibility(View.INVISIBLE);*/

                    }
                }
            });
        } catch (Exception e) {
            MsgBox.Show("Exception", e.getMessage());
            e.printStackTrace();
        }
        try{
            MsgBox.setIcon(R.drawable.ic_launcher);
            edtCustPhoneNo.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    try {
                        String phn = edtCustPhoneNo.getText().toString();
                        if (edtCustPhoneNo.getText().toString().equals(""))
                            return;
                        if (edtCustPhoneNo.getText().toString().length() == 10) {
                            Cursor crsrCust = db.getFnbCustomer(edtCustPhoneNo.getText().toString());
                            if (crsrCust.moveToFirst()) {
                                //if(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")) == edtCustPhoneNo.getText().toString()) {
                                edtCustId.setText(crsrCust.getString(crsrCust.getColumnIndex("CustId")));
                                edtCustName.setText(crsrCust.getString(crsrCust.getColumnIndex("CustName")));
                                //edtCustPhoneNo.setText(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")));
                                edtCustAddress.setText(crsrCust.getString(crsrCust.getColumnIndex("CustAddress")));
                                String gstin = crsrCust.getString(crsrCust.getColumnIndex("GSTIN"));
                                if (gstin == null)
                                    etCustGSTIN.setText("");
                                else
                                    etCustGSTIN.setText(gstin);
                                final Cursor cursor_KOT = dbBillScreen.getKOTItems(crsrCust.getInt(crsrCust.getColumnIndex("CustId")),String.valueOf(jBillingMode) );
                                if(!CustomerDetailsFilled && cursor_KOT!=null  && cursor_KOT.moveToFirst())
                                {
                                    if(jBillingMode==3)
                                        MsgBox.Show("Warning", " KOT is already present for this customer, please goto KOT status for modification  or billing");
                                    else if(jBillingMode==4)
                                        MsgBox.Show("Warning", " KOT is already present for this customer, please goto Delivery Status for modification  or billing");

                                    etCustGSTIN.setText("");
                                    edtCustId.setText("0");
                                    edtCustName.setText("");
                                    edtCustPhoneNo.setText("");
                                    edtCustAddress.setText("");
                                    return;
//
                                }else {
                                    btnAddCustomer.setVisibility(View.INVISIBLE);
                                    ControlsSetEnabled();
                                    if(jBillingMode!=2) {
                                        btnPrintBill.setEnabled(false);
                                        btnPayBill.setEnabled(false);
                                    }
                                    else
                                    {
                                        btnPrintBill.setEnabled(true);
                                        btnPayBill.setEnabled(true);
                                    }
                                }

                                //}
                            } else {
                                MsgBox.Show("Note", "Customer is not Found, Please Add Customer before Order");
                                btnAddCustomer.setVisibility(View.VISIBLE);
                                //ControlsSetDisabled();
                            }
                        } else {

                        }
                    } catch (Exception ex) {
                        MsgBox.Show("Error", ex.getMessage());
                        ex.printStackTrace();
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            aTViewSearchItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                    /*Toast.makeText(BillingHomeDeliveryActivity.this, aTViewSearchItem.getText().toString(),
                            Toast.LENGTH_SHORT).show();*/
                        if ((aTViewSearchItem.getText().toString().equals(""))) {
                            MsgBox.Show("Warning", "Enter Item Name");
                        } else {
                            Cursor MenucodeItem = db.getItemLists(aTViewSearchItem.getText().toString().trim());
                            if (MenucodeItem.moveToFirst()) {
                                btnClear.setEnabled(true);
                                AddItemToOrderTable(MenucodeItem);
                                aTViewSearchItem.setText("");
                                // ((EditText)v).setText("");
                            } else {
                                MsgBox.Show("Warning", "Item not found for Selected Item");
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                    }
                }
            });

            aTViewSearchMenuCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                    /*Toast.makeText(BillingHomeDeliveryActivity.this, aTViewSearchMenuCode.getText().toString(),
                            Toast.LENGTH_SHORT).show();*/
                        if ((aTViewSearchMenuCode.getText().toString().equals(""))) {
                            MsgBox.Show("Warning", "Enter Menu Code");
                        } else {
                            Cursor MenucodeItem = db
                                    .getItemss(Integer.parseInt(aTViewSearchMenuCode.getText().toString().trim()));
                            if (MenucodeItem.moveToFirst()) {
                                btnClear.setEnabled(true);
                                AddItemToOrderTable(MenucodeItem);
                                aTViewSearchMenuCode.setText("");
                                // ((EditText)v).setText("");
                            } else {
                                MsgBox.Show("Warning", "Item not found for Selected Item Code");
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                    }
                }
            });


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static int checkScreenResolutionWidthType(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return height;
    }

    private void getScreenResolutionWidthType(int screenResolutionType) {

        switch (screenResolutionType) {

            case mSamsungTab3VScreenResolutionWidth:
                mDataMiniDeviceTextsize = 8;
                mItemNameWidth = 105;
                mHSNWidth = 50;
                mQuantityWidth = 55;
                mRateWidth = 60;
                mAmountWidth = 60;
                break;

            case mSamsungT561ScreenResolutionWidth:
                mHeadingTextSize = 14;
                mDataMiniDeviceTextsize = 9;

                mItemNameWidth = 140;
                mHSNWidth = 70;
                mQuantityWidth = 65;
                mRateWidth = 65;
                mAmountWidth = 75;

                mItemNameTextView.setTextSize(mHeadingTextSize);
                mHSNTextView.setTextSize(mHeadingTextSize);
                mQuantityTextView.setTextSize(mHeadingTextSize);
                mRateTextView.setTextSize(mHeadingTextSize);
                mAmountTextView.setTextSize(mHeadingTextSize);
                mDeleteTextView.setTextSize(mHeadingTextSize);
                break;

            case mDataMiniScreenResolutionWidth:
                mHeadingTextSize = 14;
                mDataMiniDeviceTextsize = 11;

                mItemNameWidth = 140;
                mHSNWidth = 70;
                mQuantityWidth = 65;
                mRateWidth = 65;
                mAmountWidth = 85;

                mItemNameTextView.setTextSize(mHeadingTextSize);
                mHSNTextView.setTextSize(mHeadingTextSize);
                mQuantityTextView.setTextSize(mHeadingTextSize);
                mRateTextView.setTextSize(mHeadingTextSize);
                mAmountTextView.setTextSize(mHeadingTextSize);
                mDeleteTextView.setTextSize(mHeadingTextSize);
                break;
        }
    }



    void setInvoiceDate()
    {
        Cursor crsrSetting = dbBillScreen.getBillSetting();
        if (crsrSetting.moveToFirst())
        {
            if (crsrSetting.getInt(crsrSetting.getColumnIndex("DateAndTime")) == 1)
            {
                Date date1 = new Date();
                try {
                    CharSequence sdate = DateFormat.format("dd-MM-yyyy", date1.getTime());
                    tvDate.setText(String.valueOf(sdate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                String strDate = crsrSetting.getString(crsrSetting.getColumnIndex("BusinessDate"));
                try {
                    tvDate.setText(String.valueOf(strDate));
                    Date date1 = new Date();
                    CharSequence sdate = DateFormat.format("dd-MM-yyyy", date1.getTime());
                    if(strDate.equals(sdate.toString()))
                        idd_date.setVisibility(View.INVISIBLE);
                    else
                        idd_date.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadItems_for_dept(final int deptCode) {
        new AsyncTask<Void, Void, ArrayList<Items>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Items> doInBackground(Void... params) {
                ArrayList<Items> list = null;
                try {
                    list =  db.getItemItems_dept(deptCode);
                } catch (Exception e) {
                    list = null;
                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<Items> list) {
                super.onPostExecute(list);
                if(list!=null)
                    setItemsAdapter(list);
                //  gridViewItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private void loadItems(final int categcode) {
        new AsyncTask<Void, Void, ArrayList<Items>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Items> doInBackground(Void... params) {
                if(categcode == 0)
                    return db.getItemItems();
                else
                    return db.getItemItems(categcode);
            }

            @Override
            protected void onPostExecute(ArrayList<Items> list) {
                super.onPostExecute(list);
                if(list!=null)
                    setItemsAdapter(list);
                //  gridViewItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private void loadDepartments() {
        new AsyncTask<Void, Void, ArrayList<Department>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Department> doInBackground(Void... params) {
                return db.getItemDepartment();
            }

            @Override
            protected void onPostExecute(ArrayList<Department> list) {
                super.onPostExecute(list);
                //editTextOrderNo.setText(String.valueOf(db.getNewBillNumber()));
                if(list!=null)
                    setDepartmentAdapter(list);
                listViewDept.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private void loadCategories(final int deptCode) {
        new AsyncTask<Void, Void, ArrayList<Category>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Category> doInBackground(Void... params) {
                if(deptCode == 0)
                    return db.getAllItemCategory();
                else
                    return db.getAllItemCategory(deptCode);

            }

            @Override
            protected void onPostExecute(ArrayList<Category> list) {
                super.onPostExecute(list);
                //editTextOrderNo.setText(String.valueOf(db.getNewBillNumber()));
                if(list!=null)
                    setCategoryAdapter(list);
                listViewCateg.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    public void setItemsAdapter(ArrayList<Items> list) {
        if (mTestItemsAdapter == null) {
            mTestItemsAdapter = new TestItemsAdapter(this, list);

            mTestItemsAdapter.setOnItemClickListener(new TestItemsAdapter.OnItemsImageClickListener() {
                @Override
                public void onItemClick(int position, int itemCode, View v) {
                    Cursor cursor = db.getItemss(itemCode);
                    btnClear.setEnabled(true);
                    AddItemToOrderTable(cursor);
                }
            });
            mRecyclerGridView.setAdapter(mTestItemsAdapter);

        } else
            mTestItemsAdapter.notifyDataSetChanged(list);
    }
    /*public void setItemsAdapter(ArrayList<Items> list)
    {
        if(itemsAdapter==null){
            itemsAdapter = new ItemsAdapter(this,list);
            gridViewItems.setAdapter(itemsAdapter);
        }
        else
            itemsAdapter.notifyDataSetChanged(list);
    }*/


    public void setDepartmentAdapter(ArrayList<Department> list)
    {
        if(departmentAdapter==null){
            departmentAdapter = new DepartmentAdapter(this,list);
            listViewDept.setAdapter(departmentAdapter);
        }
        else
            departmentAdapter.notifyDataSetChanged(list);
    }

    public void setCategoryAdapter(ArrayList<Category> list)
    {
        if(categoryAdapter==null){
            categoryAdapter = new CategoryAdapter(this,list);
            listViewCateg.setAdapter(categoryAdapter);
        }
        else
            categoryAdapter.notifyDataSetChanged(list);
    }


   /* AdapterView.OnItemClickListener itemsClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Items items = (Items) itemsAdapter.getItem(position);
            Cursor cursor = db.getItemss(items.getItemCode());
            btnClear.setEnabled(true);
            AddItemToOrderTable(cursor);
        }
    };*/

    AdapterView.OnItemClickListener deptClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Department department = (Department) departmentAdapter.getItem(position);
            int deptCode = department.getDeptCode();
            if(FASTBILLINGMODE.equals("3"))// dept+cat+items
            {
                loadCategories(deptCode);
            }
            loadItems_for_dept(deptCode);


        }
    };

    AdapterView.OnItemClickListener catClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Category cat = (Category) categoryAdapter.getItem(position);
            int categcode = cat.getCategCode();
            loadItems(categcode);

        }
    };

    /*************************************************************************************************************************************
     * Initializes all view handlers present in the billing screen with
     * appropriate controls
     ************************************************************************************************************************************/
    private void IntializeViewVariables() {
        tvDate = (TextView) findViewById(R.id.tvBillDateValue);
        tvBillNumber = (EditText) findViewById(R.id.tvBillNumberValue);

        boxDept = (RelativeLayout) findViewById(R.id.boxDept);
        boxCat = (RelativeLayout) findViewById(R.id.boxCat);
        boxItem = (RelativeLayout) findViewById(R.id.boxItem);

        mItemNameTextView = (TextView) findViewById(R.id.tvColItemName);
        mHSNTextView = (TextView) findViewById(R.id.tvColHSN);
        mQuantityTextView = (TextView) findViewById(R.id.tvColItemQty);
        mRateTextView = (TextView) findViewById(R.id.tvColRate);
        mAmountTextView = (TextView) findViewById(R.id.tvColAmount);
        mDeleteTextView = (TextView) findViewById(R.id.tvColDelete);

        etCustGSTIN = (EditText) findViewById(R.id.etCustGSTIN);
        aTViewSearchItem = (AutoCompleteTextView) findViewById(R.id.aCTVSearchItem);
        aTViewSearchMenuCode = (AutoCompleteTextView) findViewById(R.id.aCTVSearchMenuCode);
        /*txtSearchItemBarcode = (EditText) findViewById(R.id.etSearchItemBarcode);
        txtSearchItemBarcode.setOnKeyListener(Item_Search_Barcode_KeyPressEvent);*/

        spnr_pos = (Spinner) findViewById(R.id.spnr_pos);

        btndepart = (Button) findViewById(R.id.btn_depart);
        btncateg = (Button) findViewById(R.id.btn_categ);
        btnitem = (Button) findViewById(R.id.btn_item);
        btndepart.setOnClickListener(this);
        btncateg.setOnClickListener(this);
        btnitem.setOnClickListener(this);

        //tvdeptline = (TextView) findViewById(R.id.tvdeptline);
        //tvcategline = (TextView) findViewById(R.id.tvcategline);
        listViewDept = (ListView) findViewById(R.id.lstDepartmentNames);
        listViewDept.setOnItemClickListener(deptClick);
        listViewCateg = (ListView) findViewById(R.id.lstCategoryNames);
        listViewCateg.setOnItemClickListener(catClick);

        //  gridViewItems = (GridView) findViewById(R.id.gridItems);
        //   gridViewItems.setOnItemClickListener(itemsClick);

        mRecyclerGridView = (RecyclerView) findViewById(R.id.gridItems);
        mRecyclerGridView.setHasFixedSize(true);

        edtCustId = (EditText) findViewById(R.id.edtCustId);
        edtCustName = (EditText) findViewById(R.id.edtCustName);
        edtCustPhoneNo = (EditText) findViewById(R.id.edtCustPhoneNo);
        edtCustAddress = (EditText) findViewById(R.id.edtCustAddress);
        btnAddCustomer = (Button) findViewById(R.id.btn_DineInAddCustomer);
        idd_date  = (LinearLayout) findViewById(R.id.idd_date);
        tblOrderItems = (TableLayout) findViewById(R.id.tblOrderItems);

        tvSubUdfValue = (EditText) findViewById(R.id.tvSubUdfValue);
        tvSubTotal = (TextView) findViewById(R.id.tvSubTotalValue);
        tvTaxTotal = (TextView) findViewById(R.id.tvTaxTotalValue);
        tvServiceTaxTotal = (TextView) findViewById(R.id.tvServiceTaxValue);
        tvBillAmount = (TextView) findViewById(R.id.tvBillTotalValue);
        tvcessValue = (TextView) findViewById(R.id.tvcessValue);
        txtOthercharges = (TextView) findViewById(R.id.txtOthercharges);
        tvDiscountPercentage = (TextView) findViewById(R.id.tvDiscountPercentage);
        tvDiscountAmount = (TextView) findViewById(R.id.tvDiscountAmount);

        tvServiceTax_text = (TextView) findViewById(R.id.tvServiceTax);

        btnDeleteBill = (WepButton) findViewById(R.id.btn_DeleteBill);
        btnPayBill = (WepButton) findViewById(R.id.btn_PayBill);
        btnSaveKOT = (WepButton) findViewById(R.id.btn_SaveKOT);
        btnPrintKOT = (WepButton) findViewById(R.id.btn_PrintKOT);
        btnPrintBill = (WepButton) findViewById(R.id.btn_PrintBill);
        btnClear = (WepButton) findViewById(R.id.btn_Clear);
        btnReprint = (WepButton) findViewById(R.id.btn_Reprint);
        btnDeliveryStatus = (WepButton) findViewById(R.id.btn_DeliveryStatus);

        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCustomer(v);
            }
        });
        btnSaveKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveKOT(v);
            }
        });
        btnPrintKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printKOT(v);
            }
        });
        btnPrintBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printBILL(v);
            }
        });
        btnPayBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tender(v);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear(v);
            }
        });
        btnDeleteBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteBill(v);
            }
        });
        btnReprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReprintBill(v);
            }
        });
        btnDeliveryStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KOTStatus(v);
            }
        });


        // GST implementation

        tvHSNCode_out = (TextView) findViewById(R.id.tvColHSN);
        relative_Interstate = (LinearLayout) findViewById(R.id.relative_interstate);
        relative_pos = (LinearLayout) findViewById(R.id.relative_pos);
        tvIGSTValue = (TextView) findViewById(R.id.tvIGSTValue);
        chk_interstate = (CheckBox) findViewById(R.id.checkbox_interstate);
        autoCompleteTextViewSearchItemBarcode = (AutoCompleteTextView) findViewById(R.id.aCTVSearchItemBarcode);

        etCustGSTIN.addTextChangedListener(this);
        edtCustName.addTextChangedListener(this);
        edtCustPhoneNo.addTextChangedListener(this);
        edtCustAddress.addTextChangedListener(this);
        aTViewSearchItem.addTextChangedListener(this);
        aTViewSearchMenuCode.addTextChangedListener(this);
        autoCompleteTextViewSearchItemBarcode.addTextChangedListener(this);

        autoCompleteTextViewSearchItemBarcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                                        /*Toast.makeText(BillingScreenActivity.this, aTViewSearchMenuCode.getText().toString(),
                            Toast.LENGTH_SHORT).show();*/
                    if ((autoCompleteTextViewSearchItemBarcode.getText().toString().equals(""))) {
                        MsgBox.Show("Warning", "Scan BarCode");
                    } else {
                        Cursor BarcodeItem = db
                                .getItemssbyBarCode((autoCompleteTextViewSearchItemBarcode.getText().toString().trim()));
                        int i =0, added =0;
                        while (BarcodeItem.moveToNext()) {
                            if(i!=position)
                            {
                                i++;
                                continue;
                            }
                            added =1;
                            btnClear.setEnabled(true);
                            AddItemToOrderTable(BarcodeItem);
                            autoCompleteTextViewSearchItemBarcode.setText("");
                            // ((EditText)v).setText("");
                        } if(added==0) {
                            MsgBox.Show("Warning", "Item not found for Selected BarCode");
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String str = charSequence.toString();
        View view = getCurrentFocus();
        if (view == null)
            return;
        switch (view.getId()) {
            case R.id.etCustGSTIN:
                tx = etCustGSTIN.getText().toString();
                break;
            case R.id.edtCustName:
                tx = edtCustName.getText().toString();
                break;
            case R.id.edtCustPhoneNo:
                tx = edtCustPhoneNo.getText().toString();
                break;
            case R.id.edtCustAddress:
                tx = edtCustAddress.getText().toString();
                break;
            case R.id.aCTVSearchItem:
                tx = aTViewSearchItem.getText().toString();
                break;
            case R.id.aCTVSearchMenuCode:
                tx = aTViewSearchMenuCode.getText().toString();
                break;
            case R.id.aCTVSearchItemBarcode:
                tx = autoCompleteTextViewSearchItemBarcode.getText().toString();
                break;

        }
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    public void afterTextChanged(Editable s) {
        try {
            View view = getCurrentFocus();
            if(view== null  || CUSTOMER_FOUND==1)
                return;
            if(view.getId() == R.id.edtCustPhoneNo){
                try {
                    if (edtCustPhoneNo.getText().toString().length() == 10) {
                        Cursor crsrCust = db.getFnbCustomer(edtCustPhoneNo.getText().toString());
                        if (crsrCust.moveToFirst()) {
                            CUSTOMER_FOUND=1;
                            //if(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")) == edtCustPhoneNo.getText().toString()) {
                            edtCustId.setText(crsrCust.getString(crsrCust.getColumnIndex("CustId")));
                            edtCustName.setText(crsrCust.getString(crsrCust.getColumnIndex("CustName")));
                            //edtCustPhoneNo.setText(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")));
                            edtCustAddress.setText(crsrCust.getString(crsrCust.getColumnIndex("CustAddress")));
                            String gstin = crsrCust.getString(crsrCust.getColumnIndex("GSTIN"));
                            if (gstin == null)
                                etCustGSTIN.setText("");
                            else
                                etCustGSTIN.setText(gstin);
                            final Cursor cursor_KOT = dbBillScreen.getKOTItems(crsrCust.getInt(crsrCust.getColumnIndex("CustId")),String.valueOf(jBillingMode) );
                            if(!CustomerDetailsFilled && cursor_KOT!=null  && cursor_KOT.moveToFirst())
                            {
                                //if(jBillingMode==3)
                                // MsgBox.Show("Warning", " KOT is already present for this customer, please goto KOT status for modification  or billing");
                                //else if(jBillingMode==4)
                                // MsgBox.Show("Warning", " KOT is already present for this customer, please goto Delivery Status for modification  or billing");
                                return;
//

                            }else {
                                btnAddCustomer.setVisibility(View.INVISIBLE);
                                ControlsSetEnabled();
                                if(jBillingMode!=2) {
                                    btnPrintBill.setEnabled(false);
                                    btnPayBill.setEnabled(false);
                                }
                                else
                                {
                                    btnPrintBill.setEnabled(true);
                                    btnPayBill.setEnabled(true);
                                }
                            }
                            CUSTOMER_FOUND=0;
                            //}
                        } else {
                            //MsgBox.Show("Note", "Customer is not Found, Please Add Customer before Order");
                            btnAddCustomer.setVisibility(View.VISIBLE);
                            //ControlsSetDisabled();
                        }
                    } else if (edtCustPhoneNo.getText().toString().trim().equals("")){
                        CUSTOMER_FOUND=1;
                        edtCustName.setText("");
                        edtCustId.setText("0");
                        edtCustAddress.setText("");
                        etCustGSTIN.setText("");
                        CUSTOMER_FOUND=0;

                    }
                } catch (Exception ex) {
                    MsgBox.Show("Error", ex.getMessage());
                    ex.printStackTrace();
                }
            }/*else if (view.getId() == R.id.aCTVSearchItemBarcode)
            {
                Toast.makeText(this, "afterTextChanged", Toast.LENGTH_SHORT).show();
                autoCompleteTextViewSearchItemBarcode.setSelection(autoCompleteTextViewSearchItem.getText().toString().length());
            }*/
        }catch (Exception ex) {
            MsgBox.Show("Error " + ex.toString(), ex.getMessage());
        }
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        int id = v.getId();
        /*if(id == R.id.btn_Clear)
        {
            if(isValidCustmerid())
                ClearAll();
        }
        else if(id == R.id.btn_DineInAddCustomer)
        {
            if(isValidCustmerid())
                addCustomer();
        }
        else if(id == R.id.btn_PrintBill)
        {
            //if(isValidCustmerid())
            printBILL();
        }
        else if(id == R.id.btn_PayBill)
        {
            Tender1();
        }
        else if(id == R.id.btn_DeleteBill)
        {
            deleteBill();
        }
        else if(id == R.id.btn_Reprint)
        {
            reprintBill();
        }*/
        if(id == R.id.btn_depart)
        {
            if(FASTBILLINGMODE.equals("3"))
                listViewCateg.setVisibility(View.INVISIBLE);
            //  gridViewItems.setVisibility(View.INVISIBLE);
            mRecyclerGridView.setVisibility(View.INVISIBLE);
            loadDepartments();
        }
        else if(id == R.id.btn_categ)
        {
            listViewDept.setVisibility(View.INVISIBLE);
            //   gridViewItems.setVisibility(View.INVISIBLE);
            mRecyclerGridView.setVisibility(View.INVISIBLE);
            loadCategories(0);
        }
        else if(id == R.id.btn_item)
        {
            switch (Integer.parseInt(FASTBILLINGMODE))
            {
                case 3 : listViewCateg.setVisibility(View.INVISIBLE);
                case 2 : listViewDept.setVisibility(View.INVISIBLE);
            }
            loadItems(0);
        }


    }

    /*************************************************************************************************************************************
     * Item grid image click listener which loads the item to billing grid if
     * selected item is not present otherwise increments the quantity by one
     *************************************************************************************************************************************/
    /*AdapterView.OnItemClickListener GridItemImageClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            // TODO Auto-generated method stub
            if (v.getTag() != null) {
                Items items = (Items) itemsAdapter.getItem(position);
                Cursor cursor = dbBillScreen.getItem(items.getItemCode());
                btnClear.setEnabled(true);
                AddItemToOrderTable(cursor);
            }}
    };*/


    /*************************************************************************************************************************************
     * Bar-code item search key-press / key-listener handler which loads the
     * item to billing grid if selected item is not present otherwise increments
     * the quantity by one
     ************************************************************************************************************************************/
    View.OnKeyListener Item_Search_Barcode_KeyPressEvent = new View.OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub

            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);

                if (((EditText) v).getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Scan item barcode");
                } else {
                    Cursor BarcodeItem = null;//dbBillScreen.getItem(((EditText) v).getText().toString());
                    if (BarcodeItem.moveToFirst()) {
                        btnClear.setEnabled(true);
                        AddItemToOrderTable(BarcodeItem);
                        // ((EditText)v).setText("");
                        return true;
                    } else {
                        MsgBox.Show("Warning", "Item not found for the above barcode");
                    }
                }
            }
            // ((EditText)v).setText("");
            return false;
        }
    };

    /*************************************************************************************************************************************
     * Quantity / Rate column text box click event listener which selects all
     * the text present in text box
     *************************************************************************************************************************************/
    View.OnClickListener Qty_Rate_Click = new View.OnClickListener() {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            //((EditText) v).setSelection(((EditText) v).getText().length());
        }

    };

    /*************************************************************************************************************************************
     * Quantity / Rate column text key press event listener
     *************************************************************************************************************************************/
    View.OnKeyListener Qty_Rate_KeyPressEvent = new View.OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (v.getTag().toString().equalsIgnoreCase("QTY_RATE"))
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                    return false;
                if ( event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);
                    Qty_Rate_Edit();
                }else if ((event.getKeyCode() >= KeyEvent.KEYCODE_0 &&event.getKeyCode()  <= KeyEvent.KEYCODE_9  ) )
                {
                    int startSelection=((EditText)v).getSelectionStart();
                    int endSelection=((EditText)v).getSelectionEnd();
                    System.out.println("StartSelection "+startSelection);
                    System.out.println("EndSelection "+endSelection);
                    if(startSelection == 0 && endSelection == 0) {
                        ((EditText) v).setText(String.valueOf(event.getNumber()));
                        ((EditText) v).setSelection(((EditText) v).getText().length());
                    }else
                    {

                    }
                }
            }
            return false;
        }
    };


    /*************************************************************************************************************************************
     * Loads all the departments to department list
     *
     * @param cursor : Cursor containing all the departments from list
     *************************************************************************************************************************************/
  /*  private void LoadDepartments(Cursor cursor) {

        String columns[] = new String[]{"_id", "DeptName"};
        int vals[] = new int[]{R.id.tvlstDeptCode, R.id.tvlstDeptName};
        deptdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_deptnames, cursor, columns, vals);

        //listViewDept.setVisibility(View.VISIBLE);
        listViewDept.setAdapter(deptdataAdapter);
        listViewDept.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                String deptcode = ((TextView) view.findViewById(R.id.tvlstDeptCode)).getText().toString();

                Cursor Category = dbBillScreen.getCategoryItems(Integer.valueOf(deptcode));
                if (Category.moveToFirst()) {
                    listViewCateg.setVisibility(View.VISIBLE);
                    LoadCategories(Category);
                    GetItemDetailsByDept(Integer.valueOf(deptcode));
                    if (Name.length > 0) {
                   //     gridViewItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                        mRecyclerGridView.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                     //   gridViewItems.setVisibility(View.VISIBLE);
                        mRecyclerGridView.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerGridView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    listViewCateg.setAdapter(null);
                    GetItemDetailsByDept(Integer.valueOf(deptcode));//, Integer.valueOf(categdeptcode));
                    if (Name.length > 0) {
                    //    gridViewItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                        mRecyclerGridView.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                   //     gridViewItems.setVisibility(View.VISIBLE);
                        mRecyclerGridView.setVisibility(View.VISIBLE);
                    } else {
                      //  gridViewItems.setVisibility(View.INVISIBLE);
                        mRecyclerGridView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void LoadDepartmentsItems(Cursor cursor) {

        String columns[] = new String[]{"_id", "DeptName"};
        int vals[] = new int[]{R.id.tvlstDeptCode, R.id.tvlstDeptName};
        deptdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_deptnames, cursor, columns, vals);

        listViewDept.setAdapter(deptdataAdapter);
        listViewDept.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                String deptcode = ((TextView) view.findViewById(R.id.tvlstDeptCode)).getText().toString();

                listViewCateg.setAdapter(null);
                GetItemDetailsByDept(Integer.valueOf(deptcode));
                if (Name.length > 0) {
                //    gridViewItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                    mRecyclerGridView.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                   // gridViewItems.setVisibility(View.VISIBLE);
                    mRecyclerGridView.setVisibility(View.VISIBLE);
                } else {
                 //   gridViewItems.setVisibility(View.INVISIBLE);
                    mRecyclerGridView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }*/

   /* private void LoadCategories(Cursor crsrCateg) {
        String columns[] = new String[]{"_id", "CategName", "DeptCode"};
        int vals[] = new int[]{R.id.tvlstCategCode, R.id.tvlstCategName, R.id.tvlstCategDeptCode};
        categdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_categnames, crsrCateg, columns, vals);

        listViewCateg.setAdapter(categdataAdapter);
        listViewCateg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")

                String categcode = ((TextView) view.findViewById(R.id.tvlstCategCode)).getText().toString();
                //String categdeptcode = ((TextView) view.findViewById(R.id.tvlstCategDeptCode)).getText().toString();
        //        gridViewItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
                GetItemDetails(Integer.valueOf(categcode));
                if (Name.length > 0) {
                  //  gridViewItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                    mRecyclerGridView.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                //    gridViewItems.setVisibility(View.VISIBLE);
                    mRecyclerGridView.setVisibility(View.VISIBLE);
                } else {
                  //  gridViewItems.setVisibility(View.INVISIBLE);
                    mRecyclerGridView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }*/

    /*************************************************************************************************************************************
     * Retrieves Item details such as MenuCode, Name, ImageUri to display in
     * Item image grid
     *
     * @param : Item details will be returned based on Department Code
     *          parameter
     ************************************************************************************************************************************/
    // Get Items by CategCode
    /*private void GetItemDetails() {
        Cursor Items = null;
        Items = dbBillScreen.getAllItems();
        //Items = dbBillScreen.getAllItemsWithoutDeptCateg();
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));
            } while (Items.moveToNext());

        } else {
            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }

    private void GetItemDetailswithoutDeptCateg() {
        Cursor Items = null;
        //Items = dbBillScreen.getAllItems();
        Items = dbBillScreen.getAllItemsWithoutDeptCateg();
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));
            } while (Items.moveToNext());

        } else {
            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }

    // Get Items by CategCode
    private void GetItemDetails(int iCategCode) {
        Cursor Items = null;
        Items = dbBillScreen.getCatbyItems(iCategCode);
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));
            } while (Items.moveToNext());

        } else {

            Log.d("LoadItemsToGrid", "No Items found for Category " + iCategCode);

            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }

    // Get Items by DeptCode
    private void GetItemDetailsByDept(int iDeptCode) {
        Cursor Items = null;
        Items = dbBillScreen.getItems(iDeptCode);
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));
            } while (Items.moveToNext());

        } else {

            Log.d("LoadItemsToGrid", "No Items found");

            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }*/

    /*************************************************************************************************************************************
     * Adds the item to billing table from the cursor
     *
     * @param crsrItem : Cursor which contains the item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void AddItemToOrderTable(Cursor crsrItem)
    {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        String strQty = "0";
        double dRate = 0, dTaxPercent = 0, dDiscPercent = 0, dTaxAmt = 0, dIGSTAmt =0, dcessAmt = 0,dDiscAmt = 0, dTempAmt = 0, dServiceTaxPercent = 0;
        double dServiceTaxAmt = 0;
        int iTaxId = 0, iServiceTaxId = 0, iDiscId = 0;
        boolean bItemExists = false;
        TableRow rowItem = null;
        Cursor crsrTax, crsrDiscount;
        TextView tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent, tvServiceTaxAmt;
        EditText etQty, etRate;
        TextView tvHSn;
        CheckBox chkNumber;
        int crsrSettingsCount = crsrSettings.getCount();
        int crsrItemCount = crsrItem.getCount();
        TextView HSNCode;
        if (crsrItem.moveToFirst() && crsrSettings.moveToFirst())
        {
            do {
                tvServiceTaxPercent = new TextView(this);
                if (iTaxType == 1)
                {
                    String txtServiceTaxPercentage = crsrItem.getString(crsrItem.getColumnIndex("ServiceTaxPercent"));
                    tvServiceTaxPercent.setText(txtServiceTaxPercentage);
                }
                else
                {
                    tvServiceTaxPercent.setText("0");
                }
                // Check for the item in Order table, if present update quantity
                // and amounts
                for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++)
                {
                    TableRow Row = (TableRow) tblOrderItems.getChildAt(iRow);
                    // Check against item number present in table
                    if (Row.getChildAt(0) != null)
                    {
                        CheckBox Number = (CheckBox) Row.getChildAt(0);
                        TextView ItemName = (TextView) Row.getChildAt(1);
                        TextView PrintKOTStatus = (TextView) Row.getChildAt(21);
                        // Check for item number and name, if name is not same
                        // add new
                        if (Number.getText().toString().equalsIgnoreCase(crsrItem.getString(crsrItem.getColumnIndex("MenuCode"))) && ItemName.getText().toString().equalsIgnoreCase(crsrItem.getString(crsrItem.getColumnIndex("ItemName"))))
                        {
                            if (PrintKOTStatus.getText().toString().equalsIgnoreCase("0"))
                            {
                                EditText Qty = (EditText) Row.getChildAt(3);
                                Qty.setEnabled(false);
                            }
                            else
                            {
                                // Quantity
                                EditText Qty = (EditText) Row.getChildAt(3);
                                // Qty.setSelectAllOnFocus(true);
                                strQty = Qty.getText().toString().equalsIgnoreCase("") ? "0" : Qty.getText().toString(); // Temp

                                if (BillwithStock == 1)
                                {
                                    String availableqty = crsrItem.getString(crsrItem.getColumnIndex("Quantity"));
                                    if (crsrItem.getFloat(crsrItem.getColumnIndex("Quantity")) < (Float.valueOf(strQty) + 1))
                                    {
                                        MsgBox.Show("Warning", "Stock is less, present stock quantity is " + availableqty);
                                        Qty.setText(String.format("%.2f", Double.parseDouble(availableqty)) );
                                        return;
                                    }
                                    else
                                    {
                                        Qty.setText(String.format("%.2f", Double.parseDouble(strQty) + 1));
                                    }
                                }
                                else
                                {
                                    Qty.setText(String.format("%.2f", Double.parseDouble(strQty) + 1));
                                }

                                // Amount
                                EditText Rate = (EditText) Row.getChildAt(4);
                                //Rate.setSelectAllOnFocus(true);
                                TextView Amount = (TextView) Row.getChildAt(5);
                                dRate = Double.parseDouble(Rate.getText().toString().equalsIgnoreCase("") ? "0" : Rate.getText().toString()); // Temp

                                // Tax and Discount Amount
                                TextView TaxPer = (TextView) Row.getChildAt(6);
                                TextView TaxAmt = (TextView) Row.getChildAt(7);
                                TextView DiscPer = (TextView) Row.getChildAt(8);
                                TextView DiscAmt = (TextView) Row.getChildAt(9);
                                TextView ServiceTax = (TextView) Row.getChildAt(15);
                                TextView ServiceTaxAmt = (TextView) Row.getChildAt(16);
                                TextView IGSTRate = (TextView) Row.getChildAt(23);
                                TextView IGSTAmt = (TextView) Row.getChildAt(24);
                                TextView cessRate = (TextView) Row.getChildAt(25);
                                TextView cessAmt = (TextView) Row.getChildAt(26);
                                TextView tvTaxableValue = (TextView) Row.getChildAt(28);


                                dTaxPercent = Double.parseDouble(TaxPer.getText().toString().equalsIgnoreCase("") ? "0"
                                        : TaxPer.getText().toString()); // Temp
                                dServiceTaxPercent = Double.parseDouble(ServiceTax.getText().toString().equalsIgnoreCase("") ? "0"
                                        : ServiceTax.getText().toString()); // Tempd
                                double dIGSTRate = Double.parseDouble(IGSTRate.getText().toString().equalsIgnoreCase("") ? "0"
                                        : IGSTRate.getText().toString()); // Temp
                                double dcessRate  = Double.parseDouble(cessRate.getText().toString().equalsIgnoreCase("") ? "0"
                                        : cessRate.getText().toString()); // Temp
                                dDiscPercent = Double.parseDouble(DiscPer.getText().toString().equalsIgnoreCase("") ? "0"
                                        : DiscPer.getText().toString()); // Temp
                                double dTaxableValue = Double.parseDouble(tvTaxableValue.getText().toString().equalsIgnoreCase("") ? "0"
                                        : tvTaxableValue.getText().toString()); // Temp



                                if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // forward tax
                                    // Discount
                                    dDiscAmt = dRate * (dDiscPercent / 100);
                                    dTempAmt = dDiscAmt;
                                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());
                                    dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));
                                    // TaxableAmount
                                    dTaxableValue = (dRate - dTempAmt) *  Double.parseDouble(Qty.getText().toString());
                                    double AmountToPrint = (Double.parseDouble(Qty.getText().toString()) * (dRate-dTempAmt));

                                    // RatetoPrint = Double.parseDouble(df_2.format(RatetoPrint));
                                    AmountToPrint = Double.parseDouble(df_2.format(AmountToPrint));
                                    dTaxableValue = Double.parseDouble(df_3.format(dTaxableValue));


                                    // Tax
                                    dTaxAmt = (dTaxableValue) * (dTaxPercent / 100);
                                    //dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());

                                    dServiceTaxAmt = (dTaxableValue) * (dServiceTaxPercent / 100);
                                    //dServiceTaxAmt = dServiceTaxAmt * Double.parseDouble(Qty.getText().toString());

                                    dIGSTAmt = (dTaxableValue) * (dIGSTRate / 100);
                                    //dIGSTAmt = dIGSTAmt * Double.parseDouble(Qty.getText().toString());

                                    dcessAmt = (dTaxableValue) * (dcessRate / 100);
                                    //dcessAmt = dcessAmt * Double.parseDouble(Qty.getText().toString());

                                    dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));
                                    dTaxAmt = Double.parseDouble(df_2.format(dTaxAmt));
                                    dServiceTaxAmt = Double.parseDouble(df_2.format(dServiceTaxAmt));
                                    dIGSTAmt = Double.parseDouble(df_2.format(dIGSTAmt));
                                    dcessAmt = Double.parseDouble(df_2.format(dcessAmt));


                                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                                    DiscAmt.setText(String.format("%.2f", dDiscAmt));
                                    ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                                    cessAmt.setText(String.format("%.2f", dcessAmt));
                                    IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                                    Amount.setText(String.format("%.2f", AmountToPrint));
                                    tvTaxableValue.setText(String.format("%.2f", Double.parseDouble(df_2.format(dTaxableValue))));

                                } else {// reverse tax
                                    TextView tvOriginalRate = (TextView) Row.getChildAt(27);
                                    double dOriginalRate = Double.parseDouble(tvOriginalRate.getText().toString().equalsIgnoreCase("") ? "0"
                                            : tvOriginalRate.getText().toString());

                                    // Discount
                                    dDiscAmt = dOriginalRate * (dDiscPercent / 100);
                                    dTempAmt = dDiscAmt;
                                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());
                                    dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));

                                    double amount = (dOriginalRate-dTempAmt) *Double.parseDouble(Qty.getText().toString());
                                    double dBasePrice = (dOriginalRate -dTempAmt)/ (1 + (dTaxPercent / 100)+(dServiceTaxPercent/100) + (dcessRate/100));
                                    double taxableValue_new = dBasePrice*Double.parseDouble(Qty.getText().toString());



                                    amount = Double.parseDouble(df_2.format(amount));
                                    taxableValue_new = Double.parseDouble(df_2.format(taxableValue_new));

                                    // Tax
                                    dTaxAmt = (dBasePrice ) * (dTaxPercent / 100);
                                    dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());
                                    //Service tax
                                    dServiceTaxAmt = (dBasePrice ) * (dServiceTaxPercent / 100);
                                    dServiceTaxAmt = dServiceTaxAmt * Double.parseDouble(Qty.getText().toString());

                                    dIGSTAmt = (dBasePrice ) * (dIGSTRate/ 100);
                                    dIGSTAmt = dIGSTAmt * Double.parseDouble(Qty.getText().toString());
                                    //Service tax
                                    dcessAmt = (dBasePrice ) * (dcessRate / 100);
                                    dcessAmt = dcessAmt * Double.parseDouble(Qty.getText().toString());

                                    amount = Double.parseDouble(df_2.format(amount));
                                    taxableValue_new = Double.parseDouble(df_2.format(taxableValue_new));
                                    dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));



                                    dTaxAmt = Double.parseDouble(df_3.format(dTaxAmt));
                                    dServiceTaxAmt = Double.parseDouble(df_3.format(dServiceTaxAmt));
                                    dIGSTAmt = Double.parseDouble(df_3.format(dIGSTAmt));
                                    dcessAmt = Double.parseDouble(df_2.format(dcessAmt));

                                    ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                                    DiscAmt.setText(String.format("%.2f", dDiscAmt));
                                    cessAmt.setText(String.format("%.2f", dcessAmt));
                                    IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                                    Amount.setText(String.format("%.2f", amount));
                                    tvTaxableValue.setText(String.format("%.2f", Double.parseDouble(df_2.format(taxableValue_new))));
                                }


                                // // delete
                                // Delete.setText("Hi");

                                // Clear all variables and set ItemExists to TRUE
                                // and break from the loop
                                dRate = 0;
                                dTaxPercent = 0;
                                dDiscPercent = 0;
                                dTaxAmt = 0;
                                dDiscAmt = 0;
                                dTempAmt = 0;
                                dIGSTAmt =0;
                                dcessAmt =0;
                                dcessRate =0;
                                dIGSTRate =0;
                                bItemExists = true;

                                break;
                            }


                        }
                    }
                }


                if (bItemExists == false) {

                    rowItem = new TableRow(BillingHomeDeliveryActivity.this);
                    rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    //crsrSettings = db.getBillSetting();

                    int PickUpRate = crsrSettings.getInt(crsrSettings.getColumnIndex("PickUpRate"));
                    int HomeDeliveryRate = crsrSettings.getInt(crsrSettings.getColumnIndex("HomeDeliveryRate"));
                    if (jBillingMode == 3) {
                        if (PickUpRate == 1) {
                            dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice1"))));
                        } else if (PickUpRate == 2) {
                            dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice2"))));
                        } else if (PickUpRate == 3) {
                            dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice3"))));
                        }
                    }
                    if (jBillingMode == 4) {
                        if (HomeDeliveryRate == 1) {
                            dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice1"))));
                        } else if (HomeDeliveryRate == 2) {
                            dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice2"))));
                        } else if (HomeDeliveryRate == 3) {
                            dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice3"))));
                        }
                    }

                    // Menu Code
                    chkNumber = new CheckBox(BillingHomeDeliveryActivity.this);
                    chkNumber.setWidth(40); // 57px ~= 85dp
                    chkNumber.setTextSize(0);
                    chkNumber.setTextColor(Color.TRANSPARENT);
                    chkNumber.setText(crsrItem.getString(crsrItem.getColumnIndex("MenuCode")));
                    //Toast.makeText(getApplicationContext(), chkNumber.getText().toString(), Toast.LENGTH_SHORT).show();

                    // Item Name
                    tvName = new TextView(BillingHomeDeliveryActivity.this);
                    //   tvName.setWidth(135); // 154px ~= 230dp
                    //    tvName.setTextSize(11);
                    tvName.setWidth(mItemNameWidth); // 154px ~= 230dp
                    tvName.setTextSize(mDataMiniDeviceTextsize);
                    tvName.setText(crsrItem.getString(crsrItem.getColumnIndex("ItemName")));
                    tvName.setTextColor(getResources().getColor(R.color.black));

                    //hsn code
                    tvHSn = new TextView(BillingHomeDeliveryActivity.this);
//                    tvHSn.setWidth(67); // 154px ~= 230dp
//                    tvHSn.setTextSize(11);
                    tvHSn.setWidth(mHSNWidth); // 154px ~= 230dp
                    tvHSn.setTextSize(mDataMiniDeviceTextsize);
                    tvHSn.setText(crsrItem.getString(crsrItem.getColumnIndex("HSNCode")));
                    if ( !HSNEnable_out.equals("1")) {
                        tvHSn.setVisibility(View.INVISIBLE);
                    }
                    tvHSn.setTextColor(getResources().getColor(R.color.black));


                    // Quantity
                    etQty = new EditText(BillingHomeDeliveryActivity.this);
                    //   etQty.setWidth(55); // 57px ~= 85dp
                    //   etQty.setTextSize(11);
                    etQty.setWidth(mQuantityWidth); // 57px ~= 85dp
                    etQty.setTextSize(mDataMiniDeviceTextsize);

                    etQty.setSelectAllOnFocus(true);
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    // Read quantity from weighing scale if read from weigh
                    // scale is set in settings
                    if (jWeighScale == 0) {
                        etQty.setText("1.00");
                    } else {
                        etQty.setText(String.format("%.2f", getQuantityFromWeighScale()));
                    }
                    etQty.setTag("QTY_RATE");
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etInputValidate.ValidateDecimalInput(etQty);
                    etQty.setEnabled(true);
                    // etQty.setOnClickListener(Qty_Rate_Click);
                    etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                    etQty.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        public void afterTextChanged(Editable s) {
                            Qty_Rate_Edit();
                        }
                    });

                    if (BillwithStock == 1) {
                        if (crsrItem.getFloat(crsrItem.getColumnIndex("Quantity")) < Float.valueOf(etQty.getText().toString())) {
                            String availableQty = crsrItem.getString(crsrItem.getColumnIndex("Quantity")) ;
                            MsgBox.Show("Warning", "Stock is less, present stock quantity is "
                                    + availableQty);
                            etQty.setText(String.format("%.2f", Double.parseDouble(availableQty)));
                            return;
                        }
                    }

                    // Rate
                    etRate = new EditText(this);
//                    etRate.setWidth(70); // 74px ~= 110dp
//                    etRate.setTextSize(11);
                    etRate.setWidth(mRateWidth); // 74px ~= 110dp
                    etRate.setTextSize(mDataMiniDeviceTextsize);
                    etRate.setSelectAllOnFocus(true);
                    etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                    etRate.setTag("QTY_RATE");
                    // Check whether Price change is enabled for the item, if
                    // not set Rate text box click able property to false
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("PriceChange")) == 0  ||
                            !(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1 ) ) {
                        // disabling rate change for reverse tax
                        etRate.setEnabled(false);
                        etRate.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        etRate.addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            public void afterTextChanged(Editable s) {
                                Qty_Rate_Edit();
                            }
                        });
                        //etRate.setOnClickListener(Qty_Rate_Click);
                        etRate.setOnKeyListener(Qty_Rate_KeyPressEvent);
                        etInputValidate.ValidateDecimalInput(etRate);
                    }


                    if(ItemwiseDiscountEnabled ==1 && crsrItem.getString(crsrItem.getColumnIndex("DiscountPercent"))!=null)  // 1->itemwise discount , 0-> billwise discount
                    {
                        dDiscPercent = Double.parseDouble(String.format("%.2f",
                                crsrItem.getDouble(crsrItem.getColumnIndex("DiscountPercent"))));
                    }
                    tvDiscPercent = new TextView(this);
                    tvDiscPercent.setText(String.format("%.2f", dDiscPercent));



                    dTaxPercent = crsrItem.getDouble(crsrItem.getColumnIndex("CGSTRate"));
                    tvTaxPercent = new TextView(this);
                    tvTaxPercent.setText(String.format("%.2f", dTaxPercent));


                    dServiceTaxPercent = crsrItem.getDouble(crsrItem.getColumnIndex("SGSTRate"));
                    tvServiceTaxPercent = new TextView(this);
                    tvServiceTaxPercent.setText(String.format("%.2f", dServiceTaxPercent));


                    double dcessPercent =0;
                    if(crsrSettings!= null && crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) // disabling cess in reverse tax
                        dcessPercent = crsrItem.getDouble(crsrItem.getColumnIndex("cessRate"));
                    TextView tvcess = new TextView(this);
                    tvcess.setText(String.format("%.2f",dcessPercent));

                    double dIGSTPercent = crsrItem.getDouble(crsrItem.getColumnIndex("IGSTRate"));
                    TextView tvIGSTRate = new TextView(this);
                    tvIGSTRate.setText(String.format("%.2f",dIGSTPercent));


                    double AmounttoPrint =0;
                    double RatetoPrint =0;
                    double TaxableValueToPrint =0;

                    // Tax Amount
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // Forward
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        RatetoPrint = dRate;
                        AmounttoPrint = dRate-dDiscAmt;
                        TaxableValueToPrint = dRate -dDiscAmt;

                        RatetoPrint = Double.parseDouble(df_2.format(RatetoPrint));
                        AmounttoPrint = Double.parseDouble(df_2.format(AmounttoPrint));
                        TaxableValueToPrint = Double.parseDouble(df_2.format(TaxableValueToPrint));

                        // Tax
                        dTaxAmt = (dRate - dDiscAmt) * (dTaxPercent / 100);
                        dServiceTaxAmt = (dRate - dDiscAmt) * (dServiceTaxPercent / 100);
                        dIGSTAmt = (dRate - dDiscAmt) * (dIGSTPercent / 100);
                        dcessAmt = (dRate - dDiscAmt) * (dcessPercent / 100);

                        dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));
                        dTaxAmt = Double.parseDouble(df_2.format(dTaxAmt));
                        dServiceTaxAmt = Double.parseDouble(df_2.format(dServiceTaxAmt));
                        dIGSTAmt = Double.parseDouble(df_2.format(dIGSTAmt));
                        dcessAmt = Double.parseDouble(df_2.format(dcessAmt));
                    } else { // Reverse Tax
                        double dBasePrice = 0;
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        AmounttoPrint = dRate -dDiscAmt;
                        dBasePrice = (dRate -dDiscAmt)/ (1 + (dTaxPercent / 100)+(dServiceTaxPercent / 100)+ (dcessPercent/100));
                        RatetoPrint = dBasePrice;
                        TaxableValueToPrint = dBasePrice;

                        //dBasePrice = Double.parseDouble(df_2.format(dBasePrice));
                        RatetoPrint = Double.parseDouble(df_2.format(RatetoPrint));
                        AmounttoPrint = Double.parseDouble(df_2.format(AmounttoPrint));
                        TaxableValueToPrint = Double.parseDouble(df_2.format(TaxableValueToPrint));


                        dTaxAmt = (dBasePrice) * (dTaxPercent / 100);
                        dServiceTaxAmt = (dBasePrice ) * (dServiceTaxPercent / 100);
                        dIGSTAmt = (dBasePrice) * (dIGSTPercent / 100);
                        dcessAmt = (dBasePrice ) * (dcessPercent / 100);

                        dDiscAmt = Double.parseDouble(df_3.format(dDiscAmt));
                        dTaxAmt = Double.parseDouble(df_3.format(dTaxAmt));
                        dServiceTaxAmt = Double.parseDouble(df_3.format(dServiceTaxAmt));
                        dIGSTAmt = Double.parseDouble(df_3.format(dIGSTAmt));
                        dcessAmt = Double.parseDouble(df_3.format(dcessAmt));
                    }

                    etRate.setText(String.format("%.2f", RatetoPrint));

                    // taxable Value
                    TextView tvTaxableValue = new TextView(this);
                    tvTaxableValue.setText(String.format("%.2f",(TaxableValueToPrint)));
                    // Amount
                    tvAmount = new TextView(this);
//                    tvAmount.setWidth(60); // 97px ~= 145dp
//                    tvAmount.setTextSize(11);
                    tvAmount.setWidth(mAmountWidth); // 97px ~= 145dp
                    tvAmount.setTextSize(mDataMiniDeviceTextsize);
                    tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                    tvAmount.setText(String.format("  %.2f", AmounttoPrint));
                    tvAmount.setTextColor(getResources().getColor(R.color.black));


                    tvDiscAmt = new TextView(this);
                    tvDiscAmt.setWidth(50);
                    tvDiscAmt.setText(String.format("%.2f", dDiscAmt));


                    tvTaxAmt = new TextView(this);
                    tvTaxAmt.setText(String.format("%.2f", dTaxAmt));

                    tvServiceTaxAmt = new TextView(this);
                    tvServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));

                    TextView tvIGSTAmt = new TextView(this);
                    tvIGSTAmt.setText(String.format("%.2f", dIGSTAmt));

                    TextView tvcessAmt = new TextView(this);
                    tvcessAmt.setText(String.format("%.2f", dcessAmt));



                    // Department Code
                    tvDeptCode = new TextView(this);
                    tvDeptCode.setText(crsrItem.getString(crsrItem.getColumnIndex("DeptCode")));

                    // Category Code
                    tvCategCode = new TextView(this);
                    tvCategCode.setText(crsrItem.getString(crsrItem.getColumnIndex("CategCode")));

                    // Kitchen Code
                    tvKitchenCode = new TextView(this);
                    tvKitchenCode.setText(crsrItem.getString(crsrItem.getColumnIndex("KitchenCode")));

                    // Tax Type [Forward - 1/ Reverse - 0]
                    tvTaxType = new TextView(this);
                    //tvTaxType.setWidth(50);
                    //tvTaxType.setText(crsrItem.getString(crsrItem.getColumnIndex("TaxType")));
                    tvTaxType.setText(crsrSettings.getString(crsrSettings.getColumnIndex("Tax")));

                    // Modifier Charge
                    tvModifierCharge = new TextView(this);
                    tvModifierCharge.setText("0.0");

                    TextView tvUOM = new TextView(this);
                    tvUOM.setText(crsrItem.getString(crsrItem.getColumnIndex("UOM")));


                    // SupplyType
                    TextView SupplyType = new TextView(this);
                    SupplyType.setText(crsrItem.getString(crsrItem.getColumnIndex("SupplyType")));

                    TextView tvSpace = new TextView(this);
                    tvSpace.setText("        ");
                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    ImageButton ImgDelete = new ImageButton(BillingHomeDeliveryActivity.this);
                    ImgDelete.setImageResource(res);
                    // btnDelete.setText(crsrItem.getString(crsrItem.getColumnIndex("MenuCode")));
                    ImgDelete.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            final View v1 = v;
                            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(BillingHomeDeliveryActivity.this);
                            /*LayoutInflater UserAuthorization = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View vwAuthorization = UserAuthorization.inflate(R.layout.deleteconfirmation, null);*/
                            AuthorizationDialog
                                    .setIcon(R.drawable.ic_launcher)
                                    .setTitle("Confirmation")
                                    .setMessage("Are you sure to delete this item")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            View row = (View) v1.getParent();
                                            ViewGroup container = ((ViewGroup) row.getParent());
                                            container.removeView(row);
                                            container.invalidate();
                                            CalculateTotalAmount();

                                        }
                                    }).show();
                        }
                    });
                    TextView tvSpace1 = new TextView(BillingHomeDeliveryActivity.this);
                    tvSpace1.setText("       ");
                    TextView tvPrintKOTStatus = new TextView(BillingHomeDeliveryActivity.this);
                    tvPrintKOTStatus.setText("1");

                    TextView originalrate = new TextView(this);
                    originalrate.setText(String.format("%.2f",dRate));


                    // Add all text views and edit text to Item Row
                    rowItem.addView(chkNumber);//0
                    rowItem.addView(tvName);//1
                    rowItem.addView(tvHSn);//2
                    rowItem.addView(etQty);//3
                    rowItem.addView(etRate);//4
                    rowItem.addView(tvAmount);//5
                    rowItem.addView(tvTaxPercent);//6
                    rowItem.addView(tvTaxAmt);//7
                    rowItem.addView(tvDiscPercent);//8
                    rowItem.addView(tvDiscAmt);//9
                    rowItem.addView(tvDeptCode);//10
                    rowItem.addView(tvCategCode);//11
                    rowItem.addView(tvKitchenCode);//12
                    rowItem.addView(tvTaxType);//13
                    rowItem.addView(tvModifierCharge);//14
                    rowItem.addView(tvServiceTaxPercent);//15
                    rowItem.addView(tvServiceTaxAmt);//16
                    rowItem.addView(SupplyType);//17
                    rowItem.addView(tvSpace);//18
                    rowItem.addView(ImgDelete);//19
                    rowItem.addView(tvSpace1);//20
                    rowItem.addView(tvPrintKOTStatus);//21
                    rowItem.addView(tvUOM);//22
                    rowItem.addView(tvIGSTRate);//23
                    rowItem.addView(tvIGSTAmt);//24
                    rowItem.addView(tvcess);//25
                    rowItem.addView(tvcessAmt);//26
                    rowItem.addView(originalrate);//27
                    rowItem.addView(tvTaxableValue);//28

                    tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                bItemExists = false;
            } while (crsrItem.moveToNext());
            CalculateTotalAmount();
        }
        else
        {
            Log.d("AddItemToOrderTable", "ItemNotFound Exception");
           // MsgBox.Show("Oops ","Item not found");
        }
    }

    private void AddItemToOrderTable_old(Cursor crsrItem) {

        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        String strQty = "0";
        double dRate = 0, dTaxPercent = 0, dDiscPercent = 0, dTaxAmt = 0, dDiscAmt = 0, dIGSTAmt =0, dcessAmt = 0,dTempAmt = 0, dServiceTaxPercent = 0;
        double dServiceTaxAmt = 0;
        int iTaxId = 0, iServiceTaxId = 0, iDiscId = 0;
        boolean bItemExists = false;

        TableRow rowItem = null;
        Cursor crsrTax, crsrDiscount;

        TextView tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, tvDeptCode, tvCategCode,
                tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent, tvServiceTaxAmt;
        EditText etQty, etRate;
        TextView tvHSn;
        CheckBox chkNumber;
        crsrSettings = db.getBillSettings();

        TextView HSNCode;
        // If item is present in cursor
        if (crsrItem.moveToFirst() && crsrSettings.moveToFirst()) {


            do {
                // Get service tax percentage
                tvServiceTaxPercent = new TextView(myContext);
                if (iTaxType == 1) {
                    /*iTaxId = crsrItem.getInt(crsrItem.getColumnIndex("AdditionalTaxId"));
                    crsrTax = dbBillScreen.getTaxConfig(iTaxId);
                    if (crsrTax.moveToFirst()) {
                        tvServiceTaxPercent.setText(crsrTax.getString(crsrTax.getColumnIndex("TotalPercentage")));
                    } else {
                        tvServiceTaxPercent.setText("0");
                    }*/
                    tvServiceTaxPercent.setText(crsrItem.getString(crsrItem.getColumnIndex("ServiceTaxPercent")));
                } else {
                    tvServiceTaxPercent.setText("0");
                }

                // Check for the item in Order table, if present update quantity
                // and amounts
                for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                    TableRow Row = (TableRow) tblOrderItems.getChildAt(iRow);
                    // Check against item number present in table
                    if (Row.getChildAt(0) != null)
                    {
                        CheckBox Number = (CheckBox) Row.getChildAt(0);
                        TextView ItemName = (TextView) Row.getChildAt(1);
                        TextView PrintKOTStatus = (TextView) Row.getChildAt(21);
                        // Check for item number and name, if name is not same
                        // add new
                        if (Number.getText().toString().equalsIgnoreCase(crsrItem.getString(crsrItem.getColumnIndex("MenuCode"))) && ItemName.getText().toString().equalsIgnoreCase(crsrItem.getString(crsrItem.getColumnIndex("ItemName"))))
                        {
                            if (PrintKOTStatus.getText().toString().equalsIgnoreCase("0"))
                            {
                                EditText Qty = (EditText) Row.getChildAt(3);
                                Qty.setEnabled(false);
                            }
                            else
                            {
                                // Quantity
                                EditText Qty = (EditText) Row.getChildAt(3);
                                //Qty.setSelectAllOnFocus(true);
                                strQty = Qty.getText().toString().equalsIgnoreCase("") ? "0" : Qty.getText().toString(); // Temp

                                if (BillwithStock == 1)
                                {
                                    String availableqty = crsrItem.getString(crsrItem.getColumnIndex("Quantity"));
                                    if (crsrItem.getFloat(crsrItem.getColumnIndex("Quantity")) < (Float.valueOf(strQty) + 1))
                                    {
                                        MsgBox.Show("Warning", "Stock is less, present stock quantity is " + availableqty);
                                        Qty.setText(String.format("%.2f", Double.parseDouble(availableqty)) );
                                        return;
                                    }
                                    else
                                    {
                                        Qty.setText(String.format("%.2f", Double.parseDouble(strQty) + 1));
                                    }
                                }
                                else
                                {
                                    Qty.setText(String.format("%.2f", Double.parseDouble(strQty) + 1));
                                }

                                // Amount
                                EditText Rate = (EditText) Row.getChildAt(4);
                                //Rate.setSelectAllOnFocus(true);
                                TextView Amount = (TextView) Row.getChildAt(5);
                                dRate = Double.parseDouble(Rate.getText().toString().equalsIgnoreCase("") ? "0" : Rate.getText().toString()); // Temp

                                // Tax and Discount Amount
                                TextView TaxPer = (TextView) Row.getChildAt(6);
                                TextView TaxAmt = (TextView) Row.getChildAt(7);
                                TextView DiscPer = (TextView) Row.getChildAt(8);
                                TextView DiscAmt = (TextView) Row.getChildAt(9);
                                TextView ServiceTax = (TextView) Row.getChildAt(15);
                                TextView ServiceTaxAmt = (TextView) Row.getChildAt(16);
                                TextView IGSTRate = (TextView) Row.getChildAt(23);
                                TextView IGSTAmt = (TextView) Row.getChildAt(24);
                                TextView cessRate = (TextView) Row.getChildAt(25);
                                TextView cessAmt = (TextView) Row.getChildAt(26);


                                dTaxPercent = Double.parseDouble(TaxPer.getText().toString().equalsIgnoreCase("") ? "0"
                                        : TaxPer.getText().toString()); // Temp
                                dServiceTaxPercent = Double.parseDouble(ServiceTax.getText().toString().equalsIgnoreCase("") ? "0"
                                        : ServiceTax.getText().toString()); // Tempd
                                double dIGSTRate = Double.parseDouble(IGSTRate.getText().toString().equalsIgnoreCase("") ? "0"
                                        : IGSTRate.getText().toString()); // Temp
                                double dcessRate  = Double.parseDouble(cessRate.getText().toString().equalsIgnoreCase("") ? "0"
                                        : cessRate.getText().toString()); // Temp
                                dDiscPercent = Double.parseDouble(DiscPer.getText().toString().equalsIgnoreCase("") ? "0"
                                        : DiscPer.getText().toString()); // Temp

                                if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // forward tax
                                    // Discount
                                    dDiscAmt = dRate * (dDiscPercent / 100);
                                    dTempAmt = dDiscAmt;
                                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());

                                    // Tax
                                    dTaxAmt = (dRate - dTempAmt) * (dTaxPercent / 100);
                                    dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());

                                    dServiceTaxAmt = (dRate - dTempAmt) * (dServiceTaxPercent / 100);
                                    dServiceTaxAmt = dServiceTaxAmt * Double.parseDouble(Qty.getText().toString());

                                    dIGSTAmt = (dRate - dTempAmt) * (dIGSTRate / 100);
                                    dIGSTAmt = dIGSTAmt * Double.parseDouble(Qty.getText().toString());

                                    dcessAmt = (dRate - dTempAmt) * (dcessRate / 100);
                                    dcessAmt = dcessAmt * Double.parseDouble(Qty.getText().toString());

                                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                                    DiscAmt.setText(String.format("%.2f", dDiscAmt));
                                    ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                                    cessAmt.setText(String.format("%.2f", dcessAmt));
                                    IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                                    Amount.setText(String.format("%.2f", (Double.parseDouble(Qty.getText().toString()) * (dRate-dTempAmt))));

                                } else {// reverse tax
                                    double dBasePrice = 0;
                                    dBasePrice = dRate *(1-(dDiscPercent/100))/ (1 + (dTaxPercent / 100)+(dServiceTaxPercent/100));

                                    // Discount
                                    dDiscAmt = dBasePrice * (dDiscPercent / 100);
                                    dTempAmt = dDiscAmt;
                                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());

                                    // Tax
                                    dTaxAmt = (dBasePrice ) * (dTaxPercent / 100);
                                    dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());
                                    //Service tax
                                    dServiceTaxAmt = (dBasePrice ) * (dServiceTaxPercent / 100);
                                    dServiceTaxAmt = dServiceTaxAmt * Double.parseDouble(Qty.getText().toString());

                                    dIGSTAmt = (dBasePrice ) * (dIGSTRate/ 100);
                                    dIGSTAmt = dIGSTAmt * Double.parseDouble(Qty.getText().toString());
                                    //Service tax
                                    dcessAmt = (dBasePrice ) * (dcessRate / 100);
                                    dcessAmt = dcessAmt * Double.parseDouble(Qty.getText().toString());

                                    ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                                    DiscAmt.setText(String.format("%.2f", dDiscAmt));
                                    cessAmt.setText(String.format("%.2f", dcessAmt));
                                    IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                                    Amount.setText(String.format("%.2f", (Double.parseDouble(Qty.getText().toString()) * (dRate))));
                                }


                                // // delete
                                // Delete.setText("Hi");

                                // Clear all variables and set ItemExists to TRUE
                                // and break from the loop
                                dRate = 0;
                                dTaxPercent = 0;
                                dDiscPercent = 0;
                                dTaxAmt = 0;
                                dDiscAmt = 0;
                                dTempAmt = 0;
                                dIGSTAmt =0;
                                dcessAmt =0;
                                dcessRate =0;
                                dIGSTRate =0;
                                bItemExists = true;

                                break;
                            }



                        }
                    }
                }


                if (bItemExists == false) {

                    rowItem = new TableRow(myContext);
                    rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    //crsrSettings = dbBillScreen.getBillSetting();

                    int DineInRate = crsrSettings.getInt(crsrSettings.getColumnIndex("DineInRate"));
                    int CounterSalesRate = crsrSettings.getInt(crsrSettings.getColumnIndex("CounterSalesRate"));
                    int PickUpRate = crsrSettings.getInt(crsrSettings.getColumnIndex("PickUpRate"));
                    int HomeDeliveryRate = crsrSettings.getInt(crsrSettings.getColumnIndex("HomeDeliveryRate"));
                    if (jBillingMode == 3) {
                        if (PickUpRate == 1) {
                            dRate = crsrItem.getInt(crsrItem.getColumnIndex("DineInPrice1"));
                        } else if (PickUpRate == 2) {
                            dRate = crsrItem.getInt(crsrItem.getColumnIndex("DineInPrice2"));
                        } else if (PickUpRate == 3) {
                            dRate = crsrItem.getInt(crsrItem.getColumnIndex("DineInPrice3"));
                        }
                    }
                    if (jBillingMode == 4) {
                        if (HomeDeliveryRate == 1) {
                            dRate = crsrItem.getInt(crsrItem.getColumnIndex("DineInPrice1"));
                        } else if (HomeDeliveryRate == 2) {
                            dRate = crsrItem.getInt(crsrItem.getColumnIndex("DineInPrice2"));
                        } else if (HomeDeliveryRate == 3) {
                            dRate = crsrItem.getInt(crsrItem.getColumnIndex("DineInPrice3"));
                        }
                    }
                    // Menu Code
                    chkNumber = new CheckBox(myContext);
                    chkNumber.setWidth(40); // 57px ~= 85dp
                    chkNumber.setTextSize(0);
                    chkNumber.setTextColor(Color.TRANSPARENT);
                    chkNumber.setText(crsrItem.getString(crsrItem.getColumnIndex("MenuCode")));

                    // Item Name
                    tvName = new TextView(myContext);
                    tvName.setWidth(135); // 154px ~= 230dp
                    tvName.setTextSize(11);
                    tvName.setText(crsrItem.getString(crsrItem.getColumnIndex("ItemName")));

                    //hsn code
                    tvHSn = new TextView(myContext);
                    tvHSn.setWidth(67); // 154px ~= 230dp
                    tvHSn.setTextSize(11);
                    tvHSn.setText(crsrItem.getString(crsrItem.getColumnIndex("HSNCode")));
                    if ( !HSNEnable_out.equals("1")) {
                        tvHSn.setVisibility(View.INVISIBLE);
                    }

                    // Quantity
                    etQty = new EditText(myContext);
                    etQty.setWidth(55); // 57px ~= 85dp
                    etQty.setTextSize(11);
                    //etQty.setSelectAllOnFocus(true);
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    // Read quantity from weighing scale if read from weigh
                    // scale is set in settings
                    if (jWeighScale == 0) {
                        etQty.setText("1.00");
                    } else {
                        etQty.setText(String.format("%.2f", getQuantityFromWeighScale()));
                    }
                    etQty.setTag("QTY_RATE");
                    //etQty.setOnClickListener(Qty_Rate_Click);
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                    etInputValidate.ValidateDecimalInput(etQty);
                    etQty.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        public void afterTextChanged(Editable s) {
                            Qty_Rate_Edit();
                        }
                    });

                    int BillwithStock = crsrSettings.getInt(crsrSettings.getColumnIndex("BillwithStock"));
                    if (BillwithStock == 1) {
                        if (crsrItem.getFloat(crsrItem.getColumnIndex("Quantity")) < Float.valueOf(etQty.getText().toString())) {
                            String availableQty = crsrItem.getString(crsrItem.getColumnIndex("Quantity")) ;
                            MsgBox.Show("Warning", "Stock is less, present stock quantity is "
                                    + availableQty);
                            etQty.setText(String.format("%.2f", Double.parseDouble(availableQty)));
                            return;
                        }
                    }

                    // Rate
                    etRate = new EditText(myContext);
                    etRate.setWidth(70); // 74px ~= 110dp
                    etRate.setTextSize(11);
                    //etRate.setSelectAllOnFocus(true);
                    etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etRate.setText(String.format("%.2f", dRate));
                    etRate.setTag("QTY_RATE");
                    // Check whether Price change is enabled for the item, if
                    // not set Rate text box click able property to false
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("PriceChange")) == 0) {
                        etRate.setEnabled(false);
                    } else {
                        // etRate.setOnClickListener(Qty_Rate_Click);
                        etRate.setOnKeyListener(Qty_Rate_KeyPressEvent);
                        etInputValidate.ValidateDecimalInput(etRate);
                        etRate.addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            public void afterTextChanged(Editable s) {
                                Qty_Rate_Edit();
                            }
                        });
                    }

                    if(ItemwiseDiscountEnabled ==1 && crsrItem.getString(crsrItem.getColumnIndex("DiscountPercent"))!=null)  // 1->itemwise discount , 0-> billwise discount
                    {
                        dDiscPercent = Double.parseDouble(String.format("%.2f",
                                crsrItem.getDouble(crsrItem.getColumnIndex("DiscountPercent"))));
                    }
                    tvDiscPercent = new TextView(myContext);
                    tvDiscPercent.setWidth(50);
                    tvDiscPercent.setText(String.format("%.2f", dDiscPercent));

                    // Discount Amount
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // Forward
                        // Tax
                        dDiscAmt = dRate * (dDiscPercent / 100);
                    } else { // Reverse Tax
                        double dBasePrice = 0;
                        dBasePrice = dRate / (1 + (dTaxPercent / 100));
                        dDiscAmt = dBasePrice * (dDiscPercent / 100);
                    }
                    tvDiscAmt = new TextView(myContext);
                    tvDiscAmt.setWidth(50);
                    tvDiscAmt.setText(String.format("%.2f", dDiscAmt));


                    // Amount
                    tvAmount = new TextView(myContext);
                    tvAmount.setWidth(60); // 97px ~= 145dp
                    tvAmount.setTextSize(11);
                    tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // Forward
                        tvAmount.setText(String.format("  %.2f", dRate - dDiscAmt));
                    }else
                    {
                        tvAmount.setText(String.format("  %.2f", dRate));
                    }



                    // Discount Percent - Check whether Discount is enabled for
                    // the item,
                    // if enabled get discount percentage from discount table
                    /*if (crsrItem.getInt(crsrItem.getColumnIndex("DiscountEnable")) == 1) {
                        iDiscId = crsrItem.getInt(crsrItem.getColumnIndex("DiscId"));
                        crsrDiscount = db.getDiscountConfig(iDiscId);
                        if (!crsrDiscount.moveToFirst()) {
                            MsgBox.Show("Warning", "Failed to read Discount from crsrDiscount");
                            return;
                        } else {

                            dDiscPercent = crsrDiscount.getDouble(crsrDiscount.getColumnIndex("DiscPercentage"));
                        }
                    }
                    tvDiscPercent = new TextView(myContext);
                    tvDiscPercent.setWidth(50);
                    tvDiscPercent.setText(String.format("%.2f", dDiscPercent));*/





                    dTaxPercent = crsrItem.getDouble(crsrItem.getColumnIndex("CGSTRate"));
                    tvTaxPercent = new TextView(BillingHomeDeliveryActivity.this);
                    tvTaxPercent.setText(String.format("%.2f", dTaxPercent));


                    dServiceTaxPercent = crsrItem.getDouble(crsrItem.getColumnIndex("SGSTRate"));
                    tvServiceTaxPercent = new TextView(BillingHomeDeliveryActivity.this);
                    tvServiceTaxPercent.setText(String.format("%.2f", dServiceTaxPercent));



                    double dcessPercent = crsrItem.getDouble(crsrItem.getColumnIndex("cessRate"));
                    TextView tvcess = new TextView(this);
                    tvcess.setText(String.format("%.2f",dcessPercent));

                    double dIGSTPercent = crsrItem.getDouble(crsrItem.getColumnIndex("IGSTRate"));
                    TextView tvIGSTRate = new TextView(this);
                    tvIGSTRate.setText(String.format("%.2f",dIGSTPercent));


                    // Tax Amount
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // Forward
                        // Tax
                        dTaxAmt = (dRate - dDiscAmt) * (dTaxPercent / 100);
                        dServiceTaxAmt = (dRate - dDiscAmt) * (dServiceTaxPercent / 100);
                        dIGSTAmt = (dRate - dDiscAmt) * (dIGSTPercent / 100);
                        dcessAmt = (dRate - dDiscAmt) * (dcessPercent / 100);
                    } else { // Reverse Tax
                        double dBasePrice = dRate *(1-(dDiscPercent/100))/ (1 + (dTaxPercent / 100)+(dServiceTaxPercent / 100));
                        dTaxAmt = (dBasePrice) * (dTaxPercent / 100);
                        dServiceTaxAmt = (dBasePrice ) * (dServiceTaxPercent / 100);
                        dIGSTAmt = (dBasePrice) * (dIGSTPercent / 100);
                        dcessAmt = (dBasePrice ) * (dcessPercent / 100);
                    }
                    tvTaxAmt = new TextView(this);
                    tvTaxAmt.setWidth(50);
                    tvTaxAmt.setText(String.format("%.2f", dTaxAmt));

                    tvServiceTaxAmt = new TextView(this);
                    tvServiceTaxAmt.setWidth(50);
                    tvServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));

                    TextView tvIGSTAmt = new TextView(this);
                    tvIGSTAmt.setWidth(50);
                    tvIGSTAmt.setText(String.format("%.2f", dIGSTAmt));

                    TextView tvcessAmt = new TextView(this);
                    tvcessAmt.setWidth(50);
                    tvcessAmt.setText(String.format("%.2f", dcessAmt));



                    // Department Code
                    tvDeptCode = new TextView(myContext);
                    tvDeptCode.setWidth(50);
                    tvDeptCode.setText(crsrItem.getString(crsrItem.getColumnIndex("DeptCode")));

                    // Category Code
                    tvCategCode = new TextView(myContext);
                    tvCategCode.setWidth(50);
                    tvCategCode.setText(crsrItem.getString(crsrItem.getColumnIndex("CategCode")));

                    // Kitchen Code
                    tvKitchenCode = new TextView(myContext);
                    tvKitchenCode.setWidth(50);
                    tvKitchenCode.setText(crsrItem.getString(crsrItem.getColumnIndex("KitchenCode")));

                    // Tax Type [Forward - 1/ Reverse - 2]
                    tvTaxType = new TextView(myContext);
                    tvTaxType.setWidth(50);
                    //tvTaxType.setText(crsrItem.getString(crsrItem.getColumnIndex("TaxType")));
                    tvTaxType.setText(crsrSettings.getString(crsrSettings.getColumnIndex("Tax")));

                    // Modifier Charge
                    tvModifierCharge = new TextView(myContext);
                    tvModifierCharge.setWidth(50);
                    tvModifierCharge.setText("0.0");

                    TextView tvUOM = new TextView(myContext);
                    tvUOM.setWidth(50);
                    tvUOM.setText(crsrItem.getString(crsrItem.getColumnIndex("UOM")));

                    // SupplyType
                    TextView SupplyType = new TextView(myContext);
                    SupplyType.setText(crsrItem.getString(crsrItem.getColumnIndex("SupplyType")));
                    SupplyType.setWidth(30);

                    TextView tvSpace = new TextView(myContext);
                    tvSpace.setText("        ");

                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    ImageButton ImgDelete = new ImageButton(myContext);
                    ImgDelete.setImageResource(res);
                    // btnDelete.setText(crsrItem.getString(crsrItem.getColumnIndex("MenuCode")));
                    ImgDelete.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final View v1 = v;
                            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);
                            AuthorizationDialog
                                    .setIcon(R.drawable.ic_launcher)
                                    .setTitle("Confirmation")
                                    .setMessage("Are you sure to delete this item")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub

                                            View row = (View) v1.getParent();
                                            ViewGroup container = ((ViewGroup) row.getParent());
                                            container.removeView(row);
                                            container.invalidate();
                                            CalculateTotalAmount();

                                        }
                                    })
                                    .show();

                        }
                    });

                    TextView tvSpace1 = new TextView(myContext);
                    tvSpace1.setText("       ");

                    TextView tvPrintKOTStatus = new TextView(myContext);
                    tvPrintKOTStatus.setText("1");

                    // Add all text views and edit text to Item Row
                    rowItem.addView(chkNumber);//0
                    rowItem.addView(tvName);//1
                    rowItem.addView(tvHSn);//2
                    rowItem.addView(etQty);//3
                    rowItem.addView(etRate);//4
                    rowItem.addView(tvAmount);//5
                    rowItem.addView(tvTaxPercent);//6
                    rowItem.addView(tvTaxAmt);//7
                    rowItem.addView(tvDiscPercent);//8
                    rowItem.addView(tvDiscAmt);//9
                    rowItem.addView(tvDeptCode);//10
                    rowItem.addView(tvCategCode);//11
                    rowItem.addView(tvKitchenCode);//12
                    rowItem.addView(tvTaxType);//13
                    rowItem.addView(tvModifierCharge);//14
                    rowItem.addView(tvServiceTaxPercent);//15
                    rowItem.addView(tvServiceTaxAmt);//16
                    rowItem.addView(SupplyType);//17
                    rowItem.addView(tvSpace);//18
                    rowItem.addView(ImgDelete);//19
                    rowItem.addView(tvSpace1);//20
                    rowItem.addView(tvPrintKOTStatus);//21
                    rowItem.addView(tvUOM);//22
                    rowItem.addView(tvIGSTRate);//23
                    rowItem.addView(tvIGSTAmt);//24
                    rowItem.addView(tvcess);//25
                    rowItem.addView(tvcessAmt);//26
                    tblOrderItems.addView(rowItem,
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }

                bItemExists = false;

            } while (crsrItem.moveToNext());

            CalculateTotalAmount();
            btnPayBill.setEnabled(false);
            btnPrintBill.setEnabled(false);

            // Display Total Items
            //txtTotalItems.setText(String.valueOf(tblOrderItems.getChildCount()));

        } else {
            Log.d("AddItemToOrderTable", "ItemNotFound Exception");
        }
    }

    /*************************************************************************************************************************************
     * Calculates all values such as Tax, Discount, Sub Total and grand total
     * whenever Quantity or Rate text value changed
     *************************************************************************************************************************************/
    private void Qty_Rate_Edit() {

        double strQty = 0;
        double dTaxPercent = 0,dServiceTaxPercent = 0, dDiscPercent = 0, dDiscAmt = 0, dTempAmt = 0, dTaxAmt = 0,dServiceTaxAmt =0;
        double dRate,dIGSTAmt=0, dcessAmt=0;
        try {
            for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                TableRow Row = (TableRow) tblOrderItems.getChildAt(iRow);

                // Check against item number present in table
                if (Row.getChildAt(0) != null) {

                    CheckBox Number = (CheckBox) Row.getChildAt(0);
                    TextView ItemName = (TextView) Row.getChildAt(1);


                    // Quantity
                    EditText Qty = (EditText) Row.getChildAt(3);
                    //Qty.setSelectAllOnFocus(true);
                    strQty = Double.parseDouble(
                            Qty.getText().toString().equalsIgnoreCase("") ? "0" : Qty.getText().toString()); // Temp
                    if (BillwithStock == 1) {
                        Cursor ItemCrsr = db.getItemDetail(ItemName.getText().toString());
                        if(ItemCrsr!=null && ItemCrsr.moveToFirst())
                        {
                            double availableStock = ItemCrsr.getDouble(ItemCrsr.getColumnIndex("Quantity"));
                            if ( availableStock < strQty) {
                                MsgBox.Show("Warning", "Stock is less, present stock quantity is "
                                        + String.valueOf(availableStock));
                                Qty.setText(String.format("%.2f", availableStock));

                                return;
                            }
                        }

                    }





                    // Amount
                    EditText Rate = (EditText) Row.getChildAt(4);
                    //Rate.setSelectAllOnFocus(true);
                    TextView Amount = (TextView) Row.getChildAt(5);
                    dRate = Double.parseDouble(
                            Rate.getText().toString().equalsIgnoreCase("") ? "0" : Rate.getText().toString()); // Temp


                    // Tax and Discount Amount
                    TextView TaxPer = (TextView) Row.getChildAt(6);
                    TextView TaxAmt = (TextView) Row.getChildAt(7);
                    TextView DiscPer = (TextView) Row.getChildAt(8);
                    TextView DiscAmt = (TextView) Row.getChildAt(9);
                    TextView ServiceTax = (TextView) Row.getChildAt(15);
                    TextView ServiceTaxAmt = (TextView) Row.getChildAt(16);
                    TextView IGSTRate = (TextView) Row.getChildAt(23);
                    TextView IGSTAmt = (TextView) Row.getChildAt(24);
                    TextView cessRate = (TextView) Row.getChildAt(25);
                    TextView cessAmt = (TextView) Row.getChildAt(26);
                    TextView tvTaxableValue = (TextView) Row.getChildAt(28);
                    TextView tvOriginalRate = (TextView) Row.getChildAt(27);

                    dTaxPercent = Double.parseDouble(TaxPer.getText().toString().equalsIgnoreCase("") ? "0"
                            : TaxPer.getText().toString()); // Temp
                    dServiceTaxPercent = Double.parseDouble(ServiceTax.getText().toString().equalsIgnoreCase("") ? "0"
                            : ServiceTax.getText().toString()); // Temp
                    double dIGSTRate = Double.parseDouble(IGSTRate.getText().toString().equalsIgnoreCase("") ? "0"
                            : IGSTRate.getText().toString()); // Temp
                    double dcessRate  = Double.parseDouble(cessRate.getText().toString().equalsIgnoreCase("") ? "0"
                            : cessRate.getText().toString()); // Temp
                    dDiscPercent = Double.parseDouble(DiscPer.getText().toString().equalsIgnoreCase("") ? "0"
                            : DiscPer.getText().toString()); // Temp

                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // forward tax
                        // Discount
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        dTempAmt = dDiscAmt;
                        dDiscAmt = dDiscAmt * strQty;

                        // Tax
                        dTaxAmt = (dRate - dTempAmt) * (dTaxPercent / 100);
                        dTaxAmt = dTaxAmt * strQty;

                        dServiceTaxAmt = (dRate - dTempAmt) * (dServiceTaxPercent / 100);
                        dServiceTaxAmt = dServiceTaxAmt * strQty;

                        dIGSTAmt = (dRate - dTempAmt) * (dIGSTRate / 100);
                        dIGSTAmt = dIGSTAmt * strQty;

                        dcessAmt = (dRate - dTempAmt) * (dcessRate / 100);
                        dcessAmt = dcessAmt * strQty;



                        TaxAmt.setText(String.format("%.2f", dTaxAmt));
                        DiscAmt.setText(String.format("%.2f", dDiscAmt));
                        ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                        cessAmt.setText(String.format("%.2f", dcessAmt));
                        IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                        Amount.setText(String.format("%.2f", (strQty * (dRate-dTempAmt))));
                        tvTaxableValue.setText(String.format("%.2f", (strQty * (dRate-dTempAmt))));
                        tvOriginalRate.setText(String.format("%.2f", (dRate)));

                    } else {// reverse tax

                        TextView originalRate = (TextView) Row.getChildAt(27);
                        dRate = Double.parseDouble(originalRate.getText().toString().equalsIgnoreCase("") ? "0"
                                : originalRate.getText().toString()); //
                        // Discount
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        dTempAmt = dDiscAmt;
                        dDiscAmt = dDiscAmt * strQty;

                        double dBasePrice = 0;
                        dBasePrice = (dRate-dTempAmt) / (1 + (dTaxPercent / 100)+(dServiceTaxPercent/100)+ (dcessRate/100));

                        // Tax
                        dTaxAmt = (dBasePrice ) * (dTaxPercent / 100);
                        dTaxAmt = dTaxAmt *strQty;;

                        dIGSTAmt = (dBasePrice ) * (dIGSTRate/ 100);
                        dIGSTAmt = dIGSTAmt * strQty;

                        dcessAmt = (dBasePrice ) * (dcessRate / 100);
                        dcessAmt = dcessAmt * strQty;;

                        //Service tax
                        dServiceTaxAmt = (dBasePrice) * (dServiceTaxPercent / 100);
                        dServiceTaxAmt = dServiceTaxAmt * strQty;

                        double amount = (strQty * (dRate-dTempAmt));
                        double taxVal = (strQty * dBasePrice);

                        dServiceTaxAmt = Double.parseDouble(df_3.format(dServiceTaxAmt));
                        dTaxAmt = Double.parseDouble(df_3.format(dTaxAmt));
                        dDiscAmt = Double.parseDouble(df_3.format(dDiscAmt));
                        dcessAmt = Double.parseDouble(df_3.format(dcessAmt));
                        dIGSTAmt = Double.parseDouble(df_3.format(dIGSTAmt));
                        amount = Double.parseDouble(df_2.format(amount));
                        taxVal = Double.parseDouble(df_2.format(taxVal));

                        Pattern p = Pattern.compile("^(-?[0-9]+[\\.\\,][0-9]{1,2})?[0-9]*$");
                        Matcher m = p.matcher(String.valueOf(taxVal));
                        boolean matchFound = m.find();
                        if (matchFound) {
                            System.out.println(Double.valueOf(m.group(1)));
                        }


                        ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                        TaxAmt.setText(String.format("%.2f", dTaxAmt));
                        DiscAmt.setText(String.format("%.2f", dDiscAmt));
                        cessAmt.setText(String.format("%.2f", dcessAmt));
                        IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                        Amount.setText(String.format("%.2f",amount ));
                        tvTaxableValue.setText(String.format("%.2f",taxVal ));
                    }
                    // // delete
                    // Delete.setText("Hi");

                    // Clear all variables and set ItemExists to TRUE
                    // and break from the loop
                    dRate = 0;
                    dTaxPercent = 0;
                    dDiscPercent = 0;
                    dTaxAmt = 0;
                    dDiscAmt = 0;
                    dTempAmt = 0;
                    //bItemExists = true;

                }

            }
            CalculateTotalAmount();
        } catch (Exception e) {
            MsgBox.setMessage("Error while changing quantity directly :" + e.getMessage()).setPositiveButton("OK", null).show();
            e.printStackTrace();
        }
    }
    /*************************************************************************************************************************************
     * Calculates bill sub total, sales tax amount, service tax amount and Bill
     * total amount.
     ************************************************************************************************************************************/
    private void CalculateTotalAmount() {

        double dSubTotal = 0, dTaxTotal = 0, dModifierAmt = 0, dServiceTaxAmt = 0, dOtherCharges = 0, dTaxAmt = 0, dSerTaxAmt = 0, dIGSTAmt=0, dcessAmt=0;
        float dTaxPercent = 0, dSerTaxPercent = 0;

        // Item wise tax calculation ----------------------------
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

            TableRow RowItem = (TableRow) tblOrderItems.getChildAt(iRow);

            if (RowItem.getChildAt(0) != null) {

                TextView ColTaxType = (TextView) RowItem.getChildAt(13);
                TextView ColAmount = (TextView) RowItem.getChildAt(5);
                TextView ColDisc = (TextView) RowItem.getChildAt(9);
                TextView ColTax = (TextView) RowItem.getChildAt(7);
                TextView ColModifierAmount = (TextView) RowItem.getChildAt(14);
                TextView ColServiceTaxAmount = (TextView) RowItem.getChildAt(16);
                TextView ColIGSTAmount = (TextView) RowItem.getChildAt(24);
                TextView ColcessAmount = (TextView) RowItem.getChildAt(26);
                dTaxTotal += Double.parseDouble(ColTax.getText().toString());
                dServiceTaxAmt += Double.parseDouble(ColServiceTaxAmount.getText().toString());
                dIGSTAmt += Double.parseDouble(ColIGSTAmount.getText().toString());
                dcessAmt += Double.parseDouble(ColcessAmount.getText().toString());
                dSubTotal += Double.parseDouble(ColAmount.getText().toString());

            }
        }
        // ------------------------------------------

        // Bill wise tax Calculation -------------------------------
        Cursor crsrtax = dbBillScreen.getTaxConfig(1);
        if (crsrtax.moveToFirst()) {
            dTaxPercent = crsrtax.getFloat(crsrtax.getColumnIndex("TotalPercentage"));
            dTaxAmt += dSubTotal * (dTaxPercent / 100);
        }
        Cursor crsrtax1 = dbBillScreen.getTaxConfig(2);
        if (crsrtax1.moveToFirst()) {
            dSerTaxPercent = crsrtax1.getFloat(crsrtax1.getColumnIndex("TotalPercentage"));
            dSerTaxAmt += dSubTotal * (dSerTaxPercent / 100);
        }
        // -------------------------------------------------

        dOtherCharges = Double.valueOf(txtOthercharges.getText().toString());
        //String strTax = crsrSettings.getString(crsrSettings.getColumnIndex("Tax"));
        if (crsrSettings.moveToFirst()) {
            if (crsrSettings.getString(crsrSettings.getColumnIndex("Tax")).equalsIgnoreCase("1")) {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1")) {

                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvTaxTotal.setText(String.format("%.2f", dTaxTotal));
                    tvServiceTaxTotal.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                    }

                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxTotal + dServiceTaxAmt + dOtherCharges+dcessAmt));
                } else {

                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvTaxTotal.setText(String.format("%.2f", dTaxTotal));
                    tvServiceTaxTotal.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxAmt + dSerTaxAmt + dOtherCharges+dcessAmt));
                }
            } else {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1")) {
                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvTaxTotal.setText(String.format("%.2f", dTaxTotal));
                    tvServiceTaxTotal.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dOtherCharges));

                } else {
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvTaxTotal.setText(String.format("%.2f", dTaxTotal));
                    tvServiceTaxTotal.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));


                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                    }
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dOtherCharges));
                }
            }
        }
    }

    private void CalculateTotalAmountforRePrint() {
        CalculateTotalAmount();

        /*double dSubTotal = 0, dTaxTotal = 0, dModifierAmt = 0, dServiceTaxAmt = 0, dOtherCharges = 0, dTaxAmt = 0, dSerTaxAmt = 0;
        float dTaxPercent = 0, dSerTaxPercent = 0;
        double discountamt = Double.parseDouble(tvDiscountAmount.getText().toString());
        // Item wise tax calculation ----------------------------
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

            TableRow RowItem = (TableRow) tblOrderItems.getChildAt(iRow);

            if (RowItem.getChildAt(0) != null) {

                //TextView ColTaxType = (TextView) RowItem.getChildAt(12);
                TextView ColAmount = (TextView) RowItem.getChildAt(5);
                //TextView ColDisc = (TextView) RowItem.getChildAt(8);
                TextView ColTax = (TextView) RowItem.getChildAt(7);
                //TextView ColModifierAmount = (TextView) RowItem.getChildAt(13);
                TextView ColServiceTaxAmount = (TextView) RowItem.getChildAt(16);

                dTaxTotal += Double.parseDouble(ColTax.getText().toString());
                dServiceTaxAmt += Double.parseDouble(ColServiceTaxAmount.getText().toString());

                dSubTotal += Double.parseDouble(ColAmount.getText().toString());
            }
        }
        // ------------------------------------------

        // Bill wise tax Calculation -------------------------------
        Cursor crsrtax = dbBillScreen.getTaxConfig(1);
        if (crsrtax.moveToFirst()) {
            dTaxPercent = crsrtax.getFloat(crsrtax.getColumnIndex("TotalPercentage"));
            dTaxAmt += dSubTotal * (dTaxPercent / 100);
        }
        Cursor crsrtax1 = dbBillScreen.getTaxConfig(2);
        if (crsrtax1.moveToFirst()) {
            dSerTaxPercent = crsrtax1.getFloat(crsrtax1.getColumnIndex("TotalPercentage"));
            dSerTaxAmt += dSubTotal * (dSerTaxPercent / 100);
        }
        // -------------------------------------------------

        //dOtherCharges = Double.valueOf(txtOthercharges.getText().toString());
        if (crsrSettings.moveToFirst()) {
            if (crsrSettings.getString(crsrSettings.getColumnIndex("Tax")).equalsIgnoreCase("1")) {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1")) {

                    tvIGSTValue.setText(String.format("%.2f", dTaxTotal + dServiceTaxAmt));
                    tvTaxTotal.setText(String.format("%.2f", dTaxTotal));
                    tvServiceTaxTotal.setText(String.format("%.2f", dServiceTaxAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxTotal + dServiceTaxAmt + dOtherCharges-discountamt));
                } else {

                    tvIGSTValue.setText(String.format("%.2f", dTaxTotal + dServiceTaxAmt));
                    tvTaxTotal.setText(String.format("%.2f", dTaxTotal));
                    tvServiceTaxTotal.setText(String.format("%.2f", dServiceTaxAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxAmt + dSerTaxAmt + dOtherCharges-discountamt));
                }
            } else {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1")) {
                    tvIGSTValue.setText(String.format("%.2f", dTaxTotal + dServiceTaxAmt));
                    tvTaxTotal.setText(String.format("%.2f", dTaxTotal));
                    tvServiceTaxTotal.setText(String.format("%.2f", dServiceTaxAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvTaxTotal.setTextColor(Color.WHITE);
                        tvServiceTaxTotal.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dOtherCharges-discountamt));

                } else {
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    if (chk_interstate.isChecked()) {
                        tvTaxTotal.setText(String.format("%.2f", dTaxAmt + dSerTaxAmt));
                        // tvServiceTaxTotal.setText(String.format("%.2f", dSerTaxAmt));
                        tvServiceTaxTotal.setText("");
                    } else {
                        tvTaxTotal.setText(String.format("%.2f", dTaxAmt));
                        tvServiceTaxTotal.setText(String.format("%.2f", dSerTaxAmt));
                    }
                    tvTaxTotal.setText(String.format("%.2f", dTaxAmt));
                    tvServiceTaxTotal.setText(String.format("%.2f", dSerTaxAmt));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dOtherCharges-discountamt));
                }
            }
        }*/
    }

    /*************************************************************************************************************************************
     * Delete all the items in billing table and clears all text boxes and item
     * grid in billing screen
     *************************************************************************************************************************************/
    private void ClearAll() {

        Time = Calendar.getInstance();
        tx = "";
        autoCompleteTextViewSearchItemBarcode.setText("");
        isReprint = false;
        //txtSearchItemBarcode.setText("");
        reprintBillingMode=0;
        tvSubUdfValue.setText("1");
        tvSubTotal.setText("0.00");
        tvTaxTotal.setText("0.00");
        tvDiscountAmount.setText("0.00");
        tvDiscountPercentage.setText("0.00");
        tvServiceTaxTotal.setText("0.00");
        tvIGSTValue.setText("0.00");
        tvcessValue.setText("0.00");
        tvBillAmount.setText("0.00");
        chk_interstate.setChecked(false);
        spnr_pos.setSelection(0);
        spnr_pos.setEnabled(false);
        aTViewSearchItem.setText("");
        aTViewSearchMenuCode.setText("");
        CustomerDetailsFilled = false;
        tblOrderItems.removeAllViews();
        edtCustName.setText("");
        edtCustId.setText("0");
        edtCustPhoneNo.setText("");
        edtCustAddress.setText("");
        etCustGSTIN.setText("");
        spnr_pos.setSelection(0);

        tvBillNumber.setText(String.valueOf(db.getNewBillNumber()));
        setInvoiceDate();
        fTotalDiscount =0;
        fRoundOfValue =0;
        AMOUNTPRINTINNEXTLINE =0;

        fCashPayment = 0;
        fCardPayment = 0;
        fCouponPayment = 0;
        fPaidTotalPayment = 0;
        dPettCashPayment = 0;
        dChangePayment = 0;
        fWalletPayment = 0;
        dFinalBillValue = 0;

    }


    /*************************************************************************************************************************************
     * Inserts all the ordered item data to database with table number as
     * reference
     *************************************************************************************************************************************/
    private void InsertKOTItems(int KOTNo) {
        // Inserted Row Id in database table
        long lResult = 0;
        // PendingKOT object
        PendingKOT objPendingKOT;
        objPendingKOT = new PendingKOT();
        // Token number variable
        /*if(KOTNo<=0) {
            //int iTokenNumber = dbBillScreen.getKOTNo();
            objPendingKOT.setTokenNumber(dbBillScreen.getKOTNo());
            long iResult = dbBillScreen.updateKOTNo(dbBillScreen.getKOTNo());
        } else {
            objPendingKOT.setTokenNumber(iKOTNo);
        }*/
        objPendingKOT.setTokenNumber(KOTNo);
        String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
        //String strTime = String.format("%tR", Time);
        String msg =  "Time:" + strTime+" No : "+KOTNo;
        Log.v("KOT Time, No",msg);

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

            TableRow RowKOTItem = (TableRow) tblOrderItems.getChildAt(iRow);
            CheckBox ItemNumber = (CheckBox) RowKOTItem.getChildAt(0);
            TextView ItemName = (TextView) RowKOTItem.getChildAt(1);
            TextView hsn = (TextView) RowKOTItem.getChildAt(2);
            EditText Quantity = (EditText) RowKOTItem.getChildAt(3);
            EditText Rate = (EditText) RowKOTItem.getChildAt(4);
            TextView Amount = (TextView) RowKOTItem.getChildAt(5);
            TextView SalesTaxPercent = (TextView) RowKOTItem.getChildAt(6);
            TextView SalesTaxAmount = (TextView) RowKOTItem.getChildAt(7);
            TextView DiscountPercent = (TextView) RowKOTItem.getChildAt(8);
            TextView DiscountAmount = (TextView) RowKOTItem.getChildAt(9);
            TextView DeptCode = (TextView) RowKOTItem.getChildAt(10);
            TextView CategCode = (TextView) RowKOTItem.getChildAt(11);
            TextView KitchenCode = (TextView) RowKOTItem.getChildAt(12);
            TextView TaxType = (TextView) RowKOTItem.getChildAt(13);
            TextView ModifierAmount = (TextView) RowKOTItem.getChildAt(14);
            TextView ServiceTaxPercent = (TextView) RowKOTItem.getChildAt(15);
            TextView ServiceTaxAmount = (TextView) RowKOTItem.getChildAt(16);
            TextView SupplyType = (TextView) RowKOTItem.getChildAt(17);
            TextView printstatus = (TextView) RowKOTItem.getChildAt(21);
            String printStatus_str = printstatus.getText().toString();
            TextView UOM = (TextView) RowKOTItem.getChildAt(22);
            TextView IGSTRate = (TextView) RowKOTItem.getChildAt(23);
            TextView IGSTAmt = (TextView) RowKOTItem.getChildAt(24);
            TextView cessRate = (TextView) RowKOTItem.getChildAt(25);
            TextView cessAmt = (TextView) RowKOTItem.getChildAt(26);
            TextView originalRate = (TextView) RowKOTItem.getChildAt(27);
            TextView OriginalRate = (TextView) RowKOTItem.getChildAt(27);
            TextView TaxableValue = (TextView) RowKOTItem.getChildAt(28);


            objPendingKOT.setTableNumber(0);

            // SubUdfNumber
            objPendingKOT.setSubUdfNumber(0);

            // WaiterId
            objPendingKOT.setEmployeeId(0);




            // Customer Id
            objPendingKOT.setCusId(Integer.valueOf(edtCustId.getText().toString()));


            // Time
            objPendingKOT.setTime(strTime);

            // Item Number
            if (RowKOTItem.getChildAt(0) != null) {
                objPendingKOT.setItemNumber(Integer.parseInt(ItemNumber.getText().toString()));
            }

            // Item Name
            if (RowKOTItem.getChildAt(1) != null) {
                objPendingKOT.setItemName(ItemName.getText().toString());
            }

            // Item Name
            if (RowKOTItem.getChildAt(2) != null) {
                objPendingKOT.setHSNCode(hsn.getText().toString());
            }

            // Quantity
            if (RowKOTItem.getChildAt(3) != null) {
                String qty_str = Quantity.getText().toString();
                double qty_d = 0.00;
                if(qty_str.equals("")) {
                    Quantity.setText("0.00");
                }else
                {
                    qty_d = Double.parseDouble(qty_str);
                }

                objPendingKOT.setQuantity(Float.parseFloat(String.format("%.2f",qty_d)));
            }

            // Rate
            if (RowKOTItem.getChildAt(4) != null) {
                String rate_str = Rate.getText().toString();
                double rate_d = 0.00;
                if(rate_str.equals("")) {
                    Rate.setText("0.00");
                }else
                {
                    rate_d = Double.parseDouble(rate_str);
                }
                objPendingKOT.setRate(Float.parseFloat(String.format("%.2f",rate_d)));
            }

            // OrigibalRate
            if (RowKOTItem.getChildAt(27) != null) {
                String orirate_str = originalRate.getText().toString();
                double orirate_d = 0.00;
                if(orirate_str.equals("")) {
                    originalRate.setText("0.00");
                }else
                {
                    orirate_d = Double.parseDouble(orirate_str);
                }
                objPendingKOT.setOriginalrate(Double.parseDouble(String.format("%.2f",orirate_d)));
            }

            // Amount
            if (RowKOTItem.getChildAt(5) != null) {
                objPendingKOT.setAmount(Double.parseDouble(Amount.getText().toString()));
            }

            // Sales Tax %
            if (RowKOTItem.getChildAt(6) != null) {
                objPendingKOT.setTaxPercent(Float.parseFloat(SalesTaxPercent.getText().toString()));
            }

            // Sales Tax Amount
            if (RowKOTItem.getChildAt(7) != null) {
                objPendingKOT.setTaxAmount(Double.parseDouble(SalesTaxAmount.getText().toString()));
            }

            // Discount %
            if (RowKOTItem.getChildAt(8) != null) {
                objPendingKOT.setDiscountPercent(Float.parseFloat(DiscountPercent.getText().toString()));
            }

            // Discount Amount
            if (RowKOTItem.getChildAt(9) != null) {
                objPendingKOT.setDiscountAmount(Float.parseFloat(DiscountAmount.getText().toString()));
            }

            // Department Code
            if (RowKOTItem.getChildAt(10) != null) {
                objPendingKOT.setDeptCode(Integer.parseInt(DeptCode.getText().toString()));
            }

            // Category Code
            if (RowKOTItem.getChildAt(11) != null) {
                objPendingKOT.setCategCode(Integer.parseInt(CategCode.getText().toString()));
            }

            // Kitchen Code
            if (RowKOTItem.getChildAt(12) != null) {
                objPendingKOT.setKitchenCode(Integer.parseInt(KitchenCode.getText().toString()));
            }

            // Tax Type
            if (RowKOTItem.getChildAt(13) != null) {
                objPendingKOT.setTaxType(Integer.parseInt(TaxType.getText().toString()));
            }

            // Modifier Amount
            if (RowKOTItem.getChildAt(14) != null) {
                objPendingKOT.setModifierAmount(Float.parseFloat(ModifierAmount.getText().toString()));
            }

            // Service Tax Percent
            if (RowKOTItem.getChildAt(15) != null) {
                objPendingKOT.setServiceTaxPercent(Float.parseFloat(ServiceTaxPercent.getText().toString()));
            }

            // Service Tax Amount
            if (RowKOTItem.getChildAt(16) != null) {
                objPendingKOT.setServiceTaxAmount(Double.parseDouble(ServiceTaxAmount.getText().toString()));
            }

            // SupplyType - G/S
            if (RowKOTItem.getChildAt(17) != null) {
                objPendingKOT.setSupplyType(SupplyType.getText().toString());
            }
            if(RowKOTItem.getChildAt(22)!=null)
            {
                objPendingKOT.setUOM(UOM.getText().toString());
            }
            if(RowKOTItem.getChildAt(23)!=null)
            {
                objPendingKOT.setIGSTRate(Float.parseFloat(IGSTRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(24)!=null)
            {
                objPendingKOT.setIGSTAmount(Float.parseFloat(IGSTAmt.getText().toString()));
            }
            if(RowKOTItem.getChildAt(25)!=null)
            {
                objPendingKOT.setCessRate(Float.parseFloat(cessRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(26)!=null)
            {
                objPendingKOT.setCessAmount(Double.parseDouble(cessAmt.getText().toString()));
            }
            if(RowKOTItem.getChildAt(27)!=null)
            {
                objPendingKOT.setOriginalrate(Double.parseDouble(OriginalRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(28)!=null)
            {
                objPendingKOT.setTaxableValue(Double.parseDouble(TaxableValue.getText().toString()));
            }

            // Order Mode
            objPendingKOT.setOrderMode(jBillingMode);

            // KOT No
            // objPendingKOT.setTokenNumber(iTokenNumber);

            // Print KOT STatus
            objPendingKOT.setPrintKOTStatus(iPrintKOTStatus);

            int tblno, subudfno, tblsplitno, itemno, Status;
            tblno = 0;
            subudfno = Integer.valueOf(tvSubUdfValue.getText().toString());
            tblsplitno = 0;
            itemno = Integer.valueOf(ItemNumber.getText().toString());
            Status = Integer.valueOf(printstatus.getText().toString());

            Cursor crsrItemsUpdate = db.getItemsForUpdatingKOT_new(tblno, subudfno, tblsplitno, itemno, jBillingMode);
            if (crsrItemsUpdate.moveToFirst()) {
                float Qty = 0;
                double Amt = 0, TaxAmt = 0, SerTaxAmt = 0;
                float qty_temp = Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("Quantity")));
                Qty = Float.valueOf(Quantity.getText().toString()) + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("Quantity")));
                Amt = Double.valueOf(Amount.getText().toString()) + (crsrItemsUpdate.getDouble(crsrItemsUpdate.getColumnIndex("Amount")));
                TaxAmt = Double.valueOf(SalesTaxAmount.getText().toString()) + Double.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("TaxAmount")));
                SerTaxAmt = Double.valueOf(ServiceTaxAmount.getText().toString()) + Double.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("ServiceTaxAmount")));
                float IAmt = Float.valueOf(IGSTAmt.getText().toString()) + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("IGSTAmount")));
                double cessAmount = Double.valueOf(cessAmt.getText().toString()) + Double.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("cessAmount")));
                double  taxableValue = Double.valueOf(TaxableValue.getText().toString()) +
                        Double.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("TaxableValue")));

                lResult = dbBillScreen.updateKOT(itemno, Qty, Amt, TaxAmt, SerTaxAmt, jBillingMode, IAmt,cessAmount,taxableValue);
                Log.d("UpdateKOT", "KOT item updated at position:" + lResult);
            } else {

                lResult = dbBillScreen.addKOT(objPendingKOT);
                Log.d("InsertKOT", "KOT item inserted at position:" + lResult);
            }


        }
    }

    /*************************************************************************************************************************************
     * Loads KOT order items to billing table
     *
     * @param crsrBillItems : Cursor with KOT order item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void LoadKOTItems(Cursor crsrBillItems) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvName, tvHSn, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;

        try{
            if (crsrBillItems.moveToFirst()) {

                // Get Token number
                iTokenNumber = crsrBillItems.getInt(crsrBillItems.getColumnIndex("TokenNumber"));

           /* et_pos.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("POS")));
            if (et_pos.getText().toString().equals(""))
            {
                chk_interstate.setChecked(false);
            }
            else
            {
                chk_interstate.setChecked(true);
            }
            */// Get waiter Id
                //tvWaiterNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("EmployeeId")));

                // Get Table number
                //tvTableNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableNumber")));

                // Get Table Split No
                //tvTableSplitNo.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableSplitNo")));

                // Get Sub Udf number
                tvSubUdfValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SubUdfNumber")));

                // Get Cust Id
                edtCustId.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CustId")));
                Cursor crsrCustomer = db.getCustomerById(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CustId")));
                if (crsrCustomer.moveToFirst()) {
                    edtCustPhoneNo.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    edtCustName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    edtCustAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                }

                // Display items in table
                do {
                    rowItem = new TableRow(myContext);
                    rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // Item Number
                    Number = new CheckBox(myContext);
                    Number.setWidth(40);
                    Number.setTextSize(0);
                    Number.setTextColor(Color.TRANSPARENT);
                    Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                    // Item Name
                    tvName = new TextView(myContext);
                    tvName.setWidth(135);
                    tvName.setTextSize(11);
                    tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));

                    //hsn code
                    tvHSn = new TextView(myContext);
                    tvHSn.setWidth(67); // 154px ~= 230dp
                    tvHSn.setTextSize(11);
                    if (GSTEnable.equalsIgnoreCase("1") && (HSNEnable_out != null) && HSNEnable_out.equals("1")) {
                        tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                    }
                    // Quantity
                    etQty = new EditText(myContext);
                    etQty.setWidth(55);
                    etQty.setTextSize(11);
                    if (crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")).equalsIgnoreCase("1")) {
                        etQty.setEnabled(true);
                    } else {
                        etQty.setEnabled(false);
                    }
                    etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                    etQty.setSelectAllOnFocus(true);
                    etQty.setTag("QTY_RATE");
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    if( jBillingMode ==3 || jBillingMode ==4)
                    {
                        //etQty.setOnClickListener(Qty_Rate_Click);
                        etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                        etInputValidate.ValidateDecimalInput(etQty);
                        etQty.addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            public void afterTextChanged(Editable s) {
                                Qty_Rate_Edit();
                            }
                        });
                    }


                    // Rate
                    etRate = new EditText(myContext);
                    etRate.setWidth(70);
                    etRate.setEnabled(false);
                    etRate.setTextSize(11);
                    etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Rate"))));
                    if(crsrSettings!=null && crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) !=1) // reverse TAx
                    {
                        etRate.setEnabled(false);
                        etRate.setTextColor(getResources().getColor(R.color.black));
                    }

                    // Amount
                    tvAmount = new TextView(myContext);
                    tvAmount.setWidth(60);
                    tvAmount.setTextSize(11);
                    tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                    tvAmount.setText(
                            String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));
                    tvAmount.setTextColor(getResources().getColor(R.color.black));

                    // Sales Tax%
                    tvTaxPercent = new TextView(myContext);
                    tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxPercent")));

                    // Sales Tax Amount
                    tvTaxAmt = new TextView(myContext);
                    tvTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxAmount")));

                    // Discount %
                    tvDiscPercent = new TextView(myContext);
                    tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                    // Discount Amount
                    tvDiscAmt = new TextView(myContext);
                    tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                    // Dept Code
                    tvDeptCode = new TextView(myContext);
                    tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                    // Categ Code
                    tvCategCode = new TextView(myContext);
                    tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                    // Kitchen Code
                    tvKitchenCode = new TextView(myContext);
                    tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                    // Tax Type
                    tvTaxType = new TextView(myContext);
                    tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                    // Modifier Amount
                    tvModifierCharge = new TextView(myContext);
                    tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                    // Service Tax %
                    tvServiceTaxPercent = new TextView(myContext);
                    tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxPercent")));

                    // Service Tax Amount
                    tvServiceTaxAmt = new TextView(myContext);
                    tvServiceTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxAmount")));

                    // Service Tax Amount
                    TextView tvSupplyType = new TextView(myContext);
                    tvSupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));

                    //Original Rate
                    TextView tvOriginalRate = new TextView(myContext);
                    tvOriginalRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("OriginalRate")));


                    TextView tvUOM = new TextView(myContext);
                    tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                    TextView tvIGSTRate = new TextView(myContext);
                    tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));


                    TextView tvIGSTAmt = new TextView(myContext);
                    tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));


                    TextView tvcess = new TextView(myContext);
                    tvcess.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                    TextView tvcessAmt = new TextView(myContext);
                    tvcessAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessAmount")));

                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    ImgDelete = new ImageButton(myContext);
                    ImgDelete.setImageResource(res);
                    ImgDelete.setVisibility(View.INVISIBLE);



                    TextView tvSpace = new TextView(myContext);
                    tvSpace.setText("        ");

                    TextView tvSpace1 = new TextView(myContext);
                    tvSpace1.setText("       ");

                    TextView tvPrintKOTStatus = new TextView(myContext);
                    if(REPRINT_KOT == 1)
                        tvPrintKOTStatus.setText("1");
                    else
                        tvPrintKOTStatus.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")));

                    TextView tvTaxableValue = new TextView(this);
                    tvTaxableValue.setText(String.format("%.2f",crsrBillItems.getDouble(crsrBillItems.getColumnIndex("TaxableValue"))));



                    // Add all text views and edit text to Item Row
                    // rowItem.addView(tvNumber);
                    rowItem.addView(Number);//0
                    rowItem.addView(tvName);//1
                    rowItem.addView(tvHSn);//2
                    rowItem.addView(etQty);//3
                    rowItem.addView(etRate);//4
                    rowItem.addView(tvAmount);//5
                    rowItem.addView(tvTaxPercent);//6
                    rowItem.addView(tvTaxAmt);//7
                    rowItem.addView(tvDiscPercent);//8
                    rowItem.addView(tvDiscAmt);//9
                    rowItem.addView(tvDeptCode);//10
                    rowItem.addView(tvCategCode);//11
                    rowItem.addView(tvKitchenCode);//12
                    rowItem.addView(tvTaxType);//13
                    rowItem.addView(tvModifierCharge);//14
                    rowItem.addView(tvServiceTaxPercent);//15
                    rowItem.addView(tvServiceTaxAmt);//16
                    rowItem.addView(tvSupplyType);//17
                    rowItem.addView(tvSpace);//18
                    rowItem.addView(ImgDelete);//19
                    rowItem.addView(tvSpace1);//20
                    rowItem.addView(tvPrintKOTStatus);//21
                    rowItem.addView(tvUOM);//22
                    rowItem.addView(tvIGSTRate);//23
                    rowItem.addView(tvIGSTAmt);//24
                    rowItem.addView(tvcess);//25
                    rowItem.addView(tvcessAmt);//26
                    rowItem.addView(tvOriginalRate);//27
                    rowItem.addView(tvTaxableValue);//28

                    // Add row to table
                    tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                } while (crsrBillItems.moveToNext());

                REPRINT_KOT =0;

                CalculateTotalAmount();
                Log.d("LoadKOTItems", "Items loaded successfully");
            } else {
                Log.d("LoadKOTItems", "No rows in cursor");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Oops","Some error occured while loading KOT");
        }
    }

    /*************************************************************************************************************************************
     * Loads KOT order items to billing table
     *
     * @param crsrBillItems : Cursor with KOT order item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void LoadModifyKOTItems(Cursor crsrBillItems) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvHSn, tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;

        try{
            if (crsrBillItems.moveToFirst()) {

                iTokenNumber = crsrBillItems.getInt(crsrBillItems.getColumnIndex("TokenNumber"));
                // Get Sub Udf number
                tvSubUdfValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SubUdfNumber")));

                // Get Cust Id
                if (!CustomerDetailsFilled) {
                    edtCustId.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CustId")));
                    Cursor crsrCustomer = db.getCustomerById(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CustId")));
                    if (crsrCustomer.moveToFirst()) {
                        CustomerDetailsFilled = true;
                        edtCustPhoneNo.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                        edtCustName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                        edtCustAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                        etCustGSTIN.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("GSTIN")));
                    }
                }
                // Display items in table
                do {
                    rowItem = new TableRow(myContext);
                    rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // Item Number
                    Number = new CheckBox(myContext);
                    Number.setWidth(40);
                    Number.setTextSize(0);
                    Number.setTextColor(Color.TRANSPARENT);
                    Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                    // Item Name
                    tvName = new TextView(myContext);
                    //tvName.setWidth(135);
                    //tvName.setTextSize(11);
                    tvName.setWidth(mItemNameWidth); // 154px ~= 230dp
                    tvName.setTextSize(mDataMiniDeviceTextsize);
                    tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));
                    tvName.setTextColor(getResources().getColor(R.color.black));


                    //hsn code
                    tvHSn = new TextView(myContext);
                    tvHSn.setWidth(mHSNWidth); // 154px ~= 230dp
                    tvHSn.setTextSize(mDataMiniDeviceTextsize);
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                    //tvHSn.setWidth(67); // 154px ~= 230dp
                    // tvHSn.setTextSize(11);
                    if ( !HSNEnable_out.equals("1")) {
                        tvHSn.setVisibility(View.INVISIBLE);
                    }
                    tvHSn.setTextColor(getResources().getColor(R.color.black));
                    // Quantity
                    etQty = new EditText(myContext);
                    //etQty.setWidth(55);
                    //etQty.setTextSize(11);
                    etQty.setWidth(mQuantityWidth); // 57px ~= 85dp
                    etQty.setTextSize(mDataMiniDeviceTextsize);

                    if (crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")).equalsIgnoreCase("1")) {
                        etQty.setEnabled(true);
                    } else {
                        etQty.setEnabled(false);
                        etQty.setTextColor(getResources().getColor(R.color.black));
                    }
                    etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                    etQty.setSelectAllOnFocus(true);
                    etQty.setTag("QTY_RATE");
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    if( jBillingMode ==3 || jBillingMode ==4)
                    {
                        //etQty.setOnClickListener(Qty_Rate_Click);
                        etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                        etInputValidate.ValidateDecimalInput(etQty);
                        etQty.addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            public void afterTextChanged(Editable s) {
                                Qty_Rate_Edit();
                            }
                        });
                    }


                    // Rate
                    etRate = new EditText(myContext);
                    //etRate.setWidth(70);
                    etRate.setEnabled(false);
                    //etRate.setTextSize(11);
                    etRate.setWidth(mRateWidth); // 74px ~= 110dp
                    etRate.setTextSize(mDataMiniDeviceTextsize);
                    etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Rate"))));
                    etRate.setTextColor(getResources().getColor(R.color.black));

                    // Amount
                    tvAmount = new TextView(myContext);
                    //tvAmount.setWidth(60);
                    //tvAmount.setTextSize(11);
                    tvAmount.setWidth(mAmountWidth); // 97px ~= 145dp
                    tvAmount.setTextSize(mDataMiniDeviceTextsize);

                    tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                    tvAmount.setText(
                            String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));
                    tvAmount.setTextColor(getResources().getColor(R.color.black));


                    // Sales Tax%
                    tvTaxPercent = new TextView(myContext);
                    tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxPercent")));

                    // Sales Tax Amount
                    tvTaxAmt = new TextView(myContext);
                    tvTaxAmt.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("TaxAmount"))));

                    // Discount %
                    tvDiscPercent = new TextView(myContext);
                    tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                    // Discount Amount
                    tvDiscAmt = new TextView(myContext);
                    tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                    // Dept Code
                    tvDeptCode = new TextView(myContext);
                    tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                    // Categ Code
                    tvCategCode = new TextView(myContext);
                    tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                    // Kitchen Code
                    tvKitchenCode = new TextView(myContext);
                    tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                    // Tax Type
                    tvTaxType = new TextView(myContext);
                    tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                    // Modifier Amount
                    tvModifierCharge = new TextView(myContext);
                    tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                    // Service Tax %
                    tvServiceTaxPercent = new TextView(myContext);
                    tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxPercent")));

                    // Service Tax Amount
                    tvServiceTaxAmt = new TextView(myContext);
                    tvServiceTaxAmt.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("ServiceTaxAmount"))));

                    // Service Tax Amount
                    TextView tvSupplyType = new TextView(myContext);
                    tvSupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));


                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    ImgDelete = new ImageButton(myContext);
                    // ImgDeleteKOT.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));
                    ImgDelete.setImageResource(res);
                    ImgDelete.setOnClickListener(mListener);

                    TextView tvSpace = new TextView(myContext);
                    tvSpace.setText("        ");

                    TextView tvSpace1 = new TextView(myContext);
                    tvSpace1.setText("       ");

                    TextView tvPrintKOTStatus = new TextView(myContext);
                    if(REPRINT_KOT == 1)
                        tvPrintKOTStatus.setText("1");
                    else
                        tvPrintKOTStatus.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")));

                    TextView tvIGSTRate = new TextView(BillingHomeDeliveryActivity.this);
                    tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                    TextView tvIGSTAmt = new TextView(BillingHomeDeliveryActivity.this);
                    tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                    TextView tvcessRate = new TextView(BillingHomeDeliveryActivity.this);
                    tvcessRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                    TextView tvcessAmt = new TextView(BillingHomeDeliveryActivity.this);
                    tvcessAmt.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("cessAmount"))));

                    TextView tvUOM = new TextView(BillingHomeDeliveryActivity.this);
                    tvUOM.setWidth(50);
                    tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                    TextView tvOriginalRate = new TextView(this);
                    tvOriginalRate.setText(String.format("%.2f",crsrBillItems.getDouble(crsrBillItems.getColumnIndex("OriginalRate"))));

                    TextView tvTaxableValue = new TextView(this);
                    tvTaxableValue.setText(String.format("%.2f",crsrBillItems.getDouble(crsrBillItems.getColumnIndex("TaxableValue"))));


                    // Add all text views and edit text to Item Row
                    // rowItem.addView(tvNumber);
                    rowItem.addView(Number);
                    rowItem.addView(tvName);
                    rowItem.addView(tvHSn);
                    rowItem.addView(etQty);
                    rowItem.addView(etRate);
                    rowItem.addView(tvAmount);
                    rowItem.addView(tvTaxPercent);
                    rowItem.addView(tvTaxAmt);
                    rowItem.addView(tvDiscPercent);
                    rowItem.addView(tvDiscAmt);
                    rowItem.addView(tvDeptCode);
                    rowItem.addView(tvCategCode);
                    rowItem.addView(tvKitchenCode);
                    rowItem.addView(tvTaxType);
                    rowItem.addView(tvModifierCharge);
                    rowItem.addView(tvServiceTaxPercent);
                    rowItem.addView(tvServiceTaxAmt);
                    rowItem.addView(tvSupplyType);
                    rowItem.addView(tvSpace);
                    rowItem.addView(ImgDelete);
                    rowItem.addView(tvSpace1);
                    rowItem.addView(tvPrintKOTStatus);
                    rowItem.addView(tvUOM);//22
                    rowItem.addView(tvIGSTRate);//23
                    rowItem.addView(tvIGSTAmt);//24
                    rowItem.addView(tvcessRate);//25
                    rowItem.addView(tvcessAmt);//26
                    rowItem.addView(tvOriginalRate); //27
                    rowItem.addView(tvTaxableValue);//28

                    for(int i=0;i<tblOrderItems.getChildCount();i++)
                    {
                        TableRow rr = (TableRow) tblOrderItems.getChildAt(i);
                        TextView amt = (TextView) rr.getChildAt(5);
                        System.out.println("Amount ="+amt.getText().toString());

                    }
                    // Add row to table
                    tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                } while (crsrBillItems.moveToNext());

                CalculateTotalAmount();

            } else {
                Log.d("LoadKOTItems", "No rows in cursor");
            }

        }catch ( Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Oops","Some error occured while Modifying KOT ");
        }
    }

    private void LoadModifyKOTItems_old(Cursor crsrBillItems) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvHSn, tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;

        if (crsrBillItems.moveToFirst()) {

            // Get Token number
            iTokenNumber = crsrBillItems.getInt(crsrBillItems.getColumnIndex("TokenNumber"));


            // Get Sub Udf number
            tvSubUdfValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SubUdfNumber")));

            // Get Cust Id
            if (!CustomerDetailsFilled) {
                edtCustId.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CustId")));
                Cursor crsrCustomer = db.getCustomerById(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CustId")));
                if (crsrCustomer.moveToFirst()) {
                    CustomerDetailsFilled = true;
                    edtCustPhoneNo.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    edtCustName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    edtCustAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                    etCustGSTIN.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("GSTIN")));
                }
            }
            // Display items in table
            do {
                rowItem = new TableRow(myContext);
                rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Item Number
                Number = new CheckBox(myContext);
                Number.setWidth(40);
                Number.setTextSize(0);
                Number.setTextColor(Color.TRANSPARENT);
                Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                // Item Name
                tvName = new TextView(myContext);
                tvName.setWidth(135);
                tvName.setTextSize(11);
                tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));

                //hsn code
                tvHSn = new TextView(myContext);
                tvHSn.setWidth(67); // 154px ~= 230dp
                tvHSn.setTextSize(11);
                if (GSTEnable.equalsIgnoreCase("1") && (HSNEnable_out != null) && HSNEnable_out.equals("1")) {
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                }
                // Quantity
                etQty = new EditText(myContext);
                etQty.setWidth(55);
                etQty.setTextSize(11);
                if (crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")).equalsIgnoreCase("1")) {
                    etQty.setEnabled(true);
                } else {
                    etQty.setEnabled(false);
                }
                etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                //etQty.setSelectAllOnFocus(true);
                etQty.setTag("QTY_RATE");
                //etQty.setOnClickListener(Qty_Rate_Click);
                etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                etInputValidate.ValidateDecimalInput(etQty);
                etQty.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    public void afterTextChanged(Editable s) {
                        Qty_Rate_Edit();
                    }
                });

                // Rate
                etRate = new EditText(myContext);
                etRate.setWidth(70);
                etRate.setEnabled(false);
                etRate.setTextSize(11);
                etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Rate"))));

                // Amount
                tvAmount = new TextView(myContext);
                tvAmount.setWidth(60);
                tvAmount.setTextSize(11);
                tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                tvAmount.setText(
                        String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));

                // Sales Tax%
                tvTaxPercent = new TextView(myContext);
                tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxPercent")));

                // Sales Tax Amount
                tvTaxAmt = new TextView(myContext);
                tvTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxAmount")));

                // Discount %
                tvDiscPercent = new TextView(myContext);
                tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                // Discount Amount
                tvDiscAmt = new TextView(myContext);
                tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                // Dept Code
                tvDeptCode = new TextView(myContext);
                tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                // Categ Code
                tvCategCode = new TextView(myContext);
                tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                // Kitchen Code
                tvKitchenCode = new TextView(myContext);
                tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                // Tax Type
                tvTaxType = new TextView(myContext);
                tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                // Modifier Amount
                tvModifierCharge = new TextView(myContext);
                tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                // Service Tax %
                tvServiceTaxPercent = new TextView(myContext);
                tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxPercent")));

                // Service Tax Amount
                tvServiceTaxAmt = new TextView(myContext);
                tvServiceTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxAmount")));

                // SupplyType
                TextView SupplyType = new TextView(myContext);
                SupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));
                SupplyType.setWidth(30);

                TextView tvIGSTRate = new TextView(this);
                tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                TextView tvIGSTAmt = new TextView(this);
                tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                TextView tvcessRate = new TextView(this);
                tvcessRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                TextView tvcessAmt = new TextView(this);
                tvcessAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessAmount")));


                TextView tvUOM = new TextView(this);
                tvUOM.setWidth(50);
                tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                // rowItem.setClickable(true);
                // rowItem.setOnClickListener(new OnClickListener() {
                // public void onClick(View view) {
                // TableRow tablerow = (TableRow) view;
                // TextView sample = (TextView) tablerow.getChildAt(0);
                //
                // Toast.makeText(myContext, sample.getText().toString(),
                // Toast.LENGTH_LONG).show();
                // }
                // });
                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                ImgDelete = new ImageButton(myContext);
                // ImgDeleteKOT.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));
                ImgDelete.setImageResource(res);
                ImgDelete.setOnClickListener(mListener);

                TextView tvSpace = new TextView(myContext);
                tvSpace.setText("        ");

                TextView tvSpace1 = new TextView(myContext);
                tvSpace1.setText("       ");

                TextView tvPrintKOTStatus = new TextView(myContext);
                tvPrintKOTStatus.setText("1");

                // Add all text views and edit text to Item Row
                // rowItem.addView(tvNumber);
                rowItem.addView(Number);
                rowItem.addView(tvName);
                rowItem.addView(tvHSn);
                rowItem.addView(etQty);
                rowItem.addView(etRate);
                rowItem.addView(tvAmount);
                rowItem.addView(tvTaxPercent);
                rowItem.addView(tvTaxAmt);
                rowItem.addView(tvDiscPercent);
                rowItem.addView(tvDiscAmt);
                rowItem.addView(tvDeptCode);
                rowItem.addView(tvCategCode);
                rowItem.addView(tvKitchenCode);
                rowItem.addView(tvTaxType);
                rowItem.addView(tvModifierCharge);
                rowItem.addView(tvServiceTaxPercent);
                rowItem.addView(tvServiceTaxAmt);
                rowItem.addView(SupplyType);
                rowItem.addView(tvSpace);
                rowItem.addView(ImgDelete);
                rowItem.addView(tvSpace1);
                rowItem.addView(tvPrintKOTStatus);
                rowItem.addView(tvUOM);//22
                rowItem.addView(tvIGSTRate);//23
                rowItem.addView(tvIGSTAmt);//24
                rowItem.addView(tvcessRate);//25
                rowItem.addView(tvcessAmt);//26


                // Add row to table
                tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            } while (crsrBillItems.moveToNext());

            CalculateTotalAmount();

        } else {
            Log.d("LoadKOTItems", "No rows in cursor");
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {

        public void onClick(View v) {

            final View v1 = v;
            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);


            AuthorizationDialog
                    .setTitle("Confirmation")
                    .setMessage("Are you sure to delete this item")
                    .setIcon(R.drawable.ic_launcher)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            TableRow tr = (TableRow) v1.getParent();
                            TextView ItemNumber = (TextView) tr.getChildAt(0);

                            DeletedKOT objDeletedKOT = new DeletedKOT();
                            objDeletedKOT.setReason("Billing Mode is 3 or 4");
                            objDeletedKOT.setEmployeeId(0);
                            objDeletedKOT.setTableNumber(0);//Integer.parseInt(tvTableNumber.getText().toString()));
                            objDeletedKOT.setSubUdfNumber(Integer.parseInt(tvSubUdfValue.getText().toString()));
                            objDeletedKOT.setTokenNumber(iTokenNumber);
                            objDeletedKOT.setTime(String.format("%tR", Time));
                            long lResult = db.addDeletedKOT_new(objDeletedKOT);
                            Log.v("VoidKOT", "Deleted KOT added, Row ID:" + lResult);
                            lResult = db.deleteKOTItemsByItemToken_new(ItemNumber.getText().toString(), iTokenNumber,
                                    Integer.parseInt(tvSubUdfValue.getText().toString()));

                            CustomerDetailsFilled = true;
                            DisplayModifyKOT();

                        }
                    })
                    .show();
        }
    };

    /*************************************************************************************************************************************
     * Updates the item stock quantity after every transaction
     *
     * @param crsrUpdateStock : Cursor containing Item detail
     * @param Quantity        : Item quantity to be subtracted from current item quantity
     *************************************************************************************************************************************/
    private void UpdateItemStock(Cursor crsrUpdateStock, float Quantity) {
        int iResult = 0;
        float fCurrentStock = 0, fNewStock = 0;

        // Get current stock of item
        fCurrentStock = crsrUpdateStock.getFloat(crsrUpdateStock.getColumnIndex("Quantity"));

        // New Stock
        fNewStock = fCurrentStock - Quantity;

        // Update new stock for item
        iResult = db.updateItemStock(crsrUpdateStock.getInt(crsrUpdateStock.getColumnIndex("MenuCode")),
                fNewStock);

        Log.d("UpdateItemStock", "Updated Rows:" + iResult);

    }

    /*************************************************************************************************************************************
     * Insert each bill item to bill items database table
     *************************************************************************************************************************************/
    private void InsertBillItems() {

        // Inserted Row Id in database table
        long lResult = 0;

        // Bill item object
        BillItem objBillItem;

        // Reset TotalItems count
        iTotalItems = 0;

        Cursor crsrUpdateItemStock = null;

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            objBillItem = new BillItem();

            TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);

            // Increment Total item count if row is not empty
            if (RowBillItem.getChildCount() > 0) {
                iTotalItems++;
            }



            // Bill Number
            objBillItem.setBillNumber(tvBillNumber.getText().toString());
            Log.d("InsertBillItems", "InvoiceNo:" + tvBillNumber.getText().toString());

            // richa_2012
            //BillingMode
            objBillItem.setBillingMode(String.valueOf(jBillingMode));
            Log.d("InsertBillItems", "Billing Mode :" + String.valueOf(jBillingMode));

            // Item Number
            if (RowBillItem.getChildAt(0) != null) {
                CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                objBillItem.setItemNumber(Integer.parseInt(ItemNumber.getText().toString()));
                Log.d("InsertBillItems", "Item Number:" + ItemNumber.getText().toString());

                crsrUpdateItemStock = dbBillScreen.getItem(Integer.parseInt(ItemNumber.getText().toString()));
            }

            // Item Name
            if (RowBillItem.getChildAt(1) != null) {
                TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                objBillItem.setItemName(ItemName.getText().toString());
                Log.d("InsertBillItems", "Item Name:" + ItemName.getText().toString());
            }

            if (RowBillItem.getChildAt(2) != null) {
                TextView HSN = (TextView) RowBillItem.getChildAt(2);
                objBillItem.setHSNCode(HSN.getText().toString());
                Log.d("InsertBillItems", "Item HSN:" + HSN.getText().toString());
            }

            // Quantity
            double qty_d = 0.00;
            if (RowBillItem.getChildAt(3) != null) {
                EditText Quantity = (EditText) RowBillItem.getChildAt(3);
                String qty_str = Quantity.getText().toString();
                if(qty_str==null || qty_str.equals(""))
                {
                    Quantity.setText("0.00");
                }else
                {
                    qty_d = Double.parseDouble(qty_str);
                }

                objBillItem.setQuantity(Float.parseFloat(String.format("%.2f",qty_d)));
                Log.d("InsertBillItems", "Quantity:" + Quantity.getText().toString());

                if (crsrUpdateItemStock!=null && crsrUpdateItemStock.moveToFirst()) {
                    // Check if item's bill with stock enabled update the stock
                    // quantity
                    if (BillwithStock == 1) {
                        UpdateItemStock(crsrUpdateItemStock, Float.parseFloat(Quantity.getText().toString()));
                    }


                }
            }

            // Rate
            double rate_d = 0.00;
            if (RowBillItem.getChildAt(4) != null) {
                EditText Rate = (EditText) RowBillItem.getChildAt(4);
                String rate_str = Rate.getText().toString();
                if((rate_str==null || rate_str.equals("")))
                {
                    Rate.setText("0.00");
                }else
                {
                    rate_d = Double.parseDouble(rate_str);
                }

                objBillItem.setValue(Float.parseFloat(String.format("%.2f",rate_d)));
                Log.d("InsertBillItems", "Rate:" + Rate.getText().toString());
            }


            // oRIGINAL rate in case of reverse tax
            if (RowBillItem.getChildAt(27) != null) {
                TextView originalRate = (TextView) RowBillItem.getChildAt(27);
                objBillItem.setOriginalRate(Double.parseDouble(originalRate.getText().toString()));
                Log.d("InsertBillItems", "Original Rate  :" + objBillItem.getOriginalRate());
            }
            if (RowBillItem.getChildAt(28) != null) {
                TextView TaxableValue = (TextView) RowBillItem.getChildAt(28);
                objBillItem.setTaxableValue(Double.parseDouble(TaxableValue.getText().toString()));
                Log.d("InsertBillItems", "TaxableValue  :" + objBillItem.getTaxableValue());
            }

            // Amount
            if (RowBillItem.getChildAt(5) != null) {
                TextView Amount = (TextView) RowBillItem.getChildAt(5);
                objBillItem.setAmount(Double.parseDouble(Amount.getText().toString()));
                String reverseTax = "";
                if (!(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1)) { // reverse tax
                    reverseTax = " (Reverse Tax)";
                    objBillItem.setIsReverTaxEnabled("YES");
                }else
                {
                    objBillItem.setIsReverTaxEnabled("NO");
                }
                Log.d("InsertBillItems", "Amount :" + objBillItem.getAmount()+reverseTax);
            }

            // oRIGINAL rate in case of reverse tax
            if (RowBillItem.getChildAt(27) != null) {
                TextView originalRate = (TextView) RowBillItem.getChildAt(27);
                objBillItem.setOriginalRate(Double.parseDouble(originalRate.getText().toString()));
                Log.d("InsertBillItems", "Original Rate  :" + objBillItem.getOriginalRate());
            }

            // Discount %
            if (RowBillItem.getChildAt(8) != null) {
                TextView DiscountPercent = (TextView) RowBillItem.getChildAt(8);
                objBillItem.setDiscountPercent(Float.parseFloat(DiscountPercent.getText().toString()));
                Log.d("InsertBillItems", "Disc %:" + DiscountPercent.getText().toString());
            }

            // Discount Amount
            if (RowBillItem.getChildAt(9) != null) {
                TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                objBillItem.setDiscountAmount(Float.parseFloat(DiscountAmount.getText().toString()));
                Log.d("InsertBillItems", "Disc Amt:" + DiscountAmount.getText().toString());
                // fTotalDiscount += Float.parseFloat(DiscountAmount.getText().toString());
            }


            // Service Tax Percent
            float sgatTax = 0;
            if (RowBillItem.getChildAt(15) != null) {
                TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                sgatTax = Float.parseFloat(ServiceTaxPercent.getText().toString());
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTAmount(0.00f);
                    Log.d("InsertBillItems", "SGST Tax %: 0");

                } else {
                    objBillItem.setSGSTRate(Float.parseFloat(ServiceTaxPercent.getText().toString()));
                    Log.d("InsertBillItems", "SGST Tax %: " + objBillItem.getSGSTRate());
                }
            }

            // Service Tax Amount
            double sgstAmt = 0;
            if (RowBillItem.getChildAt(16) != null) {
                TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                sgstAmt = Double.parseDouble(ServiceTaxAmount.getText().toString());
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTAmount(0);
                    Log.d("InsertBillItems", "SGST Amount : 0" );

                } else {
                    objBillItem.setSGSTAmount(Double.parseDouble(String.format("%.2f", sgstAmt)));
                    Log.d("InsertBillItems", "SGST Amount : " + objBillItem.getSGSTAmount());
                }
            }

            // Sales Tax %
            if (RowBillItem.getChildAt(6) != null) {
                TextView SalesTaxPercent = (TextView) RowBillItem.getChildAt(6);
                float cgsttax = (Float.parseFloat(SalesTaxPercent.getText().toString()));
                if (chk_interstate.isChecked()) {
                    //objBillItem.setIGSTRate(Float.parseFloat(String.format("%.2f", cgsttax + sgatTax)));
                    // Log.d("InsertBillItems", " IGST Tax %: " + objBillItem.getIGSTRate());
                    objBillItem.setCGSTRate(0.00f);
                    Log.d("InsertBillItems", " CGST Tax %: 0.00");
                }else{
                    // objBillItem.setIGSTRate(0.00f);
                    // Log.d("InsertBillItems", " IGST Tax %: 0.00");
                    objBillItem.setCGSTRate(Float.parseFloat(String.format("%.2f",Float.parseFloat(SalesTaxPercent.getText().toString()))));
                    Log.d("InsertBillItems", " CGST Tax %: " + SalesTaxPercent.getText().toString());
                }
            }
            // Sales Tax Amount
            if (RowBillItem.getChildAt(7) != null) {
                TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                double cgstAmt = (Double.parseDouble(SalesTaxAmount.getText().toString()));
                if (chk_interstate.isChecked()) {
                    //objBillItem.setIGSTAmount(Float.parseFloat(String.format("%.2f",cgstAmt+sgstAmt)));
                    //Log.d("InsertBillItems", "IGST Amt: " + objBillItem.getIGSTAmount());
                    objBillItem.setCGSTAmount(0);
                    Log.d("InsertBillItems", "CGST Amt: 0");
                } else {
                    // objBillItem.setIGSTAmount(0.00f);
                    //Log.d("InsertBillItems", "IGST Amt: 0");
                    objBillItem.setCGSTAmount(Double.parseDouble(String.format("%.2f", cgstAmt)));
                    Log.d("InsertBillItems", "CGST Amt: " + SalesTaxAmount.getText().toString());
                }
            }
            // IGST Tax %
            if (RowBillItem.getChildAt(23) != null) {
                TextView IGSTTaxPercent = (TextView) RowBillItem.getChildAt(23);
                float igsttax = (Float.parseFloat(IGSTTaxPercent.getText().toString()));
                if (chk_interstate.isChecked()) {
                    objBillItem.setIGSTRate(Float.parseFloat(String.format("%.2f", igsttax)));
                    Log.d("InsertBillItems", " IGST Tax %: " + objBillItem.getIGSTRate());
                }else{
                    objBillItem.setIGSTRate(0.00f);
                    Log.d("InsertBillItems", " IGST Tax %: 0.00");
                }
            }
            // IGST Tax Amount
            if (RowBillItem.getChildAt(24) != null) {
                TextView IGSTTaxAmount = (TextView) RowBillItem.getChildAt(24);
                float igstAmt = (Float.parseFloat(IGSTTaxAmount.getText().toString()));
                if (chk_interstate.isChecked()) {
                    objBillItem.setIGSTAmount(Float.parseFloat(String.format("%.2f",igstAmt)));
                    Log.d("InsertBillItems", "IGST Amt: " + objBillItem.getIGSTAmount());
                } else {
                    objBillItem.setIGSTAmount(0.00f);
                    Log.d("InsertBillItems", "IGST Amt: 0");
                }
            }

            // cess Tax %
            if (RowBillItem.getChildAt(25) != null) {
                TextView cessTaxPercent = (TextView) RowBillItem.getChildAt(25);
                float cesstax = (Float.parseFloat(cessTaxPercent.getText().toString()));
                objBillItem.setCessRate(Float.parseFloat(String.format("%.2f", cesstax)));
                Log.d("InsertBillItems", " cess Tax %: " + objBillItem.getCessRate());
            }
            // cess Tax Amount
            if (RowBillItem.getChildAt(26) != null) {
                TextView cessTaxAmount = (TextView) RowBillItem.getChildAt(26);
                double cessAmt = (Double.parseDouble(cessTaxAmount.getText().toString()));
                objBillItem.setCessAmount(Double.parseDouble(String.format("%.2f",cessAmt)));
                Log.d("InsertBillItems", "cess Amt: " + objBillItem.getCessAmount());
            }

            // Discount %
            if (RowBillItem.getChildAt(8) != null) {
                TextView DiscountPercent = (TextView) RowBillItem.getChildAt(8);
                objBillItem.setDiscountPercent(Float.parseFloat(DiscountPercent.getText().toString()));
                Log.d("InsertBillItems", "Disc %:" + DiscountPercent.getText().toString());
            }

            // Discount Amount
            if (RowBillItem.getChildAt(9) != null) {
                TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                objBillItem.setDiscountAmount(Float.parseFloat(DiscountAmount.getText().toString()));
                Log.d("InsertBillItems", "Disc Amt:" + DiscountAmount.getText().toString());

                // fTotalDiscount += Float.parseFloat(DiscountAmount.getText().toString());
            }

            // Department Code
            if (RowBillItem.getChildAt(10) != null) {
                TextView DeptCode = (TextView) RowBillItem.getChildAt(10);
                objBillItem.setDeptCode(Integer.parseInt(DeptCode.getText().toString()));
                Log.d("InsertBillItems", "Dept Code:" + DeptCode.getText().toString());
            }

            // Category Code
            if (RowBillItem.getChildAt(11) != null) {
                TextView CategCode = (TextView) RowBillItem.getChildAt(11);
                objBillItem.setCategCode(Integer.parseInt(CategCode.getText().toString()));
                Log.d("InsertBillItems", "Categ Code:" + CategCode.getText().toString());
            }

            // Kitchen Code
            if (RowBillItem.getChildAt(12) != null) {
                TextView KitchenCode = (TextView) RowBillItem.getChildAt(12);
                objBillItem.setKitchenCode(Integer.parseInt(KitchenCode.getText().toString()));
                Log.d("InsertBillItems", "Kitchen Code:" + KitchenCode.getText().toString());
            }

            // Tax Type
            if (RowBillItem.getChildAt(13) != null) {
                TextView TaxType = (TextView) RowBillItem.getChildAt(13);
                objBillItem.setTaxType(Integer.parseInt(TaxType.getText().toString()));
                Log.d("InsertBillItems", "Tax Type:" + TaxType.getText().toString());
            }

            // Modifier Amount
            if (RowBillItem.getChildAt(14) != null) {
                TextView ModifierAmount = (TextView) RowBillItem.getChildAt(14);
                objBillItem.setModifierAmount(Float.parseFloat(ModifierAmount.getText().toString()));
                Log.d("InsertBillItems", "Modifier Amt:" + ModifierAmount.getText().toString());
            }



            if (RowBillItem.getChildAt(17) != null) {
                TextView SupplyType = (TextView) RowBillItem.getChildAt(17);
                objBillItem.setSupplyType(SupplyType.getText().toString());
                Log.d("InsertBillItems", "SupplyType:" + SupplyType.getText().toString());
                /*if (GSTEnable.equals("1")) {
                    objBillItem.setSupplyType(SupplyType.getText().toString());
                    Log.d("InsertBillItems", "SupplyType:" + SupplyType.getText().toString());
                } else {
                    objBillItem.setSupplyType("");
                }*/
            }
            if (RowBillItem.getChildAt(22) != null) {
                TextView UOM = (TextView) RowBillItem.getChildAt(22);
                objBillItem.setUom(UOM.getText().toString());
                Log.d("InsertBillItems", "UOM:" + UOM.getText().toString());

            }

            // subtotal
            double subtotal = objBillItem.getAmount() + objBillItem.getIGSTAmount() + objBillItem.getCGSTAmount() + objBillItem.getSGSTAmount();
            objBillItem.setSubTotal(subtotal);
            Log.d("InsertBillItems", "Sub Total :" + subtotal);

            // Date
            String date_today = tvDate.getText().toString();
            //Log.d("Date ", date_today);
            try {
                Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
                objBillItem.setInvoiceDate(String.valueOf(date1.getTime()));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            // cust name
            String custname = edtCustName.getText().toString();
            objBillItem.setCustName(custname);
            Log.d("InsertBillItems", "CustName :" + custname);

            String custgstin = etCustGSTIN.getText().toString().trim().toUpperCase();
            objBillItem.setGSTIN(custgstin);
            Log.d("InsertBillItems", "custgstin :" + custgstin);

            if (chk_interstate.isChecked()) {
                String str = spnr_pos.getSelectedItem().toString();
                int length = str.length();
                String sub = "";
                if (length > 0) {
                    sub = str.substring(length - 2, length);
                }
                objBillItem.setCustStateCode(sub);
                Log.d("InsertBillItems", "CustStateCode :" + sub+" - "+str);
            } else {
                objBillItem.setCustStateCode(db.getOwnerPOS_counter());// to be retrieved from database later -- richa to do
                Log.d("InsertBillItems", "CustStateCode :"+objBillItem.getCustStateCode());
            }


            // BusinessType
            if (etCustGSTIN.getText().toString().equals("")) {
                objBillItem.setBusinessType("B2C");
            } else // gstin present means b2b bussiness
            {
                objBillItem.setBusinessType("B2B");
            }

            Log.d("InsertBillItems", "BusinessType : " + objBillItem.getBusinessType());

            // richa to do - hardcoded b2b bussinies type
            //objBillItem.setBusinessType("B2B");
            if (jBillingMode == 4) {
                objBillItem.setBillStatus(2);
                Log.d("InsertBillItem", "Bill Status:2");
            } else {
                objBillItem.setBillStatus(1);
                Log.d("InsertBillItem", "Bill Status:1");
            }

            lResult = db.addBillItems(objBillItem);
            Log.d("InsertBillItem", "Bill item inserted at position:" + lResult);
        }
    }

    /*************************************************************************************************************************************
     * Inserts bill details to bill detail database table
     *
     * @param TenderType : Type of tender, 1 - Pay cash, 2 - Tender Screen payment
     *************************************************************************************************************************************/
    private void InsertBillDetail(int TenderType) {

        // Inserted Row Id in database table
        long lResult = 0;

        // BillDetail object
        BillDetail objBillDetail;

        objBillDetail = new BillDetail();

        // Date
        //objBillDetail.setDate(String.valueOf(d.getTime()));
        try {
            String date_today = tvDate.getText().toString();
            //Log.d("Date ", date_today);
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
            objBillDetail.setDate(String.valueOf(date1.getTime()));
            Log.d("InsertBillDetail", "Date:" + objBillDetail.getDate());
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        // Time
        //objBillDetail.setTime(String.format("%tR", Time));
        String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
        objBillDetail.setTime(strTime);
        Log.d("InsertBillDetail", "Time:" + strTime);

        // Bill Number
        objBillDetail.setBillNumber(Integer.parseInt(tvBillNumber.getText().toString()));
        Log.d("InsertBillDetail", "Bill Number:" + tvBillNumber.getText().toString());

        // richa_2012
        //BillingMode
        objBillDetail.setBillingMode(String.valueOf(jBillingMode));
        Log.d("InsertBillDetail", "Billing Mode :" + String.valueOf(jBillingMode));

        // custStateCode
        if (chk_interstate.isChecked()) {
            String str = spnr_pos.getSelectedItem().toString();
            int length = str.length();
            String sub = "";
            if (length > 0) {
                sub = str.substring(length - 2, length);
            }
            objBillDetail.setCustStateCode(sub);
            Log.d("InsertBillDetail", "CustStateCode :" + sub+" - "+str);
        } else {
            String userPOS = db.getOwnerPOS_counter();
            objBillDetail.setCustStateCode(userPOS);
            Log.d("InsertBillDetail", "CustStateCode : "+objBillDetail.getCustStateCode());
        }


        objBillDetail.setPOS(db.getOwnerPOS_counter());// to be retrieved from database later -- richa to do
        Log.d("InsertBillDetail", "POS : "+objBillDetail.getPOS());


        // Total Items
        objBillDetail.setTotalItems(iTotalItems);
        Log.d("InsertBillDetail", "Total Items:" + iTotalItems);

        // Bill Amount
        String billamt_temp = String.format("%.2f",Double.parseDouble(tvBillAmount.getText().toString()));
//        objBillDetail.setBillAmount(Float.parseFloat(billamt_temp));
        objBillDetail.setdBillAmount(Double.parseDouble(billamt_temp));
        Log.d("InsertBillDetail", "Bill Amount:" + tvBillAmount.getText().toString());

        // Discount Percentage
        objBillDetail.setTotalDiscountPercentage(Float.parseFloat(tvDiscountPercentage.getText().toString()));
        Log.d("InsertBillDetail", "Discount Percentage:" + objBillDetail.getTotalDiscountPercentage());

        // Discount Amount
        // Discount Amount
        //    if(ItemwiseDiscountEnabled ==1)
        calculateDiscountAmount();
        float discount = Float.parseFloat(tvDiscountAmount.getText().toString());
        //objBillDetail.setTotalDiscountAmount(discount);
        objBillDetail.setTotalDiscountAmount(fTotalDiscount);
        Log.d("InsertBillDetail", "Total Discount:" + discount);

        // Sales Tax Amount
        if (chk_interstate.isChecked()) {
            objBillDetail.setIGSTAmount(Float.parseFloat(String.format("%.2f",Float.parseFloat(tvIGSTValue.getText().toString()))));
            objBillDetail.setCGSTAmount(0.00f);
            objBillDetail.setSGSTAmount(0.00f);
        } else {
            objBillDetail.setIGSTAmount(0.00f);
            objBillDetail.setCGSTAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvTaxTotal.getText().toString()))));
            objBillDetail.setSGSTAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvServiceTaxTotal.getText().toString()))));
        }
        Log.d("InsertBillDetail", "IGSTAmount : " + objBillDetail.getIGSTAmount());
        Log.d("InsertBillDetail", "CGSTAmount : " + objBillDetail.getCGSTAmount());
        Log.d("InsertBillDetail", "SGSTAmount : " + objBillDetail.getSGSTAmount());

        objBillDetail.setCessAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvcessValue.getText().toString()))));
        Log.d("InsertBillDetail", "cessAmount : " + objBillDetail.getCessAmount());
        // Delivery Charge
        objBillDetail.setDeliveryCharge(Float.parseFloat(txtOthercharges.getText().toString()));
        Log.d("InsertBillDetail", "Delivery Charge:"+objBillDetail.getDeliveryCharge());


        // Taxable Value
        double taxval_f = Double.parseDouble(tvSubTotal.getText().toString());
        objBillDetail.setAmount(String.format("%.2f", taxval_f));
        Log.d("InsertBillDetail", "Taxable Value:" + taxval_f);

        /*float cgstamt_f = 0, sgstamt_f = 0;
        if (tvTaxTotal.getText().toString().equals("") == false) {
            cgstamt_f = Float.parseFloat(tvTaxTotal.getText().toString());
        }
        if (tvServiceTaxTotal.getText().toString().equals("") == false) {
            sgstamt_f = Float.parseFloat(tvServiceTaxTotal.getText().toString());
        }*/


        double subtot_f = taxval_f + objBillDetail.getIGSTAmount() + objBillDetail.getCGSTAmount()+ objBillDetail.getSGSTAmount();
        objBillDetail.setSubTotal(subtot_f);
        Log.d("InsertBillDetail", "Sub Total :" + subtot_f);

        // cust name
        String custname = edtCustName.getText().toString();
        objBillDetail.setCustname(custname);
        Log.d("InsertBillDetail", "CustName :" + custname);
        // cust gstin
        String custGSTIN = etCustGSTIN.getText().toString().trim().toUpperCase();
        objBillDetail.setGSTIN(custGSTIN);
        Log.d("InsertBillDetail", "CustGSTIN :" + custGSTIN);

       /* // cust StateCode
        if (chk_interstate.isChecked()) {
            String str = spnr_pos.getSelectedItem().toString();
            int length = str.length();
            String sub = "";
            if (length > 0) {
                sub = str.substring(length - 2, length);
            }
            objBillDetail.setCustStateCode(sub);
            Log.d("InsertBillDetail", "CustStateCode :" + sub+" - "+str);
        } else {
            objBillDetail.setCustStateCode("29");// to be retrieved from database later -- richa to do
            Log.d("InsertBillDetail", "CustStateCode :"+objBillDetail.getCustStateCode());
        }*/
        /*String str = spnr_pos.getSelectedItem().toString();
        int length = str.length();
        String custStateCode = "";
        if (length > 0) {
            custStateCode = str.substring(length - 2, length);
        }*/
        /*objBillDetail.setCustStateCode(custStateCode);
        Log.d("InsertBillDetail", "CustStateCode :" + custStateCode);*/

        // BusinessType
        if (etCustGSTIN.getText().toString().equals("")) {
            objBillDetail.setBusinessType("B2C");
        } else // gstin present means b2b bussiness
        {
            objBillDetail.setBusinessType("B2B");
        }
        //objBillDetail.setBusinessType("B2C");
        Log.d("InsertBillDetail", "BusinessType : " + objBillDetail.getBusinessType());
        // Payment types
        if (TenderType == 1) {
            // Cash Payment
            objBillDetail.setCashPayment(Float.parseFloat(tvBillAmount.getText().toString()));
            Log.d("InsertBillDetail", "Cash:" + tvBillAmount.getText().toString());

            // Card Payment
            objBillDetail.setCardPayment(fCardPayment);
            Log.d("InsertBillDetail", "Card:" + fCardPayment);

            // Coupon Payment
            objBillDetail.setCouponPayment(fCouponPayment);
            Log.d("InsertBillDetail", "Coupon:" + fCouponPayment);

            // PettyCash Payment
//            objBillDetail.setPettyCashPayment(fPettCashPayment);
//            Log.d("InsertBillDetail", "PettyCash:" + fPettCashPayment);

            objBillDetail.setdPettyCashPayment(dPettCashPayment);
            Log.d("InsertBillDetail", "PettyCash:" + dPettCashPayment);

            // Wallet Payment
            objBillDetail.setWalletAmount(fWalletPayment);
            Log.d("InsertBillDetail", "Wallet:" + fWalletPayment);

            // PaidTotal Payment
            objBillDetail.setPaidTotalPayment(fPaidTotalPayment);
            Log.d("InsertBillDetail", "PaidTotalPayment:" + fPaidTotalPayment);

            // Change Payment
            objBillDetail.setChangePayment(dChangePayment);
            Log.d("InsertBillDetail", "ChangePayment:" + dChangePayment);

            objBillDetail.setfRoundOff(fRoundOfValue);
            Log.d("InsertBillDetail", "RoundOfValue:" + fRoundOfValue);

        } else if (TenderType == 2) {

            if (PrintBillPayment == 1) {
                // Cash Payment
                objBillDetail.setCashPayment(Float.parseFloat(tvBillAmount.getText().toString()));
                Log.d("InsertBillDetail", "Cash:" + Float.parseFloat(tvBillAmount.getText().toString()));

                // Card Payment
                objBillDetail.setCardPayment(fCardPayment);
                Log.d("InsertBillDetail", "Card:" + fCardPayment);

                // Coupon Payment
                objBillDetail.setCouponPayment(fCouponPayment);
                Log.d("InsertBillDetail", "Coupon:" + fCouponPayment);

                // PettyCash Payment
//                objBillDetail.setPettyCashPayment(fPettCashPayment);
//                Log.d("InsertBillDetail", "PettyCash:" + fPettCashPayment);

                objBillDetail.setdPettyCashPayment(dPettCashPayment);
                Log.d("InsertBillDetail", "PettyCash:" + dPettCashPayment);

                // Wallet Payment
                objBillDetail.setWalletAmount(fWalletPayment);
                Log.d("InsertBillDetail", "Wallet:" + fWalletPayment);

                // PaidTotal Payment
                objBillDetail.setPaidTotalPayment(fPaidTotalPayment);
                Log.d("InsertBillDetail", "PaidTotalPayment:" + fPaidTotalPayment);

                // Change Payment
                objBillDetail.setChangePayment(dChangePayment);
                Log.d("InsertBillDetail", "ChangePayment:" + dChangePayment);
                objBillDetail.setfRoundOff(fRoundOfValue);
                Log.d("InsertBillDetail", "RoundOfValue:" + fRoundOfValue);
            } else {
                // Cash Payment
                objBillDetail.setCashPayment(fCashPayment);
                Log.d("InsertBillDetail", "Cash:" + fCashPayment);

                // Card Payment
                objBillDetail.setCardPayment(fCardPayment);
                Log.d("InsertBillDetail", "Card:" + fCardPayment);

                // Coupon Payment
                objBillDetail.setCouponPayment(fCouponPayment);
                Log.d("InsertBillDetail", "Coupon:" + fCouponPayment);

                // PettyCash Payment
//                objBillDetail.setPettyCashPayment(fPettCashPayment);
//                Log.d("InsertBillDetail", "PettyCash:" + fPettCashPayment);

                objBillDetail.setdPettyCashPayment(dPettCashPayment);
                Log.d("InsertBillDetail", "PettyCash:" + dPettCashPayment);

                // Wallet Payment
                objBillDetail.setWalletAmount(fWalletPayment);
                Log.d("InsertBillDetail", "Wallet:" + fWalletPayment);

                // PaidTotal Payment
                objBillDetail.setPaidTotalPayment(fPaidTotalPayment);
                Log.d("InsertBillDetail", "PaidTotalPayment:" + fPaidTotalPayment);

                // Change Payment
                objBillDetail.setChangePayment(dChangePayment);
                Log.d("InsertBillDetail", "ChangePayment:" + dChangePayment);
                objBillDetail.setfRoundOff(fRoundOfValue);
                Log.d("InsertBillDetail", "RoundOfValue:" + fRoundOfValue);
            }
        }

        // Reprint Count
        objBillDetail.setReprintCount(0);
        Log.d("InsertBillDetail", "Reprint Count:0");

        // Bill Status
        if (jBillingMode == 4) {
            objBillDetail.setBillStatus(2);
            Log.d("InsertBillDetail", "Bill Status:2");
        } else {
            objBillDetail.setBillStatus(1);
            Log.d("InsertBillDetail", "Bill Status:1");
        }

        // Employee Id (Waiter / Rider)
        if (jBillingMode == 1 ) {
//            objBillDetail.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));
//            Log.d("InsertBillDetail", "EmployeeId:" + tvWaiterNumber.getText().toString());
        } else {
            objBillDetail.setEmployeeId(0);
            Log.d("InsertBillDetail", "EmployeeId:0");
        }

        // Customer Id

        objBillDetail.setCustId(Integer.valueOf(edtCustId.getText().toString()));
        Log.d("InsertBillDetail", "Customer Id:" + Integer.valueOf(edtCustId.getText().toString()));

        // User Id
        objBillDetail.setUserId(strUserId);
        Log.d("InsertBillDetail", "UserID:" + strUserId);

        lResult = db.addBilll(objBillDetail, etCustGSTIN.getText().toString().trim().toUpperCase());
        Log.d("InsertBill", "Bill inserted at position:" + lResult);
        //lResult = dbBillScreen.updateBill(objBillDetail);

        if (edtCustId.getText().toString().equalsIgnoreCase("") || edtCustId.getText().toString().equalsIgnoreCase("0")) {
        } else {
            iCustId = Integer.valueOf(edtCustId.getText().toString());
            double fTotalTransaction = db.getCustomerTotalTransaction(iCustId);
            double fCreditAmount = db.getCustomerCreditAmount(iCustId);
            fCreditAmount = fCreditAmount - Double.parseDouble(tvBillAmount.getText().toString());
            fTotalTransaction += Double.parseDouble(tvBillAmount.getText().toString());

            long lResult1 = db.updateCustomerTransaction(iCustId,
                    Double.parseDouble(tvBillAmount.getText().toString()), fTotalTransaction, fCreditAmount);
        }

        // Bill No Reset Configuration
        long Result2 = db.UpdateBillNoResetInvoiceNos(Integer.parseInt(tvBillNumber.getText().toString()));
    }

    /*************************************************************************************************************************************
     * Invokes InsertBillItems() and InsertBillDetail() functions
     *
     * @param TenderType  : Type of tender, 1 - Pay cash, 2 - Tender Screen payment
     * @param isPrintBill : True if bill needs to be printed else False
     *************************************************************************************************************************************/
    private void l(int TenderType, boolean isPrintBill) { // TenderType:
        // 1=PayCash
        // 2=Tender

        // Insert all bill items to database
        InsertBillItems();

        // Insert bill details to database
        InsertBillDetail(TenderType);

        /*if (isPrintBill) {
            // Print bill
            PrintBill();
        }*/
    }

    /*************************************************************************************************************************************
     * Updates complimentary bill details in database
     *
     * @param BillNumber          : Complimentary bill number
     * @param PaidAmount          : Amount paid for the complimentary bill
     * @param ComplimentaryReason : Reason for giving complimentary bill
     *************************************************************************************************************************************/
    private void SaveComplimentaryBill(int BillNumber, float PaidAmount, String ComplimentaryReason) {
        long lResult = 0;

        ComplimentaryBillDetail objComplimentaryBillDetail = new ComplimentaryBillDetail();

        // Set bill number
        objComplimentaryBillDetail.setBillNumber(BillNumber);

        // Set complimentary reason
        objComplimentaryBillDetail.setComplimentaryReason(ComplimentaryReason);

        // Set paid amount
        objComplimentaryBillDetail.setPaidAmount(PaidAmount);

        lResult = db.addComplimentaryBillDetails(objComplimentaryBillDetail);

        Log.v("SaveComplimentaryBill", "Complimentary Bill inserted at Row:" + lResult);
    }

    /*************************************************************************************************************************************
     * Gets quantity for item from weighing scale
     *
     * @return Returns Weighing scale reading represented by string
     *************************************************************************************************************************************/
    private String getQuantityFromWeighScale() {
        // Read data from weigh scale through serial port

        // Convert data to string and return the value to calling function

        return "1";
    }

    private int getIndex_pos(String substring){

        int index = 0;
        for (int i = 0; index==0 && i < spnr_pos.getCount(); i++){

            if (spnr_pos.getItemAtPosition(i).toString().contains(substring)){
                index = i;
            }

        }

        return index;

    }
    private String getState_pos(String substring){

        String  index = "";
        for (int i = 0; i < spnr_pos.getCount(); i++){

            if (spnr_pos.getItemAtPosition(i).toString().contains(substring)){
                index = spnr_pos.getItemAtPosition(i).toString();
                break;
            }

        }

        return index;

    }

    /*************************************************************************************************************************************
     * Opens tender window in dine in and take away billing mode
     *************************************************************************************************************************************/
    private void Tender() {
        Cursor crsrBillDetail = dbBillScreen.getBillDetailByCustomer(iCustId, 2, Float.parseFloat(tvBillAmount.getText().toString()));
        if (crsrBillDetail.moveToFirst()) {
            strPaymentStatus = "Paid";
            PrintNewBill();
        } else {
            if (strPaymentStatus== null || strPaymentStatus.equals(""))
                strPaymentStatus = "CashOnDelivery";


            ArrayList<AddedItemsToOrderTableClass> orderItemList = new ArrayList<>();
            int taxType =0;
            for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                int menuCode =0;
                String itemName= "";
                double quantity=0.00;
                double rate=0.00;
                double igstRate=0.00;
                double igstAmt=0.00;
                double cgstRate=0.00;
                double cgstAmt=0.00;
                double sgstRate=0.00;
                double sgstAmt=0.00;
                double cessRate=0.00;
                double cessAmt=0.00;
                double subtotal=0.00;
                double billamount=0.00;
                double discountamount=0.00;

                TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);


                // Item Number
                if (RowBillItem.getChildAt(0) != null) {
                    CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                    menuCode = (Integer.parseInt(ItemNumber.getText().toString()));
                }

                // Item Name
                if (RowBillItem.getChildAt(1) != null) {
                    TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                    itemName = (ItemName.getText().toString());
                }



                // Quantity
                if (RowBillItem.getChildAt(3) != null) {
                    EditText Quantity = (EditText) RowBillItem.getChildAt(3);
                    String qty_str = Quantity.getText().toString();
                    double qty_d = 0.00;
                    if(qty_str==null || qty_str.equals(""))
                    {
                        Quantity.setText("0.00");
                    }else
                    {
                        qty_d = Double.parseDouble(qty_str);
                    }
                    quantity = (Double.parseDouble(String.format("%.2f",qty_d)));

                }

                // Rate
                if (RowBillItem.getChildAt(4) != null) {
                    EditText Rate = (EditText) RowBillItem.getChildAt(4);
                    String rate_str = Rate.getText().toString();
                    double rate_d = 0.00;
                    if((rate_str==null || rate_str.equals("")))
                    {
                        Rate.setText("0.00");
                    }else
                    {
                        rate_d = Double.parseDouble(rate_str);
                    }
                    rate = (Double.parseDouble(String.format("%.2f",rate_d)));

                }


                // Service Tax Percent

                if(chk_interstate.isChecked()) // IGST
                {
                    cgstRate =0;
                    cgstAmt =0;
                    sgstRate =0;
                    sgstAmt =0;
                    if (RowBillItem.getChildAt(23) != null) {
                        TextView iRate = (TextView) RowBillItem.getChildAt(23);
                        igstRate = (Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(iRate.getText().toString()))));
                    }

                    // Service Tax Amount
                    if (RowBillItem.getChildAt(24) != null) {
                        TextView iAmt = (TextView) RowBillItem.getChildAt(24);
                        igstAmt =  Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(iAmt.getText().toString())));
                    }


                }else // CGST+SGST
                {
                    igstRate =0;
                    igstAmt =0;
                    if (RowBillItem.getChildAt(15) != null) {
                        TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                        sgstRate = (Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(ServiceTaxPercent.getText().toString()))));
                    }

                    // Service Tax Amount
                    if (RowBillItem.getChildAt(16) != null) {
                        TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                        sgstAmt =  Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(ServiceTaxAmount.getText().toString())));
                    }

                    // Sales Tax %
                    if (RowBillItem.getChildAt(6) != null) {
                        TextView SalesTaxPercent = (TextView) RowBillItem.getChildAt(6);
                        cgstRate = Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(SalesTaxPercent.getText().toString())));
                    }
                    // Sales Tax Amount
                    if (RowBillItem.getChildAt(7) != null) {
                        TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                        cgstAmt = Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(SalesTaxAmount.getText().toString())));
                    }
                }
                if (RowBillItem.getChildAt(25) != null) {
                    TextView cessRt = (TextView)RowBillItem.getChildAt(25);
                    if(!cessRt.getText().toString().equals(""))
                        cessRate = Double.parseDouble(cessRt.getText().toString());
                }
                if (RowBillItem.getChildAt(26) != null) {
                    TextView cessAt = (TextView)RowBillItem.getChildAt(26);
                    if(!cessAt.getText().toString().equals(""))
                        cessAmt = Double.parseDouble(cessAt.getText().toString());
                }



                // Tax Type
                if (RowBillItem.getChildAt(13) != null) {
                    TextView TaxType = (TextView) RowBillItem.getChildAt(13);
                    taxType = (Integer.parseInt(TaxType.getText().toString()));
                }
                // subtotal
                subtotal = (rate*quantity) + igstAmt+cgstAmt+sgstAmt;

                AddedItemsToOrderTableClass orderItem = new AddedItemsToOrderTableClass( menuCode, itemName, quantity, rate,
                        igstRate,igstAmt, cgstRate, cgstAmt, sgstRate,sgstAmt, rate*quantity,subtotal, billamount,cessRate,cessAmt,discountamount);
                orderItemList.add(orderItem);
            }


            Intent intentTender = new Intent(myContext, PayBillActivity.class);
            intentTender.putExtra("TotalAmount", tvBillAmount.getText().toString());
            intentTender.putExtra("CustId", edtCustId.getText().toString());
            intentTender.putExtra("phone", edtCustPhoneNo.getText().toString());
            intentTender.putExtra("USER_NAME", strUserName);
            intentTender.putExtra("BaseValue", Float.parseFloat(tvSubTotal.getText().toString()));
            intentTender.putExtra("ORDER_DELIVERED", strOrderDelivered);
            intentTender.putExtra("OtherCharges", Double.parseDouble(txtOthercharges.getText().toString()));
            intentTender.putExtra("TaxType", taxType);// forward/reverse
            intentTender.putParcelableArrayListExtra("OrderList", orderItemList);
            startActivityForResult(intentTender, 1);
            //l(2, true);
            //PrintNewBill();
        }

        /*int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
        Log.d("Delivery:", "Items deleted from pending KOT:" + iResult);*/

        //ClearAll();
        //Close(null);
    }

    /*************************************************************************************************************************************
     * Calculates all the amount after giving overall discount in tender window
     * and updates the new values in text boxes
     *
     * @param dDiscountPercent : Discount percent
     *************************************************************************************************************************************/
    private void OverAllDiscount(double dDiscountPercent) {
        double dRate = 0, dTaxPercent = 0, dTaxAmt = 0, dDiscAmt = 0, dTempAmt = 0;
        TableRow rowItem;
        TextView DiscAmt, DiscPercent, Qty, Rate, TaxAmt, TaxPercent, TaxType;

        for (int i = 0; i < tblOrderItems.getChildCount(); i++) {

            // Get Item row
            rowItem = (TableRow) tblOrderItems.getChildAt(i);
            if (rowItem.getChildAt(0) != null) {
                // Get Discount percent
                Qty = (TextView) rowItem.getChildAt(3);
                Rate = (TextView) rowItem.getChildAt(4);
                DiscPercent = (TextView) rowItem.getChildAt(8);
                DiscAmt = (TextView) rowItem.getChildAt(9);
                TaxPercent = (TextView) rowItem.getChildAt(6);
                TaxAmt = (TextView) rowItem.getChildAt(7);
                TaxType = (TextView) rowItem.getChildAt(13);
                DiscPercent.setText(String.format("%.2f", dDiscountPercent));

                dRate = Double.parseDouble(Rate.getText().toString());
                dTaxPercent = Double.parseDouble(TaxPercent.getText().toString());

                if (TaxType.getText().toString().equalsIgnoreCase("1")) {
                    // Discount
                    dDiscAmt = dRate * (dDiscountPercent / 100);
                    dTempAmt = dDiscAmt;
                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());

                    // Tax
                    dTaxAmt = (dRate - dTempAmt) * (dTaxPercent / 100);
                    dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());

                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                    DiscAmt.setText(String.format("%.2f", dDiscAmt));

                } else {
                    double dBasePrice = 0;
                    dBasePrice = dRate / (1 + (dTaxPercent / 100));

                    // Discount
                    dDiscAmt = dBasePrice * (dDiscountPercent / 100);
                    dTempAmt = dDiscAmt;
                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());

                    // Tax
                    dTaxAmt = (dBasePrice - dTempAmt) * (dTaxPercent / 100);
                    dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());

                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                    DiscAmt.setText(String.format("%.2f", dDiscAmt));
                }
            }
        }

        CalculateTotalAmount();
    }


    /*************************************************************************************************************************************
     * Deletes selected items from billing table
     *************************************************************************************************************************************/
    public void Delete(View v) {
        CheckBox MenuCode;

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

            TableRow Row = (TableRow) tblOrderItems.getChildAt(iRow);

            if (Row.getChildAt(0) != null) {

                MenuCode = (CheckBox) Row.getChildAt(0);

                if (MenuCode.isChecked()) {

                    // Remove all the view present in the row.
                    Row.removeAllViews();

                    // Remove the row
                    tblOrderItems.removeView(Row);

                    // Exit from the loop
                    // break;
                }
            } else {
                continue;
            }

        }

        CalculateTotalAmount();
    }

    /*************************************************************************************************************************************
     * Clear Button Click event, calls ClearAll() function
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void Clear(View v) {
        ClearAll();
        ControlsSetEnabled();

    }

    /*************************************************************************************************************************************
     * Load KOT Button Click event, opens Load KOT activity only if KOT is
     * pending
     *
     * @param  : Clicked Button
     *************************************************************************************************************************************/
//    public void LoadKOT(View v) {
//        Cursor KOT = dbBillScreen.getOccupiedTables();
//        if (KOT.moveToFirst()) {
//            do {
//                Log.d("OccupiedTables", "Table Number:" + KOT.getString(0) + " SubUdf Number:" + KOT.getString(1));
//            } while (KOT.moveToNext());
//            ClearAll(); // To avoid adding item to existing item in table layout
//            Intent intentShiftTable = new Intent(myContext, TableShiftMergeActivity.class);
//            intentShiftTable.putExtra("SHIFT_MERGE", 3);
//            intentShiftTable.putExtra("USER_NAME", strUserName);
//            startActivityForResult(intentShiftTable, 4);
//
//        } else {
//            MsgBox.Show("Warning", "No occupied tables");
//        }
//    }



    private void DisplayModifyKOT() {
        tblOrderItems.removeAllViews();
        String strTableNumber = "0";//tvTableNumber.getText().toString();
        String strTableSplitNo = "0";
        // String strSubUdfNumber = data.getStringExtra("SUB_UDF_NUMBER");
        Log.v("Load Modify KOT", "TableNumber:" + strTableNumber);
        Cursor LoadModifyKOT = null;
        if(jBillingMode==1)
            LoadModifyKOT = dbBillScreen.getKOTItems(Integer.parseInt(strTableNumber), 1, Integer.parseInt(strTableSplitNo));
        else
            LoadModifyKOT =dbBillScreen.getKOTItems(iCustId, String.valueOf(jBillingMode));

        if (LoadModifyKOT.moveToFirst()) {
            LoadModifyKOTItems(LoadModifyKOT);
            // btnDeleteKOT.setEnabled(true);
        } else {
            Log.v("Load Modify KOT", "ERROR: No items found");
            MsgBox.Show("Warning", "No Items Found in KOT");
        }
    }

    /*************************************************************************************************************************************
     * Close Button Click event, closes database connection and finishes the
     * bill screen activity
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void Close(View v) {

        // Close Database Connection
        dbBillScreen.CloseDatabase();
        db.CloseDatabase();

        // finish the activity
        this.finish();
    }


    /*************************************************************************************************************************************
     * Department list click listener which loads the items of selected
     * department to Category list
     *************************************************************************************************************************************/
   /* AdapterView.OnItemClickListener DepartmentListClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

            listViewCateg.setVisibility(View.VISIBLE);
            Cursor Category = dbBillScreen.getCatItems(position + 1);
            // Load Category to Category List
            LoadCategories(Category);
        }

    };
*/
    /*************************************************************************************************************************************
     * Category list click listener which loads the items of selected Category
     * to item grid
     *************************************************************************************************************************************/
    /*AdapterView.OnItemClickListener CategoryListClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            // TODO Auto-generated method stub
            GetItemDetails(position + 1);
            // This condition is to avoid NullReferenceException in getCount()
            // in ImageAdapter class.
            if (Name.length > 0) {
                // Assign item grid to image adapter
           //     gridViewItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                mRecyclerGridView.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
                // Make the item grid visible
              //  gridViewItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
            } else {
                // Make the item grid invisible
             //   gridViewItems.setVisibility(View.INVISIBLE);
                mRecyclerGridView.setVisibility(View.INVISIBLE);
            }
        }

    };*/
    /*************************************************************************************************************************************
     * Modifier list click listener which adds the modifier amount to selected
     * item from the billing table
     *************************************************************************************************************************************/
   /* AdapterView.OnItemClickListener ModifierListClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            // TODO Auto-generated method stub
            Cursor crsrModifier = null;
            crsrModifier = dbBillScreen.getKOTModifier(position + 1);

            AddModifier(crsrModifier);

        }

    };*/

    /*************************************************************************************************************************************
     * Menu code item search key-press / key-listener handler which loads the
     * item to billing grid if selected item is not present otherwise increments
     * the quantity by one
     *************************************************************************************************************************************/
    /*View.OnKeyListener Item_Search_MenuCode_KeyPressEvent = new View.OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub

            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);

                if (((EditText) v).getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Enter item menu code");
                } else {
                    Cursor MenucodeItem = dbBillScreen.getItem(Integer.parseInt(((EditText) v).getText().toString()));
                    if (MenucodeItem.moveToFirst()) {
                        btnClear.setEnabled(true);
                        AddItemToOrderTable(MenucodeItem);
                        // ((EditText)v).setText("");
                        return true;
                    } else {
                        MsgBox.Show("Warning", "Item not found for given menu code");
                    }
                }
            }
            // ((EditText)v).setText("");
            return false;
        }
    };*/

    /*************************************************************************************************************************************
     * Item item search key-press / key-listener handler which loads the item to
     * billing grid if selected item is not present otherwise increments the
     * quantity by one
     *************************************************************************************************************************************/
    /*View.OnKeyListener Item_Search_Itemname_ClickEvent = new View.OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub

            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);

                if (((EditText) v).getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Enter item menu code");
                } else {
                    Cursor MenucodeItem = dbBillScreen.getItem(Integer.parseInt(((EditText) v).getText().toString()));
                    if (MenucodeItem.moveToFirst()) {
                        btnClear.setEnabled(true);
                        AddItemToOrderTable(MenucodeItem);
                        // ((EditText)v).setText("");
                        return true;
                    } else {
                        MsgBox.Show("Warning", "Item not found for given menu code");
                    }
                }
            }
            // ((EditText)v).setText("");
            return false;
        }
    };*/



    /*************************************************************************************************************************************
     * Loads all the KOT modifiers to list
     *************************************************************************************************************************************/
    /*private void LoadKOTModifiers() {
        Cursor crsrKOTModifiers = null;
        crsrKOTModifiers = dbBillScreen.getAllKOTModifier();
        if (crsrKOTModifiers.moveToFirst()) {

            adapModifiers = new ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_multiple_choice);
            lstvwKOTModifiers.setAdapter(adapModifiers);
            lstvwKOTModifiers.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

            do {
                adapModifiers.add(crsrKOTModifiers.getString(crsrKOTModifiers.getColumnIndex("ModifierDescription")));

            } while (crsrKOTModifiers.moveToNext());

        } else {
            MsgBox.Show("Warning", "No KOT Modifiers found");
        }
    }
*/




    /*************************************************************************************************************************************
     * Adds selected KOT modifier to selected items in billing table
     *
     * @param Modifier : Modifier data which has to included to item
     *************************************************************************************************************************************/
   /* private void AddModifier(Cursor Modifier) {

        double dModifierAmt = 0;

        CheckBox Number;
        TextView Name, ModifierAmt;

        if (Modifier.moveToFirst()) {

            for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                TableRow RowItem = (TableRow) tblOrderItems.getChildAt(iRow);

                if (RowItem.getChildAt(0) != null) {

                    Number = (CheckBox) RowItem.getChildAt(0);
                    Name = (TextView) RowItem.getChildAt(1);
                    ModifierAmt = (TextView) RowItem.getChildAt(13);

                    if (Number.isChecked()) {
                        Name.setText(Name.getText().toString() + "+"
                                + Modifier.getString(Modifier.getColumnIndex("ModifierDescription")));

                        if (Modifier.getInt(Modifier.getColumnIndex("IsChargeable")) == 1) {

                            dModifierAmt = Double.parseDouble(ModifierAmt.getText().toString());
                            dModifierAmt += Modifier.getDouble(Modifier.getColumnIndex("ModifierAmount"));
                            ModifierAmt.setText(String.format("%.2f", dModifierAmt));
                        }
                    }
                }
            }
            CalculateTotalAmount();
        } else {
            MsgBox.Show("Warning", "No KOT modifiers found");
        }
    }
*/
    /*************************************************************************************************************************************
     * Modify KOT Button Click event, opens Load KOT activity only if KOT is
     * pending
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
   /* public void ModifyKOT(View v) {
        if (tblOrderItems.getChildCount() > 0) {
            DisplayModifyKOT();
        } else {
            MsgBox.Show("", "No Items Found in KOT");
        }
    }*/

    /*************************************************************************************************************************************
     * Void KOT Button Click event, prompts a dialog to enter reason for voiding
     * KOT and saves the reason in database and deletes the KOT
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
   /* public void VoidKOT(View v) {
        AlertDialog.Builder VoidKOT = new AlertDialog.Builder(myContext);
        final EditText txtReason = new EditText(myContext);

        VoidKOT.setTitle("Void KOT").setMessage("Please enter reason for voiding KOT").setView(txtReason)
                .setNegativeButton("Cancel", null).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (txtReason.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Enter reason before voiding KOT");
                } else {
                    // Calendar now = Calendar.getInstance();
                    DeletedKOT objDeletedKOT = new DeletedKOT();
                    objDeletedKOT.setReason(txtReason.getText().toString());
                    objDeletedKOT.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));
                    objDeletedKOT.setTableNumber(Integer.parseInt(tvTableNumber.getText().toString()));
                    objDeletedKOT.setSubUdfNumber(Integer.parseInt(tvSubUdfValue.getText().toString()));
                    objDeletedKOT.setTokenNumber(iTokenNumber);
                    String strTime = String.format("%tR", Time);
                    objDeletedKOT.setTime(strTime);
                    long lResult = dbBillScreen.addDeletedKOT(objDeletedKOT);
                    Log.v("VoidKOT", "Deleted KOT added, Row ID:" + lResult);
                    lResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                            Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                    Log.v("VoidKOT", "KOT items deleted, Rows Deleted:" + lResult);
                    ClearAll();
                }
            }
        }).show();
    }*/

    /*************************************************************************************************************************************
     * Pay Cash Button Click event, directly saves and prints the bill without
     * opening Tender dialog
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
   /* public void PayCash(View v) {
        if (tblOrderItems.getChildCount() > 0) {
            l(1, true);
            if (jBillingMode == 1) {
                int iResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                        Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                Log.d("Pay Cash", "Items deleted from pending KOT:" + iResult);
            }
            ClearAll();
        } else {
            MsgBox.Show("Warning", "No item in bill");
        }
    }*/


    /*************************************************************************************************************************************
     * Split Bill Button Click event, opens split bill window by loading present
     * bill items in split bill window
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    /*public void SplitBill(View v) {
        try {
            String UserName, TableNumber;
            //UserName = tvUserName.getText().toString();
            TableNumber = tvTableNumber.getText().toString();

            Intent intentSplitBill = new Intent(myContext, SplitBillActivity.class);
            intentSplitBill.putExtra("USER_ID", strUserId);
            intentSplitBill.putExtra("USER_NAME", strUserName);
            intentSplitBill.putExtra("BILL_DATE", strDate);
            intentSplitBill.putExtra("BILL_NUMBER", tvBillNumber.getText().toString());
            intentSplitBill.putExtra("TABLE_NUMBER", TableNumber);
            intentSplitBill.putExtra("TABLE_SPLIT_NO", tvTableSplitNo.getText().toString());
            intentSplitBill.putExtra("UDF_NUMBER", "1");// tvSubUdfValue.getText());
            intentSplitBill.putExtra("SERVICETAX_TYPE", String.valueOf(iTaxType));
            intentSplitBill.putExtra("SERVICETAX_PERCENT", String.valueOf(dServiceTaxPercent));
            startActivity(intentSplitBill);
            ClearAll();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
*/
    /*************************************************************************************************************************************
     * Tender Button Click event, opens tender dialog for bill tender process in
     * dine in billing mode
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void Tender(View v) {

        if (tvBillAmount.getText().toString().equals("") ) {
            MsgBox.Show("Warning", "Please add item to make bill");
        } else if ( tvSubTotal.getText().toString().equals("0.00")) {
            MsgBox.Show("Warning", "Please add item of rate greater than 0.00");
        }else if (edtCustId.getText().toString().equalsIgnoreCase("0"))
        {
            MsgBox.Show("Warning", "Please add customer to make bill");
        }else if (chk_interstate.isChecked() && spnr_pos.getSelectedItem().equals("")) {
            MsgBox.Show("Warning", "Please Select Code for Intersate Supply");
        }

        /*else if (jBillingMode== 4 && strPaymentStatus!= null && strPaymentStatus.equalsIgnoreCase("Paid"))
        { // richa to prevent from repaying the bill in delivery mode
            MsgBox.Show("Warning", "Bill is already paid");
        }*/

        else {
            ArrayList<AddedItemsToOrderTableClass> orderItemList = new ArrayList<>();
            int taxType =0;
            for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                int menuCode =0;
                String itemName= "";
                double quantity=0.00;
                double rate=0.00;
                double igstRate=0.00;
                double igstAmt=0.00;
                double cgstRate=0.00;
                double cgstAmt=0.00;
                double sgstRate=0.00;
                double cessRate=0.00;
                double cessAmt=0.00;
                double sgstAmt=0.00;
                double subtotal=0.00;
                double billamount=0.00;
                double discountamount=0.00;

                TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);


                // Item Number
                if (RowBillItem.getChildAt(0) != null) {
                    CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                    menuCode = (Integer.parseInt(ItemNumber.getText().toString()));
                }

                // Item Name
                if (RowBillItem.getChildAt(1) != null) {
                    TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                    itemName = (ItemName.getText().toString());
                }



                // Quantity
                if (RowBillItem.getChildAt(3) != null) {
                    EditText Quantity = (EditText) RowBillItem.getChildAt(3);
                    String qty_str = Quantity.getText().toString();
                    double qty_d = 0.00;
                    if(qty_str==null || qty_str.equals(""))
                    {
                        Quantity.setText("0.00");
                    }else
                    {
                        qty_d = Double.parseDouble(qty_str);
                    }
                    quantity = (Double.parseDouble(String.format("%.2f",qty_d)));

                }

                // Rate
                if (RowBillItem.getChildAt(4) != null) {
                    EditText Rate = (EditText) RowBillItem.getChildAt(4);
                    String rate_str = Rate.getText().toString();
                    double rate_d = 0.00;
                    if((rate_str==null || rate_str.equals("")))
                    {
                        Rate.setText("0.00");
                    }else
                    {
                        rate_d = Double.parseDouble(rate_str);
                    }
                    rate = (Double.parseDouble(String.format("%.2f",rate_d)));

                }


                if(chk_interstate.isChecked()) // IGST
                {
                    cgstRate =0;
                    cgstAmt =0;
                    sgstRate =0;
                    sgstAmt =0;
                    if (RowBillItem.getChildAt(23) != null) {
                        TextView iRate = (TextView) RowBillItem.getChildAt(23);
                        igstRate = (Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(iRate.getText().toString()))));
                    }

                    // Service Tax Amount
                    if (RowBillItem.getChildAt(24) != null) {
                        TextView iAmt = (TextView) RowBillItem.getChildAt(24);
                        igstAmt =  Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(iAmt.getText().toString())));
                    }


                }else // CGST+SGST
                {
                    igstRate =0;
                    igstAmt =0;
                    if (RowBillItem.getChildAt(15) != null) {
                        TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                        sgstRate = (Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(ServiceTaxPercent.getText().toString()))));
                    }

                    // Service Tax Amount
                    if (RowBillItem.getChildAt(16) != null) {
                        TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                        sgstAmt =  Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(ServiceTaxAmount.getText().toString())));
                    }

                    // Sales Tax %
                    if (RowBillItem.getChildAt(6) != null) {
                        TextView SalesTaxPercent = (TextView) RowBillItem.getChildAt(6);
                        cgstRate = Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(SalesTaxPercent.getText().toString())));
                    }
                    // Sales Tax Amount
                    if (RowBillItem.getChildAt(7) != null) {
                        TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                        cgstAmt = Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(SalesTaxAmount.getText().toString())));
                    }
                }
                if (RowBillItem.getChildAt(25) != null) {
                    TextView cessRt = (TextView)RowBillItem.getChildAt(25);
                    if(!cessRt.getText().toString().equals(""))
                        cessRate = Double.parseDouble(cessRt.getText().toString());
                }
                if (RowBillItem.getChildAt(26) != null) {
                    TextView cessAt = (TextView)RowBillItem.getChildAt(26);
                    if(!cessAt.getText().toString().equals(""))
                        cessAmt = Double.parseDouble(cessAt.getText().toString());
                }

                // Tax Type
                if (RowBillItem.getChildAt(13) != null) {
                    TextView TaxType = (TextView) RowBillItem.getChildAt(13);
                    taxType = (Integer.parseInt(TaxType.getText().toString()));
                }
                // subtotal
                subtotal = (rate*quantity) + igstAmt+cgstAmt+sgstAmt;

                AddedItemsToOrderTableClass orderItem = new AddedItemsToOrderTableClass( menuCode, itemName, quantity, rate,
                        igstRate,igstAmt, cgstRate, cgstAmt, sgstRate,sgstAmt,rate*quantity, subtotal, billamount,cessRate,cessAmt,discountamount);
                orderItemList.add(orderItem);
            }

            Intent intentTender = new Intent(myContext, PayBillActivity.class);
            intentTender.putExtra("TotalAmount", tvBillAmount.getText().toString());
            intentTender.putExtra("CustId", edtCustId.getText().toString());
            intentTender.putExtra("phone", edtCustPhoneNo.getText().toString());
            intentTender.putExtra("BaseValue", Float.parseFloat(tvSubTotal.getText().toString()));
            intentTender.putExtra("OtherCharges", Double.parseDouble(txtOthercharges.getText().toString()));
            intentTender.putExtra("TaxType", taxType);// forward/reverse
            intentTender.putParcelableArrayListExtra("OrderList", orderItemList);
            intentTender.putExtra("USER_NAME", strUserName);
            intentTender.putExtra("BillAmountRoundOff", BILLAMOUNTROUNDOFF);
            startActivityForResult(intentTender, 1);
        }
    }


    /*************************************************************************************************************************************
     * Delete Bill Button Click event, calls delte bill function
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void DeleteBill(View v) {

        //DeleteVoid(Byte.parseByte("1"));
        tblOrderItems.removeAllViews();

        AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.dinein_reprint, null);

        final ImageButton btnCal_reprint = (ImageButton) vwAuthorization.findViewById(R.id.btnCal_reprint);

        final EditText txtReprintBillNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInReprintBillNumber);
        final TextView tv_inv_date = (TextView) vwAuthorization.findViewById(R.id.tv_inv_date);
        tv_inv_date.setText(tvDate.getText().toString());
        btnCal_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelection(tv_inv_date);
            }
        });


        DineInTenderDialog.setIcon(R.drawable.ic_launcher)
                .setTitle("Delete Bill")
                /*.setMessage("Enter Bill Number")*/
                .setView(vwAuthorization)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if (txtReprintBillNo.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Number");
                            return;
                        } else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Date");
                            setInvoiceDate();
                            return;
                        }  else {
                            try {
                                int InvoiceNo = Integer.valueOf(txtReprintBillNo.getText().toString());
                                String date_reprint = tv_inv_date.getText().toString();
                                tvDate.setText(date_reprint);
                                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(date_reprint);
                                Cursor result = dbBillScreen.getBillDetail(InvoiceNo, String.valueOf(date.getTime()));


                                if (result.moveToFirst()) {
                                    if (result.getInt(result.getColumnIndex("BillStatus")) != 0) {
                                        int Custid = result.getInt(result.getColumnIndex("CustId"));
                                        String jBilling_str = result.getString(result.getColumnIndex("BillingMode"));
                                        Cursor crsr = dbBillScreen.getKOTItems(Custid, jBilling_str);
                                        if (crsr != null && crsr.moveToFirst()) {
                                            if (Integer.parseInt(jBilling_str) == 3) {
                                                MsgBox.Show("Warning", "Since this order is not delivered yet,\nto delete bill ,kindly goto \"PickUp Status\" " +
                                                        "\n Select this customer and click \"Cancel Order \"");
                                            } else if (Integer.parseInt(jBilling_str) == 4) {
                                                MsgBox.Show("Warning", "Since this order is not delivered yet, \nto delete bill, kindly goto \"Delivery Status\" " +
                                                        "\n Select this customer and click \"Cancel Order \"");
                                            }
                                            return;
                                        } else {
                                            VoidBill(InvoiceNo,String.valueOf(date.getTime()));
                                        }
                                    } else {

                                        //Toast.makeText(myContext, "Bill is already voided", Toast.LENGTH_SHORT).show();
                                        String msg = "Bill Number " + InvoiceNo + " is already voided";
                                        MsgBox.Show("Note",msg);
                                        Log.d("VoidBill", msg);
                                    }
                                } else {
                                    //Toast.makeText(myContext, "No bill found with bill number " + InvoiceNo, Toast.LENGTH_SHORT).show();
                                    String msg = "No bill found with bill number " + InvoiceNo;
                                    MsgBox.Show("Note",msg);
                                    Log.d("VoidBill", msg);
                                }
                                ClearAll();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
    }

    /*************************************************************************************************************************************
     * Reprint Bill Button Click event, calls reprint bill function
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void ReprintBill(View v) {

        //ReprintVoid(Byte.parseByte("1"));
        tblOrderItems.removeAllViews();

        AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.dinein_reprint, null);

        final ImageButton btnCal_reprint = (ImageButton) vwAuthorization.findViewById(R.id.btnCal_reprint);

        final EditText txtReprintBillNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInReprintBillNumber);
        final TextView tv_inv_date = (TextView) vwAuthorization.findViewById(R.id.tv_inv_date);
        tv_inv_date.setText(tvDate.getText().toString());
        btnCal_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelection(tv_inv_date);
            }
        });


        DineInTenderDialog.setIcon(R.drawable.ic_launcher)
                .setTitle("RePrint Bill")
                .setView(vwAuthorization).setNegativeButton("Cancel", null)
                .setPositiveButton("RePrint", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if (txtReprintBillNo.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Number");
                            setInvoiceDate();
                            return;
                        }else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Date");
                            setInvoiceDate();
                            return;
                        }  else {
                            try
                            {
                                int billStatus =0;
                                int billNo = Integer.valueOf(txtReprintBillNo.getText().toString());
                                String date_reprint = tv_inv_date.getText().toString();
                                tvDate.setText(date_reprint);
                                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(date_reprint);
                                Cursor LoadItemForReprint = dbBillScreen.getItemsFromBillItem_new(
                                        billNo, String.valueOf(date.getTime()));
                                if (LoadItemForReprint.moveToFirst())
                                {
                                    Cursor cursor = dbBillScreen.getBillDetail(billNo,String.valueOf(date.getTime()));
                                    if (cursor != null && cursor.moveToFirst()) {
                                        billStatus  = cursor.getInt(cursor.getColumnIndex("BillStatus"));
                                        if (billStatus == 0) {
                                            MsgBox.Show("Warning", "This bill has been deleted");
                                            setInvoiceDate();
                                            return;
                                        }
                                        String pos = cursor.getString(cursor.getColumnIndex("POS"));
                                        String custStateCode = cursor.getString(cursor.getColumnIndex("CustStateCode"));
                                        if (pos != null && !pos.equals("") && custStateCode != null && !custStateCode.equals("") && !custStateCode.equalsIgnoreCase(pos)) {
                                            chk_interstate.setChecked(true);
                                            int index = getIndex_pos(custStateCode);
                                            spnr_pos.setSelection(index);
                                        } else {
                                            chk_interstate.setChecked(false);
                                            spnr_pos.setSelection(0);
                                        }
                                        fTotalDiscount = cursor.getFloat(cursor.getColumnIndex("TotalDiscountAmount"));
                                        float discper = cursor.getFloat(cursor.getColumnIndex("DiscPercentage"));
                                        reprintBillingMode = cursor.getInt(cursor.getColumnIndex("BillingMode"));

                                        tvDiscountPercentage.setText(String.format("%.2f", discper));
                                        tvDiscountAmount.setText(String.format("%.2f", fTotalDiscount));
                                        tvBillNumber.setText(txtReprintBillNo.getText().toString());

                                        tvIGSTValue.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("IGSTAmount"))));
                                        tvTaxTotal.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("CGSTAmount"))));
                                        tvServiceTaxTotal.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("SGSTAmount"))));
                                        tvSubTotal.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("TaxableValue"))));
                                        tvBillAmount.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("BillAmount"))));
                                        edtCustId.setText(cursor.getString(cursor.getColumnIndex("CustId")));

                                        LoadItemsForReprintBill(LoadItemForReprint);

                                    }

                                } else {
                                    MsgBox.Show("Warning",
                                            "No Item is present for the Bill Number " + txtReprintBillNo.getText().toString() +", Dated :"+tv_inv_date.getText().toString());
                                    setInvoiceDate();
                                    return;
                                }
                                if(reprintBillingMode ==4 && billStatus ==2)
                                {
                                    strPaymentStatus = "Cash On Delivery";
                                }
                                else
                                    strPaymentStatus = "Paid";
                                isReprint = true;
                                PrintNewBill();
                                // update bill reprint count
                                int Result = dbBillScreen
                                        .updateBillRepintCount(Integer.parseInt(txtReprintBillNo.getText().toString()));
                                ClearAll();
                                if (!(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1)) { // reverse tax
                                    REVERSETAX = true;
                                }else
                                {
                                    REVERSETAX = false;
                                }


                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();


    }

    private void DateSelection(final TextView tv_inv_date) {        // StartDate: DateType = 1 EndDate: DateType = 2
        try {
            AlertDialog.Builder dlgReportDate = new AlertDialog.Builder(myContext);
            final DatePicker dateReportDate = new DatePicker(myContext);
            String date_str = tvDate.getText().toString();
            String dd = date_str.substring(6,10)+"-"+date_str.substring(3,5)+"-"+date_str.substring(0,2);
            DateTime objDate = new DateTime(dd);
            dateReportDate.updateDate(objDate.getYear(), objDate.getMonth(), objDate.getDay());
            String strMessage = "";



            dlgReportDate
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("Date Selection")
                    .setMessage(strMessage)
                    .setView(dateReportDate)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        String strDd = "";
                        public void onClick(DialogInterface dialog, int which) {
                            if (dateReportDate.getDayOfMonth() < 10) {
                                strDd = "0" + String.valueOf(dateReportDate.getDayOfMonth())+"-";
                            } else {
                                strDd = String.valueOf(dateReportDate.getDayOfMonth())+"-";
                            }
                            if (dateReportDate.getMonth() < 9) {
                                strDd += "0" + String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            } else {
                                strDd += String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            }

                            strDd += String.valueOf(dateReportDate.getYear());
                            tv_inv_date.setText(strDd);
                            Log.d("ReprintDate", "Selected Date:" + strDd);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************************************************************************************************************************************
     * Void Bill Button Click event, opens a dialog to enter admin user id and
     * password for voiding bill if user is admin then bill will be voided
     *
     *************************************************************************************************************************************/
    public void VoidBill(final int invoiceno , final String Invoicedate) {

        AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);

        final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
        final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);

        AuthorizationDialog.setTitle("Authorization").setIcon(R.drawable.ic_launcher).setView(vwAuthorization)
                .setNegativeButton("Cancel", null).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Cursor User = dbBillScreen.getUser(txtUserId.getText().toString(),
                        txtPassword.getText().toString());
                if (User.moveToFirst()) {
                    if (User.getInt(User.getColumnIndex("RoleId")) == 1) {
                        //ReprintVoid(Byte.parseByte("2"));
                        int result = db.makeBillVoids(invoiceno, Invoicedate);
                        if(result >0)
                        {
                            Date dd = new Date(Long.parseLong(Invoicedate));
                            String dd_str = new SimpleDateFormat("dd-MM-yyyy").format(dd);
                            String msg = "Bill Number "+invoiceno+" , Dated : "+dd_str+" voided successfully";
                            // MsgBox.Show("Warning", msg);
                            Toast.makeText(myContext, msg, Toast.LENGTH_SHORT).show();
                            Log.d("VoidBill", msg);
                        }
                    } else {
                        MsgBox.Show("Warning", "Void Bill failed due to in sufficient access privilage");
                    }
                } else {
                    MsgBox.Show("Warning", "Void Bill failed due to wrong user id or password");
                }
            }
        }).show();
    }



    /*************************************************************************************************************************************
     * Invokes saveOrder function to save order if order is new else updates the
     * order in database
     *
     * @param v : SaveOrder button
     ************************************************************************************************************************************/
    public void SaveKOT(View v) {
        iPrintKOTStatus = 1;
        int i = SaveKOT();
        Log.d("Billing Activity", "SaveKOT : Status = "+i );
        if(i>0)
        {
            btnPayBill.setEnabled(true);
            btnPrintBill.setEnabled(true);
        }
        else
        {
            btnPayBill.setEnabled(false);
            btnPrintBill.setEnabled(false);
        }
        //PrintKOT();
    }

    public int  SaveKOT() {
        int returnStatus =1;
        try{
            if (jBillingMode== 3|| jBillingMode ==4) { // billingmode -> takeaway/delivery
                String tempCustId = edtCustId.getText().toString();
                if (tempCustId.equalsIgnoreCase("") || tempCustId.equalsIgnoreCase("0")) {
                    MsgBox.Show("Warning", "Please Select Customer for Billing");
                    returnStatus = 0;

                } else {
                    if (tblOrderItems.getChildCount() < 1) {
                        MsgBox.Show("Warning", "Add Item before Saving KOT");
                        returnStatus = 0;

                    } else {
                        int iResult = 0;
                        Cursor crsrKOTNo = dbBillScreen.getKOTItems(Integer.valueOf(edtCustId.getText().toString()), String.valueOf(jBillingMode));
                        if(crsrKOTNo.moveToFirst())
                        {
                            iKOTNo = crsrKOTNo.getInt(crsrKOTNo.getColumnIndex("TokenNumber"));
                        } else {
                            iKOTNo = dbBillScreen.getKOTNo();
                            long iResult1 = dbBillScreen.updateKOTNo(iKOTNo);
                        }

                        // Delete the KOT items from Pending KOT if KOT is updated
                        iResult = dbBillScreen.deleteKOTItems(Integer.valueOf(edtCustId.getText().toString()), String.valueOf(jBillingMode));
                        Log.v("KOT Update", "Deleted rows:" + iResult);
                        InsertKOTItems(iKOTNo);
                        Toast.makeText(myContext, "KOT Saved Successfully", Toast.LENGTH_LONG).show();
                        //PrintKOT();
                        btnPrintBill.setEnabled(true);
                        btnPayBill.setEnabled(true);
                        //strPaymentStatus="";
                        //ClearAll();

                    /*if (jBillingMode == 4) {
                        btnPayBill.setEnabled(true);
                    }*/
                        //Cursor crsrLoadItems = dbBillScreen.getKOTItems(Integer.valueOf(edtCustId.getText().toString()), String.valueOf(jBillingMode));
                        //LoadKOTItems(crsrLoadItems);
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Oops","Some Error occured while saving KOT");
            returnStatus =0;
        }finally{
            return returnStatus;
        }


    }

    private void updateOutwardStock()
    {
        Log.d(TAG, "updateOutwardStock()");
        String businessdate = tvDate.getText().toString();
        DatabaseHandler db_local = new DatabaseHandler(getApplicationContext());
        db_local.CreateDatabase();
        db_local.OpenDatabase();
        StockOutwardMaintain stock_outward = new StockOutwardMaintain(getApplicationContext(), db_local);
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);
            int menuCode = -1;
            String itemname = "";
            double closingQty = 0;
            // Item Number
            if (RowBillItem.getChildAt(0) != null) {
                CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                menuCode = (Integer.parseInt(ItemNumber.getText().toString()));
            }
            // Item Name
            if (RowBillItem.getChildAt(1) != null) {
                TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                itemname = (ItemName.getText().toString());
            }

            // Quantity
            if (RowBillItem.getChildAt(3) != null){
                TextView ItemQuantity = (TextView) RowBillItem.getChildAt(3);
                double qty_to_reduce = Double.parseDouble(ItemQuantity.getText().toString());
                //Cursor cursor = db_local.getItem(menuCode);
                Cursor cursor = db_local.getOutwardStockItem(businessdate,menuCode);
                if(cursor!=null && cursor.moveToNext())
                {
                    closingQty = cursor.getDouble(cursor.getColumnIndex("ClosingStock"));
                    if(closingQty<= qty_to_reduce)
                        closingQty =0;
                    else
                        closingQty -= qty_to_reduce;
                    stock_outward.updateClosingStock_Outward(businessdate,menuCode,itemname,closingQty);
                }

            }

        } // end of for
        db_local.CloseDatabase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                //Log.d("ActivityResult",String.valueOf(requestCode));
                switch (requestCode) {
                    case 1: // PayBill Activity Result
                        boolean isComplimentaryBill, isDiscounted, isPrintBill = false;
                        //float dDiscPercent;
                        String strComplimentaryReason = "";

                        isComplimentaryBill = data.getBooleanExtra(PayBillActivity.IS_COMPLIMENTARY_BILL, false);
                        isDiscounted = data.getBooleanExtra(PayBillActivity.IS_DISCOUNTED, false);
                        isPrintBill = data.getBooleanExtra(PayBillActivity.IS_PRINT_BILL, true);

                        strComplimentaryReason = data.getStringExtra(PayBillActivity.COMPLIMENTARY_REASON);
                        strOrderDelivered = data.getBooleanExtra("ORDER_DELIVERED",false);
                        dDiscPercent = data.getFloatExtra("DISCOUNT_PERCENTAGE", 0);
                        fCashPayment = data.getFloatExtra(PayBillActivity.TENDER_CASH_VALUE, 0);

                        fCardPayment = data.getFloatExtra(PayBillActivity.TENDER_CARD_VALUE, 0);

                        fCouponPayment = data.getFloatExtra(PayBillActivity.TENDER_COUPON_VALUE, 0);
                        fTotalDiscount = 0;
                        fTotalDiscount = data.getFloatExtra(PayBillActivity.DISCOUNT_AMOUNT, 0);

//                        fPettCashPayment = data.getFloatExtra(PayBillActivity.TENDER_PETTYCASH_VALUE, 0);
                        dPettCashPayment = data.getDoubleExtra(PayBillActivity.TENDER_PETTYCASH_VALUE, 0);
                        fPaidTotalPayment = data.getFloatExtra(PayBillActivity.TENDER_PAIDTOTAL_VALUE, 0);
                        fWalletPayment = data.getFloatExtra(PayBillActivity.TENDER_WALLET_VALUE, 0);
                        dChangePayment = data.getDoubleExtra(PayBillActivity.TENDER_CHANGE_VALUE, 0);
                        fRoundOfValue = data.getFloatExtra(PayBillActivity.TENDER_ROUNDOFF, 0);
                        dFinalBillValue = data.getDoubleExtra(PayBillActivity.TENDER_FINALBILL_VALUE, 0);

                        iCustId = data.getIntExtra("CUST_ID", 1);

                        if (isDiscounted == true) {
                            Log.v("Tender Result", "Discounted:" + isDiscounted);
                            Log.v("Tender Result", "Discount Amount:" + fTotalDiscount);
                            tvDiscountAmount.setText(String.valueOf(fTotalDiscount));
                            tvDiscountPercentage.setText(String.valueOf(dDiscPercent));
                            /*float total = Float.parseFloat(tvBillAmount.getText().toString());
                            //total = Math.round(total);
                            total -= fTotalDiscount;
                            tvBillAmount.setText(String.format("%.2f",total));*/
                            double igst = data.getDoubleExtra("TotalIGSTAmount",0);
                            double cgst = data.getDoubleExtra("TotalCGSTAmount",0);
                            double sgst = data.getDoubleExtra("TotalSGSTAmount",0);
                            double cess = data.getDoubleExtra("TotalcessAmount",0);
                            double billtot = data.getDoubleExtra("TotalBillAmount",0);
                            if(billtot >0)
                            {
                                tvBillAmount.setText(String.format("%.2f",billtot));
                                tvcessValue.setText(String.format("%.2f",cess));
                                Log.d("Richa :",String.valueOf(cess));
                                if(chk_interstate.isChecked())
                                {
                                    tvIGSTValue.setText(String.format("%.2f", igst));
                                    tvTaxTotal.setText(String.format("%.2f", igst));
                                    tvServiceTaxTotal.setText("0.00");
                                } else {
                                    tvTaxTotal.setText(String.format("%.2f", cgst));
                                    tvServiceTaxTotal.setText(String.format("%.2f", sgst));
                                }

                            }

                            ArrayList<AddedItemsToOrderTableClass> orderList_recieved = data.getParcelableArrayListExtra("OrderList");
                            double taxableValue = 0.00;
                            for(int i =0;i<tblOrderItems.getChildCount();i++)
                            {
                                TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(i);

                                // Item Number
                                if (RowBillItem.getChildAt(0) != null) {
                                    CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                                    int menucode = (Integer.parseInt(ItemNumber.getText().toString()));
                                    for(AddedItemsToOrderTableClass item : orderList_recieved) {
                                        if(item.getMenuCode() == menucode) {

                                            if (RowBillItem.getChildAt(9) != null ) {
                                                TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                                                DiscountAmount.setText(String.format("%.2f",item.getDiscountamount()));
                                            }

                                            if (RowBillItem.getChildAt(16) != null) {
                                                TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                                                ServiceTaxAmount.setText(String.format("%.2f",item.getSgstAmt()));
                                            }
                                            // Sales Tax Amount
                                            if (RowBillItem.getChildAt(7) != null) {
                                                TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                                                SalesTaxAmount.setText(String.format("%.2f",item.getCgstAmt()));
                                            }
                                            if (RowBillItem.getChildAt(24) != null) {
                                                TextView IGSTTaxAmount = (TextView) RowBillItem.getChildAt(24);
                                                IGSTTaxAmount.setText(String.format("%.2f",item.getIgstAmt()));
                                            }
                                            if (RowBillItem.getChildAt(26) != null) {
                                                TextView cessAmount = (TextView) RowBillItem.getChildAt(26);
                                                cessAmount.setText(String.format("%.2f",item.getCessAmt()));
                                            }
                                            double quantity_dd = 0.00;
                                            if (RowBillItem.getChildAt(3) != null ) {
                                                TextView qty = (TextView) RowBillItem.getChildAt(3);
                                                quantity_dd = qty.getText().toString().trim().equals("")?0.00:
                                                        Double.parseDouble(String.format("%.2f",Double.parseDouble(qty.getText().toString().trim())));
                                                //System.out.println("Richa :  quantity "+quantity_dd);
                                            }
                                            if (RowBillItem.getChildAt(5) != null) {
                                                TextView Amount = (TextView) RowBillItem.getChildAt(5);
                                                Amount.setText(String.format("%.2f",item.getTaxableValue()*quantity_dd));
                                                //System.out.println("Richa :  amt "+(item.getTaxableValue()*quantity_dd));
                                            }
                                            if (RowBillItem.getChildAt(28) != null) {
                                                TextView TaxableVal = (TextView) RowBillItem.getChildAt(28);
                                                TaxableVal.setText(String.format("%.2f",item.getTaxableValue()*quantity_dd));
                                                //System.out.println("Richa :  amt "+(item.getTaxableValue()*quantity_dd));
                                                taxableValue += item.getTaxableValue()*quantity_dd;
                                            }
                                        }
                                    }
                                }
                            }
                            //OverAllDiscount(dDiscPercent);
                            tvSubTotal.setText(String.format("%.2f",taxableValue));
                        }
                        if(strOrderDelivered == true && jBillingMode==3)
                        {
                            int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
                            Log.d("HomeDeliveryBillingAct:", "Items deleted from pending KOT:" + iResult);
                            fTotalDiscount =0;
                            strOrderDelivered = false;
                        }else
                        {
                            int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
                            SaveKOT();
                        }
                        /*if(jBillingMode ==3 && tblOrderItems.getChildCount() <1){
                            Cursor BillItems = dbBillScreen.getKOTItems(iCustId, String.valueOf(jBillingMode));
                            if (BillItems.moveToFirst()) {
                                ClearAll();
                                //LoadKOTItems(BillItems);
                                LoadModifyKOTItems(BillItems);
                        }}*/
                        tvBillAmount.setText(String.format("%.2f",dFinalBillValue));
                        PrintBillPayment= 0;
                        l(2, isPrintBill);
                        Toast.makeText(myContext, "Bill saved Successfully", Toast.LENGTH_SHORT).show();
                        updateOutwardStock();

                        if (isComplimentaryBill == true) {
                            // Save complimentary bill details
                            SaveComplimentaryBill(Integer.parseInt(tvBillNumber.getText().toString()),
                                    (fCashPayment + fCardPayment + fCouponPayment), strComplimentaryReason);
                        }
                        if (isPrintBill == true) {
                            strPaymentStatus = "Paid";
                            PrintNewBill();
                        }
                        ControlsSetEnabled();
                        ClearAll();
                        break;

                    case 2: // Table Activity Result
                        break;

                    case 3: // Split bill
                        break;

                    case 4: // Table Shift Merge (Load KOT)
                       /* String strTableNumber = data.getStringExtra("TABLE_NUMBER");
                        String strTableSplitNo = data.getStringExtra("TABLE_SPLIT_NO");
                        String strSubUdfNumber = data.getStringExtra("SUB_UDF_NUMBER");
                        Log.v("Load KOT", "TableNumber:" + strTableNumber + " Sub Udf:" + strSubUdfNumber);
                        Cursor LoadKOT = dbBillScreen.getKOTItems(Integer.parseInt(strTableNumber),
                                Integer.parseInt(strSubUdfNumber), Integer.parseInt(strTableSplitNo));
                        if (LoadKOT.moveToFirst()) {
                            LoadKOTItems(LoadKOT);
                            btnDeleteKOT.setEnabled(true);
                            btnPrintKOT.setEnabled(true);
                            btnPrintBill.setEnabled(true);
                            btnPayBill.setEnabled(true);

                        } else {
                            Log.v("Load KOT", "ERROR: No items found");
                        }*/
                        break;

                    case 5: // Pickup and Delivery Status\
                        try {
                            boolean isFinish = false;
                            String strBillMode = data.getStringExtra("BILLING_MODE");
                            jBillingMode = Byte.parseByte(strBillMode);
                            strUserName = data.getStringExtra("USER_NAME");
                            strUserId = data.getStringExtra("USER_ID");
                            iCustId = data.getIntExtra("CUST_ID", 0);
                            strPaymentStatus = data.getStringExtra("PAYMENT_STATUS");
                            strMakeOrder = data.getStringExtra("MAKE_ORDER");
                            isPrintBill = data.getBooleanExtra("IS_PRINT_BILL",false);
                            isFinish = data.getBooleanExtra("IS_FINISH", false);
                            if(strBillMode.equals("4")) {
                                String strBillNo = data.getStringExtra("BILLNO");
                                if (strBillNo != null && !strBillNo.equals("") && !strBillNo.equals("0")) {
                                    tvBillNumber.setText(strBillNo);
                                    Log.d("BillNo Recieved  ", "in billing mode 4 :" + strBillNo);
                                }

                                Cursor BillItems = dbBillScreen.getKOTItems(iCustId, String.valueOf(jBillingMode));
                                if (BillItems.moveToFirst()) {
                                    ClearAll();
                                    //LoadKOTItems(BillItems);
                                    fTotalDiscount = data.getFloatExtra("DISCOUNT_AMOUNT", 0);
                                    CustomerDetailsFilled = false;
                                    LoadModifyKOTItems(BillItems);
                                    String isPaid = data.getStringExtra("IS_PAID");
                                    if (isPaid != null && !isPaid.equals("YES")) {


                                        if (BILLAMOUNTROUNDOFF == 1) {
                                            String temp = tvBillAmount.getText().toString().trim();
                                            double finalAmount = Double.parseDouble(tvBillAmount.getText().toString().trim());
                                            double roundOffFinalAmount = 0;

                                            if (!temp.contains(".00")){
                                                roundOffFinalAmount = Math.round(finalAmount);
                                                tvBillAmount.setText(String.valueOf(roundOffFinalAmount));
                                                fRoundOfValue = Float.parseFloat("0" + temp.substring(temp.indexOf(".")));
                                            }
                                        }

                                        l(2, isPrintBill);
                                        //                                    if(isPrintBill==true)
                                        //                                    {
                                        //                                        tvBillNumber.setText(String.valueOf(Integer.parseInt(tvBillNumber.getText().toString())-1));
                                        //                                    }
                                    }
                                }
                            }

                            Cursor BillItems = dbBillScreen.getKOTItems(iCustId, String.valueOf(jBillingMode));
                            if (BillItems.moveToFirst()) {
                                ClearAll();
                                //LoadKOTItems(BillItems);
                                fTotalDiscount = data.getFloatExtra("DISCOUNT_AMOUNT", 0);
                                CustomerDetailsFilled = false;
                                LoadModifyKOTItems(BillItems);
                                btnPayBill.setEnabled(true);
                                btnPrintBill.setEnabled(true);
                                if (!strMakeOrder.equalsIgnoreCase("YES") && !strMakeOrder.equalsIgnoreCase("")) {
                                    strOrderDelivered=true;
                                    Tender();
                                }else if(isPrintBill){
                                    String strBillNo = data.getStringExtra("BILLNO");
                                    if (strBillNo != null && !strBillNo.equals("") && !strBillNo.equals("0")) {
                                        tvBillNumber.setText(strBillNo);
                                        Log.d("BillNo Recieved: ", strBillNo);
                                        Date newdate = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
                                        long milli = newdate.getTime();
                                        Log.d("BusinessDate Recieved: ", businessDate);
                                        Log.d("BusinessDate: ", String.valueOf(milli));
                                        Cursor cursorBillInfo = db.getBillDetail_counter(Integer.parseInt(strBillNo), String.valueOf(milli));
                                        System.out.println("Cursor:"+cursorBillInfo.getCount());
                                        if (cursorBillInfo.moveToNext()) {
                                            fRoundOfValue = cursorBillInfo.getString(cursorBillInfo.getColumnIndex("TotalDiscountAmount"))== null?
                                                    0: Float.parseFloat(String.format("%.2f", cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("RoundOff"))));
                                            tvDiscountAmount.setText(String.format("%.2f", cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("TotalDiscountAmount"))));
                                            tvDiscountPercentage.setText(String.format("%.2f", cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("DiscPercentage"))));
                                            tvBillAmount.setText(String.format("%.2f", cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("BillAmount"))));
                                            txtOthercharges.setText(String.format("%.2f", cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("DeliveryCharge"))));
                                            String pos = cursorBillInfo.getString(cursorBillInfo.getColumnIndex("POS"));
                                            String custStateCode = cursorBillInfo.getString(cursorBillInfo.getColumnIndex("CustStateCode"));
                                            if(pos!=null && custStateCode!=null && pos.trim().equalsIgnoreCase(custStateCode.trim())) // cgst+sgst
                                            {
                                                tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                                                tvTaxTotal.setTextColor(Color.WHITE);
                                                tvServiceTaxTotal.setTextColor(Color.WHITE);
                                                tvTaxTotal.setText(String.format("%.2f",cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("CGSTAmount"))));
                                                tvServiceTaxTotal.setText(String.format("%.2f",cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("SGSTAmount"))));
                                            }else
                                            {

                                                chk_interstate.setChecked(true);
                                                spnr_pos.setSelection(getIndex_pos(custStateCode));
                                                tvTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                                                tvServiceTaxTotal.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                                                tvIGSTValue.setTextColor(Color.WHITE);
                                                tvIGSTValue.setText(String.format("%.2f",cursorBillInfo.getDouble(cursorBillInfo.getColumnIndex("IGSTAmount"))));
                                            }
                                        }
                                    }
                                    else{
                                        CalculateTotalAmountforRePrint();
                                    }
                                    isReprint = true;
                                    if (isPrinterAvailable) {
                                        PrintNewBill();
                                        int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
                                        Log.d("HomeDeliveryBillingAct:", "1 Items deleted from pending KOT:" + iResult);
                                        ClearAll();
                                    } else {
                                        askForConfig();
                                        PrintNewBill();
                                        int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
                                        Log.d("HomeDeliveryBillingAct:", "1 Items deleted from pending KOT:" + iResult);
                                        ClearAll();
                                    }



                                }else if(isFinish)
                                {
                                    int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
                                    Log.d("HomeDeliveryBillingAct:", "Items deleted from pending KOT:" + iResult);
                                    ClearAll();

                                }

                            }

                        } catch (Exception ex) {
                            MsgBox.Show("Error", ex.getMessage());
                            ex.printStackTrace();
                        }
                        break;
                }
            } else if (resultCode == RESULT_CANCELED) {
                try {
                    if (data.getBooleanExtra("isCancelled", false)) {
                        finish();
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*************************************************************************************************************************************/


    private void loadAutoCompleteData() {

        // List - Get Item Name
        List<String> labelsItemName = dbBillScreen.getAllItemsName();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                labelsItemName);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        // attaching data adapter to spinner
        aTViewSearchItem.setAdapter(dataAdapter);

        // List - Get Menu Code
        List<String> labelsMenuCode = dbBillScreen.getAllMenuCode();
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                labelsMenuCode);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        aTViewSearchMenuCode.setAdapter(dataAdapter1);

        POS_LIST = ArrayAdapter.createFromResource(this, R.array.poscode, android.R.layout.simple_spinner_item);
        spnr_pos.setAdapter(POS_LIST);

        // barcode

        List<String> labelsBarCode = dbBillScreen.getAllBarCode_fnb();
        ArrayAdapter<String> dataAdapter11 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                labelsBarCode);
        dataAdapter11.setDropDownViewResource(android.R.layout.simple_list_item_1);
        autoCompleteTextViewSearchItemBarcode.setAdapter(dataAdapter11);

    }


    public ArrayList<BillKotItem> kotPrint() {
        ArrayList<BillKotItem> billKotItems = new ArrayList<BillKotItem>();
        int count = 1;
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            TableRow row = (TableRow) tblOrderItems.getChildAt(iRow);
            CheckBox itemId = (CheckBox) row.getChildAt(0);
            TextView itemName = (TextView) row.getChildAt(1);
            TextView HSNCode = (TextView) row.getChildAt(2);
            EditText itemQty = (EditText) row.getChildAt(3);
            EditText itemRate = (EditText) row.getChildAt(4);
            TextView itemAmount = (TextView) row.getChildAt(5);
            TextView printstatus = (TextView) row.getChildAt(21);
            TextView UOM_tv = (TextView) row.getChildAt(22);
            if (printstatus.getText().toString().equalsIgnoreCase("1")) {
                int id = Integer.parseInt(itemId.getText().toString().trim());
                int sno = count;
                String name = itemName.getText().toString().trim();
                String hsn = HSNCode.getText().toString().trim();
                String UOM = UOM_tv.getText().toString().trim();
                Double qty = Double.parseDouble(itemQty.getText().toString().trim());
                double rate = Double.parseDouble(itemRate.getText().toString().trim());
                double amount = Double.parseDouble(itemAmount.getText().toString().trim());
                BillKotItem billKotItem = new BillKotItem(sno, name, qty, rate, amount,hsn,UOM,"");
                billKotItems.add(billKotItem);
                count++;
            }
        }
        return billKotItems;
    }

    public ArrayList<BillKotItem> billPrint(ArrayList<BillTaxSlab> billTaxSlabs) {
        ArrayList<BillKotItem> billKotItems = new ArrayList<BillKotItem>();
        AMOUNTPRINTINNEXTLINE =0;
        int count = 1;
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            TableRow row = (TableRow) tblOrderItems.getChildAt(iRow);
            CheckBox itemId = (CheckBox) row.getChildAt(0);
            TextView itemName = (TextView) row.getChildAt(1);
            TextView HSNCode = (TextView) row.getChildAt(2);
            EditText itemQty = (EditText) row.getChildAt(3);
            EditText itemRate = (EditText) row.getChildAt(4);
            TextView itemAmount = (TextView) row.getChildAt(5);
            TextView printstatus = (TextView) row.getChildAt(21);
            TextView UOM_tv = (TextView) row.getChildAt(22);
            TextView IGST_tv = (TextView) row.getChildAt(23);
            TextView CGST_tv = (TextView) row.getChildAt(6);
            TextView SGST_tv = (TextView) row.getChildAt(15);
            TextView OriginalRate_tv = (TextView) row.getChildAt(27);

            int id = Integer.parseInt(itemId.getText().toString().trim());
            int sno = count;
            String name = itemName.getText().toString().trim();
            String hsncode = HSNCode.getText().toString().trim();
            String UOM = UOM_tv.getText().toString().trim();
            Double qty = Double.parseDouble(itemQty.getText().toString().trim());
            double rate = Double.parseDouble(itemRate.getText().toString().trim());
            double originalRate = Double.parseDouble(
                    OriginalRate_tv.getText().toString().trim().equals("")?"0": OriginalRate_tv.getText().toString().trim());
            double amount =0;
            /*if(REVERSETAX)
                amount = originalRate *qty;
            else
                amount = Double.parseDouble(itemAmount.getText().toString().trim());*/
            amount = originalRate *qty;
            if(String.format("%.2f",amount).length()>8)
                AMOUNTPRINTINNEXTLINE = 1;
            String taxIndex = " ";
            double TaxRate =0;
            if(chk_interstate.isChecked())
                TaxRate = Double.parseDouble(IGST_tv.getText().toString().trim());
            else
                TaxRate = Double.parseDouble(CGST_tv.getText().toString().trim()) + Double.parseDouble(SGST_tv.getText().toString().trim());


            for (BillTaxSlab taxEntry : billTaxSlabs)
            {
                if(String.format("%.2f",TaxRate).equals(String.format("%.2f",taxEntry.getTaxRate())))
                {
                    taxIndex = taxEntry.getTaxIndex();
                    break;
                }
            }
            BillKotItem billKotItem = new BillKotItem(sno, name, qty, rate, amount, hsncode,UOM,taxIndex);
            billKotItems.add(billKotItem);
            count++;

        }
        return billKotItems;
    }

    public ArrayList<BillTaxSlab> TaxSlabPrint_IntraState() {
        ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();

        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForTaxSlabPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            int count = 0;
            //System.out.println(crsrTax.getCount());
            if (crsrTax.moveToFirst()) {
                do {
                    Double taxpercent = Double.parseDouble(String.format("%.2f",(crsrTax.getDouble(crsrTax.getColumnIndex("CGSTRate")) +
                            crsrTax.getDouble(crsrTax.getColumnIndex("SGSTRate")))));

                    Double cgstamt  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("CGSTAmount"))));
                    Double sgstamt  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("SGSTAmount"))));
                    Double taxableValue  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"))));

                    if(taxpercent ==0)
                        continue;

                    BillTaxSlab taxItem = new BillTaxSlab("",taxpercent, 0.00,cgstamt,sgstamt, taxableValue,cgstamt+sgstamt);
                    int found =0;
                    for (BillTaxSlab taxSlabItem : billTaxSlabs )
                    {
                        if (taxSlabItem.getTaxRate() == taxpercent)
                        {
                            taxSlabItem.setCGSTAmount(taxSlabItem.getCGSTAmount()+cgstamt);
                            taxSlabItem.setSGSTAmount(taxSlabItem.getSGSTAmount()+sgstamt);
                            taxSlabItem.setTaxableValue(taxSlabItem.getTaxableValue()+taxableValue);
                            taxSlabItem.setTotalTaxAmount(taxSlabItem.getTotalTaxAmount()+cgstamt+sgstamt);
                            found =1;
                            break;
                        }
                    }
                    if(found == 0){
                        taxItem.setTaxIndex(Character.toString((char)('A'+count)));
                        count++;
                        billTaxSlabs.add(taxItem);
                    }

                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxSlabs = new ArrayList<BillTaxSlab>();
        }
        finally
        {
            return billTaxSlabs;
        }

    }
    public ArrayList<BillTaxSlab> TaxSlabPrint_InterState() {
        ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();

        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForTaxSlabPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            int count = 0;
            //System.out.println(crsrTax.getCount());
            if (crsrTax.moveToFirst()) {
                do {
                    Double taxpercent = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("IGSTRate"))));
                    Double igstamt  = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("IGSTAmount"))));
                    Double taxableValue  = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"))));

                    if(taxpercent ==0)
                        continue;
                    BillTaxSlab taxItem = new BillTaxSlab("",taxpercent, igstamt,0.00,0.00, taxableValue,igstamt);
                    int found =0;
                    for (BillTaxSlab taxSlabItem : billTaxSlabs )
                    {
                        if (taxSlabItem.getTaxRate() == taxpercent)
                        {
                            taxSlabItem.setIGSTAmount(taxSlabItem.getIGSTAmount()+igstamt);
                            taxSlabItem.setTaxableValue(taxSlabItem.getTaxableValue()+taxableValue);
                            taxSlabItem.setTotalTaxAmount(taxSlabItem.getTotalTaxAmount()+igstamt);
                            found =1;
                            break;
                        }
                    }
                    if(found == 0){
                        taxItem.setTaxIndex(Character.toString((char)('A'+count)));
                        count++;
                        billTaxSlabs.add(taxItem);
                    }

                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxSlabs = new ArrayList<BillTaxSlab>();
        }
        finally
        {
            return billTaxSlabs;
        }

    }
    public ArrayList<BillTaxItem> taxPrint() {
        ArrayList<BillTaxItem> billTaxItems = new ArrayList<BillTaxItem>();
        try {

            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForCGSTTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "CGST "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = crsrTax.getString(crsrTax.getColumnIndex("CGSTRate"));
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("CGSTAmount")));

                    BillTaxItem taxItem = new BillTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxItems = new ArrayList<BillTaxItem>();
        }
        finally
        {
            return billTaxItems;
        }
    }
    public ArrayList<BillTaxItem> taxPrint_IGST() {
        ArrayList<BillTaxItem> billTaxItems = new ArrayList<BillTaxItem>();
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForIGSTTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "IGST "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = crsrTax.getString(crsrTax.getColumnIndex("IGSTRate"));
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("IGSTAmount")));

                    BillTaxItem taxItem = new BillTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxItems = new ArrayList<BillTaxItem>();
        }
        finally
        {
            return billTaxItems;
        }
    }
    public ArrayList<BillServiceTaxItem> cessTaxPrint() {
        ArrayList<BillServiceTaxItem> billcessTaxItems = new ArrayList<BillServiceTaxItem>();
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForcessTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "cess "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("cessRate")));
                    Double taxvalue = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("cessAmount"))));

                    BillServiceTaxItem taxItem = new BillServiceTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billcessTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billcessTaxItems = new ArrayList<BillServiceTaxItem>();
        }
        finally
        {
            return billcessTaxItems;
        }

    }
    public ArrayList<BillTaxItem> otherChargesPrint() {
        ArrayList<BillTaxItem> billOtherChargesItems = new ArrayList<BillTaxItem>();
        String billingmode= "";
        if(isReprint)
        {
            Cursor crsrTax = dbBillScreen.getBillDetail(Integer.parseInt(tvBillNumber.getText().toString()));
            if(crsrTax.moveToFirst())
            {
                String taxname = "OtherCharges";
                double taxpercent = 0;
                Double taxvalue = crsrTax.getDouble(crsrTax.getColumnIndex("DeliveryCharge"));

                if(taxvalue>0)
                {
                    BillTaxItem taxItem = new BillTaxItem(taxname, (taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billOtherChargesItems.add(taxItem);
                    /*double totalamt = Double.parseDouble(tvBillAmount.getText().toString().trim());
                    totalamt+= taxvalue;
                    tvBillAmount.setText(String.format("%.2f", totalamt));*/
                }
            }

        }else
        { // fresh print
            if(jBillingMode==4)
                billingmode= HomeDeliveryCaption;
            else if (jBillingMode == 3)
                billingmode= TakeAwayCaption;
            Cursor crsrTax = dbBillScreen.getItemsForOtherChargesPrint(billingmode);
            if (crsrTax.moveToFirst()) {
                do {

                    String taxname = crsrTax.getString(crsrTax.getColumnIndex("ModifierDescription"));
                    String taxpercent = "0";
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("ModifierAmount")));

                    BillTaxItem taxItem = new BillTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billOtherChargesItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }
        return billOtherChargesItems;
    }

    public ArrayList<BillServiceTaxItem> servicetaxPrint() {
        ArrayList<BillServiceTaxItem> billServiceTaxItems = new ArrayList<BillServiceTaxItem>();

        Cursor crsrTax = dbBillScreen.getItemsForServiceTaxPrint(Integer.valueOf(tvBillNumber.getText().toString()));
        if (crsrTax.moveToFirst()) {
            //do {
            BillServiceTaxItem ServicetaxItem = new BillServiceTaxItem("Service Tax", Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("ServiceTaxPercent"))), Double.parseDouble(String.format("%.2f", Double.parseDouble(tvServiceTaxTotal.getText().toString()))));
            billServiceTaxItems.add(ServicetaxItem);
            //Log.d("netserviceTAX",crsrTax.getString(crsrTax.getColumnIndex("ServiceTaxPercent")) );
            //} while (crsrTax.moveToNext());
        }
        return billServiceTaxItems;
    }

    public ArrayList<BillSubTaxItem> subtaxPrint() {
        ArrayList<BillSubTaxItem> billSubTaxItems = new ArrayList<BillSubTaxItem>();
        Cursor crsrSubTax = dbBillScreen.getAllSubTaxConfig("2");
        if (crsrSubTax.moveToFirst()) {
            do {
                String subtaxname = crsrSubTax.getString(crsrSubTax.getColumnIndex("SubTaxDescription"));
                String subtaxpercent = crsrSubTax.getString(crsrSubTax.getColumnIndex("SubTaxPercent"));
                double subtaxvalue = Double.parseDouble(tvSubTotal.getText().toString().trim()) * (Double.parseDouble(subtaxpercent) / 100);

                BillSubTaxItem taxSubItem = new BillSubTaxItem(subtaxname, Double.parseDouble(subtaxpercent), Double.parseDouble(String.format("%.2f", subtaxvalue)));
                billSubTaxItems.add(taxSubItem);

                fTotalsubTaxPercent += Float.parseFloat(subtaxpercent);

            } while (crsrSubTax.moveToNext());
        }
        return billSubTaxItems;
    }

    public ArrayList<BillServiceTaxItem> SGSTtaxPrint() {
        ArrayList<BillServiceTaxItem> billServiceTaxItems = new ArrayList<BillServiceTaxItem>();
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForSGSTTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "SGST "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = crsrTax.getString(crsrTax.getColumnIndex("SGSTRate"));
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("SGSTAmount")));

                    BillServiceTaxItem taxItem = new BillServiceTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billServiceTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billServiceTaxItems = new ArrayList<BillServiceTaxItem>();
        }
        finally
        {
            return billServiceTaxItems;
        }
    }

    public void printKOT(View view) {
        int proceed = 1;

        String tempCustId = "";
        if (tblOrderItems.getChildCount() < 1){
            MsgBox.Show("Warning", "Add Item before Saving KOT");
            proceed =0;
        }
        else if (jBillingMode==4 || jBillingMode==3)
        {
            tempCustId = edtCustId.getText().toString();
            if (tempCustId.equalsIgnoreCase("") || tempCustId.equalsIgnoreCase("0")) {
                MsgBox.Show("Warning", "Please Select Customer for Billing");
                proceed =0;
            }
        }
        if(proceed == 1)
        {
            if (isPrinterAvailable) {
                iPrintKOTStatus = 0;
                int i = SaveKOT();
                if(i == 0) // print only when KOT is saved successfully
                {
                    return;
                }
                PrintKOT();
                for(i=0;i<tblOrderItems.getChildCount();i++)
                {
                    TableRow row = (TableRow)tblOrderItems.getChildAt(i);
                    ImageButton img = (ImageButton) row.getChildAt(19);
                    img.setVisibility(View.INVISIBLE);

                    TextView qty = (TextView) row.getChildAt(3);
                    qty.setEnabled(false);

                    TextView rate = (TextView) row.getChildAt(4);
                    rate.setEnabled(false);

                }
                //tblOrderItems.removeAllViews();
                //Cursor LoadKOT = dbBillScreen.getKOTItems(0,0, 0);
//                if (LoadKOT.moveToFirst()) {
//                    LoadKOTItems(LoadKOT);
//                    btnPrintBill.setEnabled(true);
//                    btnPayBill.setEnabled(true);
//                }
                btnPrintBill.setEnabled(true);
                btnPayBill.setEnabled(true);

            } else {
                Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
                askForConfig();
            }
        }
    }

    protected void PrintKOT() {
        if (isPrinterAvailable) {
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print KOT");
                return;
            } else {
                int tableId = 0, waiterId = 0, orderId = 0;
                if (!tvBillNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    /*tableId = Integer.parseInt(tvTableNumber.getText().toString().trim());
                    waiterId = Integer.parseInt(tvWaiterNumber.getText().toString().trim());*/
                    orderId = Integer.parseInt(tvBillNumber.getText().toString().trim());
                    ArrayList<BillKotItem> billKotItems = kotPrint();
                    if(billKotItems.size()<1)
                    {
                        MsgBox.Show("Oops", "No new item in KOT to print");
                        return;
                    }
                    PrintKotBillItem item = new PrintKotBillItem();
                    item.setBillKotItems(billKotItems);
                    item.setTableNo(String.valueOf(tableId));
                    item.setWaiterNo(waiterId);
                    item.setBillNo(String.valueOf(iKOTNo));
                    item.setOrderBy(strUserName);
                    item.setBillingMode(String.valueOf(jBillingMode));
                    item.setDate(tvDate.getText().toString());
                    String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
                    item.setTime(strTime);
//                    item.setDate(businessDate);
//                    item.setTime(TimeUtil.getTime());
            /*Intent intent = new Intent(getApplicationContext(), PrinterSohamsaActivity.class);
            intent.putExtra("printType", "KOT");
            intent.putExtra("printData", item);
            startActivity(intent);*/
                    String prf = Preferences.getSharedPreferencesForPrint(BillingHomeDeliveryActivity.this).getString("kot", "--Select--");
                    Intent intent = null;
                    if (prf.equalsIgnoreCase("Sohamsa")) {
                    /*intent = new Intent(getApplicationContext(), PrinterSohamsaActivity.class);
                    intent.putExtra("printType", "KOT");
                    intent.putExtra("printData", item);
                    startActivity(intent);*/
                        //printSohamsa(item, "KOT");
                        Toast.makeText(myContext, "KOT is printing... ", Toast.LENGTH_LONG).show();
                    } else if (prf.equalsIgnoreCase("Heyday")) {
                    /*intent = new Intent(getApplicationContext(), PrinterFragment.class);
                    intent.putExtra("printType", "KOT");
                    intent.putExtra("printData", item);
                    startActivity(intent);*/
                        if (isPrinterAvailable) {
                            printHeydeyKOT(item, "KOT");
                        } else {
                            askForConfig();
                        }
                        Toast.makeText(myContext, "KOT is printing... ", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(myContext, "Printer not configured. Kindly goto settings and configure printer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BillingHomeDeliveryActivity.this, "Please enter bill,waiter,table number", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
            askForConfig();
        }
    }

    public void printBILL(View view) {
        int proceed =1;
        if (tblOrderItems.getChildCount() < 1){
            MsgBox.Show("Warning", "Add item before Printing Bill");
            proceed =0;
        }else if (tvBillAmount.getText().toString().equals("") ) {
            MsgBox.Show("Warning", "Please add item to make bill");
            proceed =0;
        } else if ( tvBillAmount.getText().toString().equals("0.00")) {
            MsgBox.Show("Warning", "Please make bill of amount greater than 0.00");
            proceed =0;
        }else if (edtCustId.getText().toString().equals("0")){
            MsgBox.Show("Warning", "Add customer before Printing Bill");
            proceed =0;
        }else if (chk_interstate.isChecked() && spnr_pos.getSelectedItem().equals("")) {
            MsgBox.Show("Warning", "Please Select Code for Intersate Supply");
            proceed =0;
        }

        else if (jBillingMode==4 )
        {
            String tempCustId = edtCustId.getText().toString();
            if (tempCustId.equalsIgnoreCase("") || tempCustId.equalsIgnoreCase("0")) {
                MsgBox.Show("Warning", "Please Select Customer for Billing");
                proceed =0;
            }
        }
        if(proceed == 0)
            return;
        if (isPrinterAvailable) {
            strPaymentStatus = "Paid";
            PrintBillPayment = 1;

            // Print Bill with Save Bill
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {

                if (BILLAMOUNTROUNDOFF == 1) {
                    String temp = tvBillAmount.getText().toString().trim();
                    double finalAmount = Double.parseDouble(tvBillAmount.getText().toString().trim());
                    double roundOffFinalAmount = 0;

                    if (!temp.contains(".00")){
                        roundOffFinalAmount = Math.round(finalAmount);
                        tvBillAmount.setText(String.valueOf(roundOffFinalAmount));
                        fRoundOfValue = Float.parseFloat("0" + temp.substring(temp.indexOf(".")));
                    }
                }

                l(2, true);
                updateOutwardStock();
                PrintNewBill();
                Toast.makeText(myContext, "Bill Saved Successfully", Toast.LENGTH_SHORT).show();
                if (jBillingMode == 3) {
                    int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
                    //Log.d("Pick Up:TenderActivity Result", "Items deleted from pending KOT:" + iResult);
                    ClearAll();
                    //Close(null);
                } else if (jBillingMode == 4) {
                    //int iResult = dbBillScreen.deleteKOTItems(iCustId, String.valueOf(jBillingMode));
                    //Log.d("Pick Up:TenderActivity Result", "Items deleted from pending KOT:" + iResult);
                    ClearAll();
                    // Close(null);
                }
            }
        } else {
            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
            askForConfig();
        }
    }
    void calculateDiscountAmount()
    {
        fTotalDiscount =0;
        for(int i=0;i<tblOrderItems.getChildCount();i++)
        {
            TableRow row = (TableRow)tblOrderItems.getChildAt(i);
            TextView discountAmt = (TextView) row.getChildAt(9);
            if(discountAmt.getText().toString()!= null && !discountAmt.getText().toString().equals("") )
                fTotalDiscount += Double.parseDouble(discountAmt.getText().toString());
        }
    }
    protected void PrintNewBill() {
        if (isPrinterAvailable) {
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {
                int tableId = 0, waiterId = 0, orderId = 0;
                if (!tvBillNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    tableId = 0;//Integer.parseInt(tvTableNumber.getText().toString().trim());
                    waiterId = 0;//Integer.parseInt(tvWaiterNumber.getText().toString().trim());
                    orderId = Integer.parseInt(tvBillNumber.getText().toString().trim());
                    // ArrayList<BillKotItem> billKotItems = billPrint();
                    ArrayList<BillTaxItem> billOtherChargesItems = otherChargesPrint();
                    ArrayList<BillTaxItem> billTaxItems ;
                    ArrayList<BillServiceTaxItem> billServiceTaxItems = new ArrayList<BillServiceTaxItem>();
                    ArrayList<BillServiceTaxItem> billcessTaxItems = new ArrayList<BillServiceTaxItem>();
                    ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();

                    billcessTaxItems = cessTaxPrint();
                    //ArrayList<BillSubTaxItem> billSubTaxItems = subtaxPrint();
                    PrintKotBillItem item = new PrintKotBillItem();

                    item.setPrintService(PRINTSERVICE);
                    item.setBoldHeader(BOLDHEADER);
                    item.setOwnerDetail(PRINTOWNERDETAIL);

                    Cursor crsrCustomer = dbBillScreen.getCustomer(Integer.valueOf(edtCustId.getText().toString()));
                    String CustDetails = "---";
                    if (crsrCustomer.moveToFirst()) {
                        CustDetails = crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName"));
                        if(jBillingMode ==4)
                        {
                            CustDetails = CustDetails +"\n"+crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress"));
                        }
                    }
                    if(reprintBillingMode>0) {
                        item.setIsDuplicate("\n(Duplicate Bill)");
                    }else{
                        item.setIsDuplicate("");
                    }
                    if(chk_interstate.isChecked())
                    {
                        item.setIsInterState("y");
                        billTaxSlabs = TaxSlabPrint_InterState();
                    }
                    else
                    {
                        item.setIsInterState("n");
                        billTaxSlabs = TaxSlabPrint_IntraState();
                    }
                    ArrayList<BillKotItem> billKotItems = billPrint(billTaxSlabs);
                    item.setCustomerName(CustDetails);
                    item.setBillKotItems(billKotItems);
                    item.setAmountInNextLine(AMOUNTPRINTINNEXTLINE);
                    item.setBillOtherChargesItems(billOtherChargesItems);
                    //item.setBillTaxItems(billTaxItems);
                    //item.setBillServiceTaxItems(billServiceTaxItems);
                    item.setBillTaxSlabs(billTaxSlabs);
                    item.setBillcessTaxItems(billcessTaxItems);
                    //item.setBillSubTaxItems(billSubTaxItems);
                    item.setSubTotal(Double.parseDouble(tvSubTotal.getText().toString().trim()));
                    item.setNetTotal(Double.parseDouble(tvBillAmount.getText().toString().trim()));
                    //Log.d("netTotal",String.valueOf(item.getNetTotal()) );
                    item.setTableNo(String.valueOf(tableId));
                    item.setWaiterNo(waiterId);
                    item.setUTGSTEnabled(UTGSTENABLED);
                    item.setHSNPrintEnabled_out(HSNPRINTENABLED);
                    String billNoPrefix  = db.getBillNoPrefix();
                    item.setBillNo(billNoPrefix+String.valueOf(orderId));
                    item.setOrderBy(strUserName);
                    item.setBillingMode(String.valueOf(jBillingMode));
                    if (strPaymentStatus.equalsIgnoreCase("")) {
                        item.setPaymentStatus("");
                    } else {
                        item.setPaymentStatus(strPaymentStatus);
                    }
                    /*item.setDate(businessDate);
                    item.setTime(TimeUtil.getTime());*/
                    item.setdiscountPercentage(Float.parseFloat(tvDiscountPercentage.getText().toString()));
                    //    if(ItemwiseDiscountEnabled ==1)
                    calculateDiscountAmount();
                    item.setFdiscount(fTotalDiscount);

                    item.setTotalsubTaxPercent(fTotalsubTaxPercent);
                    item.setTotalSalesTaxAmount(tvTaxTotal.getText().toString());
                    item.setTotalServiceTaxAmount(tvServiceTaxTotal.getText().toString());
                    item.setRoundOff(fRoundOfValue);
                    String date_today = tvDate.getText().toString();
                    //Log.d("Date ", date_today);
                    try {
                        Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
                        Cursor paymentModeinBillcrsr = db.getBillDetail_counter((orderId),String.valueOf(date1.getTime()));
                        if(paymentModeinBillcrsr!=null && paymentModeinBillcrsr.moveToFirst())
                        {
                            double cardValue = 0.00,eWalletValue = 0.00,couponValue = 0.00,pettyCashValue = 0.00,
                                    cashValue = 0.00,changeValue = 0.00;

                            if(paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex("CardPayment")) !=null)
                                cardValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex("CardPayment"));
                            if(paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex("WalletPayment")) !=null)
                                eWalletValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex("WalletPayment"));
                            if(paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex("CouponPayment")) !=null)
                                couponValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex("CouponPayment"));
                            if(paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex("PettyCashPayment")) !=null)
                                pettyCashValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex("PettyCashPayment"));
                            if(paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex("CashPayment")) !=null)
                                cashValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex("CashPayment"));
                            if(paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex("ChangePayment")) !=null)
                                changeValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex("ChangePayment"));


                            cardValue = Double.parseDouble(String.format("%.2f",cardValue));
                            eWalletValue = Double.parseDouble(String.format("%.2f",eWalletValue));
                            couponValue = Double.parseDouble(String.format("%.2f",couponValue));
                            pettyCashValue = Double.parseDouble(String.format("%.2f",pettyCashValue));
                            cashValue = Double.parseDouble(String.format("%.2f",cashValue));
                            changeValue = Double.parseDouble(String.format("%.2f",changeValue));

                            item.setCardPaymentValue(cardValue);
                            item.seteWalletPaymentValue(eWalletValue);
                            item.setCouponPaymentValue(couponValue);
                            item.setPettyCashPaymentValue(pettyCashValue);
                            item.setCashPaymentValue(cashValue);
                            item.setChangePaymentValue(changeValue);
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    if(reprintBillingMode == 0) {
                        if (jBillingMode == 4)
                            item.setStrBillingModeName(HomeDeliveryCaption);
                        else if (jBillingMode == 3)
                            item.setStrBillingModeName(TakeAwayCaption);

                        item.setDate(tvDate.getText().toString());
                        String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
                        item.setTime(strTime);

                    }else // reprint
                    {
                        switch (reprintBillingMode)
                        {
                            case 1 : item.setStrBillingModeName(DineInCaption);
                                item.setBillingMode("1");
                                //item.setPaymentStatus(""); // payment status not required for dinein Mode
                                break;
                            case 2 : item.setStrBillingModeName(CounterSalesCaption);
                                item.setBillingMode("2");
                                //item.setPaymentStatus(""); // payment status not required for CounterSales Mode
                                break;
                            case 3 : item.setStrBillingModeName(TakeAwayCaption);
                                item.setBillingMode("3");
                                break;
                            case 4 : item.setStrBillingModeName(HomeDeliveryCaption);
                                item.setBillingMode("4");
                                if(crsrCustomer!=null){
                                    String CustDetail = crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName"));
                                    CustDetail = CustDetail +"\n"+crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress"));
                                    item.setCustomerName(CustDetail);
                                }
                                break;
                        }
                        try{
                            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
                            Cursor c  = db.getBillDetail_counter(orderId,String.valueOf(date.getTime()));
                            if(c!=null && c.moveToNext())
                            {

                                String time = c.getString(c.getColumnIndex("Time"));
                                item.setTime(time);
                                item.setDate(tvDate.getText().toString());
                                float round = c.getString(c.getColumnIndex("RoundOff"))==(null)?0:c.getFloat(c.getColumnIndex("RoundOff"));
                                item.setRoundOff(Float.parseFloat(String.format("%.2f",round)));

                                if(reprintBillingMode==1)
                                {
                                    String tableNo = c.getString(c.getColumnIndex("TableNo"));
                                    String splitno = c.getString(c.getColumnIndex("TableSplitNo"));
                                    if(splitno!=null && !splitno.equals(""))
                                        item.setTableNo(tableNo+" - "+splitno);
                                    else
                                        item.setTableNo(tableNo);
                                }
                                String userId = c.getString(c.getColumnIndex("UserId"));
                                if( userId!=null)
                                {
                                    Cursor user_cursor = db.getUsers_counter(userId);
                                    if(user_cursor!=null && user_cursor.moveToFirst())
                                    {
                                        item.setOrderBy(user_cursor.getString(user_cursor.getColumnIndex("Name")));
                                    }
                                }

                            }}catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    String prf = Preferences.getSharedPreferencesForPrint(BillingHomeDeliveryActivity.this).getString("bill", "--Select--");
                /*Intent intent = new Intent(getApplicationContext(), PrinterSohamsaActivity.class);*/
                    Intent intent = null;
                    if (prf.equalsIgnoreCase("Sohamsa")) {
                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";
                        Cursor crsrHeaderFooterSetting = null;
                        crsrHeaderFooterSetting = dbBillScreen.getBillSetting();
                        if (crsrHeaderFooterSetting.moveToFirst()) {
                            try {
                                tokens = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText")).split(Pattern.quote("|"));
                            } catch (Exception e) {
                                tokens[0] = "";
                                tokens[1] = "";
                                tokens[2] = "";
                            }
                            if (!tokens[0].equalsIgnoreCase(""))
                                item.setAddressLine1(tokens[0]);
                            if (!tokens[1].equalsIgnoreCase(""))
                                item.setAddressLine2(tokens[1]);
                            if (!tokens[2].equalsIgnoreCase(""))
                            {
                                String addres3= tokens[2];
                                if(chk_interstate.isChecked())
                                {
                                    addres3 = addres3 + " ("+getState_pos(ownerPos)+") ";
                                }
                                if(reprintBillingMode>0) {
                                    item.setAddressLine3(addres3+"\n(Duplicate Bill)");
                                }else{
                                    item.setAddressLine3(addres3);
                                }
                            }
                            item.setFooterLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText1")));
                            item.setFooterLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText2")));
                            item.setFooterLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText3")));
                            item.setFooterLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText4")));
                            item.setFooterLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText5")));
                        } else {
                            Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                        }

                        //printSohamsaBILL(item, "BILL");
                    } else if (prf.equalsIgnoreCase("Heyday")) {
                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";
                        Cursor crsrHeaderFooterSetting = null;
                        crsrHeaderFooterSetting = db.getOwnerDetail_counter();
                        if (crsrHeaderFooterSetting.moveToFirst()) {
                            try {
                                tokens[0] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("GSTIN"));
                                tokens[1] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FirmName"));
                                tokens[2] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("Address"));
                            } catch (Exception e) {
                                tokens[0] = "";
                                tokens[1] = "";
                                tokens[2] = "";
                            }
                            if (!tokens[0].equalsIgnoreCase(""))
                                item.setAddressLine1(tokens[0]);
                            if (!tokens[1].equalsIgnoreCase(""))
                                item.setAddressLine2(tokens[1]);

                            if(chk_interstate.isChecked())
                            {
                                item.setCustomerName(item.getCustomerName()+ "  ("+(spnr_pos.getSelectedItem().toString())+") ");
                                tokens[2] =  tokens[2] + "\n ("+getState_pos(db.getOwnerPOS_counter())+") ";
                            }
                            item.setAddressLine3(tokens[2]);
                            crsrHeaderFooterSetting = db.getBillSettings();
                            if(crsrHeaderFooterSetting.moveToNext()) {
                                item.setHeaderLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText1")));
                                item.setHeaderLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText2")));
                                item.setHeaderLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText3")));
                                item.setHeaderLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText4")));
                                item.setHeaderLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText5")));
                                item.setFooterLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText1")));
                                item.setFooterLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText2")));
                                item.setFooterLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText3")));
                                item.setFooterLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText4")));
                                item.setFooterLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText5")));
                            }
                        } else {
                            Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                        }
                        //startActivity(intent);
                        if (isPrinterAvailable) {
                            printHeydeyBILL(item, "BILL");
                        } else {
                            askForConfig();
                        }
                    } else {
                        Toast.makeText(myContext, "Printer not configured. Kindly goto settings and configure printer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BillingHomeDeliveryActivity.this, "Please Enter Bill, Waiter, Table Number", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
            askForConfig();
            MsgBox.Show("Printer Error", "Kindly Note your bill number for reprinting the bill. \n Your order no is  "+
                    tvBillNumber.getText().toString());
        }
    }

    public void ReprintKOT(final View v) {
        //takeScreenshot();
       /* tblOrderItems.removeAllViews();

        AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.tender_dinein, null);

        final EditText txtTblNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInTenderTableNumber);
        final EditText txtTblSplitNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInTenderTableSplitNo);
        txtTblSplitNo.setText("1");

        DineInTenderDialog.setIcon(R.drawable.ic_launcher).setTitle("RePrint KOT").setMessage("Enter Table Number")
                .setView(vwAuthorization).setNegativeButton("Cancel", null)
                .setPositiveButton("RePrint", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if (txtTblNo.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please Enter Table Number");
                            return;
                        } else if (txtTblSplitNo.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please Enter Table Split Number");
                            return;
                        } else {

                            tvTableNumber.setText(txtTblNo.getText().toString());
                            tvTableSplitNo.setText(txtTblSplitNo.getText().toString());
                            tvSubUdfValue.setText("1");
                            Cursor BillItems = dbBillScreen.getKOTItems(Integer.parseInt(txtTblNo.getText().toString()),
                                    1, Integer.parseInt(txtTblSplitNo.getText().toString()));
                            if (BillItems.moveToFirst()) {
                                REPRINT_KOT =1;
                                LoadKOTItems(BillItems);

                                printKOT(v);
                            } else {
                                MsgBox.Show("Warning",
                                        "No KOT is present for the table number " + txtTblNo.getText().toString());
                            }
                        }
                    }
                }).show();*/
    }


    /*************************************************************************************************************************************
     * Loads KOT order items to billing table
     *
     * @param crsrBillItems : Cursor with KOT order item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void LoadItemsForReprintBill(Cursor crsrBillItems) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvHSn, tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt, tvUOM;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;

        if (crsrBillItems.moveToFirst()) {
            if (crsrBillItems.getString(crsrBillItems.getColumnIndex("IsReverseTaxEnable")).equalsIgnoreCase("YES")) { // reverse tax
                REVERSETAX = true;
            }else
            {
                REVERSETAX = false;
            }

            if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintOwnerDetail")) == 1)) { // print owner detail
                PRINTOWNERDETAIL = 1;
            }else
            {
                PRINTOWNERDETAIL = 0;
            }

            if ((crsrSettings.getInt(crsrSettings.getColumnIndex("BoldHeader")) == 1)) { // bold header
                BOLDHEADER = 1;
            }else
            {
                BOLDHEADER = 0;
            }

            if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintService")) == 1)) { // bold header
                PRINTSERVICE = 1;
            }else
            {
                PRINTSERVICE = 0;
            }

            if ((crsrSettings.getInt(crsrSettings.getColumnIndex("BillAmountRoundOff")) == 1)) { // Bill Amount Round Off
                BILLAMOUNTROUNDOFF = 1;
            }else
            {
                BILLAMOUNTROUNDOFF = 0;
            }

            // Display items in table
            do {
                rowItem = new TableRow(myContext);
                rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Item Number
                Number = new CheckBox(myContext);
                Number.setWidth(40);
                Number.setTextSize(0);
                Number.setTextColor(Color.TRANSPARENT);
                Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                // Item Name
                tvName = new TextView(myContext);
                tvName.setWidth(135);
                tvName.setTextSize(11);
                tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));

                //hsn code
                tvHSn = new TextView(myContext);
                tvHSn.setWidth(67); // 154px ~= 230dp
                tvHSn.setTextSize(11);
                if (GSTEnable.equalsIgnoreCase("1") && (HSNEnable_out != null) && HSNEnable_out.equals("1")) {
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                }

                // Quantity
                etQty = new EditText(myContext);
                etQty.setWidth(55);
                etQty.setTextSize(11);
                etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                //etQty.setOnClickListener(Qty_Rate_Click);
                etInputValidate.ValidateDecimalInput(etQty);

                // Rate
                etRate = new EditText(myContext);
                etRate.setWidth(75);
                etRate.setEnabled(false);
                etRate.setTextSize(11);
                etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Value"))));

                // Amount
                tvAmount = new TextView(myContext);
                tvAmount.setWidth(105);
                tvAmount.setTextSize(11);
                tvAmount.setText(
                        String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));

                // Sales Tax%
                tvTaxPercent = new TextView(myContext);
                tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CGSTRate")));

                // Sales Tax Amount
                tvTaxAmt = new TextView(myContext);
                tvTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CGSTAmount")));

                // Discount %
                tvDiscPercent = new TextView(myContext);
                tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                // Discount Amount
                tvDiscAmt = new TextView(myContext);
                tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                // Dept Code
                tvDeptCode = new TextView(myContext);
                tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                // Categ Code
                tvCategCode = new TextView(myContext);
                tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                // Kitchen Code
                tvKitchenCode = new TextView(myContext);
                tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                // Tax Type
                tvTaxType = new TextView(myContext);
                tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                // Modifier Amount
                tvModifierCharge = new TextView(myContext);
                tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                // Service Tax %
                tvServiceTaxPercent = new TextView(myContext);
                tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SGSTRate")));

                // Service Tax Amount
                tvServiceTaxAmt = new TextView(myContext);
                tvServiceTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SGSTAmount")));

                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                ImgDelete = new ImageButton(myContext);
                ImgDelete.setImageResource(res);
                ImgDelete.setVisibility(View.INVISIBLE);
                // UOM
                tvUOM = new TextView(BillingHomeDeliveryActivity.this);
                tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                // space
                TextView tvSpace = new TextView(BillingHomeDeliveryActivity.this);
                TextView tvSpace1 = new TextView(BillingHomeDeliveryActivity.this);
                TextView tvPrintKOTStatus = new TextView(BillingHomeDeliveryActivity.this);

                // SupplyType
                TextView SupplyType = new TextView(BillingHomeDeliveryActivity.this);
                SupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));

                // IGSTRate
                TextView tvIGSTRate = new TextView(BillingHomeDeliveryActivity.this);
                tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                // IGSTAmount
                TextView tvIGSTAmt = new TextView(BillingHomeDeliveryActivity.this);
                tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                // cessRate
                TextView tvcess = new TextView(BillingHomeDeliveryActivity.this);
                tvcess.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                // IGSTAmount
                TextView tvcessAmt = new TextView(BillingHomeDeliveryActivity.this);
                tvcessAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessAmount")));

                TextView originalRate = new TextView(this);
                originalRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("OriginalRate")));

                TextView taxableValue = new TextView(this);
                taxableValue.setText(String.format("%.2f",crsrBillItems.getDouble(crsrBillItems.getColumnIndex("TaxableValue"))));


                // Add all text views and edit text to Item Row
                // rowItem.addView(tvNumber);
                rowItem.addView(Number);
                rowItem.addView(tvName);
                rowItem.addView(tvHSn);
                rowItem.addView(etQty);
                rowItem.addView(etRate);
                rowItem.addView(tvAmount);
                rowItem.addView(tvTaxPercent);
                rowItem.addView(tvTaxAmt);
                rowItem.addView(tvDiscPercent);
                rowItem.addView(tvDiscAmt);
                rowItem.addView(tvDeptCode);
                rowItem.addView(tvCategCode);
                rowItem.addView(tvKitchenCode);
                rowItem.addView(tvTaxType);
                rowItem.addView(tvModifierCharge);
                rowItem.addView(tvServiceTaxPercent);
                rowItem.addView(tvServiceTaxAmt);//16
                rowItem.addView(SupplyType);//17
                rowItem.addView(tvSpace);//18
                rowItem.addView(ImgDelete);//19
                rowItem.addView(tvSpace1);//20
                rowItem.addView(tvPrintKOTStatus);//21
                rowItem.addView(tvUOM);//22
                rowItem.addView(tvIGSTRate);//23
                rowItem.addView(tvIGSTAmt);//24
                rowItem.addView(tvcess);//25
                rowItem.addView(tvcessAmt);//26
                rowItem.addView(originalRate);//27
                rowItem.addView(taxableValue);//28


                // Add row to table
                tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            } while (crsrBillItems.moveToNext());

            //CalculateTotalAmountforRePrint();

        } else {
            Log.d("LoadKOTItems", "No rows in cursor");
        }
    }

    public void KOTStatus(View view) {


        Intent intentDelivery = new Intent(myContext, CustomerOrdersActivity.class);
        intentDelivery.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
        intentDelivery.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
        intentDelivery.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
        //intentDelivery.putExtra("DISCOUNT_AMOUNT", fTotalDiscount);//spUser.getString("USER_NAME", "GHOST"));
        startActivityForResult(intentDelivery, 5);

    }

    // Add Customer if Customer is not Found for Pickup and Home Delivery
    public void AddCustomer(View v) {
        try {
            if (edtCustName.getText().toString().equalsIgnoreCase("") || edtCustPhoneNo.getText().toString().equalsIgnoreCase("") || edtCustAddress.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please fill all details before adding customer");
            } else if (edtCustPhoneNo.getText().toString().length()!= 10)
            {
                MsgBox.Show("Warning", "Please fill 10 digit customer phone number");
                return;
            } else{
                Cursor crsrCust = dbBillScreen.getCustomer(edtCustPhoneNo.getText().toString());
                if (crsrCust.moveToFirst()) {
                    MsgBox.Show("Note", "Customer Already Exists");
                } else {
                    String gstin = etCustGSTIN.getText().toString().trim().toUpperCase();
                    if (gstin == null) {
                        gstin = "";
                    }
                    boolean mFlag = GSTINValidation.checkGSTINValidation(gstin);
                    if(mFlag)
                    {
                        InsertCustomer(edtCustAddress.getText().toString(), edtCustPhoneNo.getText().toString(),
                                edtCustName.getText().toString(), 0, 0, 0, gstin);
                        //ResetCustomer();
                        //MsgBox.Show("", "Customer Added Successfully");
                        Toast.makeText(myContext, "Customer Added Successfully", Toast.LENGTH_SHORT).show();
                        ControlsSetEnabled();
                    }else
                    {
                        MsgBox.Show("Invalid Information","Please enter valid GSTIN for customer");
                    }

                }
            }
        } catch (Exception ex) {
            MsgBox.Show("Error", ex.getMessage());
        }
    }




    private void InsertCustomer(String strAddress, String strContactNumber, String strName, float fLastTransaction,
                                float fTotalTransaction, float fCreditAmount, String gstin) {
        long lRowId;

        Customer objCustomer = new Customer(strAddress, strName, strContactNumber, fLastTransaction, fTotalTransaction,
                fCreditAmount, gstin,0.00);

        lRowId = dbBillScreen.addCustomer(objCustomer);
        /*edtCustId.setText(String.valueOf(lRowId));*/

        if (edtCustPhoneNo.getText().toString().length() == 10) {
            Cursor crsrCust = dbBillScreen.getCustomer(edtCustPhoneNo.getText().toString());
            if (crsrCust.moveToFirst()) {
                edtCustId.setText(crsrCust.getString(crsrCust.getColumnIndex("CustId")));
            }
        }
        Log.d("Customer", "Row Id: " + String.valueOf(lRowId));

    }



    public void ControlsSetEnabled() {
        btnAddCustomer.setVisibility(View.VISIBLE);
        //tvHSNCode_out.setEnabled(true);
        //txtSearchItemBarcode.setEnabled(true);

        listViewDept.setEnabled(true);
        listViewCateg.setEnabled(true);
        //  gridViewItems.setEnabled(true);
        mRecyclerGridView.setEnabled(true);

        btnReprint.setEnabled(true);
        btnDeliveryStatus.setEnabled(true);
        btnSaveKOT.setEnabled(true);

        btnPayBill.setEnabled(true);
        btnPrintBill.setEnabled(true);
        btnPrintKOT.setEnabled(true);

        //chk_interstate.setChecked(false);
        //spnr_pos.setSelection(0);

        /*if(tblOrderItems.getChildCount()>0)
        {
            btnPayBill.setEnabled(true);
            btnPrintBill.setEnabled(true);
            btnPrintKOT.setEnabled(true);
        }else
        {
            btnPayBill.setEnabled(false);
            btnPrintBill.setEnabled(false);
            btnPrintKOT.setEnabled(false);
        }*/
        btndepart.setEnabled(true);
        btncateg.setEnabled(true);
        btnitem.setEnabled(true);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);
            LayoutInflater UserAuthorization = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);
            final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
            final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);
            final TextView tvAuthorizationUserId = (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserId);
            final TextView tvAuthorizationUserPassword = (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserPassword);
            tvAuthorizationUserId.setVisibility(View.GONE);
            tvAuthorizationUserPassword.setVisibility(View.GONE);
            txtUserId.setVisibility(View.GONE);
            txtPassword.setVisibility(View.GONE);
            AuthorizationDialog
                    .setIcon((R.drawable.ic_launcher))
                    .setTitle("Are you sure you want to exit ?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            db.close();
                            dbBillScreen.CloseDatabase();
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);


    }


    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.wep.common.app.R.menu.menu_main, menu);
        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }else if (id == com.wep.common.app.R.id.action_home) {
            //ActionBarUtils.navigateHome(this);
            finish();
        }else if (id == com.wep.common.app.R.id.action_screen_shot) {
            com.wep.common.app.ActionBarUtils.takeScreenshot(this,findViewById(android.R.id.content).getRootView());
        }
        return super.onOptionsItemSelected(item);
    }

    void addBarCodeItemToOrderTable()
    {
        String barcode = autoCompleteTextViewSearchItemBarcode.getText().toString().trim();
        System.out.println("Barcode = "+barcode);
        if(barcode == null || barcode.equals("") )
            return;
        Cursor crsr = dbBillScreen.getItem(barcode);
        if(crsr!=null && crsr.moveToFirst())
            AddItemToOrderTable(crsr);
        else
            MsgBox.Show("Oops ","Item not found");
        autoCompleteTextViewSearchItemBarcode.setText("");
        linefeed="";
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
       // System.out.println("Richa : "+event.getKeyCode()+" SC "+event.getScanCode());
        //Toast.makeText(myContext, "Richa : "+event.getKeyCode()+" keycode = "+keyCode, Toast.LENGTH_SHORT).show();
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
        {
            //System.out.println("Richa : Enter encountered for barcode");
            addBarCodeItemToOrderTable();
        }else if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT   )
        //}else if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT ||event.getKeyCode() == KeyEvent.KEYCODE_SHIFT_LEFT  )
        {
            linefeed +=String.valueOf(event.getKeyCode());
            if(linefeed.equalsIgnoreCase("38113")|| linefeed.equalsIgnoreCase("11338")) // line feed value
                addBarCodeItemToOrderTable();
        }else
        {
            linefeed = "";
            if (dd<15 && dd >0 && CUSTOMER_FOUND==0) {
                View v = getCurrentFocus();
                //System.out.println(v);
                //EditText etbar = (EditText)findViewById(R.id.etItemBarcode);
                //EditText ed = (WepEditText)findViewById(v.getId());

                if (v.getId() != R.id.aCTVSearchItemBarcode) {
                    switch (v.getId()) {
                        case R.id.aCTVSearchItem:
                            aTViewSearchItem.setText(tx);
                            break;
                        case R.id.aCTVSearchMenuCode:
                            aTViewSearchMenuCode.setText(tx);
                            break;

                        case R.id.etCustGSTIN:
                        case R.id.edtCustName:
                        case R.id.edtCustPhoneNo:
                        case R.id.edtCustAddress:
                            EditText ed = (EditText) findViewById(v.getId());
                            //String ed_str = ed.getText().toString();
                            ed.setText(tx);
                    }
                    String bar_str = autoCompleteTextViewSearchItemBarcode.getText().toString();
                    bar_str += (char) event.getUnicodeChar();
                    autoCompleteTextViewSearchItemBarcode.setText(bar_str.trim());
                    autoCompleteTextViewSearchItemBarcode.showDropDown();

                } else if (v.getId() == R.id.aCTVSearchItemBarcode) {
                /*tx = autoCompleteTextViewSearchMenuCode.getText().toString();
                String bar_str = autoCompleteTextViewSearchItemBarcode.getText().toString().trim();*/
                    tx += (char) event.getUnicodeChar();
                    autoCompleteTextViewSearchItemBarcode.setText(tx.trim());
                    autoCompleteTextViewSearchItemBarcode.showDropDown();
                /*Toast.makeText(this, ""+bar_str+" : "+bar_str.length(), Toast.LENGTH_SHORT).show();
                if(bar_str.length()>2)
                {
                    String ss = bar_str.substring(1,bar_str.length())+bar_str.substring(0,1);
                    autoCompleteTextViewSearchItemBarcode.setText(ss.trim());
                    Toast.makeText(this, ""+ss, Toast.LENGTH_SHORT).show();
                    autoCompleteTextViewSearchItemBarcode.showDropDown();
                }*/


                }
            }
        }


        /*Toast.makeText(myContext, "keyUp:"+keyCode+" : "+dd, Toast.LENGTH_SHORT).show();*/


        return true;
    }


}


/*************************************************************************************************************************************
 * End Of File
 * <p>
 * <p>
 * /************************************************************
 * End Of File
 * <p>
 * <p>
 * /
 *************************************************************************************************************************************/