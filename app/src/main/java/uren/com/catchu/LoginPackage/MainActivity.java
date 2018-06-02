package uren.com.catchu.LoginPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
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

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.google.firebase.auth.FacebookAuthProvider;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import uren.com.catchu.LoginPackage.utils.ClickableImageView;
import uren.com.catchu.ModelsPackage.User;
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
    private TwitterLoginButton mLoginButton;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private boolean fbLogin = false;
    private boolean twLogin = false;

    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_main);

        mLoginButton = findViewById(R.id.twitterLoginButton);

        getHashKey();

        user = new User();
        mAuth = FirebaseAuth.getInstance();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setStatusBarTransparent();
        initVariables();

    }

    private void getHashKey() {

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "uren.com.catchu",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

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

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.facebookLoginButton);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile",
                "email",
                "user_birthday",
                "user_friends"));

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                getFacebookuserInfo(loginResult);

                Log.i("Info", "facebook:onSucces:" + loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("Info", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {

                Log.i("Info", "facebook:onError:" + error);
            }
        });

        loginButton.performClick();
    }
    private void imgTwitterClicked() {

        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {

                Log.i("Info", "twitterLogin:failure:" + exception);
            }
        });

        mLoginButton.performClick();
    }

    private void loginBtnClicked() {

    }

    private void handleTwitterSession(final TwitterSession session) {

        Log.i("Info", "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.i("Info", "signInWithCredential:success");

                            twLogin = true;

                        } else {
                            Log.i("Info", "  >>signInWithCredential:failure:" + task.getException());
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Info", "  >>signInWithCredential:onFailure:" + e.toString());
            }
        });
    }

    public void getFacebookuserInfo(final LoginResult loginResult) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.i("Info", "Facebook response:" + response.toString());

                        try {

                            user.setEmail(object.getString("email"));
                            user.setBirthdate(object.getString("birthday"));
                            //user.setGender(object.getString("gender"));
                            user.setProviderId(object.getString("id"));
                            user.setUsername(" ");
                            user.setPhoneNum(" ");

                            String[] elements = object.getString("name").split(" ");
                            user.setName(elements[0]);

                            String[] lastname = Arrays.copyOfRange(elements, 1, elements.length);

                            StringBuilder builder = new StringBuilder();
                            for (String s : lastname) {
                                builder.append(s);
                                builder.append(" ");
                            }
                            String surname = builder.toString().trim();
                            user.setSurname(surname);

                            String fbUserId = object.getString("id");
                            setFacebookProfilePicture(fbUserId);

                            Log.i("FBLogin", "  >>email     :" + user.getEmail());
                            Log.i("FBLogin", "  >>birthday  :" + user.getBirthdate());
                            //Log.i("FBLogin", "  >>gender    :" + user.getGender());
                            Log.i("FBLogin", "  >>name      :" + user.getName());
                            Log.i("FBLogin", "  >>surname   :" + user.getSurname());

                            handleFacebookAccessToken(loginResult.getAccessToken());

                        } catch (JSONException e) {
                            Log.i("Info", "  >>JSONException error:" + e.toString());
                        } catch (Exception e) {
                            Log.i("Info", "  >>Profile error:" + e.toString());
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void setFacebookProfilePicture(String userID) {

        try {
            String url = "https://graph.facebook.com/" + userID + "/picture?type=large";
            user.setProfilePicSrc(url);

        } catch (Exception e) {

            Log.i("Info", "  >>setFacebookProfilePicture error:" + e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            // Pass the activity result to the Twitter login button.
            mLoginButton.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Log.i("Info", "onActivityResult error:" + e.toString());
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {

        Log.i("Info", "handleFacebookAccessToken starts:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        try {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                Log.i("Info", "  >>signInWithCredential:success");

                                fbLogin = true;

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                user.setUserId(currentUser.getUid());

                               /* if (user.getProfilePicSrc() != null) {

                                    try {
                                        DownloadTask taskManager = new DownloadTask();
                                        taskManager.execute(user.getProfilePicSrc()).get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }*/

                                //saveProfPicViaSocialApp();
                                //new FirebaseUserAdapter(EnterPageActivity.this, user);

                               // FBAddFacebookUserAdapter.saveFacebookUser(user.getProviderId());

                                //startNextPage();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("Info", "  >>signInWithCredential:failure:" + task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i("Info", "  >>handleFacebookAccessToken error:" + e.toString());
        }
    }
}
