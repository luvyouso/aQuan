

package jp.co.asaichi.pubrepo.common;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class PopUpDlg {

    public static final int POPUPDLG_DEFAULT = 1;
    public static final int POPUPDLG_PERCENT = 2;
    public static final int POPUPDLG_NUMBER = 3;
    private AlertDialog mDlg;
    private Context mContext;
    private AlertDialog alertDialog;


    /**
     * Constructor
     *
     * @param context
     * @param modal   if modal is true then user must close popup by interacting with controls
     *                inside popup. Touching out side popup to canceled is not permit
     */
    public PopUpDlg(Context context, boolean modal) {
        if (context != null) {
            mDlg = new AlertDialog.Builder(context).create();
            mDlg.setCanceledOnTouchOutside(!modal);
            mContext = context;
            //Here's the magic..
            //Set the dialog to not focusable (makes navigation ignore us adding the window)
//            mDlg.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        } else {
            throw new RuntimeException("Context is null. Need a context to create Popup!");
        }
    }

    /**
     * Show common popup with message and return response for caller by callback function
     * that mean caller must implement callback function to get response from user and do
     * business logic
     *
     * @param title
     * @param msg
     * @param positiveBnt positive button title
     * @param negativeBnt negative button title
     * @param onOK
     * @param onCancel
     */
    public void show(String title, String msg, String positiveBnt, String negativeBnt,
                     final DialogInterface.OnClickListener onOK,
                     final DialogInterface.OnClickListener onCancel) {

        if (mDlg != null) {
            mDlg.setTitle(title);
            mDlg.setMessage(msg);
            if (!TextUtils.isEmpty(positiveBnt)) {
                mDlg.setButton(DialogInterface.BUTTON_POSITIVE, positiveBnt, onOK);
            }
            if (!TextUtils.isEmpty(negativeBnt)) {
                mDlg.setButton(DialogInterface.BUTTON_NEGATIVE, negativeBnt, onCancel);
            }
            mDlg.show();
        }
    }

    /**
     * show popup that allow user input.
     *
     * @param inputView The edit text for user inputting
     * @param retVal    Use to get input from user
     * @param title     Title for popup
     * @param msg       Message for popup
     * @param onOK      Caller must implement this interface to do something when user choose OK
     * @param onCancel  Caller must implement this interface to do something when user choose Cancel
     */
    public void show(final EditText inputView, final StringBuilder retVal, String title, String msg,
                     final int type,
                     final DialogInterface.OnClickListener onOK,
                     final DialogInterface.OnClickListener onCancel) {

        inputView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
        inputView.setLongClickable(false);

        if ((mDlg != null) && (inputView != null) && (retVal != null)) {
            mDlg.setTitle(title);
            mDlg.setMessage(msg);
            inputView.setSelection(inputView.getText().length());
            inputView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String enteredValue = editable.toString();
                }
            });
            mDlg.setView(inputView);
            mDlg.setButton(DialogInterface.BUTTON_POSITIVE, "退会する", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDlg.dismiss();
                    onOK.onClick(dialog, which);
                }
            });

            mDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDlg.dismiss();
                    onCancel.onClick(dialog, which);
                }
            });

            mDlg.show();
        }
    }

}
