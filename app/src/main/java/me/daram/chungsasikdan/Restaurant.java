package me.daram.chungsasikdan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daram on 2018-03-25.
 */

public final class Restaurant {
    public static List<Restaurant> getRestaurantList (Context context, String chungsaCode) throws IOException {
        Pattern chungsaPattern = Pattern.compile("[\t ]*<option value=\"([0-9]+)\" label=\"(.*)\"[ ]*>(.*)</option>");
        
        List<Restaurant> retList = new ArrayList<>();

        BufferedReader in = new BufferedReader (PageGetter.getPageToInputStreamReader (context, String.format ( "http://www.chungsa.go.kr/chungsa/frt/popup/a01/foodMenu.do?searchCode1=GBD&selGbdVal=%s", chungsaCode )));
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
    public String getMenuURL (Context context) throws IOException {
        if (urlCache != null)
            return urlCache;
        else {
            Pattern chungsaPattern = Pattern.compile("[\t ]*<img src='(\\/chungsa\\/cmm\\/fms\\/getImage.do[a-zA-Z0-9_&=?.;]+)'[ ]+width=\"([0-9]+)px\"[ ]+height=\"([0-9]+)px\"[ ]+alt=\"(.*)\"[ ]+\\/>");
            
            BufferedReader in = new BufferedReader(PageGetter.getPageToInputStreamReader (context, String.format ( "http://www.chungsa.go.kr/chungsa/frt/popup/a01/foodMenu.do?searchCode1=REST&selGbdVal=%s&selRestVal=%s", chungsaCode, code )));
            String str;
            while ((str = in.readLine()) != null) {
                Matcher matcher = chungsaPattern.matcher(str);
                if (!matcher.matches())
                    continue;
	            in.close();
                return urlCache = String.format("http://www.chungsa.go.kr%s", matcher.group(1));
            }
            in.close();

            return null;
        }
    }
    public Drawable getMenuImage (Context context) throws IOException {
    	if ( urlBitmap != null )
    		return urlBitmap;
        String menuURL = getMenuURL (context);
	    InputStream page = PageGetter.getPageToInputStream ( context, menuURL );
	    urlBitmap = new BitmapDrawable ( page );
	    page.close ();
	    return urlBitmap;
    }

    public Restaurant ( String name, int code, String chungsaCode ) {
        this.name = name.trim ();
        this.code = code;
        this.chungsaCode = chungsaCode.trim ();
        this.urlCache = null;
    }

    private String name;
    private int code;
    private String chungsaCode;
    private String urlCache;
    private Drawable urlBitmap;
}
