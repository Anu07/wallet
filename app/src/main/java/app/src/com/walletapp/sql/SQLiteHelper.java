package app.src.com.walletapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import app.src.com.walletapp.model.onlinepayment.Payment;

import static app.src.com.walletapp.wifip2p.wifi.WiFiDirectActivity.TAG;

/**
 * Created by SONY on 3/7/2018.
 * //We are creating a java file called SQLiteHelper and extending SQLiteOpenHelper
 * <p>
 * //class and It is used to create a bridge between android and SQLite.To perform basic SQL operations we need to extend SQLiteOpenHelper class.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WalletDatabase.db";
    private static final String TBL_USERDATA = "userData";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_USERPHONE = "phoneName";
    private static final String KEY_PHONESTATUS = "deviceStatus";
    private static final String TBL_USERTXN = "userTransactions";
    private static final String KEY_RECEIVER_DEVICE_ID = "receiverDeviceId";
    private static final String KEY_RECEIVER_USERPHONE = "phoneName";
    private static final String KEY_AMT_SENT = "amountSent";
    private static final String KEY_TXN_ID = "transactionId";
    private static final String KEY_SENDER_DEVICE_ID = "senderDeviceId";
    private static final String KEY_INDEX = "keyindex";
    private SQLiteDatabase database;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TBL_USERDATA + " ( " + KEY_INDEX + " VARCHAR PRIMARY KEY ," + KEY_DEVICE_ID + " VARCHAR," + KEY_USERPHONE + " VARCHAR, " + KEY_PHONESTATUS + " VARCHAR);");
        sqLiteDatabase.execSQL("create table " + TBL_USERTXN + " ( " + KEY_TXN_ID + " VARCHAR PRIMARY KEY ," + KEY_RECEIVER_USERPHONE + " VARCHAR, " + KEY_AMT_SENT + " VARCHAR, " + KEY_RECEIVER_DEVICE_ID + " VARCHAR, " + KEY_SENDER_DEVICE_ID + " VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBL_USERDATA);
        onCreate(sqLiteDatabase);
    }


    public void insertPeerRecord(List<WifiP2pDevice> device) {
        try {
            database = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < device.size(); i++) {
                contentValues.put(KEY_INDEX, i);
                contentValues.put(KEY_DEVICE_ID, device.get(i).deviceAddress);
                contentValues.put(KEY_USERPHONE, device.get(i).deviceName);
                contentValues.put(KEY_PHONESTATUS, device.get(i).status);
                database.insert(TBL_USERDATA, null, contentValues);
            }
            database.close();
        } catch (Exception e) {
            Log.e(TAG, "insertRecord: " + e.getMessage());
        }
        Log.i(TAG, "insertPeerRecord: "+database.getPath());
    }

    public void saveTxnDetails(String txnId, String amtEntered, String deviceAddress, String s) {
        try {
            database = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_TXN_ID,txnId);
            contentValues.put(KEY_RECEIVER_DEVICE_ID,deviceAddress);
            contentValues.put(KEY_AMT_SENT,amtEntered);
            contentValues.put(KEY_SENDER_DEVICE_ID,s);
            database.insert(TBL_USERTXN, null, contentValues);
            database.close();
        } catch (Exception e) {
            Log.e(TAG, "insertRecord: " + e.getMessage());
        }
    }
}


