package com.whydoito.common.utils.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.crypto.Cipher;     
import javax.crypto.spec.IvParameterSpec;     
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public abstract class AES128CBCEncryption {      

	public static final String KEY_ALGORITHM="AES";
	//"算法/模式/补码方式"
	public static final String CIPHER_ALGORITHM="AES/CBC/PKCS5Padding"; 
	//使用CBC模式，需要一个向量iv，可增加加密算法的强度
	public static final String ivParameter="1234567890123456";

    
	    public static String Encrypt(String sSrc, String sKey){
	
	        String enString="";
			try {
				String str = AesEncrypt(sSrc, sKey);
				enString= new BASE64Encoder().encodeBuffer(hex2byte(str));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        return enString;
	    }
	    
	    
	    // Aes加密 
	    public static String AesEncrypt(String sSrc, String sKey) throws Exception { 
	        if (sKey == null) { 
	            System.out.print("在加密中Key为空null"); 
	            return null; 
	        } 
	        // 判断Key是否为16位 
	        if (sKey.length() != 16) { 
	            System.out.print("在加密中Key长度不是16位"); 
	            return null; 
	        } 
	        String enCode=Charset.defaultCharset().name();
	        byte[] raw = sKey.getBytes(); 
	        System.out.println("key length="+raw.length);
	        System.out.println("key="+sKey+";;; enCode="+enCode);
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM); 
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM); 
	        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes()); 
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv); 
	        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8")); 
	          
//	        System.out.println("TEXT = " + new String(encrypted)); 
	        return byte2hex(encrypted); 

	    } 
	    
	    
	    //先执行base64解码
	    public static String Decrypt(String sSrc, String cKey){
	        String resultStr="";
			try {
				byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
		        resultStr = AesDecrypt(byte2hex(encrypted1), cKey); 
				resultStr=new String(resultStr.getBytes(),"UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        return resultStr;
	    }
	    
	    // AES解码
	    public static String AesDecrypt(String sSrc, String sKey) throws Exception { 
	        try { 
	            // 判断Key是否正确 
	            if (sKey == null) { 
	                System.out.println("在解密中Key为空null"); 
	                return null; 
	            } 
	            // 判断Key是否为16位 
	            if (sKey.length() != 16) { 
	                System.out.println("在解密中Key长度不是16位"); 
	                return null; 
	            } 
	            byte[] raw = sKey.getBytes("ASCII"); 
	            SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM); 
	            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM); 
	            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes()); 
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv); 
	            byte[] encrypted1 = hex2byte(sSrc); 
	            try { 
	                byte[] original = cipher.doFinal(encrypted1); 
	                String originalString = new String(original); 
	                return originalString; 
	            } catch (Exception e) { 
	                System.out.println(e.toString()); 
	                return null; 
	            } 
	        } catch (Exception ex) { 
	            System.out.println(ex.toString()); 
	            return null; 
	        } 
	    } 
	  
	    public static byte[] hex2byte(String strhex) { 
	        if (strhex == null) { 
	            return null; 
	        } 
	        int l = strhex.length(); 
	        if (l % 2 == 1) { 
	            return null; 
	        } 
	        byte[] b = new byte[l / 2]; 
	        for (int i = 0; i != l / 2; i++) { 
	            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 
	                    16); 
	        } 
	        return b; 
	    } 
	  
	    public static String byte2hex(byte[] b) { 
	        String hs = ""; 
	        String stmp = ""; 
	        for (int n = 0; n < b.length; n++) { 
	            stmp = (Integer.toHexString(b[n] & 0XFF));
	            if (stmp.length() == 1) { 
	                hs = hs + "0" + stmp; 
	            } else { 
	                hs = hs + stmp; 
	            } 
	        } 
	        return hs.toUpperCase(); 
	    } 
	  
	    
	    
	    public static void main(String[] args) throws Exception { 
	        /* 
	         * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定 
	         */
//	        String cKey = "00000000001234232231310009990099"; 
	    	System.out.println(MD5Encryption.string2MD5("_2016-11-30 11:34:31_Markor"));
	    	String cKey=MD5Encryption.encrypt16("_2015-10-29 15:35:05_Markor");
		    System.out.println("cKey="+cKey);  
	    	cKey="b853fc8c60ab6431";
		    
	        String cSrc = "[加密后{\"USERNAME\":\"A32\",\"password\":\"Lynn19870130\"}]";
		    System.out.println(cSrc);
	        		// 加密
	        String enString = AES128CBCEncryption.Encrypt(cSrc, cKey); 
	        System.out.println("加密后的字串是：" + enString);
	        String DeString=AES128CBCEncryption.Decrypt(enString,cKey); 
	        System.out.println("解密后的字串是：" + DeString);
 
	   } 
	}
