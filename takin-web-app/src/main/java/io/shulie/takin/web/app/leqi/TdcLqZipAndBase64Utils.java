package io.shulie.takin.web.app.leqi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class TdcLqZipAndBase64Utils {
    private static String string;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String zipAndBase64Encode(String input, String fileName) {
        /* 44*/
        String ouput = null;
        /* 45*/
        ZipOutputStream zout = null;
        /* 46*/
        ByteArrayOutputStream out = null;
        /* 47*/
        byte[] compressed = null;
        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            /* 51*/
            zout.putNextEntry(new ZipEntry(fileName));
            /* 52*/
            zout.write(input.getBytes());
            /* 53*/
            zout.closeEntry();
            /* 54*/
            out.close();
            /* 55*/
            zout.close();
            /* 56*/
            compressed = out.toByteArray();
        } catch (Exception e) {
            try {
            } catch (Throwable throwable) {
                StreamUtil.closeStream((OutputStream) out);
                /* 61*/
                StreamUtil.closeStream(zout);
                throw throwable;
            }
            /* 60*/
            StreamUtil.closeStream((OutputStream) out);
            /* 61*/
            StreamUtil.closeStream((OutputStream) zout);
        }
        /* 60*/
        StreamUtil.closeStream((OutputStream) out);
        /* 61*/
        StreamUtil.closeStream((OutputStream) zout);
        /* 63*/
        if (compressed != null) {
            /* 64*/
            ouput = TdcBase64Util.encode(compressed);
        }
        /* 66*/
        return ouput;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String unzipAndBase64Decode(String input) {
        String ouput;
        block11:
        {
            /* 75*/
            byte[] bytes = TdcBase64Util.decodeByte((String) input);
            /* 76*/
            ouput = "";
            /* 77*/
            ByteArrayInputStream is = null;
            /* 78*/
            ZipInputStream zis = null;
            /* 79*/
            GZIPInputStream gis = null;
            /* 80*/
            ByteArrayOutputStream baos = null;
            /* 81*/
            InputStreamReader isr = null;
            try {
                /* 83*/
                if ("ZIP".equals(TdcLqZipAndBase64Utils.detectFormat(bytes))) {
                    is = new ByteArrayInputStream(bytes);
                    zis = new ZipInputStream(is);
                    ZipEntry ze = zis.getNextEntry();
                    if (ze != null && !ze.isDirectory()) {
                        int len;
                        baos = new ByteArrayOutputStream();
                        /* 90*/
                        byte[] buffer = new byte[1024];
                        /* 92*/
                        while ((len = zis.read(buffer)) > -1) {
                            /* 93*/
                            baos.write(buffer, 0, len);
                        }
                        /* 95*/
                        baos.flush();
                        /* 96*/
                        byte[] bytes1 = baos.toByteArray();
                        ouput = new String(bytes1, StandardCharsets.UTF_8);
                    }
                } else if ("GZIP".equals(TdcLqZipAndBase64Utils.detectFormat(bytes))) {
                    is = new ByteArrayInputStream(bytes);
                    gis = new GZIPInputStream(is);
                    isr = new InputStreamReader((InputStream) gis, StandardCharsets.UTF_8);
                    /*103*/
                    ouput = StreamUtil.getString((Reader) isr);
                } else {
                }
                /*110*/
                StreamUtil.closeStream((InputStream) is);
            } catch (Exception e) {
                break block11;
            } finally {
                /*110*/
                StreamUtil.closeStream(is);
                /*111*/
                StreamUtil.closeStream(zis);
                /*112*/
                StreamUtil.closeStream(gis);
                /*113*/
                StreamUtil.closeReader(isr);
                /*114*/
                StreamUtil.closeStream(baos);
            }
            /*111*/
            StreamUtil.closeStream((InputStream) zis);
            /*112*/
            StreamUtil.closeStream((InputStream) gis);
            /*113*/
            StreamUtil.closeReader((Reader) isr);
            /*114*/
            StreamUtil.closeStream((OutputStream) baos);
        }
        /*116*/
        return ouput;
    }

    public static String detectFormat(byte[] data) {
        /*120*/
        if (data == null || data.length < 2) {
            /*121*/
            return "Unknown";
        }
        /*124*/
        int byte1 = data[0] & 0xFF;
        /*125*/
        int byte2 = data[1] & 0xFF;
        /*127*/
        if (byte1 == 80 && byte2 == 75) {
            /*128*/
            return "ZIP";
        }
        /*129*/
        if (byte1 == 31 && byte2 == 139) {
            /*130*/
            return "GZIP";
        }
        /*132*/
        return "Unknown";
    }

    public static void main(String[] args) throws IOException {
        /*137*/
        String a = "H4sIAAAAAAAAAKWT7U4TQRSGb4XUP0roZj52dmb4qYk3QfiDFZramsoSKSUkhQiIQT5C8asFUhRSEwERpQUp3IvZ2W3vwjMzmxbRxBg32WTnnMx5z/Oes0PTidF8KpcYTDCMEJYu9lBiAGJpHUOYSSYQhsij/PgTiBBEaBKTJOF9SA5id5BRSE74+SIksTBXixMj5gSHzJQuwz0XE48z7iEPS4yIa0oW/FF/JA15iRnGHCGCQRARhu7ZbO4BJIPmgaq0op2T8MuZ+vAxenYafi6352rB91ZYXeq8XVPzn9RqU1UqqrHVvrxUje1OuaTKh+r5gk1BsbHcDSmXEcqZK9l9mzVSWqe1bitF1dmo/E29qMTlD9+Hx0tq5Sq4rOj4tdqZh3A1Ca1j5lDtgG8DFM6G088+1V46WFuV8dMZk8YuRQ6VEDJuqYuSmm9E57vq4LXa3Vali6i+EjQ3onJdra5He/XgqhbOHrVr9ejilVptGAxCKCYIUeIxPaJMthD7nvb9rCmrh+lPwcGPD7nChP6kRtc3vSPrdqqY0g5ZC1VzrlN6Bz6o5fOwutV5cwJzaDeOXMFAHAkskgQTQgSzl6fSRX1ZNfbatWV7MyxDt8t90KKePEYS2mWYY0Kt5bFez9lmNTirdTYXo7X94HwdxIwMI54gXNo7sYzdCRBTmwudjVOQsVWspFGErdWKLrygq5scfeyPZwtm141FkwVwa2gaHCmk45jZgv5w5yvsWj8C0L52ax8mYCO3f8zv3enOlzDhSK83fymxI834syYthUNh0fQDwZQeuud43COIm6mPjRlZdMsW14WyqUltyMvFm0vj50dM24gDFUYU3vhJzAx0CUiP4LilCSS5hmBCGuFuj0Fy10Gky8BdSRyXdhkwEgLyNyGE8IAOsR6EJLds/X+hwH+moL9RsL9QCOIg2oXw4LeSvMsgGJx+JeAOpdLVc7hGwP6XYHhm+Ccz4RriTQUAAA==";
        /*139*/
        String b = TdcLqZipAndBase64Utils.unzipAndBase64Decode(a);
        /*140*/
        String c = TdcLqZipAndBase64Utils.zipAndBase64Encode(b, "dzfp_22442000000002040261_20220923201742.xml");
        /*141*/
        System.out.println(b);
    }
}
