

package jp.co.asaichi.pubrepo.common;


import android.app.ProgressDialog;
import android.content.Context;


public class ProgressDlg {

    private ProgressDialog progressDialog;


    public ProgressDlg(Context context) {
        if (null == context) {
            return;
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //progressDialog.setContentView(R.layout.dialog_custom_progress);
    }

    /**
     * Show Progress Dialog on top screen with message
     */
    public void show() {
        //progressDialog.setMessage("");
        progressDialog.show();

    }

    /**
     * Show Progress Dialog on top screen with message
     */
    public void show(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    /**
     * Hide Progress Dialog, if it is showing.
     */
    public void hide() {
        progressDialog.dismiss();
    }

    /**
     * Check if Progress Dialog is showing.
     */
    public boolean isShowing() {
        return progressDialog.isShowing();
    }
}
