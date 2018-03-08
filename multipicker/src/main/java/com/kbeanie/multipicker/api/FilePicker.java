package com.kbeanie.multipicker.api;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;

import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.kbeanie.multipicker.api.exceptions.PickerException;
import com.kbeanie.multipicker.core.PickerManager;
import com.kbeanie.multipicker.core.threads.FileProcessorThread;
import com.kbeanie.multipicker.utils.LogUtils;
import com.src.paymemi.model.SendCredit.CreditModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Choose a file from your device. Gallery, Downloads, Dropbox etc.
 */
public final class FilePicker extends PickerManager {
    private final static String TAG = FilePicker.class.getSimpleName();
    private FilePickerCallback callback;

    private String mimeType = "*/*";
    private String str;

    /**
     * Constructor for choosing a file from an {@link Activity}
     *
     * @param activity
     */
    public FilePicker(Activity activity) {
        super(activity, Picker.PICK_FILE);
    }

    /**
     * Constructor for choosing a file from a {@link Fragment}
     *
     * @param fragment
     */
    public FilePicker(Fragment fragment) {
        super(fragment, Picker.PICK_FILE);
    }

    /**
     * Constructor for choosing a file from a {@link android.app.Fragment}
     *
     * @param appFragment
     */
    public FilePicker(android.app.Fragment appFragment) {
        super(appFragment, Picker.PICK_FILE);
    }

    /**
     * Allow multiple files to be chosen. Default is false. This will only work for applications that support multiple file selection. Else, you will get only one result.
     */
    public void allowMultiple() {
        this.allowMultiple = true;
    }

    /**
     * Listener which gets callbacks when your file is processed and ready to be used.
     *
     * @param callback
     */
    public void setFilePickerCallback(FilePickerCallback callback) {
        this.callback = callback;
    }

    /**
     * Default: All types of files. Set this value to a specific mimetype to pick.
     * <p>
     * ex: application/pdf, application/xls
     *
     * @param mimeType
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Triggers file selection
     */
    public void pickFile() {
        try {
            pick();
        } catch (PickerException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    @Override
    protected String pick() throws PickerException {
        if (callback == null) {
            throw new PickerException("FilePickerCallback is null!!! Please set one");
        }
        String action = Intent.ACTION_GET_CONTENT;
        Intent intent = new Intent(action);
        intent.setType(mimeType);
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickInternal(intent, pickerType);
        return null;
    }

    /**
     * Call this method from
     * {@link Activity#onActivityResult(int, int, Intent)}
     * OR
     * {@link Fragment#onActivityResult(int, int, Intent)}
     * OR
     * {@link android.app.Fragment#onActivityResult(int, int, Intent)}
     *  @param data
     * @param userId
     * @param transactionId
     */
    @Override
    public void submit(Intent data, String userId, String transactionId) {
            handleFileData(data,userId,transactionId);
    }


    private void handleFileData(Intent intent, String userId, String transactionId) {
        List<CreditModel> msgs = new ArrayList<>();
        if (intent != null) {
            if (intent.getData()!=null) {
                String msg = intent.getDataString();
                LogUtils.d(TAG, "handleFileData: " + msg);
                CreditModel creditRequest=new CreditModel(userId,msg,transactionId);
                msgs.add(creditRequest);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    LogUtils.d(TAG, "handleFileData: Multiple files with ClipData");
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        LogUtils.d(TAG, "Item [" + i + "]: " + item.getUri().toString());
                        CreditModel creditRequest=new CreditModel(userId,item.getUri().toString(),transactionId);
                        msgs.add(creditRequest);
                    }
                }
            }
           /* if (intent.hasExtra("uris")) {
                ArrayList<Uri> paths = intent.getParcelableArrayListExtra("uris");
                for (int i = 0; i < paths.size(); i++) {
                    uris.add(paths.get(i).toString());
                }
            }*/
            processFiles(msgs);
        }
    }

    private void processFiles(List<CreditModel> uris) {
        FileProcessorThread thread = new FileProcessorThread(getContext(), getFileObjects(uris), cacheLocation);
        thread.setFilePickerCallback(callback);
        thread.setRequestId(requestId);
        thread.start();
    }

    private void onError(final String errorMessage) {
        try {
            if (callback != null) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(errorMessage);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private List<ChosenFile> getFileObjects(List<CreditModel> uris) {
        List<ChosenFile> files = new ArrayList<>();
        for (CreditModel uri : uris) {
            ChosenFile file = new ChosenFile();
            file.setQueryUri(uri.getAmount());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                file.setDirectoryType(Environment.DIRECTORY_DOCUMENTS);
            } else {
                file.setDirectoryType(Environment.DIRECTORY_DOWNLOADS);
            }
            file.setType("file");
            files.add(file);
        }
        return files;
    }
}
