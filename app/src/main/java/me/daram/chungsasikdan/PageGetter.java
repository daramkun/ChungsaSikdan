package me.daram.chungsasikdan;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by daram on 2018-03-26.
 */

public final class PageGetter {
    private static final Date today = new Date ();
    private static final Calendar calendar = Calendar.getInstance ();
    
    public static InputStream getPageToInputStream (Context context, String url ) throws IOException {
        File cacheDir = context.getCacheDir();
        File cacheFile = new File(String.format ( "%s/%s", cacheDir.getPath(), MD5(url)));
    
        try {
            if (cacheFile.exists()) {
                Date date = new Date(cacheFile.lastModified());
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
            
                if (date.after(today)) {
                    Log.i("청사식단 로그", "페이지 캐시 가져옴: " + url);
                    return new FileInputStream(cacheFile);
                }
            }
        
            InputStream inputStream = new URL(url).openStream();
            OutputStream outputStream = new FileOutputStream(cacheFile);
        
            byte[] buffer = new byte[4096];
            while (true) {
                int read = inputStream.read(buffer, 0, 4096);
                if (read == -1)
                    break;
                outputStream.write(buffer, 0, read);
            }
        
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        
            Log.i("청사식단 로그", "페이지 캐시함: " + url);
            return new FileInputStream(cacheFile);
        } catch ( IOException ex ) {
            cacheFile.delete ();
            throw ex;
        }
    }

    public static InputStreamReader getPageToInputStreamReader (Context context, String url) throws IOException {
        return new InputStreamReader( getPageToInputStream (context, url));
    }

    public static void deletePageCache (Context context, String url) {
        File cacheDir = context.getCacheDir ();
        File cacheFile = new File (cacheDir.getPath () + "/" + MD5 (url));
        if ( cacheFile.exists ())
            cacheFile.delete();
    }

    private static String MD5 (String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte b : byteData) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void clearCaches (Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static boolean deleteDir (File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return (dir != null && dir.isFile() && dir.delete());
    }
}
