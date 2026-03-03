package io.shulie.takin.web.app.leqi;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.*;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;

public final class SM4UtilCopy {
    private static final Charset ENCODING;
    public static final String ALGORITHM_NAME = "SM4";
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    public static final String privateKey = "1079832465abcdef1079832465fedcba";
    public static final String iv = "1079832465fedcba";

    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        /* 73*/         Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        /* 75*/         cipher.init(mode, sm4Key);
        /* 76*/         return cipher;
    }

    public static byte[] encryptEcbPadding(byte[] key, byte[] data) throws Exception {
        /*109*/         Cipher cipher = SM4UtilCopy.generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 1, key);
        /*110*/         return cipher.doFinal(data);
    }

    public static byte[] decryptEcbPadding(byte[] key, byte[] cipherText) throws Exception {
        /*144*/         Cipher cipher = SM4UtilCopy.generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 2, key);
        /*145*/         return cipher.doFinal(cipherText);
    }

    public static String decryptEcb(String hexKey, String cipherText) throws Exception {
        /*125*/         byte[] keyData = ByteUtils.fromHexString((String)hexKey);
        /*127*/         byte[] cipherData = Base64.getDecoder().decode(cipherText);
        /*129*/         byte[] srcData = SM4UtilCopy.decryptEcbPadding(keyData, cipherData);
        String decryptStr = new String(srcData, ENCODING);
        /*132*/         return decryptStr;
    }

    public static String decryptSM4(String key, String iv, String encryptedData) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        /*151*/         byte[] keyBytes = Hex.decode((String)key);
        /*152*/         byte[] ivBytes = Hex.decode((String)iv);
        /*153*/         byte[] encryptedBytes = Hex.decode((String)encryptedData);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM_NAME);
        /*156*/         Cipher cipher = Cipher.getInstance(ALGORITHM_NAME_ECB_PADDING, "BC");
        /*157*/         cipher.init(2, secretKeySpec);
        /*159*/         byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static String encryptEcb(String hexKey, String paramStr) throws Exception {
        /* 90*/         byte[] keyData = ByteUtils.fromHexString((String)hexKey);
        /* 92*/         byte[] srcData = paramStr.getBytes(ENCODING);
        /* 94*/         byte[] cipherArray = SM4UtilCopy.encryptEcbPadding(keyData, srcData);
        /* 96*/         String cipherText = Base64.getEncoder().encodeToString(cipherArray);
        /* 97*/         return cipherText;
    }

    private SM4UtilCopy() {
    }

    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
        /* 41*/         ENCODING = StandardCharsets.UTF_8;
    }

    public static void main(String[] args) {
        String key = "036a6a04e748b702dbbeb6e4b91e5ff5";
//        String content0 = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRUr4K6HJMsqHklB1/367WS5Es/9i5/VPQfMtmCxGqs97nXgD+5px6HA1l+6Vitmm2Huhd44h5Gy7WzJJdm96wy0y/+U2r9tSI4XnUdRBTtTpxdXE7mrm+m4Iqbko8EdrUb5/19MS56TGU1lvFvJTK8H32gdm0JsZQISkYhWAhn8tZn+4eGV98dcwqhSahG1Sxpa/nWZixinWE8wDdEC8bCrogm69T/860aFiWzPjXb/7Y6zw/9sWy4arCW2VqLJldNv4cndkcGeIU9lhN5x4+moitI4un7gXCaZ01CSB7/vqovc9fQ27Wa1XesseYSngTtTohGTnShRs/o01B2/qLWHjIR3gaOlD8V7K2SKJ3/OblUs3r1qkCzffiNSXt33eqZDoNeZYbsAV6XIo/qTti3e6BOGRKSUfV0v65wIUngJizQ3E7sK/8m3unF/HwWyNJeGD1zoPDCNEf/y1/wA1t6j3Now20xpfYldGxnF5fBf9fP4T7S+a9fn349NYA044ERwCykd0rD/HLECgularJr5H5QLcy9+8ppdGl4GiKZLFcWGO0O2xkzHEJaC2Za0kskppmUVRXEZOqB6LH+nt4ybGo2BSnASkEvjh+x2BC+WLyAXx1mRNHHHrKysoqgUMAc1bt2i3krqj+qazXb96aVsa/FsCGf9WilLFL8yJuT2XzR0GOkhosX5/e4PBcuYPx+UHZmFcZb/3/ydn17s6tldgz7Pjxtu7qoWhxLyksGXm8h8cgt9Ap874+XvzSzoaMmqBXRQLAlEVU+a4eYg7oCfeCcBnC8LTAsG6liZQ+tBCC9Pe9UaTxRwJpqOdxfn+3fT2PSW07xYG6aYfvjY1dBmdkRZ0sO8WoK61ULrwUQPVEZgSCT6uBGkjhAqKBQ9+fehfZZsxfBbEU2Bnk2MEmNjiIuAzsmve11iHf0/4U78SnL7/w9LKz9cH6uUUfkzejPuiI3MC/5FQjSDBK/ivAns3O6/Iex6Amsy4EYzhW+7L6j00rbJREqO2x4oLYiBaCyXksKa/GB314nJmJYsrbuWFNdQFrbCCnSfny2I5I3lWUZN6xdNdtd82lmI90soPnJ4T87gLzrQm9gtlsZ65LqyqzvyRYpR6mFbVfhtRSnkJ8CGeKQQP3R6m3OLbVtnZr3/BMRHQ+e/lD+NK/ePw+Lfqy+9rdj4miEtLb/VNexaulLPTLxPSaCfL+DuytrJzix19v4+t4x7MKbBvUdq0yhdOeHmyL2KS/vp9F6T4Noh5yybWoCOlGTurf6yCBJyoR1fWN1q0vD0yA4zVfCPTEtwvonJQHBaGMQgfDUlFTShsaB9xmkr+gkd5/tSJqCRfY/DXlbuHyCdDI0Pamdp2rVPBk92W0TepTOnBbJZJP4aTuKhHqsBq/wAJWSfM+rZGwo=";
//        String content1 = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRUpfrPmMYEHE6alipiQBOlMlUd1xsJA8S7Q0as2Rzw04/bv3CaiXT5IovfFDaN4YR8GVw0M+014f8sTIarS8RY4";
//        String content2 = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRWjwyqpffIo01moYZi208/CSDth3RCBAQLye/YBPSrQPeskW4JjUuiSw5PZUwdjn6lrhj3ot+pXWkgrCAO93x3F1JRnblXh0+OWg3xJfFUiasHtn17yYXsAG+dZoGsqElOADz8SHOW6YbiJ6Aq7rO5uN7z5jxaxQtAUjD+3OmoG90FLUdstn8JLT1m190ZTkR4CHrhrjTNsaDNJDUW6Bw1b+mnbYituRJIKPDF+7paU3OVagrODTEXQxXOjpaYihkdyhKQp4Go1H00NoUAeWOSV6jptDpAlj1zCP36rXt+RuZhfAglUukuB+Brim5czqLL6zE5pHI96XylaAJSdc3byKqroQkKDu8Gs/UxyeDmBlh//oArIHvXE0bqlGvWIQFDqaGDN8sDl2Cq1eqpB8ehXX4mZ97rcQNkhA15PDSdA3MJonDGoRUQllG/27V9Yb8lbc+G4Fm3TC3pRnfARbBYWFNSd6cKKpveT2lyikZ88ENOcNVEDlj27ajJky+pLjMOWdGOALYecPnr1KAl+zeW3sozGxlIrr30KirVQ314LqcyZ9uqTBvBSnvdV2uqMLmaws1P1Wv7bVJMyVmMzaE7poNmeK7sgQovFtYhouOUAR3z4e02ONoik10r2yoqYKtQnashyrltt2TE9NRjobSPCaBU9Bgnt/t37xKQadU+NIiJ+qo9lw0Fh8mJEaelK5rkMF8OjZ7FVDFOcB2S33A170TVJ7DVxnHYboLOJxOwCoV510zRQjAQu6XzVqtASCz11SMO3yfcvxnDxp5hRqdycoeF4p/mxxtXMJ9atbeN887JQeRh1Y7fCb7QNtCsEaEu8xK9eJ5LTXYNsDPVqLJVcCzFKno0BKXscW4uu2+kWMRO5bEQyeRr7j5Tzi5pLV+buxq0S/Eq5QmJl2ZOj3Ql86YDkdqFb0iwznLDqhh6fZVPVF5KFoHbB418TqzYElqpTHN1RM/QKwuFKC9JaabTrdaGYyYtiyzf1nzd7afr0N+bOKuwt2pb11CvOKxnYgIoDIf4SdSdxx+RxEPUlyrbTv3I2uQXCDT/jZH4Knk0+ISuwg6SxM/ewcyVXCt/i6oe8J5OpHInVsg5LWP3g3qpDvmae2h5Tb5aJoBYdSg16QufqElOruTF+UJcc7K1VFxoJEdh+aMjvCFk3wLClzEsG8+lg6G6fZuS3JGhfCJ33ZBjVF+rQsIU1hclv4JjB/4DYP8u86lxoS404irrTkYV96sKonr7sigQnkATcJhdnyx/N92QxVuNVfYmpbNoxj6dncvESRkOzjwLRpdsJaAM2rxKQNHeJ7fLfQagHFzumjue0C0vHBeyjoA1C+v34X4Nk5H6ZuhObHVnoCjWRKW3sM93pOUzl4NiCToobtVCOrQH4wSGcSOSCmZgf1/inZs8OEzWPxs5mxcEWtbRoaIeIIPqRivDtD5UJwfncK4yBb/NGBUXTvQM+XkopI4z2eEWgX7crihbS1AqPBSlEmFbtN631MaR0K5hdaHBN7LkN0EpXrWYcqXpNUK/YgdYfLJgjPfCFlNLkEKcbzBGOfBYc6yRbgmNS6JLDk9lTB2OfqWuGPei36ldaSCsIA73fHcU/YqSNYiLE6CKATEbNVd2Bwe2fXvJhewAb51mgayoSU4APPxIc5bphuInoCrus7m43vPmPFrFC0BSMP7c6agb3QUtR2y2fwktPWbX3RlORHj+6qR7D3iVjkB2yla9lbor6adtiK25Ekgo8MX7ulpTc5VqCs4NMRdDFc6OlpiKGR3KEpCngajUfTQ2hQB5Y5JUNu16kHoAwSCskRkxzlWnEmF8CCVS6S4H4GuKblzOosvrMTmkcj3pfKVoAlJ1zdvIqquhCQoO7waz9THJ4OYGWCys3Ln6S3Sreh7HgQRiwhVL969JgXkNFupHSo6XF5LaJVrMAH3IhmSTTwOAJAU/6";
//        String content3 = "UEsDBBQACAgIADyRklgAAAAAAAAAAAAAAAAGAAAAMS5qc29uXVPZTttAFP2VyM8FzWI7dp76DTy1VH1J02CZpEoZVEwQUkAUlbKEiAipJKSBikIXAkgsJgT6Mx6P8xedO OohZHy4Jlz7z3LzYIRsCLLe0bOcLFJkDxZbNq2jTE2JyeMZ0ZQfMdmSkGhLCFYfk Vi/NeFQqi8JS37vldP k9DHeuk4ONDLaJS20na2Ji246j4eU3EiyR4r7BLy6isC3aS6J5xT 3 MffvB7GNyt8cy96aMGlupF1 Srz38JIyWgcQafC9Aume0V33/igxusNcXQi2l95rzNs1nizF /e8u1NcXyu545k0SyiCGHbxMii1HopX9k8K VZqkg6UKgWABvvH4n92nDpKPnTFnsrvH0uWzrxfsNxeP0mg7MkS WPUOBTLc68lzUEEXMM0THsZjDJEZqjJgzQ3MctGDBdeYQkVga5OYvmsKOEgjuHHV4biJMtsOB0Owp3RPNEC5ycGNa6z6EWUWJBN78UpMzLwexIRLGiEkIK6DgWxhRoTM8E5QwMGHSjcG3konZG25icrfJPv6Q8aOJJuHwRW tRv6mZy8/k6ioK76PwVs2pzirKSDunAnliP/ xynvXOoSRs6ff44s1Hi7Hlz/lEvCN/nC5J44bwy Hyc1Zai 1XbU6rq17P9qzqN PzxvpnimgZVOCESYU4D7zfGU5mO7CxePtYaUPQHocvPKYTH kwZsLpJ2vFoypqQCmJccrcXfAO vaklK6KORpqH6pMAfG1mHbtAsHt6LV0xbIHJNL8Kvg/1dTDtQIrPik9P5RtfATnqySV/86CQNdMlqUHmPxNUThwbPK23Eg7llWUbJAsD8Pj45JUVYeTLBDEbGRiV1j8S9QSwcIYfD82oECAAD2AwAAUEsBAhQAFAAICAgAPJGSWGHw/NqBAgAA9gMAAAYAAAAAAAAAAAAAAAAAAAAAADEuanNvblBLBQYAAAAAAQABADQAAAC1AgAAAAA=";
//        try {
//            System.out.println("0:" + decryptEcb(key, content0));
//        } catch (Exception e) {
//            System.out.println("0:ERROR");
//        }
//        try {
//            System.out.println("1:" + decryptEcb(key, content1));
//        } catch (Exception e) {
//            System.out.println("1:ERROR");
//        }
//        try {
//            System.out.println("2:" + decryptEcb(key, content2));
//        } catch (Exception e) {
//            System.out.println("2:ERROR");
//        }
//        try {
//            System.out.println("3:" + decryptEcb(key, content3));
//        } catch (Exception e) {
//            System.out.println("3:ERROR");
//        }
        try {
            String request = "UDVVOXIyUmlTZHRHaVVSdnhGOExDemZoYXFwU3VEMDhQVm5rSGVRMy9IcVcwbjhqalZueldnTkJiaHpDL3VnY1pqd01Cd3h3SGQzYVE1Qkp0ckZUN1pCb2VRUVNLeVhnajQ4MHZsVSt0NXlGV1Z5ZnlJYmpQeDhiK1ZxSTFNdlhXdnNXNjRMUWNXZzNXT1RXeXlqcDJCK1lrMmVKM1lBbDhJdTFkbnVOQ0N1YXhtZ3o5Vlo4N0pzUk1FQXIvQXdDNXZoWHBCU0QrZUt2UWFuQ2tVcC9KZVRzdHhqTlRlZFQrSFdieUFiQzlWR000eXljeUxJZTBxa3RTQm5PNUVkb0JKWXpkeFZZN3IxUmhyR0tLVUszQU50cEFNUUNTQ0xqOHBrQWZTWHptQmJYVUc4amFXOVVpOEJUUGJwY05kVnZBV1ZpNTBoOFJUTk8xVXZSY05KcGZqWWNvWnNnRUs3NnlWVHpkN080dlpvcm1kZjFOL3kzNnM2R2FLRVQxcml5NndhdjdDWVBvRmFrVlV2T254bjNWQUVGaTg1QXZKNkhRbDBFSm1lTUlsUVNPbitOaTFCV29vZWpoVHpZM09zbHNhZnM4NVEveHQrSHh4NzFEWEo2aHFyTE5CaWI2TGt2YkZUU01oYTJsVUk1MDVMT0hXZFJOY2o4Wk9SNDExZ0JUWTJxY00zU0NkL2pPN3BTN1pkZ1FOMkdocnc5aTJtZkFHNEdka0JZVFN0emtiU3ozNFVqaWV1U2hiL2lsdFBWbDJqVzAvN0dtSXpOeGNyMWRjbzNCTEc1SkRwR2p1M3Q1cTBETVluSXlWZUw3ZjFoRVlrdVZoZ2laTFpTTnZzd21wWDlsOXZmbnk2RklkSlVYOEtKSG9Bblg0MG5yM2ZGaFo4VG40WUJhWHJMOVJuUWhyZ2xWVUlva05ic2pTMmpLNVliZDQvQytBNWxoN1E0RnpHbWR4ZWExNEQ4d09MS0xtNTBKUlZhTkc0blF0VXFyTDRJY3luVmNmTFFoeEtNSklPV08rV1lNbGRVcVNxSHFNaTNCQUhjL1YzekxSMFJMTmZtamNGaXNHWkk0Z2VZYzdKSmkrRnpRdjVrNzNiRVpyNGtVd3NDTnh5VTNlOUFWKzFaQWZscnRjSTY0cjZheXBxOWM4bW5LVGNTL2hvTlBMZjNBT3dEMEhyZzZmbzQ0RnJsMXkxeG8vRWU2SkxHVXRRS3c0aDVvZDZjM250M2F5bU5odWQ4c25wTHdYcXFEWllGYjB0alc2cEllY0YzUStOT216S3lkTXJ3WlRaVng1UVBpUzQ3dGtWMWh6MWJIbEdUQ0FhTkszUjFMVDJrc3VrcisrR1lNR01NaWJWRXZ5K1RkQXVGd0VFOXpyVjZmMlg2MzR0aUo4VnlIdnVXeHRIMGJ5VGlXVUxUdDc4WkJQbVZhcjFYbTdvUjkyVDdIYWNLRmJucThPck9wZ3JpaU5NclJjbFpBU2Izd1NDclRoYjFja3VxdnAySWdHMDJ4NGx4Ym4zQ3F0NUNzMytlS0dZWHNabkIvOTFHZHp5QXMyQVZndTJqbmo0bE4vUWhYK3lUUy9oQXdsNis5MkdDTlExOWhEempjN0NRQVIyamtwSE0zZ1E4dE1FdEVOUzduaGZmQzJGSjFFUVFZdFYvalBvdzJaZFNYS2lBODVoOVJYU2JvMjNhSWNiYXNmNFU2b2xUSkhYWFd0UGFoMll0K3FGa1JLK3lvaDBJY3J4VEpBWUkrakM0bEw3NmVFRzJKZWYrODdLUXpOc1ZNaDVLeno4eEZYU1NrT3kwbVlGRmZFck41YmYrUHhyVVlrdllsUHFIc25EWFlicWUvRzQvbkF4R1ludFNZbnBXT3BmblhoYWxodHNNZW1uY1VFU2dRUHFYamhCVWVjSmZYaUlzemd3WjhNdzg1Z1UzaC8rdFZLTkRoRlZicWI1elA2b3JMTG5zb08va0lzeTdWbHMrVnFVVWw5dkdaaWVNRXBqM0Vid0FUQTU4QnJXdjNnQzN2bFVuamtvRTBUQkcyNmJSckVnaitrcW1zT0RSVjBDS1doZkZNb1ROZ0VVSjBDT2hJM0VFRVJsOCtTc1hLVTlscmJ4SXJRTmZhUE5MYytoa0NhYVEyMDdkV1lubVJlVE5XaW9QY2MxREtWcmwyRER2ZFh3dW1ZV05YUk9rTzJ2aW5UY0xuQ3h1R1Z1aWF3azBHMncyeTI0YUw3SWxMZ0dRdElnSGNjWE1zUnpWYmJhQUVQQWdEZ2dSdnhPZnhBK0pybjVvT3JYRFNocWZUbjNWNGJLT1VRQy91SGovU1J6N2tTYmFZaW5rRjRDZFdJSVpFcnhrMmROZFY1STdvRWk4Sld1S09qNEJZbkx5djdQOXV5NWxlQnAzL3JEOXdRbzNvL3BuSGErK205MnFUdVJnd0M0M056b0pEeVVSNXh0bGZmWThlYk9Wc3NXWjdHNkg=";
            System.out.println("QDFPSC request:" + decryptEcb(key, request));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/202007/QDFPSC: request ERROR");
        }
        try {
            /**
             * {"returnmsg":"成功","returncode":"00","sllsh":"f0f4d6beb42b46d68960dc0dfd5adf83"}
             */
            String response = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRUpfrPmMYEHE6alipiQBOlME0E1GJvE38pMj6wbYDNz35VeNSee6QzwjDqvX5J9cA7OT5gO3Wm1UqdIwMZ4R84z";
            System.out.println("QDFPSC response:" + decryptEcb(key, response));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/202007/QDFPSC: response ERROR");
        }

        try {
            String request = "MGQ4UmxLdW9GNGNROXZlSmhSdlRZTVo5OVFQK0UzWlBWK3dSY1J2ajRDRXJFNEZnRFlBRU9HS2RHTlNBbUU2bw==";
            System.out.println("CXQDFPSCJG request:" + decryptEcb(key, request));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/202007/CXQDFPSCJG: request ERROR");
        }

        try {
            /**
             * {"returnmsg":"成功","returncode":"00","resultList":[{"status":"00","message":"上传成功","fphm":"24634804680000073894"}]}
             */
            String response = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRWjwyqpffIo01moYZi208/Cy+rjHwMc8fVZclWc47o4NQI2UhbfmN3FiXqUljFOa+zlWoKzg0xF0MVzo6WmIoZHiHtbSdkXqbEmTDSnT5bjjj0/rq45xD3UhdLmqoiPOpc=";
            System.out.println("CXQDFPSCJG response:" + decryptEcb(key, response));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/202007/CXQDFPSCJG: response ERROR");
        }

        try {
            String request = "RTYwbGQ2TGlOSXc4TklNRk50akhPbS83SEpUSkNQRytRVC9ZNzUwNnQ0U2hSVVV4cVQwbEVKNmRFNjVPRWVuWGNlcXp3TXpUb05NbDdxUUtpY2NYUWVvTFFRamFudTcvb1htcmlsVitjSkdPLzZNS3VaN3MyNk5Hb3oweVdrZWxCMmhvU2xsZVhTWk9oNDRqRFVGNEVRPT0=";
            System.out.println("FPCY_NEW request:" + decryptEcb(key, request));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/203059/FPCY_NEW: request ERROR");
        }

        try {
            /**
             * 1.FPCY_NEW response:{"returnmsg":"成功","returncode":"00","cyjgxx":"\nUEsDBBQACAgIADyRklgAAAAAAAAAAAAAAAAGAAAAMS5qc29uXVPZTttAFP2VyM8FzWI7dp76DTy1VH1J02CZpEoZVEwQUkAUlbKEiAipJKSBikIXAkgsJgT6Mx6P8xedO OohZHy4Jlz7z3LzYIRsCLLe0bOcLFJkDxZbNq2jTE2JyeMZ0ZQfMdmSkGhLCFYfk Vi/NeFQqi8JS37vldP k9DHeuk4ONDLaJS20na2Ji246j4eU3EiyR4r7BLy6isC3aS6J5xT 3 MffvB7GNyt8cy96aMGlupF1 Srz38JIyWgcQafC9Aume0V33/igxusNcXQi2l95rzNs1nizF /e8u1NcXyu545k0SyiCGHbxMii1HopX9k8K VZqkg6UKgWABvvH4n92nDpKPnTFnsrvH0uWzrxfsNxeP0mg7MkS WPUOBTLc68lzUEEXMM0THsZjDJEZqjJgzQ3MctGDBdeYQkVga5OYvmsKOEgjuHHV4biJMtsOB0Owp3RPNEC5ycGNa6z6EWUWJBN78UpMzLwexIRLGiEkIK6DgWxhRoTM8E5QwMGHSjcG3konZG25icrfJPv6Q8aOJJuHwRW tRv6mZy8/k6ioK76PwVs2pzirKSDunAnliP/ xynvXOoSRs6ff44s1Hi7Hlz/lEvCN/nC5J44bwy Hyc1Zai 1XbU6rq17P9qzqN PzxvpnimgZVOCESYU4D7zfGU5mO7CxePtYaUPQHocvPKYTH kwZsLpJ2vFoypqQCmJccrcXfAO vaklK6KORpqH6pMAfG1mHbtAsHt6LV0xbIHJNL8Kvg/1dTDtQIrPik9P5RtfATnqySV/86CQNdMlqUHmPxNUThwbPK23Eg7llWUbJAsD8Pj45JUVYeTLBDEbGRiV1j8S9QSwcIYfD82oECAAD2AwAAUEsBAhQAFAAICAgAPJGSWGHw/NqBAgAA9gMAAAYAAAAAAAAAAAAAAAAAAAAAADEuanNvblBLBQYAAAAAAQABADQAAAC1AgAAAAA="}
             * 2.
             */
            String response = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRUr4K6HJMsqHklB1/367WS5Es/9i5/VPQfMtmCxGqs97nXgD+5px6HA1l+6Vitmm2Huhd44h5Gy7WzJJdm96wy0y/+U2r9tSI4XnUdRBTtTpxdXE7mrm+m4Iqbko8EdrUb5/19MS56TGU1lvFvJTK8H32gdm0JsZQISkYhWAhn8tZn+4eGV98dcwqhSahG1Sxpa/nWZixinWE8wDdEC8bCrogm69T/860aFiWzPjXb/7Y6zw/9sWy4arCW2VqLJldNv4cndkcGeIU9lhN5x4+moitI4un7gXCaZ01CSB7/vqovc9fQ27Wa1XesseYSngTtTohGTnShRs/o01B2/qLWHjIR3gaOlD8V7K2SKJ3/OblUs3r1qkCzffiNSXt33eqZDoNeZYbsAV6XIo/qTti3e6BOGRKSUfV0v65wIUngJizQ3E7sK/8m3unF/HwWyNJeGD1zoPDCNEf/y1/wA1t6j3Now20xpfYldGxnF5fBf9fP4T7S+a9fn349NYA044ERwCykd0rD/HLECgularJr5H5QLcy9+8ppdGl4GiKZLFcWGO0O2xkzHEJaC2Za0kskppmUVRXEZOqB6LH+nt4ybGo2BSnASkEvjh+x2BC+WLyAXx1mRNHHHrKysoqgUMAc1bt2i3krqj+qazXb96aVsa/FsCGf9WilLFL8yJuT2XzR0GOkhosX5/e4PBcuYPx+UHZmFcZb/3/ydn17s6tldgz7Pjxtu7qoWhxLyksGXm8h8cgt9Ap874+XvzSzoaMmqBXRQLAlEVU+a4eYg7oCfeCcBnC8LTAsG6liZQ+tBCC9Pe9UaTxRwJpqOdxfn+3fT2PSW07xYG6aYfvjY1dBmdkRZ0sO8WoK61ULrwUQPVEZgSCT6uBGkjhAqKBQ9+fehfZZsxfBbEU2Bnk2MEmNjiIuAzsmve11iHf0/4U78SnL7/w9LKz9cH6uUUfkzejPuiI3MC/5FQjSDBK/ivAns3O6/Iex6Amsy4EYzhW+7L6j00rbJREqO2x4oLYiBaCyXksKa/GB314nJmJYsrbuWFNdQFrbCCnSfny2I5I3lWUZN6xdNdtd82lmI90soPnJ4T87gLzrQm9gtlsZ65LqyqzvyRYpR6mFbVfhtRSnkJ8CGeKQQP3R6m3OLbVtnZr3/BMRHQ+e/lD+NK/ePw+Lfqy+9rdj4miEtLb/VNexaulLPTLxPSaCfL+DuytrJzix19v4+t4x7MKbBvUdq0yhdOeHmyL2KS/vp9F6T4Noh5yybWoCOlGTurf6yCBJyoR1fWN1q0vD0yA4zVfCPTEtwvonJQHBaGMQgfDUlFTShsaB9xmkr+gkd5/tSJqCRfY/DXlbuHyCdDI0Pamdp2rVPBk92W0TepTOnBbJZJP4aTuKhHqsBq/wAJWSfM+rZGwo=";
            System.out.println("FPCY_NEW response:" + decryptEcb(key, response));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/203059/FPCY_NEW: response ERROR");
        }


        try {
            String request = "Z2c5THRxdDdoSi9qWjhrVkoya0xobHA5VkM0akZaUjZCOHFTRGFsWUpMST0=";
            System.out.println("CXSXED request:" + decryptEcb(key, TdcBase64Util.decode(request,TdcBase64Util.ENCODING)));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/202007/CXSXED: request ERROR");
        }

        try {
            String response = "sdquU+WPb8meym6gKiboR+LKVTiC7UvDDyGy32lAGaYSnZhaU7niUYjXZ1ttLQlMj9i4SxhPwzZieKoTVRbDoh4hP07i5wpLRJl/ZGMfT2FHWYEr58hJDt3emfl0WjM4JjLW4aGl8W9rmWtr3VudbqzQvk5EDF9R7lKrRbozKZjN6seRUCSAwytVOWa1Vx9ZNQvsPdMz7EqwIpyq1+bzJg==";
            System.out.println("CXSXED response:" + decryptEcb(key, response));
        } catch (Exception e) {
            System.out.println("/access/newsandbox/v2/invoke/202007/CXSXED: response ERROR");
        }

    }
}