package uren.com.catchu.LoginPackage.utils;

import android.text.TextUtils;

/**
 * Created by ASUS on 30.5.2018.
 */

public class Validation {

    private static Validation instance = null;
    private String errorMessage;

    //Constants
    private final static String EMAIL_ERR_REQUIRED = "Email boş olamaz!";
    private final static String EMAIL_ERR_INVALID = "Geçerli bir email adresi giriniz!";
    private final static String PASSWORD_ERR_REQUIRED = "Şifre boş olamaz!";
    private final static String PASSWORD_ERR_LENGTH = "Şifre en az 6 karakter olmalıdır!";
    private final static String USERNAME_ERR_REQUIRED = "Kullanıcı adı boş olamaz!";

    private final static int PASSWORD_MAX_LENGTH = 6;

    public static Validation getInstance() {
        if (instance == null) {
            instance = new Validation();
        }
        return(instance);
    }

    private Validation() {
    }

    public boolean isValidEmail(String email){

        if (TextUtils.isEmpty(email)) {
            errorMessage = EMAIL_ERR_REQUIRED;
            return false;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorMessage = EMAIL_ERR_INVALID;
            return false;
        }

        return true;
    }

    public boolean isValidPassword(String password){

        if (TextUtils.isEmpty(password)) {
            errorMessage = PASSWORD_ERR_REQUIRED;
            return false;
        }

        if (password.length() < PASSWORD_MAX_LENGTH) {
            errorMessage = PASSWORD_ERR_LENGTH;
            return false;
        }

        return true;
    }

    public boolean isValidUserName(String userName){

        if (TextUtils.isEmpty(userName)) {
            errorMessage = USERNAME_ERR_REQUIRED;
            return false;
        }

        return true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
