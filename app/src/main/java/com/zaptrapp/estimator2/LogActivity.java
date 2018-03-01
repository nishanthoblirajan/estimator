package com.zaptrapp.estimator2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.zaptrapp.estimator2.Models.EstimateLog;
import com.zaptrapp.estimator2.Models.ProductHolder;
import com.zaptrapp.estimator2.Printer.ShowMsg;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogActivity extends AppCompatActivity implements ReceiveListener {

    public static final String TAG = LogActivity.class.getSimpleName();
    public String PRINTER = "BT:00:01:90:C2:AE:35";
    String ipAddressM30 = "";
    SharedPreferences mSharedPreferences;
    FirebaseRecyclerAdapter<EstimateLog, ProductHolder> logAdapter;
    String dateStamp = new SimpleDateFormat("dd-MM-yy").format(new Date());
    String timeStamp;
    Printer mPrinter;
    int selected_printer = 1;
    Context mContext;
    String product;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView logRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initView();
        initDatabase();
        initRecycler();
        mContext = this;
        selected_printer = getIntent().getIntExtra("printerExtra", 1);
        ipAddressM30 = getIntent().getStringExtra("ipExtra");
        switch (selected_printer) {
            case 1:
                PRINTER = "TCP:" + ipAddressM30;
                break;
            case 2:
                PRINTER = "BT:00:01:90:C2:AE:35";
                break;
        }
        product = getIntent().getStringExtra("productExtra");
        Log.d(TAG, "onCreate: " + selected_printer + " " + product + " " + ipAddressM30);
    }

    private void initRecycler() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String choice = mSharedPreferences.getString("materialPref", "1");
        String materialChoice = "gold";
        switch (choice) {
            case "1":
                //This is gold
                materialChoice = "gold";

                break;
            case "2":
                //This is silver
                materialChoice = "silver";
                break;
            default:
                //This is default
                break;
        }
        Query query = FirebaseDatabase.getInstance().getReference("estimator2").child("Estimates").child(materialChoice).child(dateStamp).orderByChild("timeStamp");
        Log.d(TAG, "initRecycler: " + query);
        logRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<EstimateLog> estimateLogFirebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<EstimateLog>()
                        .setQuery(query, EstimateLog.class)
                        .build();
        Log.d(TAG, "initRecycler: " + estimateLogFirebaseRecyclerOptions.toString());

        logAdapter = new FirebaseRecyclerAdapter<EstimateLog, ProductHolder>(estimateLogFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(ProductHolder holder, int position, final EstimateLog model) {
                Log.d(TAG, "onBindViewHolder: ");
                holder.product.setText(model.getTimeStamp().replaceAll("-", ":") + " - \u20B9 " + model.getEstimate().split("_")[1]);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(model.getEstimate());
                    }
                });
            }

            @Override
            public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.log_recycler_item, parent, false);
                return new ProductHolder(view);
            }
        };
        logRecyclerView.setAdapter(logAdapter);
        final String finalMaterialChoice = materialChoice;
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                EstimateLog estimateLog = logAdapter.getItem(position);

                firebaseDatabase.getReference("estimator2").child("Estimates").child(finalMaterialChoice).child(dateStamp).child(estimateLog.getTimeStamp()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError!=null){
                            Toast.makeText(mContext, "Delete operation unsuccessful", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, "Log data deleted", Toast.LENGTH_SHORT).show();

                            /*On successful data remove from the recycler view logAdapter,
                                    fabrics will register the click event (#7) 'Log Item Removed'*/
                            EstimateActivity.registerClickEventInFabrics(7);
                        }
                    }
                });

                logAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(logRecyclerView);
    }


    private void initView() {
        logRecyclerView = findViewById(R.id.log_recyclerView);
    }

    public void showDialog(final String string) {
        new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("\u20B9 " + string.split("_")[1])
                .setDescription(string.split("_")[0])
                .setPositiveText("Print")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        runPrintReceiptSequence(string);
                    }
                })
                .setScrollable(true,20)
                .setNegativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .setCancelable(true)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logAdapter.stopListening();
    }

    private boolean runPrintReceiptSequence(String printString) {
        dateStamp = new SimpleDateFormat("dd-MM-yy").format(new Date());
        timeStamp = new SimpleDateFormat("HH-mm-ss").format(new Date());
        EstimateLog estimateLog = new EstimateLog(timeStamp, printString);
        Log.d(TAG, "runPrintReceiptSequence: ");
        Log.d(TAG, "runPrintReceiptSequence: " + estimateLog.toString());
        databaseReference.child("Estimates").child(product).child(dateStamp).child(estimateLog.getTimeStamp()).setValue(estimateLog);
        Log.d(TAG, "runPrintReceiptSequence: ");
        try {

            if (!initializeObject()) {
                Log.d(TAG, "runPrintReceiptSequence: initalise");
                return false;
            }

            if (!createReceiptData(printString)) {
                Log.d(TAG, "runPrintReceiptSequence: create");
                finalizeObject();
                return false;
            }

            if (!printData()) {
                Log.d(TAG, "runPrintReceiptSequence: ");
                finalizeObject();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Print Failed, Try Again", Toast.LENGTH_SHORT).show();
        }
        return true;

    }

    private boolean initializeObject() {
        Log.d(TAG, "initializeObject: ");
        try {
            //TODO added shared preference here
            mPrinter = new Printer(selected_printer,
                    Printer.MODEL_ANK,
                    this);
            Log.d(TAG, "initializeObject: inside try");
        } catch (Exception e) {
            Log.d(TAG, "initializeObject: Exception");
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }

        mPrinter.setReceiveEventListener((ReceiveListener) mContext);

        return true;
    }

    private boolean createReceiptData(String printString) {
        Log.d(TAG, "createReceiptData: ");

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            String dateStamp = new SimpleDateFormat("dd/MM/yy").format(new Date());
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            mPrinter.addText("Date: " + dateStamp);
            mPrinter.addFeedLine(1);
            mPrinter.addTextSize(2, 2);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addFeedLine(1);
            mPrinter.addText("ESTIMATE");
            mPrinter.addFeedLine(2);
            mPrinter.addTextSize(1, 1);
            mPrinter.addText(printString.split("_")[0]);
            mPrinter.addText("------------------------------------------\n");
            mPrinter.addFeedLine(1);
            mPrinter.addTextSize(2, 2);
            mPrinter.addText("Rs " + printString.split("_")[1]);
            mPrinter.addFeedLine(3);
            mPrinter.addTextSize(1, 1);
            mPrinter.addText("------------------------------------------\n");
            mPrinter.addText("Thank You\n");
            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addFeedLine(4);
            mPrinter.addText("Time " + timeStamp + "\n");
            mPrinter.addCut(Printer.CUT_FEED);
            FirebaseDatabase firebase = FirebaseDatabase.getInstance();
            String dateStampChild = new SimpleDateFormat("dd").format(new Date());
            firebase.getReference("estimates").child(dateStampChild).child(timeStamp).setValue(printString.split("_")[1]);
        } catch (Exception e) {
            ShowMsg.showException(e, "", mContext);
            return false;
        }

        return true;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            //print available
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();


        if (!isPrintable(status)) {
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(PRINTER, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", mContext);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void finalizeObject() {
        Log.d(TAG, "finalizeObject: ");
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    //
    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), mContext);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }

        finalizeObject();
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estimator2");
    }
}
