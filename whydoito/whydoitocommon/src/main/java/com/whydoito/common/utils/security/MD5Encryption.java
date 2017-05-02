package com.whydoito.common.utils.security;
 
import java.security.MessageDigest;


public abstract class MD5Encryption { 
	
    /*** 
     * MD5加码 生成32位md5码 
    */  
	public static String string2MD5(String encryptStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = encryptStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }
        return hexValue.toString();  
  
    }  
    
   /*** 
    * MD5加码 生成16位md5码 
    */
	 public static String encrypt16(String encryptStr) {  
		return string2MD5(encryptStr).substring(0, 16);  
	 }  

    /** 
     * 加密解密算法 执行一次加密，两次解密 
     */   
    public static String convertMD5(String inStr){  
  
        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ 't');  
        }  
        String s = new String(a);  
        return s;  
  
    }  
  
    /**
	 * 字符串转换byte  "be"-->be
	 * @param inputStr
	 * @return
	 */
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
   
	
    // 测试主函数  
    public static void main(String args[]) {  
        String s = new String("Lynn19870130");  //bebf2c3ac79d74fd85b30ea3008561fc
        System.out.println("原始：" + s);  
        System.out.println("MD5后：" + string2MD5(s));
        System.out.println("解密的：" + convertMD5(convertMD5(s)));  
  
    }  
} 

