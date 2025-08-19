package eu.dec21.wp.users.entity;

public class UserBuilder {
    private User user;

    public UserBuilder() {
        this.user = new User();
    }

    public UserBuilder reset() {
        this.user = new User();
        return this;
    }

    public UserBuilder setFirstName(String firstName) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setFirstName(firstName);
        return this;
    }

    public UserBuilder setLastName(String lastName) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setLastName(lastName);
        return this;
    }

    public UserBuilder setEmail(String email) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setEmail(email);
        return this;
    }

    public UserBuilder setPassword(String password) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setPassword(password);
        return this;
    }

    public UserBuilder setId(Long id) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setId(id);
        return this;
    }

    public UserBuilder setAuthSystem(String authSystem) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setAuthSystem(authSystem);
        return this;
    }

    public UserBuilder setAuthID(String authId) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setAuthID(authId);
        return this;
    }

    public UserBuilder setSuspended(boolean suspended) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setSuspended(suspended);
        return this;
    }

    public UserBuilder setVersion(Long version) {
        if (this.user == null) {
            this.reset();
        }
        this.user.setVersion(version);
        return this;
    }

    public User build() {
        if (this.user == null) {
            this.reset();
        }
        return this.user;
    }
}
