package uren.com.catchu.General_Utils;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by ASUS on 31.5.2018.
 */

public class DialogBox {
    private static DialogBox instance = null;

    public static DialogBox getInstance() {
        if (instance == null) {
            instance = new DialogBox();
        }
        return(instance);
    }

    private DialogBox() {
    }

    public void showDialogBox(Context context, String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("OOPS!!");
        alert.setMessage(message);
        alert.setPositiveButton("OK",null);
        alert.show();
    }







}
