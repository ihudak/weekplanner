package eu.dec21.wp.helper;

import lombok.Getter;

@Getter
public class Constraints {
    public final static String emailRegExp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public final static String passwordRegExp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";
    public final static int minPrio = 0;   // minimum priority for th To-Do items and Categories
    public final static int maxPrio = 100; // maximum priority for th To-Do items and Categories
}
