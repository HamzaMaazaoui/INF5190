package com.inf5190.chat.auth.repository;

public class FirestoreUserAccount {
    private String username;
    private String encodedPassword;

    public FirestoreUserAccount() {
    }

    public FirestoreUserAccount(String username, String encodedPassword) {
        this.username = username;
        this.encodedPassword = encodedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((encodedPassword == null) ? 0 : encodedPassword.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FirestoreUserAccount other = (FirestoreUserAccount) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (encodedPassword == null) {
            if (other.encodedPassword != null)
                return false;
        } else if (!encodedPassword.equals(other.encodedPassword))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "FirestoreUserAccount [username=" + username + ", encodedPassword=" + encodedPassword + "]";
    }
}
