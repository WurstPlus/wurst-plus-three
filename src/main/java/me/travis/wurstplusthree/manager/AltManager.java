package me.travis.wurstplusthree.manager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.util.elements.Alt;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

public class AltManager {
    ArrayList<Alt> alts;
    String altApi = "https://auth.mcleaks.net/v1/";
    Minecraft mc = Minecraft.getMinecraft();

    public AltManager(){
        alts = new ArrayList<>();
    }

    public void addAlt(String u, String p){
        alts.add(new Alt(u, p));
    }

    public void removeAlt(Alt alt){
        alts.remove(alt);
    }

    public boolean login(Alt alt){
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(
                Proxy.NO_PROXY,""
        ).createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(alt.getUsername());
        auth.setPassword(alt.getPassword());
        try {
            auth.logIn();
            Session session = new Session(
                    auth.getSelectedProfile().getName(),
                    auth.getSelectedProfile().getId().toString(),
                    auth.getAuthenticatedToken(), "mojang"
            );
            setSession(session);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean loginCracked(String alt){
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(
                Proxy.NO_PROXY,""
        ).createUserAuthentication(Agent.MINECRAFT);
        auth.logOut();
        Session crackedSession = new Session(alt, alt, "0", "legacy");
        try {
            setSession(crackedSession);
            WurstplusThree.LOGGER.info("Logged in as " + alt);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    private void setSession(Session newSession) throws Exception {
        Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();
        try {
            Field session = null;

            for (Field field : mc.getDeclaredFields()) {
                if (field.getType().isInstance(newSession)) {
                    session = field;
                    System.out.println("Attempting Injection into Session.");
                }
            }

            if (session == null) {
                throw new IllegalStateException("No field of type " + Session.class.getCanonicalName() + " declared.");
            }

            session.setAccessible(true);
            session.set(Minecraft.getMinecraft(), newSession);
            session.setAccessible(false);
        } catch (Exception exeption) {
            exeption.printStackTrace();
            throw exeption;
        }
    }
}
