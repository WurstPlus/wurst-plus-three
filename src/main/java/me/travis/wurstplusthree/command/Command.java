package me.travis.wurstplusthree.command;

import me.travis.wurstplusthree.util.Globals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Command implements Globals {

    protected List<String> names;

    public Command(String name) {
        this.names = Collections.singletonList(name);
    }

    public Command(String... names) {
        this.names = Arrays.asList(names.clone());
    }

    public abstract void execute(String[] message);

    public List<String> getNames() {
        return this.names;
    }

    public boolean isName(String name) {
        for (String name_ : this.names) {
            if (name_.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
