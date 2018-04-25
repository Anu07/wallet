package app.src.com.walletapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * @author Dhagz
 * @since 5/12/2016
 */
public class MyProgressDialog extends Dialog {

    public static MyProgressDialog show(Context context) {
        MyProgressDialog dialog = new MyProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException badTokenException) {
            badTokenException.printStackTrace();
        }
        return dialog;
    }

    public static void dismiss(MyProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (IllegalArgumentException ex) {
                //
            }
        }
    }

    public MyProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        RelativeLayout layout = new RelativeLayout(context);
        ProgressBar progressBar = new ProgressBar(context);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        int dp16 = dpToPx(16);
        layout.setPadding(dp16, dp16, dp16, dp16);
        layout.addView(progressBar, params);

        setContentView(layout);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        // disable on back pressed
        // super.onBackPressed();
    }

    /**
     * Source: http://stackoverflow.com/questions/8295986/how-to-calculate-dp-from-pixels-in-android-programmatically
     *
     * @param dp value to be converted to pixels
     * @return pixel value for dp
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}