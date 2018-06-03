package uren.com.catchu.ModelsPackage;

public class User {

    private String userId;
    private String email;
    private String username;
    private String name;
    private String surname;
    private String gender;
    private String profilePicSrc;
    private String miniProfPicUrl;
    private String birthdate;
    private String phoneNum;
    private String providerId;
    private String password ;

    public User() {
        this.userId = "";
        this.email = "";
        this.username = "";
        this.name = "";
        this.surname = "";
        this.gender = "";
        this.profilePicSrc = "";
        this.miniProfPicUrl = "";
        this.birthdate = "";
        this.phoneNum = "";
        this.providerId = "";
        this.password = "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getMiniProfPicUrl() {
        return miniProfPicUrl;
    }

    public void setMiniProfPicUrl(String miniProfPicUrl) {
        this.miniProfPicUrl = miniProfPicUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicSrc() {
        return profilePicSrc;
    }

    public void setProfilePicSrc(String profilePicSrc) {
        this.profilePicSrc = profilePicSrc;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
