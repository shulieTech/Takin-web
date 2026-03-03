package io.shulie.takin.web.app.leqi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class StreamUtil {
    private static final int BUFFERSIZE = 1024;

    public static void closeStream(OutputStream stream) {
        /* 54*/         if (stream != null) {
            try {
                /* 56*/                 stream.close();
            }
            catch (Exception e) {
                throw  new RuntimeException("error");
            }
        }
    }

    public static void closeStream(InputStream stream) {
        /* 39*/         if (stream != null) {
            try {
                /* 41*/                 stream.close();
            }
            catch (Exception e) {
                throw  new RuntimeException("error");
            }
        }
    }

    public static void closeWriter(Writer writer) {
        /* 69*/         if (writer != null) {
            try {
                /* 71*/                 writer.close();
            }
            catch (Exception e) {
                throw  new RuntimeException("error");
            }
        }
    }

    public static void closeReader(Reader reader) {
        /* 84*/         if (reader != null) {
            try {
                /* 86*/                 reader.close();
            }
            catch (Exception e) {
                throw  new RuntimeException("error");
            }
        }
    }

    public static String getString(Reader reader) throws IOException {
        StringBuffer buffer = new StringBuffer();
        /*108*/         int c = 0;
        /*109*/         char[] cs = new char[1024];
        /*110*/         while ((c = reader.read(cs)) > 0) {
            /*111*/             buffer.append(cs, 0, c);
        }
        /*113*/         return buffer.toString();
    }

    public static byte[] serialize(Object o) {
        /*161*/         ByteArrayOutputStream bos = null;
        /*162*/         ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            /*166*/             os.writeObject(o);
            /*167*/             os.close();
            /*168*/             bos.close();
            /*169*/             byte[] byArray = bos.toByteArray();
            /*169*/             return byArray;
        }
        catch (Exception e) {
            throw  new RuntimeException("error");
        }
        finally {
            if (bos != null) {
                try {
                    /*177*/                     bos.close();
                }
                catch (IOException e) {
                    throw  new RuntimeException("error");
                }
            }
            /*184*/             if (os != null) {
                try {
                    /*186*/                     os.close();
                }
                catch (IOException e) {
                    throw  new RuntimeException("error");
                }
            }
        }
    }

    public static Object deserialize(byte[] in) {
        /*204*/         if (in == null) {
            /*205*/             return null;
        }
        /*207*/         ByteArrayInputStream bis = null;
        /*208*/         ObjectInputStream is = null;
        try {
            Object rv;
            bis = new ByteArrayInputStream(in);
            is = new ObjectInputStream(bis);
            Object object = rv = is.readObject();
            /*213*/             return object;
        }
        catch (Exception e) {
            throw  new RuntimeException("error");
        }
        finally {
            if (bis != null) {
                try {
                    /*221*/                     bis.close();
                }
                catch (IOException e) {
                    throw  new RuntimeException("error");
                }
            }
            /*230*/             if (is != null) {
                try {
                    /*232*/                     is.close();
                }
                catch (IOException e) {
                    throw  new RuntimeException("error");
                }
            }
        }
    }

    private StreamUtil() {
    }

    public static byte[] getBytes(File file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file);){
            /*147*/             byte[] byArray = StreamUtil.getBytes(stream);
            /*147*/             return byArray;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        try {
            byte[] result;
            /*126*/             int c = 0;
            /*127*/             byte[] bs = new byte[1024];
            /*128*/             while ((c = stream.read(bs)) > 0) {
                /*129*/                 out.write(bs, 0, c);
            }
            byte[] byArray = result = out.toByteArray();
            /*132*/             return byArray;
        }
        finally {
            /*134*/             StreamUtil.closeStream(out);
        }
    }
}