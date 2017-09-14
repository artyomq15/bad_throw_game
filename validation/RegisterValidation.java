package by.bsu.mmf.badthrowgame.validation;

import java.util.Arrays;
import java.util.regex.Pattern;


public class RegisterValidation {
    public static Pattern pattern = Pattern.compile("[A-Za-z0-9_]+");

    public static boolean loginRegisterValidation(String login) {
        return (login != null) && pattern.matcher(login).matches();
    }

    public static boolean passwordConfirmValidation(char[] password, char[] confirm){
        return Arrays.equals(password, confirm) && pattern.matcher(String.valueOf(password)).matches();
    }
}
