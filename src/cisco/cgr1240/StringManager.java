package cisco.cgr1240;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shinny on 2016/04/05.
 */
public class StringManager {
    public static String getStringRepresentation(ArrayList<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());

        for(Character ch: list) {
            builder.append(ch);
        }

        return builder.toString();
    }

    public static String removeString(String strSrc, String strRemove) {
        Pattern pattern = Pattern.compile(strRemove);
        Matcher matcher = pattern.matcher(strSrc);
        String strTmp = matcher.replaceAll("");

        return strTmp;
    }

    public static String removeUnits(String strSrc) {
        String regex = "\\[.+|\\(.+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(strSrc);
        String strTmp = matcher.replaceAll("");

        return strTmp;
    }
}
