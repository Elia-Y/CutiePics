package ygz.cutiepics.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

// [START blog_user_class]
@IgnoreExtraProperties
public class User implements Serializable {

    private String email;
    private String photo;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String photo) {
        this.email = email;
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

}
// [END blog_user_class]
