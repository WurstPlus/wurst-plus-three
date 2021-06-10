package me.travis.wurstplusthree.gui.alt.Objects;

import me.travis.wurstplusthree.gui.alt.tools.EncryptionTools;

import java.io.Serializable;

/**
 * @author Madmegsox1
 * @since 23/05/2021
 */

public class AccountData implements Serializable {
    /*
    public static final long serialVersionUID = 0xF72DEBAC;
    public final String user;
    public final String pass;
    public String alias;

    protected AccountData(String user, String pass, String alias) {
        this.user = EncryptionTools.encode(user);
        this.pass = EncryptionTools.encode(pass);
        this.alias = alias;
    }

    public boolean equalsBasic(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccountData other = (AccountData) obj;
        return user.equals(other.user);
    }
     */
}
