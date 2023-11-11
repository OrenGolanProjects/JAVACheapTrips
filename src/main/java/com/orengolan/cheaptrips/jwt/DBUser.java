package com.orengolan.cheaptrips.jwt;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.google.common.base.MoreObjects;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
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
    @Indexed(unique = true)
    private String email;
    @Max(255)
    private String password;

    protected DBUser() {}

    public static String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("name", email)
                .toString();
    }

    public String getName() {
        return email;
    }
    public void setPassword(String password) {
        this.password = password; //DBUser.hashPassword(unencryptedPassword);
    }
    public String getPassword() {
        return password;
    }
    public void setName(String name) {
        this.email = name;
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
        private String name;
        private String password;

        private UserBuilder() {
        }

        public static UserBuilder anUser() {
            return new UserBuilder();
        }

        public UserBuilder name(String name) {
            this.name = name;
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
            DBUser user = new DBUser();
            user.setName(name);
            user.setPassword(password);
            user.setId(id);
            return user;
        }
    }
}