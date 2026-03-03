package io.shulie.takin.web.app.leqi;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;

public class TdcBase64Util {
    public static final String ENCODING = "UTF-8";

    private static Boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static byte[] base64ToByteArry(String base64Str) {
        /* 28*/         if (isEmpty((String)base64Str)) {
            /* 29*/             return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            /* 33*/             return decoder.decodeBuffer(base64Str);
        }
        catch (Exception e) {
            /* 35*/             e.printStackTrace();
            /* 36*/             return null;
        }
    }

    public static byte[] decodeByte(String input) {
        /*112*/         byte[] ouput = null;
        try {
            /*114*/             ouput = Base64.decodeBase64(input.getBytes(ENCODING));
        }
        catch (UnsupportedEncodingException e) {
            /*116*/             e.printStackTrace();
        }
        /*118*/         return ouput;
    }

    public static String decode(String input) {
        /* 96*/         String ouput = "";
        try {
            /* 98*/             byte[] decodeBase64 = Base64.decodeBase64(input.getBytes("GBK"));
            ouput = new String(decodeBase64, "GBK");
        }
        catch (UnsupportedEncodingException e) {
            /*101*/             e.printStackTrace();
        }
        /*103*/         return ouput;
    }

    public static String decode(String input, String charSet) {
        /*128*/         String ouput = "";
        try {
            /*130*/             byte[] decodeBase64 = Base64.decodeBase64(input.getBytes(charSet));
            ouput = new String(decodeBase64, charSet);
        }
        catch (UnsupportedEncodingException e) {
            /*133*/             e.printStackTrace();
        }
        /*135*/         return ouput;
    }

    public static String encode(String input, String charSet) {
        try {
            input = new String(Base64.encodeBase64(input.getBytes(charSet)), charSet);
        }
        catch (UnsupportedEncodingException e) {
            /* 68*/             e.printStackTrace();
            /* 69*/             input = "";
        }
        /* 71*/         return input;
    }

    public static String encode(String input) {
        try {
            input = new String(Base64.encodeBase64(input.getBytes("GBK")), "GBK");
        }
        catch (UnsupportedEncodingException e) {
            /* 53*/             e.printStackTrace();
            /* 54*/             input = "";
        }
        /* 56*/         return input;
    }

    public static String encode(byte[] input) {
        /* 80*/         String ouput = "";
        try {
            ouput = new String(Base64.encodeBase64(input), ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            /* 84*/             e.printStackTrace();
            /* 85*/             ouput = "";
        }
        /* 87*/         return ouput;
    }
}
