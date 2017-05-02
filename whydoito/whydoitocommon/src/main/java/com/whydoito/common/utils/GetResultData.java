package com.whydoito.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import com.whydoito.common.entity.Head;
import com.whydoito.common.entity.Result;
import com.whydoito.common.entity.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class GetResultData {
	
	private static final Logger log = LoggerFactory.getLogger(GetResultData.class);
	
	public static ResultData getResultData(String flag,String msg)
	{
		String cveraddress = PropertiesUtil.getProperty("service.cver.address");
		String updateUrl = PropertiesUtil.getProperty("service.updateUrl.address"); 
		ResultData r = new ResultData();
		Result result = new Result();
		result.setCode(flag);
		Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result.setDateTime(format.format(date));
		result.setMessage(msg);
		Head head = new Head();
		head.setDateTime(format.format(date));
//		以下2个属性值 丢入CommonOutInterceptor中处理，取值方式区分Https /http
//		String str = "";
//		try {
//			str = IpAddrUtil.httpsRequest(cveraddress, "POST", null);//readFileByLines(cveraddress);
//		} catch (MalformedURLException e) {
//			log.error("cveraddress is fail", e);
//			return GetResultData.getResultData(ResultData.FAIL_STATUS,e.getMessage());
//		} catch (IOException e) {
//			log.error("cveraddress is fail", e);
//			return GetResultData.getResultData(ResultData.FAIL_STATUS,e.getMessage());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			return GetResultData.getResultData(ResultData.FAIL_STATUS,e.getMessage());
//		}
//		head.setCver(str);
//		head.setUpdateUrl(updateUrl);
		r.setResult(result);
		r.setHead(head);
		return r;
	}
	
	public static ResultData getResultData2(String flag,String msg,Object data)
	{
		String cveraddress = PropertiesUtil.getProperty("service.cver.address");
		String updateUrl = PropertiesUtil.getProperty("service.updateUrl.address"); 
		ResultData r = new ResultData();
		Result result = new Result();
		result.setCode(flag);
		Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result.setDateTime(format.format(date));
		result.setMessage(msg);
		r.setResult(result);
		r.setData(data);
		Head head = new Head();
		head.setDateTime(format.format(date));
//		以下2个属性值 丢入CommonOutInterceptor中处理，取值方式区分Https /http
//		String str = "";
//		try {
//			str = IpAddrUtil.httpsRequest(cveraddress, "POST", null);//readFileByLines(cveraddress);
//		} catch (IOException e) {
//			log.error("cveraddress is fail", e);
//			return GetResultData.getResultData(ResultData.FAIL_STATUS,e.getMessage());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			return GetResultData.getResultData(ResultData.FAIL_STATUS,e.getMessage());
//		}
//		head.setCver(str);
//		head.setUpdateUrl(updateUrl);
		r.setHead(head);
		return r;
	}
	
	public static ResultData getResultData3(String flag,String msg,String cver,Object data) throws Exception
	{
		String cveraddress = PropertiesUtil.getProperty("service.cver.address");
		String updateUrl = PropertiesUtil.getProperty("service.updateUrl.address"); 
		ResultData r = new ResultData();
		Result result = new Result();
		result.setCode(flag);
		Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result.setDateTime(format.format(date));
		result.setMessage(msg);
		r.setResult(result);
		r.setData(data);
		Head head = new Head();
		head.setDateTime(format.format(date));
		if("".equals(cver)){
			String str = IpAddrUtil.httpsRequest(cveraddress, "POST", null);//readFileByLines(cveraddress);
			if(cver.equals(str)){
				head.setCver(str);
				head.setUpdateUrl(updateUrl);
			}else{
				head.setCver(cver);
				head.setUpdateUrl(null);
			}
		}
		
		r.setHead(head);
		return r;
	}
	
	/**
     * 以行为单位读取文件，常用于读面向行的格式化文件
	 * @throws IOException 
     */
    public static String readFileByLines(String fileName) throws IOException {
    	String str = "";
//        File file = new File(fileName);
        URL urla = new URL(fileName);		
        HttpsURLConnection urlCon = null;  
        urlCon = (HttpsURLConnection) (new URL(fileName.toString())).openConnection();
        urlCon.setDoInput(true);  
        urlCon.setDoOutput(true);  
        urlCon.setRequestMethod("POST");  
//        urlCon.setRequestProperty("Content-Length",  
//                String.valueOf(xml.getBytes().length));  
//        urlCon.setUseCaches(false);  
        //设置为utf-8可以解决服务器接收时读取的数据中文乱码问题  
//        urlCon.getOutputStream().write(xml.getBytes("utf-8"));  
//        urlCon.getOutputStream().flush();  
//        urlCon.getOutputStream().close();  
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
//            reader = new BufferedReader(new FileReader(file));urla.openStream()
            reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
//                System.out.println("line " + line + ": " + tempString);
                if(line == 22){
//                	System.out.println("gaosi");
                	int beginIndex = tempString.indexOf(">")+1;
                	int endIndex = tempString.lastIndexOf("<");
                	str = tempString.substring(beginIndex, endIndex);
//                	System.out.println(str);
                	break;
                }
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return str;
    }
	
//	public static void main(String[] args) throws Exception {
////		System.out.println(getIntArrayFromFile("d:\\installIPA.plist"));
//		readFileByLines("http://218.22.63.10:9013/installIPA.plist");
////		readFileByChars("d:\\installIPA.plist");
//		String str = "one123";   
//        String regex = "(?<=one)(?=123)";   
//        String[] strs = str.split(regex);   
//        for(int i = 0; i < strs.length; i++) {   
//            System.out.printf("strs[%d] = %s%n", i, strs[i]);   
//        }  
//		String hybrisMedias = cverAddress;//PropertiesUtil.getProperty("service.cver.address");
//		System.out.println(hybrisMedias);
//	}
//	
//	public static List<Map<String,String>> getIntArrayFromFile(String file) throws Exception{
//		List<Map<String,String>> list= new ArrayList<Map<String,String>>();
//		BufferedReader br=new BufferedReader(new FileReader(file));
//		String str=null;
//		Map<String,String> map=null;
//		while((str=br.readLine())!=null){
//			if("<dict>".equals(str.trim())){
//				if(map!=null&&map.size()>0){
//					list.add(map);
//				}
//				 map=new HashMap<String,String>();
//			}else{
//				String[] array=str.split(":",2);
//				if(map!=null&&array.length==2){
//					map.put(array[0], array[1]);
//				}
//			}
//		
//		}
//		br.close();
//		return list;
//	}
//	
//	
//    
//    /**
//     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
//     */
//    public static void readFileByChars(String fileName) {
//        File file = new File(fileName);
//        Reader reader = null;
//        try {
//            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
//            // 一次读一个字符
//            reader = new InputStreamReader(new FileInputStream(file));
//            int tempchar;
//            while ((tempchar = reader.read()) != -1) {
//                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
//                // 但如果这两个字符分开显示时，会换两次行。
//                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
//                if (((char) tempchar) != '\r') {
//                    System.out.print((char) tempchar);
//                }
//            }
//            reader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
//            // 一次读多个字符
//            char[] tempchars = new char[30];
//            int charread = 0;
//            reader = new InputStreamReader(new FileInputStream(fileName));
//            // 读入多个字符到字符数组中，charread为一次读取字符数
//            while ((charread = reader.read(tempchars)) != -1) {
//                // 同样屏蔽掉\r不显示
//                if ((charread == tempchars.length)
//                        && (tempchars[tempchars.length - 1] != '\r')) {
//                    System.out.print(tempchars);
//                } else {
//                    for (int i = 0; i < charread; i++) {
//                        if (tempchars[i] == '\r') {
//                            continue;
//                        } else {
//                            System.out.print(tempchars[i]);
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//    }

}
