package me.daram.chungsasikdan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daram on 2018-03-25.
 */

public final class Restaurant {
    public static List<Restaurant> getRestaurantList (String chungsaCode ) throws IOException {
        Pattern chungsaPattern = Pattern.compile("[\t ]*<option value=\"([0-9]+)\" label=\"(.*)\"[ ]*>(.*)</option>");
        URL url = new URL("http://www.chungsa.go.kr/chungsa/frt/popup/a01/foodMenu.do?searchCode1=GBD&selGbdVal=" + chungsaCode);

        List<Restaurant> retList = new ArrayList<Restaurant>();

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String str;
        while ((str = in.readLine()) != null) {
            Matcher matcher = chungsaPattern.matcher(str);
            if (!matcher.matches())
                continue;
            retList.add(new Restaurant(matcher.group(3), Integer.parseInt(matcher.group(1)), chungsaCode));
        }
        in.close();

        return retList;
    }

    public String getName () { return name; }
    public int getCode () { return code; }
    public String getChungsaCode () { return chungsaCode; }
    public String getMenuURL () throws IOException {
        Pattern chungsaPattern = Pattern.compile("[\t ]*<img src='(\\/chungsa\\/cmm\\/fms\\/getImage.do[a-zA-Z0-9_&=?.;]+)'[ ]+width=\"([0-9]+)px\"[ ]+height=\"([0-9]+)px\"[ ]+alt=\"(.*)\"[ ]+\\/>");
        URL url = new URL("http://www.chungsa.go.kr/chungsa/frt/popup/a01/foodMenu.do?searchCode1=REST&selGbdVal=" + chungsaCode + "&selRestVal=" + code);

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String str;
        while ((str = in.readLine()) != null)
        {
            Matcher matcher = chungsaPattern.matcher(str);
            if ( !matcher.matches () )
                continue;
            return "http://www.chungsa.go.kr" + matcher.group(1);
        }
        in.close();

        return null;
    }

    public Restaurant ( String name, int code, String chungsaCode ) {
        this.name = name.trim ();
        this.code = code;
        this.chungsaCode = chungsaCode.trim ();
    }

    private String name;
    private int code;
    private String chungsaCode;
}
