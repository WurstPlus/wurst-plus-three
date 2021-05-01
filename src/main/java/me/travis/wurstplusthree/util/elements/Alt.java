package me.travis.wurstplusthree.util.elements;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

public class Alt {
    String username;
    String password;
    boolean isCracked;

    public Alt(String username, String password) {
        this.username = username;
        this.password = password;
        this.isCracked = password.isEmpty();
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public boolean getCracked(){
        return this.isCracked;
    }

}
