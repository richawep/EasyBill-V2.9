/****************************************************************************
 * Project Name		:	VAJRA
 * <p>
 * File Name		:	StockActivity
 * <p>
 * Purpose			:	Represents Item Stock management activity, takes care of all
 * UI back end operations in this activity, such as event
 * handling data read from or display in views.
 * <p>
 * DateOfCreation	:	26-November-2012
 * <p>
 * Author			:	Balasubramanya Bharadwaj B S
 ****************************************************************************/
package com.wepindia.pos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.Category;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.Department;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.ItemOutward;
import com.wep.common.app.models.Items;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.RecyclerDirectory.TestItemsAdapter;
import com.wepindia.pos.adapters.CategoryAdapter;
import com.wepindia.pos.adapters.DepartmentAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.StockOutwardMaintain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockActivity extends WepBaseActivity implements TextWatcher {

    // Context object
    Context myContext;

    // DatabaseHandler object
    DatabaseHandler dbStock ;
    // MessageDialog object
    MessageDialog MsgBox;

    String linefeed = "";
    String tx ="";
    // View handlers
    AutoCompleteTextView ItemLongName, AutoCompleteItemBarcodeValue;
    TextView tvExistingStock;

    EditText txtNewStock, txtRate1, txtRate2, txtRate3;
    WepButton btnUpdate,btnClearStock,btnCloseStock ;

    //  private ItemsAdapter itemsAdapter;
    private DepartmentAdapter departmentAdapter;
    private CategoryAdapter categoryAdapter;
    //private GridView gridViewItems;
    private ListView listViewDept,listViewCat;

    // Variables

    String strMenuCode = "", strUserName = "";
    //String[] Name;
    //String[] ImageUri;
    //int[] MenuCode;
    Cursor crsrSettings = null;
    private Toolbar toolbar;
    String FASTBILLINGMODE = "1";

    private RecyclerView mRecyclerGridView;
    private TestItemsAdapter mTestItemsAdapter;
    private GridLayoutManager mGridLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbStock = new DatabaseHandler(this);


        myContext = this;
        MsgBox = new MessageDialog(myContext);

        strUserName = getIntent().getStringExtra("USER_NAME");

        //tvTitleUserName.setText(strUserName.toUpperCase());
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        //tvTitleDate.setText("Date : " + s);

        com.wep.common.app.ActionBarUtils.setupToolbar(this,toolbar,getSupportActionBar(),"Price and Stock",strUserName," Date:"+s.toString());

        try {

            TextView tvdeptline = (TextView) findViewById(R.id.tvStockdeptline);
            TextView tvcategline = (TextView) findViewById(R.id.tvStockcategline);
            WepButton btndepart = (WepButton) findViewById(R.id.btn_Stockdepart);
            WepButton btncateg = (WepButton) findViewById(R.id.btn_Stockcateg);
            WepButton btnitem = (WepButton) findViewById(R.id.btn_Stockitem);

            dbStock.CreateDatabase();
            dbStock.OpenDatabase();
            //DisplayItems();
            InitializeViews();
            loadAutoCompleteData_ItemNames();
            clickEvent();
            ResetStock();
            // Get departments

            // Get Category
            crsrSettings = dbStock.getBillSetting();

            /*btndepart.setVisibility(View.INVISIBLE);
            btncateg.setVisibility(View.INVISIBLE);
            btnitem.setVisibility(View.INVISIBLE);*/
            if(crsrSettings.moveToFirst()) {
                FASTBILLINGMODE = crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode"));
                if (crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode")).equalsIgnoreCase("1")) {
                    btndepart.setVisibility(View.GONE);
                    btncateg.setVisibility(View.GONE);
                    btnitem.setVisibility(View.VISIBLE);

                    listViewDept.setVisibility(View.GONE);
                    tvdeptline.setVisibility(View.GONE);
                    listViewCat.setVisibility(View.GONE);
                    tvcategline.setVisibility(View.GONE);
                    //    gridViewItems.setNumColumns(6);
                    mGridLayoutManager = new GridLayoutManager(StockActivity.this, 6);
                    mRecyclerGridView.setLayoutManager(mGridLayoutManager);

                } else if (crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode")).equalsIgnoreCase("2")) {
                    btndepart.setVisibility(View.VISIBLE);
                    btncateg.setVisibility(View.GONE);
                    btnitem.setVisibility(View.VISIBLE);

                    listViewCat.setVisibility(View.GONE);
                    tvcategline.setVisibility(View.GONE);
                    //   gridViewItems.setNumColumns(4);
                    mGridLayoutManager = new GridLayoutManager(StockActivity.this, 4);
                    mRecyclerGridView.setLayoutManager(mGridLayoutManager);

                } else if (crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode")).equalsIgnoreCase("3")) {
                    btndepart.setVisibility(View.VISIBLE);
                    btncateg.setVisibility(View.VISIBLE);
                    btnitem.setVisibility(View.VISIBLE);
                    //   gridViewItems.setNumColumns(2);
                    mGridLayoutManager = new GridLayoutManager(StockActivity.this, 2);
                    mRecyclerGridView.setLayoutManager(mGridLayoutManager);

                }

                btndepart.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        if (FASTBILLINGMODE.equals("3"))
                            listViewCat.setVisibility(View.INVISIBLE);
                        //   gridViewItems.setVisibility(View.INVISIBLE);
                        mRecyclerGridView.setVisibility(View.INVISIBLE);
                        loadDepartments();
                        ResetStock();
                    }
                });

                btncateg.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        listViewDept.setVisibility(View.INVISIBLE);
                        //    gridViewItems.setVisibility(View.INVISIBLE);
                        mRecyclerGridView.setVisibility(View.INVISIBLE);
                        loadCategories(0);
                        ResetStock();
                    }
                });

                btnitem.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        switch (Integer.parseInt(FASTBILLINGMODE))
                        {
                            case 3 : listViewCat.setVisibility(View.INVISIBLE);
                            case 2 : listViewDept.setVisibility(View.INVISIBLE);
                        }
                        loadItems(0);
                        ResetStock();
                    }
                });


            }
            loadItems(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MsgBox.Show("Error", e.getMessage());
        }
    }

    private  void clickEvent()
    {
        ItemLongName.setOnTouchListener(new View.OnTouchListener(){
            //@Override
            public boolean onTouch(View v, MotionEvent event){
                ItemLongName.showDropDown();
                return false;
            }
        });
        ItemLongName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = ItemLongName.getText().toString();
                Cursor cursorItem = dbStock.getItemDetail(itemName);
                if(cursorItem!=null && cursorItem.moveToFirst())
                {
                    strMenuCode = cursorItem.getString(cursorItem.getColumnIndex("MenuCode"));
                    //ItemLongName.setText(cursorItem.getString(cursorItem.getColumnIndex("ItemName")));
                    AutoCompleteItemBarcodeValue.setText(cursorItem.getString(cursorItem.getColumnIndex("ItemBarcode")));
                    tvExistingStock.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("Quantity"))));
                    txtRate1.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("DineInPrice1"))));
                    txtRate2.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("DineInPrice2"))));
                    txtRate3.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("DineInPrice3"))));
                    txtNewStock.setText("0");
                    btnUpdate.setEnabled(true);
                }else
                {
                    Log.d("ItemManagement","No Such item present");
                }
            }
        });

        AutoCompleteItemBarcodeValue.setOnTouchListener(new View.OnTouchListener(){
            //@Override
            public boolean onTouch(View v, MotionEvent event){
                AutoCompleteItemBarcodeValue.showDropDown();
                return false;
            }
        });
        AutoCompleteItemBarcodeValue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onBarcodeSelected();
                }
            });
    }

    void onBarcodeSelected() {
        String barcode = AutoCompleteItemBarcodeValue.getText().toString();
        Cursor cursorItem = (new DatabaseHandler(myContext)).getItemssbyBarCode(barcode);
        if(cursorItem!=null && cursorItem.moveToFirst()) {
            strMenuCode = cursorItem.getString(cursorItem.getColumnIndex("MenuCode"));
            ItemLongName.setText(cursorItem.getString(cursorItem.getColumnIndex("ItemName")));
            //AutoCompleteItemBarcodeValue.setText(cursorItem.getString(cursorItem.getColumnIndex("ItemName")));
            tvExistingStock.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("Quantity"))));
            txtRate1.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("DineInPrice1"))));
            txtRate2.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("DineInPrice2"))));
            txtRate3.setText(String.format("%.2f", cursorItem.getDouble(cursorItem.getColumnIndex("DineInPrice3"))));
            txtNewStock.setText("0");
            btnUpdate.setEnabled(true);
        }
        else
        {
            Log.d("Stock Activity","Item not found");
            MsgBox.Show("Oops","Item not found");
        }
    }
    /*private AdapterView.OnItemClickListener itemsClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Items items = (Items) itemsAdapter.getItem(position);
            //Cursor cursor = dbStock.getItemss(items.getItemCode());
            Cursor Item = null;
            if (view.getTag() != null) {
                Item = dbStock.getItemss(items.getItemCode());
                if (Item.moveToNext()) {
                    strMenuCode = Item.getString(Item.getColumnIndex("MenuCode"));
                    ItemLongName.setText(Item.getString(Item.getColumnIndex("ItemName")));
                    tvExistingStock.setText(String.format("%.2f",Item.getDouble(Item.getColumnIndex("Quantity"))));
                    txtRate1.setText(String.format("%.2f",Item.getDouble(Item.getColumnIndex("DineInPrice1"))));
                    txtRate2.setText(String.format("%.2f",Item.getDouble(Item.getColumnIndex("DineInPrice2"))));
                    txtRate3.setText(String.format("%.2f",Item.getDouble(Item.getColumnIndex("DineInPrice3"))));
                    txtNewStock.setText("0");
                    btnUpdate.setEnabled(true);
                }
            }
        }
    };*/

    private AdapterView.OnItemClickListener deptClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Department department = (Department) departmentAdapter.getItem(position);
            int deptCode = department.getDeptCode();
            if(FASTBILLINGMODE.equals("3"))// dept+cat+items
            {
                loadCategories(deptCode);
            }
            loadItems_for_dept(deptCode);
            ResetStock();

        }
    };

    private AdapterView.OnItemClickListener catClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Category cat = (Category) categoryAdapter.getItem(position);
            int categcode = cat.getCategCode();
            loadItems(categcode);
            ResetStock();
        }
    };


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
                    list =  dbStock.getItemItems_dept(deptCode);
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
                //   gridViewItems.setVisibility(View.VISIBLE);
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
                    return dbStock.getItemItems();
                else
                    return dbStock.getItemItems(categcode);
            }

            @Override
            protected void onPostExecute(ArrayList<Items> list) {
                super.onPostExecute(list);
                //editTextOrderNo.setText(String.valueOf(dbStock.getNewBillNumber()));
                if(list!=null)
                    setItemsAdapter(list);
                // gridViewItems.setVisibility(View.VISIBLE);
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
                return dbStock.getItemDepartment();
            }

            @Override
            protected void onPostExecute(ArrayList<Department> list) {
                super.onPostExecute(list);
                //editTextOrderNo.setText(String.valueOf(dbStock.getNewBillNumber()));
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
                    return dbStock.getAllItemCategory();
                else
                    return dbStock.getAllItemCategory(deptCode);

            }

            @Override
            protected void onPostExecute(ArrayList<Category> list) {
                super.onPostExecute(list);
                //editTextOrderNo.setText(String.valueOf(dbStock.getNewBillNumber()));
                if(list!=null)
                    setCategoryAdapter(list);
                listViewCat.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

   /* public void setItemsAdapter(ArrayList<Items> list)
    {
        if(itemsAdapter==null){
            itemsAdapter = new ItemsAdapter(this,list);
            gridViewItems.setAdapter(itemsAdapter);
        }
        else
            itemsAdapter.notifyDataSetChanged(list);
    }*/

    public void setItemsAdapter(ArrayList<Items> list) {
        if (mTestItemsAdapter == null) {
            mTestItemsAdapter = new TestItemsAdapter(this, list);

            mTestItemsAdapter.setOnItemClickListener(new TestItemsAdapter.OnItemsImageClickListener() {
                @Override
                public void onItemClick(int position, int itemCode, View v) {

                    //    Items items = (Items) itemsAdapter.getItem(position);
                    //Cursor cursor = dbStock.getItemss(items.getItemCode());
                    Cursor Item = null;
                    //if (v.getTag() != null) {
                        Item = dbStock.getItemss(itemCode);
                        if (Item.moveToNext()) {
                            strMenuCode = Item.getString(Item.getColumnIndex("MenuCode"));
                            ItemLongName.setThreshold(1000);
                            AutoCompleteItemBarcodeValue.setThreshold(1000);
                            ItemLongName.setText(Item.getString(Item.getColumnIndex("ItemName")));
                            ItemLongName.setThreshold(1);
                            AutoCompleteItemBarcodeValue.setThreshold(1);
                            AutoCompleteItemBarcodeValue.setText(Item.getString(Item.getColumnIndex("ItemBarcode")));
                            tvExistingStock.setText(String.format("%.2f", Item.getDouble(Item.getColumnIndex("Quantity"))));
                            txtRate1.setText(String.format("%.2f", Item.getDouble(Item.getColumnIndex("DineInPrice1"))));
                            txtRate2.setText(String.format("%.2f", Item.getDouble(Item.getColumnIndex("DineInPrice2"))));
                            txtRate3.setText(String.format("%.2f", Item.getDouble(Item.getColumnIndex("DineInPrice3"))));
                            txtNewStock.setText("0");
                            btnUpdate.setEnabled(true);
                        }
                   // }
                }
            });
            mRecyclerGridView.setAdapter(mTestItemsAdapter);

        } else
            mTestItemsAdapter.notifyDataSetChanged(list);
    }

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
            listViewCat.setAdapter(categoryAdapter);
        }
        else
            categoryAdapter.notifyDataSetChanged(list);
    }

    private void InitializeViews() {
        ItemLongName = (AutoCompleteTextView) findViewById(R.id.autoTextItemLongNameValue);
        ItemLongName.requestFocus();
        AutoCompleteItemBarcodeValue = (AutoCompleteTextView) findViewById(R.id.AutoCompleteItemBarcodeValue);
        tvExistingStock = (TextView) findViewById(R.id.tvItemExistingStockValue);
        txtNewStock = (EditText) findViewById(R.id.etItemNewStock);
        txtRate1 = (EditText) findViewById(R.id.etItemRate1);
        txtRate2 = (EditText) findViewById(R.id.etItemRate2);
        txtRate3 = (EditText) findViewById(R.id.etItemRate3);
        btnUpdate = (WepButton) findViewById(R.id.btnUpdateStock);
        btnClearStock = (WepButton) findViewById(R.id.btnClearStock);
        btnCloseStock = (WepButton) findViewById(R.id.btnCloseStock);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateStock(v);
            }
        });

        btnClearStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearStock(v);
            }
        });
        btnCloseStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseStock(v);
            }
        });


        ItemLongName.addTextChangedListener(this);
        txtNewStock.addTextChangedListener(this);
        txtRate1.addTextChangedListener(this);
        txtRate2.addTextChangedListener(this);
        txtRate3.addTextChangedListener(this);

        mRecyclerGridView = (RecyclerView) findViewById(R.id.listViewFilter3);
        mRecyclerGridView.setHasFixedSize(true);

    /*
        gridViewItems = (GridView) findViewById(R.id.listViewFilter3);
        gridViewItems.setOnItemClickListener(itemsClick);*/

        listViewDept = (ListView) findViewById(R.id.listViewFilter1);
        listViewDept.setOnItemClickListener(deptClick);
        listViewCat = (ListView) findViewById(R.id.listViewFilter2);
        listViewCat.setOnItemClickListener(catClick);

