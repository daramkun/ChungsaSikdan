package me.daram.chungsasikdan;

import android.content.Context;

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

public final class Chungsa {
    public static List<Chungsa> getChungsaList (Context context) throws IOException {
        Pattern chungsaPattern = Pattern.compile("[\t ]*<option value=\"(BD[0-9]+)\" label=\"(.*)\"[ ]*>(.*)</option>");
        //URL url = new URL("http://www.chungsa.go.kr/chungsa/frt/popup/a01/foodMenu.do");

        List<Chungsa> retList = new ArrayList<Chungsa>();

        BufferedReader in = new BufferedReader (PageGetter.getPage(context, "http://www.chungsa.go.kr/chungsa/frt/popup/a01/foodMenu.do"));//new BufferedReader(new InputStreamReader(url.openStream()));
        String str;
        while ((str = in.readLine()) != null) {
            Matcher matcher = chungsaPattern.matcher(str);
            if (!matcher.matches())
                continue;
            retList.add(new Chungsa(matcher.group(3), matcher.group(1)));
        }
        in.close();

        return retList;
    }

    public String getName () { return name; }
    public String getCode () { return code; }

    public Chungsa ( String name, String code ) {
        this.name = name.trim ();
        this.code = code.trim ();
    }

    private String name, code;
}
