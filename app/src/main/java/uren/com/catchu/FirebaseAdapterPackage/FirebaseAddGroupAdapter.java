package uren.com.catchu.FirebaseAdapterPackage;


import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uren.com.catchu.ModelsPackage.Friend;
import uren.com.catchu.ModelsPackage.Group;

import static uren.com.catchu.Constants.FirebaseConstants.Admin;
import static uren.com.catchu.Constants.FirebaseConstants.GroupName;
import static uren.com.catchu.Constants.FirebaseConstants.GroupPictureUrl;
import static uren.com.catchu.Constants.FirebaseConstants.Groups;
import static uren.com.catchu.Constants.FirebaseConstants.UserGroups;
import static uren.com.catchu.Constants.FirebaseConstants.UserList;
import static uren.com.catchu.Constants.FirebaseConstants.nameSurname;
import static uren.com.catchu.Constants.FirebaseConstants.profilePictureUrl;
import static uren.com.catchu.Constants.FirebaseConstants.userName;

public class FirebaseAddGroupAdapter {

    Group group;
    public static DatabaseReference mDbref;
    String groupID;

    public String getGroupID() {
        return groupID;
    }

    public FirebaseAddGroupAdapter(Group group){

        this.group = group;
        saveGroupToFB();
    }

    private void saveGroupToFB() {

        mDbref = null;
        mDbref = FirebaseDatabase.getInstance().getReference();

        this.groupID = mDbref.child(Groups).push().getKey();

        Map<String, String> values = new HashMap<>();

        values.put(Admin, group.getAdminID());
        values.put(GroupName, group.getGroupName());

        addGroupItems(values);
        addGroupUserIDs();

        for(Friend friend: group.getFriendList()){
            String userID = friend.getUserID();
            addGroupToUserGroup(userID);
        }

        addGroupToUserGroup(group.getAdminID());
    }

    public void addGroupItems(Map values) {

        Log.i("Info", "addGroupItems starts");

        mDbref.child(Groups).child(getGroupID()).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }

    public void savePictureUrl(String picUrl){

        mDbref = FirebaseDatabase.getInstance().getReference().child(Groups).child(getGroupID());

        Map<String, Object> values = new HashMap<>();

        values.put(GroupPictureUrl, picUrl);

        mDbref.updateChildren(values);
    }

    public void addGroupUserIDs(){

        for(Friend friend: group.getFriendList()){
            setUserIDToUserList(friend);
        }

        addAdminToUserList();
    }

    private void addAdminToUserList() {

        FirebaseGetAccountHolder firebaseGetAccountHolder = FirebaseGetAccountHolder.getInstance(group.getAdminID());

        Friend friend = new Friend();

        String nameSurname = "";
        if(firebaseGetAccountHolder.getUser().getName() != null && firebaseGetAccountHolder.getUser().getSurname() != null)
            nameSurname = firebaseGetAccountHolder.getUser().getName().trim() + " " + firebaseGetAccountHolder.getUser().getSurname().trim();

        friend.setNameSurname(nameSurname);
        friend.setProfilePicSrc(firebaseGetAccountHolder.getUser().getProfilePicSrc());
        friend.setUserID(firebaseGetAccountHolder.getUser().getUserId());
        friend.setUserName(firebaseGetAccountHolder.getUser().getUsername());

        setUserIDToUserList(friend);
    }

    public void setUserIDToUserList(Friend friend){

        Map<String, String> tempMap = new HashMap<String, String>();
        tempMap.put(nameSurname, friend.getNameSurname());
        tempMap.put(profilePictureUrl, friend.getProfilePicSrc());
        tempMap.put(userName, friend.getUserName());

        String userID = friend.getUserID();

        mDbref.child(Groups)
                .child(getGroupID())
                .child(UserList)
                .child(userID).setValue(tempMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }

    public void addGroupToUserGroup(String userID){

        mDbref = FirebaseDatabase.getInstance().getReference().child(UserGroups).child(userID);

        mDbref.child(getGroupID()).setValue(" ", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }
}
