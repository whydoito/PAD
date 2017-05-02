package com.whydoito.common.utils.security;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;


public class test {
	private static String[] strArray = {"updateFee","checkvoucher","voucher","point","cash",
			"customerListService","customerDetailService","addorupadateCustomerService","addDeliveryAddressService","getDeliveryAddressService",
			"sendSecurityCodeService","verifySecurityCodeService"};
	
	public static void main(String[] args) {
		System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset in Use=" + getDefaultCharSet());
        
		
	}
	 private static String getDefaultCharSet() {
	        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
	        String enc = writer.getEncoding();
	        return enc;
	    }

}
