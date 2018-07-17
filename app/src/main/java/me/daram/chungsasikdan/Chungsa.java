package me.daram.chungsasikdan;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
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

        List<Chungsa> retList = new ArrayList<>();

        String url = "http://www.chungsa.go.kr/chungsa/frt/popup/a01/foodMenu.do";
        BufferedReader in = new BufferedReader (PageGetter.getPageToInputStreamReader (context, url));
        String str;
        while ((str = in.readLine()) != null) {
            Matcher matcher = chungsaPattern.matcher(str);
            if (!matcher.matches())
                continue;
            retList.add(new Chungsa(matcher.group(3), matcher.group(1)));
        }
        in.close();

        if (retList.size() == 0)
            PageGetter.deletePageCache(context, url);

        return retList;
    }

    public String getName () { return name; }
    public String getCode () { return code; }

    public Chungsa (String name, String code) {
        this.name = name.trim ();
        this.code = code.trim ();
    }

    private String name, code;
}
