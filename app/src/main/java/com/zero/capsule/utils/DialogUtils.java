package com.zero.capsule.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;


/**
 * Created by azharuddin on 04/08/17.
 * Dialog utils for showing progress dialog, Toast, SnackBar etc...
 */

public class DialogUtils {
    private static ProgressDialog mProgressDialog;

    private static void appDialog(final Activity activity, @SuppressWarnings("SameParameterValue") String title, String Message) {
        AlertDialog.Builder alertBox = new AlertDialog.Builder(activity);
        alertBox.setTitle(title);
        alertBox.setMessage(Message);
        alertBox.setPositiveButton("Ok", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        alertBox.show();
    }

    public static void appDialogWithCallBack(final Activity activity, String title, String Message, final OnClickListener listener) {
        onClickListener = listener;
        AlertDialog.Builder alertBox = new AlertDialog.Builder(activity);
        alertBox.setTitle(title);
        alertBox.setMessage(Message);
        alertBox.setPositiveButton("Logout", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        onClickListener.onOk();
                    }
                });
        alertBox.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        onClickListener.onCancel();
                        dialog.dismiss();
                    }
                });

        alertBox.show();
    }

    private static OnClickListener onClickListener;

    interface OnClickListener {
        public void onOk();

        public void onCancel();
    }

    public static void appDialog(final Activity activity, String Message) {
        appDialog(activity, "", Message);
    }

    public static void appToastLong(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
    }

    public static void appToastShort(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }

    public static void showProgressDialog(Context context, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
                mProgressDialog.cancel();
        }
    }

}
