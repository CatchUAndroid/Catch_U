package uren.com.catchu.FirebaseAdapterPackage;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import uren.com.catchu.Constants.FirebaseConstants;
import uren.com.catchu.ModelsPackage.Friend;

import static uren.com.catchu.Constants.FirebaseConstants.nameSurname;
import static uren.com.catchu.Constants.FirebaseConstants.profilePictureUrl;
import static uren.com.catchu.Constants.FirebaseConstants.userId;
import static uren.com.catchu.Constants.FirebaseFunctionsConstant.getFriends;

public class FirebaseGetFriends {

    String userId;
    static ValueEventListener valueEventListenerForFriendList;

    private static FirebaseGetFriends FBGetFriendsInstance = null;

    ArrayList<Friend> friendList;

    public static FirebaseGetFriends getInstance(String userId) {

        if (FBGetFriendsInstance == null)
            FBGetFriendsInstance = new FirebaseGetFriends(userId);

        return FBGetFriendsInstance;
    }

    public static FirebaseGetFriends getFBGetFriendsInstance() {
        return FBGetFriendsInstance;
    }

    public static void setInstance(FirebaseGetFriends instance) {
        FBGetFriendsInstance = instance;
    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<Friend> friendList) {
        this.friendList = friendList;
    }

    public FirebaseGetFriends(String userID) {

        this.userId = userID;
        fillFriendList();
    }

    public int getListSize() {
        return friendList.size();
    }

    private void fillFriendList() {

        friendList = new ArrayList<>();

        FirebaseFunctions.getInstance()
                .getHttpsCallable(getFriends)
                .call()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Info", "Function call failure:" + e.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Log.i("Info", "Function call is ok");
                        Log.i("Info", "Function httpsCallableResult:" + httpsCallableResult.toString());

                        if(friendList != null) friendList.clear();

                        Map<String, Object> friendArray = (Map) httpsCallableResult.getData();
                        //Log.i("Info", "Function call is ok:" + friendArray.toString() );

                        if (friendArray != null) {
                            for (String friendUserID : friendArray.keySet()) {

                                Friend friend = new Friend();
                                Map<String, Object> users = (Map<String, Object>) friendArray.get(friendUserID);
                                friend.setUserID(friendUserID);
                                friend.setNameSurname((String) users.get(nameSurname));
                                friend.setProfilePicSrc((String) users.get(profilePictureUrl));
                                friendList.add(friend);
                            }
                        }
                    }
            });




        /*friendList = new ArrayList<>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(FirebaseConstants.Friend).child(userId);

        valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(friendList != null)
                    friendList.clear();

                for(DataSnapshot friendsSnapshot: dataSnapshot.getChildren()){

                    if(friendsSnapshot.getValue() != null) {

                        final Friend friend = new Friend();
                        String friendUserID = friendsSnapshot.getKey();
                        friend.setUserID(friendUserID);

                        Map<String, Object> userList = (Map) dataSnapshot.getValue();
                        Map<String, Object> users = (Map<String, Object>) userList.get(friendUserID);

                        friend.setNameSurname((String) users.get(nameSurname));
                        friend.setProfilePicSrc((String) users.get(profilePictureUrl));

                        friendList.add(friend);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i("Info", "onCancelled2 error:" + databaseError.toString());
            }
        });*/
    }
}