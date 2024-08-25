package com.orengolan.cheaptrips.jwt;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.google.common.base.MoreObjects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Document(collection = "usersJWT")
public class DBUser implements Serializable, Persistable<String> {

    private static final long serialVersionUID = -5554304839188669754L;

    @Id
    private String _id;
    @Override
    public boolean isNew() {
        return null == getId();
    }

    @NotNull
    @Indexed(unique = true) // unique index
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", message = "Invalid email format, must be a valid email address")
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one letter and one number")
    private String password;

    public DBUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("email", email)
                .toString();
    }

    public String getEmail() {
        return email;
    }
    public void setPassword(String password) {
        this.password = password; //DBUser.hashPassword(unencryptedPassword);
    }
    public String getPassword() {
        return password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String getId() {
        return this._id;
    }
    public void setId(String id) {
        this._id = id;
    }

    public static final class UserBuilder {
        private String id;
        private String email;
        private String password;

        private UserBuilder() {
        }

        public static UserBuilder anUser() {
            return new UserBuilder();
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder id(String id) {
            this.id = id;
            return this;
        }

        public DBUser build() {
            DBUser user = new DBUser(email, password);
            user.setId(id);
            return user;
        }
    }
}