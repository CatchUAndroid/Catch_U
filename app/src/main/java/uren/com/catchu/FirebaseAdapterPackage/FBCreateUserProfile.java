package uren.com.catchu.FirebaseAdapterPackage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;

import uren.com.catchu.ModelsPackage.User;

import static uren.com.catchu.Constants.FirebaseConstants.*;
import static uren.com.catchu.Constants.FirebaseFunctionsConstant.createUserProfile;

public class FBCreateUserProfile {

    User user;
    Context context;

    public FBCreateUserProfile(Context context, User user){
        this.user = user;
        this.context = context;
        saveUserToDB();
    }

    public void saveUserToDB() {

        JSONObject jsonUser = null;

        try {
            JSONObject jsonUserDtl = new JSONObject();

            jsonUserDtl.put(userId, user.getUserId());
            jsonUserDtl.put(email, user.getEmail());
            jsonUserDtl.put(userName, user.getUsername());
            jsonUserDtl.put(name, user.getName());
            jsonUserDtl.put(surname, user.getSurname());
            jsonUserDtl.put(mobilePhone, user.getPhoneNum().toString());
            jsonUserDtl.put(birthday, user.getBirthdate());
            jsonUserDtl.put(profilePictureUrl, user.getProfilePicSrc());
            //jsonUserDtl.put(profilePicMiniUrl, user.getMiniProfPicUrl());
            jsonUserDtl.put(providerId, user.getProviderId());

            Log.i("Info", "user json:" + jsonUserDtl.toString());

            FirebaseFunctions.getInstance()
                    .getHttpsCallable(createUserProfile)
                    .call(jsonUserDtl)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Info", "Function call failure:" + e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                @Override
                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                    Log.i("Info", "Function call is ok");

                    FirebaseGetAccountHolder.getInstance(user.getUserId());
                }
            });

        }catch (Exception e){
            //Toast.makeText(context, "Teknik hata:" + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //new SaveJsonUser().execute(jsonUser);
    }
}
