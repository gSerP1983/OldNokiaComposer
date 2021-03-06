package com.serp1983.nokiacomposer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.serp1983.nokiacomposer.R;
import com.serp1983.nokiacomposer.domain.RingtoneVM;
import com.serp1983.nokiacomposer.logic.ShareHelper;

public class DialogHelper {
    public interface Callback<T> {
        void onComplete(T input);
    }

    private final static DialogInterface.OnClickListener nullOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public static void showNumberPickerDialog(Context context, String title, int min, int max, Integer defValue,
                                   final Callback<Integer> callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null && !title.isEmpty())
            builder.setTitle(title);

        final NumberPicker input = new NumberPicker(context);
        input.setMinValue(min);
        input.setMaxValue(max);
        input.setWrapSelectorWheel(false);
        if (defValue != null)
            input.setValue(defValue);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null)
                    callback.onComplete(input.getValue());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, nullOnClickListener);

        builder.show();
    }

    public static void inputDialog(Context context, String title, String hint, String defValue,
                                   final Callback<String> callback){
        inputDialogInner(context, title, hint, defValue, callback, false);
    }

    public static void multilineInputDialog(Context context, String title, String hint, String defValue,
                                   final Callback<String> callback){
        inputDialogInner(context, title, hint, defValue, callback, true);
    }

    private static void inputDialogInner(Context context, String title, String hint, String defValue,
                                   final Callback<String> callback, boolean multiline){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null && !title.isEmpty())
            builder.setTitle(title);

        final EditText input = new EditText(context);
        if (multiline){
            input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setSingleLine(false);
        }
        else
            input.setInputType(InputType.TYPE_CLASS_TEXT);

        if (defValue != null && !defValue.isEmpty())
            input.setText(defValue, TextView.BufferType.EDITABLE);

        if (hint != null && !hint.isEmpty())
            input.setHint(hint);

        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null)
                    callback.onComplete(input.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, nullOnClickListener);

        builder.show();
    }

    public static void showAlert(
            Context context,
            CharSequence title,
            CharSequence message,
            DialogInterface.OnClickListener okOnClickListener){

        if (okOnClickListener == null) okOnClickListener = nullOnClickListener;

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, okOnClickListener);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showSingleChoice(
            Context context,
            String title,
            @ArrayRes int itemsId,
            int checkedItem,
            DialogInterface.OnClickListener choiceOnClickListener,
            DialogInterface.OnClickListener cancelOnClickListener) {
        showSingleChoice(context, title, itemsId, checkedItem, choiceOnClickListener, cancelOnClickListener, null);
    }
    public static void showSingleChoice(
            Context context,
            String title,
            @ArrayRes int itemsId,
            int checkedItem,
            DialogInterface.OnClickListener choiceOnClickListener,
            DialogInterface.OnClickListener cancelOnClickListener,
            DialogInterface.OnClickListener okOnClickListener) {

        if (choiceOnClickListener == null) choiceOnClickListener = nullOnClickListener;
        if (cancelOnClickListener == null) cancelOnClickListener = nullOnClickListener;

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(itemsId, checkedItem, choiceOnClickListener)
                .setNegativeButton(android.R.string.cancel, cancelOnClickListener);

        if (okOnClickListener != null)
            builder.setPositiveButton(android.R.string.ok, okOnClickListener);

        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void showShareDialog(final Context context, final RingtoneVM ringtone){
        String title = context.getString(R.string.action_share) + ":";
        DialogHelper.showSingleChoice(context, title, R.array.share_array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                CharSequence[] arr = context.getResources().getTextArray(R.array.share_array);
                if (item >= 0 && item < arr.length) {
                    String input = arr[item].toString();
                    if (input.equals(context.getString(R.string.action_share_text)))
                        ShareHelper.shareText(context, ringtone);
                    if (input.equals(context.getString(R.string.action_share_wav)))
                        ShareHelper.shareWav(context, ringtone);
                    if (input.equals(context.getString(R.string.action_share_mp3)))
                        ShareHelper.shareMp3(context, ringtone);
                }
                dialog.dismiss();
            }
        }, null);
    }
}
