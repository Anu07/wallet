package app.src.com.walletapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.src.com.walletapp.wifip2p.GlobalActivity;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;

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
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_TYPE = "user_type";             //0 for sender and 1 for receiver
    private static final String KEY_BAL_LEFT = "bal_left";
    private static SQLiteDatabase database;
    private static List<String> list = new ArrayList<>();

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TBL_USERDATA +" ( " + KEY_INDEX + " VARCHAR PRIMARY KEY                          \n" +
                "   AUTOINCREMENT," + KEY_DEVICE_ID + " VARCHAR," + KEY_USERPHONE + " VARCHAR, " + KEY_PHONESTATUS + " VARCHAR, " + KEY_TYPE + " VARCHAR);");
        sqLiteDatabase.execSQL("create table " + TBL_USERTXN + " ( " + KEY_TXN_ID + " VARCHAR PRIMARY KEY ," + KEY_RECEIVER_USERPHONE + " VARCHAR, " + KEY_AMT_SENT + " VARCHAR, " + KEY_RECEIVER_DEVICE_ID + " VARCHAR, " + KEY_SENDER_DEVICE_ID + " VARCHAR, " + KEY_BAL_LEFT + " VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBL_USERDATA);
        onCreate(sqLiteDatabase);
    }


    public boolean insertPeerRecord(List<WifiP2pDevice> device, float balance, Context context) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < device.size(); i++) {
            contentValues.put(KEY_INDEX, i);
            if(i==0 && device.get(0).deviceAddress== SharedPreferencesHandler.getStringValues(context,"ownAddress")){
                contentValues.put(KEY_BALANCE, balance);
                contentValues.put(KEY_TYPE, "1");
            }else{
                contentValues.put(KEY_BALANCE, "0");
                contentValues.put(KEY_TYPE, "1");
            }
            contentValues.put(KEY_DEVICE_ID, device.get(i).deviceAddress);
            contentValues.put(KEY_USERPHONE, device.get(i).deviceName);
            contentValues.put(KEY_PHONESTATUS, device.get(i).status);
        }
        long result = database.insert(TBL_USERDATA, null, contentValues);
        database.close();
        Log.i(TAG, "insertPeerRecord: " + database.getPath());
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }


    public void saveTxnDetails(String txnId, String amtEntered, String deviceAddress, Context context) {
        try {
            database = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_TXN_ID, txnId);
            if(GlobalActivity.userType.equalsIgnoreCase("S")){
                contentValues.put(KEY_SENDER_DEVICE_ID, SharedPreferencesHandler.getStringValues(context,"ownAddress"));
                contentValues.put(KEY_RECEIVER_DEVICE_ID, deviceAddress);

            }else{
                contentValues.put(KEY_SENDER_DEVICE_ID, deviceAddress);
                contentValues.put(KEY_RECEIVER_DEVICE_ID, SharedPreferencesHandler.getStringValues(context,"ownAddress"));
            }

            contentValues.put(KEY_AMT_SENT, amtEntered);
            contentValues.put(KEY_BAL_LEFT, SharedPreferencesHandler.getFloatValues(context,"balance"));
            database.insert(TBL_USERTXN, null, contentValues);
            database.close();
        } catch (Exception e) {
            Log.e(TAG, "insertRecord: " + e.getMessage());
        }
    }


    public void updateDetails(String deviceAddress, String updatedBalance) {
        String strSQL = "UPDATE" + TBL_USERDATA + " SET " + KEY_DEVICE_ID + " = " + deviceAddress + " WHERE " + KEY_BALANCE + " = " + updatedBalance;
        database.execSQL(strSQL);
    }


    public void getAllDevices() {
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TBL_USERTXN, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(KEY_TXN_ID));
                list.add(name);
                cursor.moveToNext();
            }
        }
        Log.i(TAG, "getAllDevices: " + list.size());
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_USERDATA, "ID = ?", new String[]{id});
    }

}


