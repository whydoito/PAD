package com.whydoito.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.util.IPAddressUtil;


@SuppressWarnings("restriction")
public class IpAddrUtil {

	private static final Logger log = LoggerFactory.getLogger(IpAddrUtil.class);

	static {  
        //for localhost testing only   
        HttpsURLConnection.setDefaultHostnameVerifier(
        new javax.net.ssl.HostnameVerifier(){  
   
            public boolean verify(String hostname,  
                    javax.net.ssl.SSLSession sslSession) {  
                if (hostname.equals("localhost")) {  
                    return true;  
                }else  if (hostname.equals("218.22.63.6")) {  //https://218.22.63.10:9013/installIPA.plist
                    return true;  
                }  
                return false;  
            }  
        });  
    }
	/**
	 * 获取当前网络ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

	/**
	 * 区分内外网ip
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean internalIp(String ip) {
		byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);
		return internalIp(addr);
	}

	public static boolean internalIp(byte[] addr) {
		final byte b0 = addr[0];
		final byte b1 = addr[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		switch (b0) {
		case SECTION_1:
			return true;
		case SECTION_2:
			if (b1 >= SECTION_3 && b1 <= SECTION_4) {
				return true;
			}
		case SECTION_5:
			switch (b1) {
			case SECTION_6:
				return true;
			}
		default:
			return false;
		}
	}

	public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) throws ConnectException, Exception{
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
//			while ((str = bufferedReader.readLine()) != null) {
//				buffer.append(str);
//			}
			
			int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((str = bufferedReader.readLine()) != null) {
                if(line == 22){
                	int beginIndex = str.indexOf(">")+1;
                	int endIndex = str.lastIndexOf("<");
                	str = str.substring(beginIndex, endIndex);
                	buffer.append(str);
                	break;
                }
                line++;
            }
			
			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			return buffer.toString();
//		return null;
	}

	// System.out.println("===>>>"+message.get(message.PATH_INFO)
	// +"=="+message.get(message.BASE_PATH)
	// +"=="+message.get(message.RESPONSE_CODE)
	// +"=ENDPOINT_ADDRESS="+message.get(message.ENDPOINT_ADDRESS)
	// +"=QUERY_STRING="+message.get(message.QUERY_STRING)
	// +"=PROPOGATE_EXCEPTION="+message.get(message.PROPOGATE_EXCEPTION)
	// +"=WSDL_PORT="+message.get(message.WSDL_PORT)
	// );
	// System.out.println("-----ServletPath---" + request.getServletPath());
	// System.out.println("-----ContextPath---" + request.getContextPath());
	// System.out.println("-----referer---" + request.getHeader("referer"));
	// System.out.println("-----host---" + request.getHeader("host"));
	// System.out.println("-----RequestURI---" + request.getRequestURI());
	// System.out.println("-----RequestURL---" + request.getRequestURL());
	// System.out.println("-----ServerName---" + request.getServerName());
	// System.out.println("-----ServerPort---" + request.getServerPort());
	// String realPath1 = "http://"+ request.getServerName()+ ":"+
	// request.getServerPort()+ request.getContextPath()+
	// request.getServletPath().substring(0,request.getServletPath().lastIndexOf("/")
	// + 1);
	// System.out.println("web URL 路径:" + realPath1);
	//
	// System.out.println("获取项目名="+request.getContextPath());
	// System.out.println("获取参数="+request.getQueryString());
	// System.out.println("获取全路径="+request.getRequestURL());
	/*
	 * 获取所有请求参数，封装到Map中
	 */
	// Map<String,String[]> map =
	// (Map<String,String[]>)request.getParameterMap();
	// for(String name:map.keySet()){
	// String[] values = map.get(name);
	// System.out.println(name+"="+Arrays.toString(values));
	// }

}
