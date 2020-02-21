package model;

public class User {
    private String userId;
    private String firstname;
    private String lastname;

    public String generateBlockchainReference(){
        return "resource:de.tbi.prototyp.User#" + userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
