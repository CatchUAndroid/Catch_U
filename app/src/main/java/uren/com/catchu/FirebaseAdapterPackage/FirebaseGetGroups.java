package uren.com.catchu.FirebaseAdapterPackage;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class FirebaseGetGroups {

    String userId;
    ArrayList<Group> groupArrayList;
    boolean groupDeleted = false;

    public static FirebaseGetGroups FBGetGroupsInstance = null;


    public static FirebaseGetGroups getInstance(String userId) {

        if (FBGetGroupsInstance == null)
            FBGetGroupsInstance = new FirebaseGetGroups(userId);
        else
            setGroupDeleted(false);

        return FBGetGroupsInstance;
    }

    public static void setInstance(FirebaseGetGroups instance) {
        FBGetGroupsInstance = instance;
    }

    public ArrayList<Group> getGroupArrayList() {
        return groupArrayList;
    }

    public void setGroupArrayList(ArrayList<Group> groupArrayList) {
        this.groupArrayList = groupArrayList;
    }

    public static void setGroupDeleted(boolean groupDeleted) {
        FBGetGroupsInstance.groupDeleted = groupDeleted;
    }

    public FirebaseGetGroups(String userID) {
        this.userId = userID;
        this.groupDeleted = false;
        fillGroupList();
    }

    public void addGroupToList(Group group) {

        boolean groupFounded = false;
        this.groupDeleted = false;

        for (Group group1 : FBGetGroupsInstance.groupArrayList) {
            if (group1.getGroupID().equals(group.getGroupID())) {
                groupFounded = true;
                break;
            }
        }

        if (!groupFounded)
            FBGetGroupsInstance.groupArrayList.add(group);
    }

    public void removeGroupFromList(String groupID) {
        int index = 0;
        for (Group group : FBGetGroupsInstance.groupArrayList) {
            if (group.getGroupID().equals(groupID)) {
                FBGetGroupsInstance.groupArrayList.remove(index);
                this.groupDeleted = true;
                break;
            }
            index++;
        }
    }

    public int getListSize() {
        return FBGetGroupsInstance.groupArrayList.size();
    }

    private void fillGroupList() {

        this.groupArrayList = new ArrayList<Group>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(UserGroups).child(userId);

        //UserGroups altindan groupID bilgileri okunur
        ValueEventListener valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot groupSnapShot : dataSnapshot.getChildren()) {

                    final Group group = new Group();
                    String groupID = groupSnapShot.getKey();

                    group.setGroupID(groupID);

                    final DatabaseReference mDbrefDetails = FirebaseDatabase.getInstance().getReference(Groups).child(groupID);

                    ValueEventListener valueEventListenerForDetails = mDbrefDetails.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null &&
                                    !FBGetGroupsInstance.groupDeleted) {

                                Map<String, Object> map = new HashMap<String, Object>();
                                map = (Map) dataSnapshot.getValue();

                                group.setAdminID((String) map.get(Admin));
                                group.setGroupName((String) map.get(GroupName));
                                group.setPictureUrl((String) map.get(GroupPictureUrl));

                                Map<String, Object> userList = new HashMap<String, Object>();
                                userList = (Map<String, Object>) map.get(UserList);

                                Log.i("Info", "xx");

                                ArrayList<Friend> friendArrayList = new ArrayList<Friend>();
                                Iterator it = userList.entrySet().iterator();

                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry) it.next();
                                    String key = (String) pair.getKey();

                                    Friend friend = new Friend();

                                    Map<String, String> userDetail = new HashMap<String, String>();
                                    userDetail = (Map<String, String>) pair.getValue();

                                    friend.setUserID((String) pair.getKey());
                                    friend.setProfilePicSrc(userDetail.get(profilePictureUrl));
                                    friend.setNameSurname(userDetail.get(nameSurname));
                                    friend.setUserName(userDetail.get(userName));

                                    friendArrayList.add(friend);
                                    it.remove();
                                }

                                group.setFriendList(friendArrayList);
                                addGroupToList(group);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("Info", "onCancelled1 error:" + databaseError.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Info", "onCancelled2 error:" + databaseError.toString());
            }
        });
    }
}