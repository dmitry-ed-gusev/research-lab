package dg.social;

/**
 * Common abstract config for social network client.
 * Created by gusevdm on 12/12/2016.
 */

public abstract class AbstractSocialNetConfig {

    private String username;
    private String password;

    /***/
    public AbstractSocialNetConfig(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
