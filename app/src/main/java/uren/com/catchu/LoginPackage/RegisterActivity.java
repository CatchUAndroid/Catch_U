package uren.com.catchu.LoginPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import uren.com.catchu.LoginPackage.utils.Validation;
import uren.com.catchu.R;
import uren.com.catchu.Utils.DialogBox;

public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {

    //XML
    Toolbar mToolBar;
    RelativeLayout backgroundLayout;
    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    Button btnRegister;

    //Local
    String userName;
    String userEmail;
    String userPassword;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setStatusBarTransparent();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        init();

    }

    private void init() {
        backgroundLayout = (RelativeLayout) findViewById(R.id.registerLayout);
        usernameET = (EditText) findViewById(R.id.input_username);
        emailET = (EditText) findViewById(R.id.input_email);
        passwordET = (EditText) findViewById(R.id.input_password);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        backgroundLayout.setOnClickListener(this);
        usernameET.setOnClickListener(this);
        emailET.setOnClickListener(this);
        passwordET.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerLayout:
                break;
            case R.id.input_username:
                break;
            case R.id.input_email:
                break;
            case R.id.input_password:
                break;
            case R.id.btnRegister:
                btnRegisterClicked();
                break;

            default:
                break;
        }

    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /*****************************CLICK EVENTS******************************/

    private void btnRegisterClicked() {

        userName = usernameET.getText().toString();
        userEmail = emailET.getText().toString();
        userPassword = passwordET.getText().toString();

        //validation controls
        if(!checkValidation(userName, userEmail, userPassword)){return;}






    }

    private boolean checkValidation(String name, String email, String password) {

        //username validation
        if(!Validation.getInstance().isValidUserName(name)){
            //Toast.makeText(this, Validation.getInstance().getErrorMessage() , Toast.LENGTH_SHORT).show();
            openDialog(Validation.getInstance().getErrorMessage());
            return false;
        }

        //email validation
        if(!Validation.getInstance().isValidEmail(email)){
            //Toast.makeText(this, Validation.getInstance().getErrorMessage() , Toast.LENGTH_SHORT).show();
            openDialog(Validation.getInstance().getErrorMessage());
            return false;
        }

        //password validation
        if(!Validation.getInstance().isValidPassword(password)){
            //Toast.makeText(this, Validation.getInstance().getErrorMessage() , Toast.LENGTH_SHORT).show();
            openDialog(Validation.getInstance().getErrorMessage());
            return false;
        }

        return true;
    }

    public void openDialog(String message){

        DialogBox.getInstance().showDialogBox(this, message);

    }




}