//        lstvwDepartment = (ListView) findViewById(R.id.lstStockDepartmentNames);
//        lstvwCategory = (ListView) findViewById(R.id.lstStockCategoryNames);
//        grdItems = (GridView) findViewById(R.id.gridStockItems);
//        grdItems.setOnItemClickListener(GridItemImageClick);
    }

    private void UpdateItemStock(int MenuCode, float NewStock, float Rate1, float Rate2, float Rate3) {
        long lRowId = 0;

        lRowId = dbStock.updateItemStock(MenuCode, NewStock, Rate1, Rate2, Rate3);

        Toast.makeText(myContext, "Updated Successfully", Toast.LENGTH_LONG).show();
        Log.d("StockUpdate", "Row Id:" + String.valueOf(lRowId));
    }

    private void ResetStock() {
        tx = "";
        ItemLongName.setText("");
        AutoCompleteItemBarcodeValue.setText("");
        txtNewStock.setText("0");
        tvExistingStock.setText("0");
        txtRate1.setText("0");
        txtRate2.setText("0");
        txtRate3.setText("0");
        btnUpdate.setEnabled(false);
    }

    public void UpdateStock(View v) {
        String strExistingStock = tvExistingStock.getText().toString();
        String strNewStock = txtNewStock.getText().toString();
        String strRate1 = txtRate1.getText().toString();
        String strRate2 = txtRate2.getText().toString();
        String strRate3 = txtRate3.getText().toString();

        if (strNewStock.equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Enter stock before updating");
            return;
        }

        float newStock = Float.parseFloat(String.format("%.2f",Float.parseFloat(strNewStock)));

        UpdateItemStock(Integer.parseInt(strMenuCode), (Float.parseFloat(strExistingStock) + newStock),
                Float.parseFloat(String.format("%.2f",Float.parseFloat(strRate1))),
                Float.parseFloat(String.format("%.2f",Float.parseFloat(strRate2))),
                Float.parseFloat(String.format("%.2f",Float.parseFloat(strRate3))) );


        txtNewStock.setText("0");
        tvExistingStock.setText(String.format("%.2f",(Float.parseFloat(strExistingStock) + newStock)));
        //Toast.makeText(myContext, "Price & Stock Updated Successfully", Toast.LENGTH_LONG).show();
        //DisplayItems();
        // updating in table outwardStock
        String itemName = ItemLongName.getText().toString();
        double rate =0;
        if(Double.parseDouble(strRate1) >0)
            rate =Double.parseDouble(String.format("%.2f",Float.parseFloat(strRate1)));
        else if(Double.parseDouble(strRate2) >0)
            rate = Double.parseDouble(String.format("%.2f",Float.parseFloat(strRate2)));
        else if(Double.parseDouble(strRate3) >0)
            rate = Double.parseDouble(String.format("%.2f",Float.parseFloat(strRate3)));

        Cursor date_cursor = dbStock.getCurrentDate();
        String currentdate = "";
        if(date_cursor.moveToNext())
            currentdate = date_cursor.getString(date_cursor.getColumnIndex("BusinessDate"));
        StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbStock);
        double OpeningQuantity =0;
        double ClosingQuantity =0;
        Cursor outward_item_stock = dbStock.getOutwardStockItem(currentdate,Integer.parseInt(strMenuCode));
        if(outward_item_stock!=null && outward_item_stock.moveToNext()){
            OpeningQuantity = outward_item_stock.getDouble(outward_item_stock.getColumnIndex("OpeningStock"));
            OpeningQuantity += Double.parseDouble(strNewStock);

            ClosingQuantity = outward_item_stock.getDouble(outward_item_stock.getColumnIndex("ClosingStock"));
            ClosingQuantity += Double.parseDouble(strNewStock);
        }
        else
        {
            OpeningQuantity = (Double.parseDouble(strExistingStock) + Double.parseDouble(strNewStock));
            ClosingQuantity = OpeningQuantity;
        }
        stock_outward.updateOpeningStock_Outward( currentdate, Integer.parseInt(strMenuCode),itemName,OpeningQuantity, rate );
        stock_outward.updateClosingStock_Outward( currentdate, Integer.parseInt(strMenuCode),itemName,ClosingQuantity);
        //ResetStock();

    }

    public void ClearStock(View v) {
        ResetStock();
    }

    public void CloseStock(View v) {
        // Close Database connection and finish the activity
        dbStock.CloseDatabase();
        hideKeyboard();
        this.finish();
    }

    private void loadAutoCompleteData_ItemNames() {
        List ItemName_list = dbStock.getAllItemsName();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, ItemName_list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        ItemLongName.setAdapter(dataAdapter);

        List ItemBarcode_list = dbStock.getAllItemsBarCode();
        ArrayAdapter<String> dataAdapter_barCode = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, ItemBarcode_list);
        dataAdapter_barCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AutoCompleteItemBarcodeValue.setAdapter(dataAdapter_barCode);
    }

    private void LoadDepartments(Cursor crsrDept) {

//        Cursor cursor = dbStock.getDepartments();
//        String columns[] = new String[]{"_id", "DeptName"};
//        int vals[] = new int[]{R.id.tvlstDeptCode, R.id.tvlstDeptName};
//        deptdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_deptnames, cursor, columns, vals);
//
//        lstvwDepartment.setVisibility(View.VISIBLE);
//        lstvwDepartment.setAdapter(deptdataAdapter);
//        lstvwDepartment.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                @SuppressWarnings("unchecked")
//                String deptcode = ((TextView) view.findViewById(R.id.tvlstDeptCode)).getText().toString();
//                Cursor Category = dbStock.getCategoryItems(Integer.valueOf(deptcode));
//                // Load Category to Category List
//                if (Category.moveToFirst()) {
//                    lstvwCategory.setVisibility(View.VISIBLE);
//                    LoadCategories(Category);
//
//                    /*GetItemDetailsByDept(Integer.valueOf(deptcode));
//                    if (Name.length > 0) {
//                        // Assign item grid to image adapter
//                        grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
//                        // Make the item grid visible
//                        grdItems.setVisibility(View.VISIBLE);
//                    } else {
//                        // Make the item grid invisible
//                        grdItems.setVisibility(View.INVISIBLE);
//                    }*/
//                }
//                else {
//
//                   Toast.makeText(myContext, "No category found for this department", Toast.LENGTH_SHORT).show();
//                    /*
//                    lstvwCategory.setAdapter(null);
//                    //MsgBox.Show("","Items");
//                    GetItemDetailsByDept(Integer.valueOf(deptcode));//, Integer.valueOf(categdeptcode));
//                    // This condition is to avoid NullReferenceException in getCount()
//                    // in ImageAdapter class.
//                    if (Name.length > 0) {
//                        // Assign item grid to image adapter
//                        grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
//                        // Make the item grid visible
//                        grdItems.setVisibility(View.VISIBLE);
//                    } else {
//                        // Make the item grid invisible
//                        grdItems.setVisibility(View.INVISIBLE);
//                    }*/
//                }
//            }
//        });
    }

    private void LoadDepartmentsItems(Cursor crsrDept) {

//        Cursor cursor = dbStock.getDepartments();
//        String columns[] = new String[]{"_id", "DeptName"};
//        int vals[] = new int[]{R.id.tvlstDeptCode, R.id.tvlstDeptName};
//        deptdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_deptnames, cursor, columns, vals);
//
//        lstvwDepartment.setAdapter(deptdataAdapter);
//        lstvwDepartment.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                @SuppressWarnings("unchecked")
//                //String val = o.get("id");
//                        String deptcode = ((TextView) view.findViewById(R.id.tvlstDeptCode)).getText().toString();
//
//                lstvwCategory.setAdapter(null);
//                //MsgBox.Show("","Items");
//                GetItemDetailsByDept(Integer.valueOf(deptcode));//, Integer.valueOf(categdeptcode));
//                // This condition is to avoid NullReferenceException in getCount()
//                // in ImageAdapter class.
//                if (Name.length > 0) {
//                    // Assign item grid to image adapter
//                    grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
//                    // Make the item grid visible
//                    grdItems.setVisibility(View.VISIBLE);
//                } else {
//                    // Make the item grid invisible
//                    Toast.makeText(myContext, "No item found for this department ", Toast.LENGTH_SHORT).show();
//                    grdItems.setVisibility(View.INVISIBLE);
//                }
//
//            }
//        });
        // LoadItemsForAllDepartment();
    }

    void LoadItemsForAllDepartment()
    {
//        Cursor Items = dbStock.getItemsForAllDepartments();
//        int count = 0;
//        while  (Items!=null && Items.moveToNext())
//        {
//            count++;
//            Name = new String[Items.getCount()];
//            ImageUri = new String[Items.getCount()];
//            MenuCode = new int[Items.getCount()];
//
//            do {
//                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
//                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
//                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));
//            } while (Items.moveToNext());
//
//        }
//        if(count ==0){
//
//            Log.d("LoadItemsToGrid", "No Items found");
//
//            Name = new String[0];
//            ImageUri = new String[0];
//            MenuCode = new int[0];
//        }
//        if (Name.length > 0) {
//            // Assign item grid to image adapter
//            grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
//            // Make the item grid visible
//            grdItems.setVisibility(View.VISIBLE);
//        } else {
//            // Make the item grid invisible
//            grdItems.setVisibility(View.INVISIBLE);
//        }
    }

    private void LoadCategories(Cursor crsrCateg) {

        //Cursor cursor = crsrCateg;
//        String columns[] = new String[]{"_id", "CategName", "DeptCode"};
//        int vals[] = new int[]{R.id.tvlstCategCode, R.id.tvlstCategName, R.id.tvlstCategDeptCode};
//        categdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_categnames, crsrCateg, columns, vals);
//
//        lstvwCategory.setAdapter(categdataAdapter);
//        grdItems.setAdapter(null);
//        lstvwCategory.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                @SuppressWarnings("unchecked")
//
//                String categcode = ((TextView) view.findViewById(R.id.tvlstCategCode)).getText().toString();
//                String categdeptcode = ((TextView) view.findViewById(R.id.tvlstCategDeptCode)).getText().toString();
//
////                Toast.makeText(myContext, message, Toast.LENGTH_LONG).show();
//                grdItems.setVisibility(View.VISIBLE);
//                GetItemDetails(Integer.valueOf(categcode));//, Integer.valueOf(categdeptcode));
//                // This condition is to avoid NullReferenceException in getCount()
//                // in ImageAdapter class.
//                if (Name.length > 0) {
//                    // Assign item grid to image adapter
//                    grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
//                    // Make the item grid visible
//                    grdItems.setVisibility(View.VISIBLE);
//                } else {
//                    // Make the item grid invisible
//                    Toast.makeText(myContext, "No item found for this category ", Toast.LENGTH_SHORT).show();
//                    grdItems.setVisibility(View.INVISIBLE);
//                }
//
//            }
//        });
//

    }

    // Get Items by CategCode
    private void GetItemDetails() {
//        Cursor Items = null;
//        Items = dbStock.getAllItems();
//        //Items = dbBillScreen.getAllItemsWithoutDeptCateg();
//        if (Items.moveToFirst()) {
//
//            Name = new String[Items.getCount()];
//            ImageUri = new String[Items.getCount()];
//            MenuCode = new int[Items.getCount()];
//
//            do {
//                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
//                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
//                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));
//            } while (Items.moveToNext());
//
//        } else {
//            Name = new String[0];
//            ImageUri = new String[0];
//            MenuCode = new int[0];
//        }
    }

    private void GetItemDetailswithoutDeptCateg() {
//        Cursor Items = null;
//        //Items = dbBillScreen.getAllItems();
//        Items = dbStock.getAllItemsWithoutDeptCateg();
//        if (Items.moveToFirst()) {
//
//            Name = new String[Items.getCount()];
//            ImageUri = new String[Items.getCount()];
//            MenuCode = new int[Items.getCount()];
//
//            do {
//                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
//                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
//                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));
//            } while (Items.moveToNext());
//
//        } else {
//            Name = new String[0];
//            ImageUri = new String[0];
//            MenuCode = new int[0];
//        }
    }

    // Get Items by CategCode
    /*private void GetItemDetails(int iCategCode) {
        Cursor Items = null;
        Items = dbStock.getCatbyItems(iCategCode);
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
    }*/

    // Get Items by DeptCode
    /*private void GetItemDetailsByDept(int iDeptCode) {
        Cursor Items = null;
        Items = dbStock.getItems(iDeptCode);
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

    OnItemClickListener GridItemImageClick = new OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            // TODO Auto-generated method stub
            //MsgBox.Show("", v.getTag().toString());
            Cursor Item = null;
            if (v.getTag() != null) {
                Item = dbStock.getItem(Integer.parseInt(v.getTag().toString()));
                if (Item.moveToNext()) {
                    strMenuCode = Item.getString(Item.getColumnIndex("MenuCode"));
                    ItemLongName.setText(Item.getString(Item.getColumnIndex("ItemName")));
                    AutoCompleteItemBarcodeValue.setText(Item.getString(Item.getColumnIndex("ItemBarcode")));
                    tvExistingStock.setText(Item.getString(Item.getColumnIndex("Quantity")));
                    txtRate1.setText(Item.getString(Item.getColumnIndex("DineInPrice1")));
                    txtRate2.setText(Item.getString(Item.getColumnIndex("DineInPrice2")));
                    txtRate3.setText(Item.getString(Item.getColumnIndex("DineInPrice3")));
                    txtNewStock.setText("0");
                    btnUpdate.setEnabled(true);
                }
            }

        }
    };

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
                            dbStock.CloseDatabase();
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

    void additemtoKOT()
    {
        String barcode = AutoCompleteItemBarcodeValue.getText().toString().trim();
        System.out.println("Barcode = "+barcode);
        onBarcodeSelected();
        AutoCompleteItemBarcodeValue.setText("");
        linefeed="";
        //txtRate1.setFocusable(true);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        long dd = event.getEventTime()-event.getDownTime();
        System.out.println("Richa : "+event.getKeyCode()+" SC "+event.getScanCode());
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
        {
            //System.out.println("Richa : Enter encountered for barcode");
            additemtoKOT();
        }else if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT  )
        {
            linefeed +=String.valueOf(event.getKeyCode());
            if(linefeed.equalsIgnoreCase("38113")|| linefeed.equalsIgnoreCase("11338")) // line feed value
                additemtoKOT();
        }else {
            if (dd<15 && dd >0)
            {
                View v = getCurrentFocus();
                AutoCompleteTextView etbar = (AutoCompleteTextView)findViewById(R.id.AutoCompleteItemBarcodeValue);


                if (v.getId()!= R.id.AutoCompleteItemBarcode)
                {

                    switch (v.getId())
                    {
                        case R.id.autoTextItemLongNameValue :ItemLongName.setText(tx);
                            break;
                        case R.id.etItemNewStock:
                        case R.id.etItemRate1:
                        case R.id.etItemRate2:
                        case R.id.etItemRate3:
                            EditText ed = (EditText)findViewById(v.getId());
                            ed.setText(tx);
                    }
                    String bar_str = etbar.getText().toString();
                    bar_str += (char)event.getUnicodeChar();
                    etbar.setText(bar_str);
                    etbar.showDropDown();

                }


            }
        }

        return true;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String str = charSequence.toString();
        View view = getCurrentFocus();
        if(view== null)
            return;
        switch(view.getId()){
            case R.id.autoTextItemLongNameValue:
                tx = ItemLongName.getText().toString();
                break;
            case R.id.etItemRate1:
                tx = txtRate1.getText().toString();
                break;
            case R.id.etItemRate2:
                tx = txtRate2.getText().toString();
                break;
            case R.id.etItemRate3:
                tx = txtRate3.getText().toString();
                break;
            case R.id.etItemNewStock:
                tx = txtNewStock.getText().toString();
                break;

        }
    }
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    public void afterTextChanged(Editable editable) {

    }
}
