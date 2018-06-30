package uren.com.catchu.ModelsPackage;

import java.io.Serializable;

public class Friend implements Serializable {

    String userID;
    String nameSurname;
    String profilePicSrc;
    String providerId;
    String friendStatus;
    String userName;

    public Friend() {
        this.userID = "";
        this.nameSurname = "";
        this.profilePicSrc = "";
        this.providerId = "";
        this.friendStatus = "";
        this.userName = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getProfilePicSrc() {
        return profilePicSrc;
    }

    public void setProfilePicSrc(String profilePicSrc) {
        this.profilePicSrc = profilePicSrc;
    }
}
