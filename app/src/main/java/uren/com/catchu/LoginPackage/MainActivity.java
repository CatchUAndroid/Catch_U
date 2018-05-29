package uren.com.catchu.LoginPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import uren.com.catchu.LoginPackage.utils.ClickableImageView;
import uren.com.catchu.R;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    RelativeLayout backgroundLayout;
    EditText emailET;
    EditText passwordET;
    TextView registerText;
    TextView forgetPasText;
    ClickableImageView imgFacebook;
    ClickableImageView imgTwitter;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarTransparent();
        initVariables();
    }

    private void initVariables() {
        backgroundLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        emailET = (EditText) findViewById(R.id.input_email);
        passwordET = (EditText) findViewById(R.id.input_password);
        registerText = (TextView) findViewById(R.id.btnRegister);
        forgetPasText = (TextView) findViewById(R.id.btnForgetPassword);
        imgFacebook = (ClickableImageView) findViewById(R.id.clickImageFB);
        imgTwitter = (ClickableImageView) findViewById(R.id.clickImageTwitter);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        setClickableTexts(this);

        backgroundLayout.setOnClickListener(this);
        emailET.setOnClickListener(this);
        passwordET.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgTwitter.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    private void setClickableTexts(Activity act) {
        final Activity activity = act;
        String textRegister=getResources().getString(R.string.createAccount);
        String textForgetPssword = getResources().getString(R.string.forgetPassword);
        final SpannableString spanStringRegister = new SpannableString(textRegister);
        final SpannableString spanStringForgetPas= new SpannableString(textForgetPssword);
        spanStringRegister.setSpan(new UnderlineSpan(), 0, spanStringRegister.length(), 0);
        spanStringForgetPas.setSpan(new UnderlineSpan(), 0, spanStringForgetPas.length(), 0);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                if(textView.equals(registerText)){
                    //Toast.makeText(MainActivity.this, "Register click!", Toast.LENGTH_SHORT).show();
                    registerTextClicked();
                }else if(textView.equals(forgetPasText)){
                    Toast.makeText(MainActivity.this, "Forgetpas click!", Toast.LENGTH_SHORT).show();
                    forgetPasTextClicked();
                }else{
                    Toast.makeText(MainActivity.this, "sıçtık!", Toast.LENGTH_SHORT).show();
                }


            }
        };
        spanStringRegister.setSpan(clickableSpan,0, spanStringRegister.length(), 0);
        spanStringForgetPas.setSpan(clickableSpan,0, spanStringForgetPas.length(),0);

        registerText.setText(spanStringRegister);
        forgetPasText.setText(spanStringForgetPas);
        registerText.setMovementMethod(LinkMovementMethod.getInstance());
        forgetPasText.setMovementMethod(LinkMovementMethod.getInstance());
        registerText.setHighlightColor(Color.TRANSPARENT);
        forgetPasText.setHighlightColor(Color.TRANSPARENT);
        registerText.setLinkTextColor(getColor(R.color.clearRed));
        forgetPasText.setLinkTextColor(getColor(R.color.clearRed));

    }

    @Override
    public void onClick(View view) {

        if (view == backgroundLayout) {
            //continue
        } else if (view == emailET) {

        } else if (view == passwordET) {

        } else if (view == imgFacebook) {
            Toast.makeText(MainActivity.this, "click!", Toast.LENGTH_SHORT).show();
            imgFacebookClicked();
        } else if (view == imgTwitter) {
            imgTwitterClicked();
        }else if(view == btnLogin) {
            loginBtnClicked();
        } else {

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
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

    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /*****************************CLICK EVENTS******************************/

    private void registerTextClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        //finish();
    }
    private void forgetPasTextClicked() {

    }
    private void imgFacebookClicked() {

    }
    private void imgTwitterClicked() {

    }
    private void loginBtnClicked() {

    }
}