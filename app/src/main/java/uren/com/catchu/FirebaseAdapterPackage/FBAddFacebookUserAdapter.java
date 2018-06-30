package uren.com.catchu.FirebaseAdapterPackage;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;

import static uren.com.catchu.Constants.FirebaseConstants.firebaseUserId;
import static uren.com.catchu.Constants.FirebaseFunctionsConstant.addFacebookUser;

public class FBAddFacebookUserAdapter {

    public static void saveFacebookUser(String providerID){

        JSONObject faceJsonMain = null;

        try {
            JSONObject faceUserDtl = new JSONObject();

            faceUserDtl.put(firebaseUserId, FirebaseGetAccountHolder.getUserID());

            faceJsonMain = new JSONObject();
            faceJsonMain.put(providerID, faceUserDtl);

        }catch (Exception e){
            e.printStackTrace();
        }

        FirebaseFunctions.getInstance()
                .getHttpsCallable(addFacebookUser)
                .call(faceJsonMain)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Info", "Function call failure:" + e.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.i("Info", "Function call is ok");
            }
        });
    }
}