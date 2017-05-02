package com.whydoito.service.interceptor;



import com.whydoito.common.entity.Head;
import com.whydoito.common.entity.ResultData;
import com.whydoito.common.exception.ServiceException;
import com.whydoito.common.utils.GetResultData;
import com.whydoito.common.utils.IpAddrUtil;
import com.whydoito.common.utils.JsonBeanUtil;
import com.whydoito.common.utils.PropertiesUtil;
import com.whydoito.common.utils.security.AES128CBCEncryption;
import com.whydoito.common.utils.security.MD5Encryption;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


@Component
public class CommonInterceptor extends AbstractPhaseInterceptor<Message> {
	


	private String cveraddress = "";
	private String updateUrl = "";   
	
	private static final Logger log = LoggerFactory.getLogger(CommonInterceptor.class);
	
	private String[] strArray = {"updateFee","checkvoucher","voucher","point","cash",
			"customerListService","customerDetailService","addorupadateCustomerService","addDeliveryAddressService","getDeliveryAddressService",
			"sendSecurityCodeService","verifySecurityCodeService"};
	
	private String[] noDecryptService = {"refreshProductData","getPictureUrl","logAnalysisService","pictureClickService","getChannelfirstPicture","pictureUploadService","addAndUpdateForumMain","addAndUpdateForumReply"};

	public CommonInterceptor(String phase) {
		super(phase);
	}
	
	public CommonInterceptor() {  
        super(Phase.USER_LOGICAL);  
    }

	@SuppressWarnings({ "rawtypes", "static-access" })
	@Override
	public void handleMessage(Message message) throws Fault {
		System.out.println("===============开始=====================");
		Map<String,String> map = new HashMap<String,String>();
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		String ipAddress = IpAddrUtil.getIpAddr(request);
		System.out.println(ipAddress+"-------is internalIP="+IpAddrUtil.internalIp(ipAddress));
		boolean validIpAddr = IpAddrUtil.internalIp(ipAddress);
		
		String url = "" + request.getContextPath() + request.getServletPath() + request.getPathInfo();
	    System.out.println(url);
	    map.put("url", url);
		map.put("urlhost", request.getServerName());
		map.put("urlpath", request.getRequestURI());
		map.put("clientip", ipAddress);
		if(validIpAddr){
			map.put("netkind", "N");
		}else{
			map.put("netkind", "Y");
		}

		String pathInfo=""+message.get(message.PATH_INFO).toString();
		String[] str = pathInfo.split("/");
		log.debug("url== "+pathInfo); 
		String strb = str[str.length-1].trim();
		
		//解密处理
		goDecryptData(message,strb);
		//获得版本号及升级地址
		if("".equals(cveraddress)){
			Head head=getHead(message);
			cveraddress=head.getCver();
			updateUrl=head.getUpdateUrl();
			log.debug("cveraddress= "+cveraddress+";updateUrl= "+updateUrl);
		}
		
		HttpServletResponse resp = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
		resp.setHeader("WEBSERVICE-CVER-ADDRESS", cveraddress);
		resp.setHeader("WEBSERVICE-UPDATEURL-ADDRESS", updateUrl);
		
    	if(!validIpAddr){//外网
    		if(("authService").equals(strb)){//外网不允许 自动登录  ----判断条件 --自动登录 userid不为空
    			Object obj = message.getContent(List.class).get(0);
            	List list = (List) obj;
            	ResultData rd = (ResultData) list.get(0);
            	if("".equals(""+rd.getHead().getUserid())){
            		sendRequest(message,map,strb);
            	}else{
            		ResultData r = GetResultData.getResultData2(ResultData.FAIL_STATUS,"外网时请手动输入登录信息，重新登录",null);
    				Head headIn=r.getHead();
    				headIn.setCver(cveraddress);
    				headIn.setUpdateUrl(updateUrl);
        			Response response = Response.status(Response.Status.OK)
                            .header("Content-Type", "application/json")
                            .entity(r)
                            .build();
                    message.getExchange().put(Response.class, response);
            	}
    		}else if(Arrays.asList(strArray).contains(strb)){//外网不允许访问的模块
    			ResultData r = GetResultData.getResultData2(ResultData.FAIL_STATUS,"不允许外网访问请到内网访问",null);
    			Head headIn=r.getHead();
    			headIn.setCver(cveraddress);
    			headIn.setUpdateUrl(updateUrl);
    			Response response = Response.status(Response.Status.OK)
                        .header("Content-Type", "application/json")
                        .entity(r)
                        .build();
                message.getExchange().put(Response.class, response);
    		}else{
    			sendRequest(message,map,strb);
    		}
    	}else{
			sendRequest(message,map,strb);
    	}
	}
	
	/**
	 * 发送请求
	 * @param message
	 */
	public void sendRequest(Message message,Map<String,String> map,String serviceName){
//		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		String reqParams=null;
		ResultData rd = null;
		List list = null;
		if(message.get(message.HTTP_REQUEST_METHOD).equals("GET")){//采用GET方式请求  
			System.out.println("=========================get==========================>>>>>");
//            reqParams=(String) message.get(message.QUERY_STRING);  
//            message.remove(message.QUERY_STRING);  
//            reqParams=this.getParams(this.getParamsMap(reqParams));  
////            message.put(message.QUERY_STRING,reqParams);  
//			try {
//				String str2 = readFileByLines2(cveraddress);
//				if(reqParams.equals(str2)){
//					System.out.println("===============继续=====================");
//	            }else{
//    				ResultData r = GetResultData.getResultData2(ResultData.SUCC_STATUS,ResultData.SUCC_MSG,null);
//    				Head head = r.getHead();
//    				head.setCver(str2);
//    				head.setUpdateUrl(updateUrl);
//    				r.setHead(head);
//    				Response response = Response.status(Response.Status.OK)
//    	                    .header("Content-Type", "application/json")
//    	                    .entity(r)
//    	                    .build();
//    	            message.getExchange().put(Response.class, response);
//    			}
//	              
//			} catch (MalformedURLException e) {
//				log.error("MalformedURLException异常",e);
//			} catch (IOException e) {
//				log.error("IOException异常",e);
//			}
            
        }else if(message.get(message.HTTP_REQUEST_METHOD).equals("POST")){//采用POST方式请求  
        	System.out.println("=========================POST==========================>>>>>");
    		try {
    			Object obj = message.getContent(List.class).get(0);
            	list = (List) obj;
            	rd = (ResultData) list.get(0);
            	String type = rd.getHead().getType();
            	if("pad".equals(type)){
            		String str1 = rd.getHead().getCver();
        			if(!str1.equals(cveraddress)){
        				ResultData r = GetResultData.getResultData2(ResultData.SUCC_STATUS,ResultData.SUCC_MSG,null); 
        				Head headIn=r.getHead();
        				headIn.setCver(cveraddress);
        				headIn.setUpdateUrl(updateUrl);
        				Response response = Response.status(Response.Status.OK)
        	                    .header("Content-Type", "application/json")
        	                    .entity(r)
        	                    .build();
        	            message.getExchange().put(Response.class, response);
        			}else{
        				Head hd = rd.getHead();
        				String requestUrl=""+map.get("url");
        				hd.setUrlparam(getUrlParam(rd,requestUrl));
        				hd.setUrl(requestUrl);
        				hd.setUrlhost(map.get("urlhost"));
        				hd.setUrlpath(map.get("urlpath"));
        				hd.setClientip(map.get("clientip"));
        				hd.setNetkind(map.get("netkind"));
        				rd.setHead(hd); 
        			}
            	}
    		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
		

		
	}
	
	/**
	 * 处理日志信息   insert into T_LOG_Request 中参数部分字段---urlParam 
	 * @param rd
	 * @param map
	 * @return
	 */
	private String getUrlParam(ResultData rd, String requestUrl){
		String param="";
		try {
			if (requestUrl.endsWith("/product/getProductListForPad1")) {//此接口带参数 仅仅用于保留日志用
				HashMap<String,Object> requestMap = (HashMap<String,Object>)rd.getData();
				List<String> conditionList = (List<String>)requestMap.get("condition");
				List<String> menuIdList = (List<String>)requestMap.get("menuId");
				String keyWords = requestMap.get("keyWords")+""; 
				param="condition="+ JsonBeanUtil.bean2JsonString(conditionList)+";menuId="+JsonBeanUtil.bean2JsonString(menuIdList)+";keyWords="+keyWords;
				log.debug(param); 
			}else if(requestUrl.endsWith("/product/getProductDetail")){ 
				List<LinkedHashMap<String, String>> list = (List<LinkedHashMap<String, String>>)rd.getData();
				String skuCode = list.get(0).get("skuCode");
				param="skuCode="+skuCode;
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return param;
	}
	
	/**
	 * 装载head内的属性  版本号---版本更新url
	 * @param message
	 * @return
	 */
	private Head getHead(Message message){
		Head head=new Head();
		String cveraddress = PropertiesUtil.getProperty("service.cver.address");
		String updateUrl = PropertiesUtil.getProperty("service.updateUrl.address");  

		String integrityUrl=""+ message.get("http.base.path");
		try {
			String str2="";
			if(integrityUrl.startsWith("https://")){
				str2 = IpAddrUtil.httpsRequest(cveraddress, "POST", null);
			}else{
				str2 = readFileByLines2(cveraddress);
//				str2 = IpAddrUtil.httpsRequest(cveraddress, "POST", null);
			}
			head.setCver(str2);
			head.setUpdateUrl(updateUrl);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//readFileByLines2(cveraddress);
		return head;
		
	}
	
	private Map<String,String> getParamsMap(String strParams){  
        if(strParams==null||strParams.trim().length()<=0){  
            return null;  
        }  
        Map<String,String> map =new HashMap<String,String>();  
        String[] params=strParams.split("&");  
        for(int i=0;i<params.length;i++){  
            String[] arr=params[i].split("=");  
            map.put(arr[0], arr[1]);  
        }  
        return map;  
    }  
      
    private String getParams(Map<String,String> map){  
        if(map==null||map.size()==0){  
            return null;  
        }  
        StringBuffer sb=new StringBuffer();  
        Iterator<String> it =map.keySet().iterator();  
        while(it.hasNext()){  
            String key=it.next();  
            String value =map.get(key);  
            //这里可以对客户端上送过来的输入参数进行特殊处理。如密文解密；对数据进行验证等等。。。 
           if(key.equals("cver")){ 
//                value.replace("%3D", "="); 
//                value = DesEncrypt.convertPwd(value, "DES");
            	sb.append(value);
            }
//            if(sb.length()<=0){  
//                sb.append(key+"="+value);  
//            }else{  
//                sb.append("&"+key+"="+value);  
//            }
        }  
        return sb.toString();  
    }
	
	
	/**
     * 以行为单位读取文件，常用于读面向行的格式化文件
	 * @throws MalformedURLException 
     */
    @SuppressWarnings("unused")
	public static String readFileByLines1(InputStream is) throws MalformedURLException {
    	String str = "";
    	String tempString = "";
    	BufferedReader reader =new BufferedReader(new InputStreamReader(is));
		 int line = 1;
	     // 一次读入一行，直到读入null为文件结束
	     try {
			while ((tempString = reader.readLine()) != null) {
			     // 显示行号
				 String reg=".*cver.*";  //判断字符串中是否含有特定字符串cver
				 boolean blean = tempString.matches(reg);
				 if(blean == true){
					String strs = tempString.trim();
					int beginIndex = strs.indexOf("cver")+8;
	            	int endIndex = strs.lastIndexOf(",")-1;
	            	str = strs.substring(beginIndex, endIndex);
//	            	break;
				 }
			     line++;
			 }
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException e1) {
	            }
	        }
	    }
//    	byte b[] = new byte[1024];   
//        int len = 0;   
//        int temp=0;          //所有读取的内容都使用temp接收   
//        try {
//			while((temp=is.read())!=-1){    //当没有读取完时，继续读取   
//			    String reg=".*cver.*";  //判断字符串中是否含有特定字符串cver
//				boolean blean = tempString.matches(reg);
//				if(blean == true){
//					String strs = tempString.trim();
//					int beginIndex = strs.indexOf("cver")+8;
//			       	int endIndex = strs.lastIndexOf(",")-1;
//			       	str = strs.substring(beginIndex, endIndex);
//			       	break;
//				}
//			    len++;   
//			}
//			is.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        
        return str;
    }
    
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     * @throws Exception 
     */
    public static String readFileByLines2(String fileName) throws Exception {
    	String str = "";
        URL urla = new URL(fileName);	
       
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
//            HttpsURLConnection urlCon = null;  
//            urlCon = (HttpsURLConnection) (new URL(fileName.toString())).openConnection();
//            urlCon.setDoInput(true);  
//            urlCon.setDoOutput(true);  
//            urlCon.setRequestMethod("POST");  
            
//            URL url = new URL(null,fileName,new sun.net.www.protocol.https.Handler());//重点在这里，需要使用带有URLStreamHandler参数的URL构造方法  
//            javax.net.ssl.HttpsURLConnection httpConnection = (javax.net.ssl.HttpsURLConnection) url.openConnection();//由于我调用的是官方给微信API接口，所以采用HTTPS连接  
//            String st = IpAddrUtil.httpsRequest(fileName, "POST", null);
            reader = new BufferedReader(new InputStreamReader(urla.openStream()));//urla.openStream()
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                if(line == 22){
                	int beginIndex = tempString.indexOf(">")+1;
                	int endIndex = tempString.lastIndexOf("<");
                	str = tempString.substring(beginIndex, endIndex);
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
	
    public void goDecryptData(Message message,String serviceName){
    	//保留值用于 CommonOutInterceptor 中判断是否要加密
		HttpServletResponse resp = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
		resp.setHeader("WEBSERVICE-SNAME", serviceName);
    	//图片上传 不用加密，， 增加论坛中也有图片
    	if(!Arrays.asList(noDecryptService).contains(serviceName)){
    		//判断数据是否需要进行解密
			Object object = message.getContent(List.class);
    		Object objContent = message.getContent(List.class).get(0);
        	List listD = (List) objContent;
        	ResultData resultData = (ResultData) listD.get(0);
        	
    		Head head=resultData.getHead();
    		resp.setHeader("USERID-IN-HEAD", head.getUserid());
    		resp.setHeader("ACESSTYPE-IN-HEAD", head.getType());
    		if("pad".equals(head.getType())){
        		String data = ""+ resultData.getData();
        		String obj=""+getDecryptData(data,head);
        		if (obj.trim().startsWith("{")){
        			//data 对象 转格式
            		try {
            			LinkedHashMap dataMap=(LinkedHashMap)JsonBeanUtil.jsonString2Bean((String)obj,LinkedHashMap.class);
        				resultData.setData(dataMap);
        			} catch (ServiceException e1) { 
        				log.debug("transfer json used jsonString2Bean--(MAP) failed ");
        			}
        		}else{//if(obj instanceof java.util.List)
        			//data 对象 转格式  不知道 data具体是哪种格式 一下只做了2种转换 确保某种对应上
            		try {
            			List<LinkedHashMap> dataList=(List<LinkedHashMap>)JsonBeanUtil.jsonString2BeanList((String)obj,LinkedHashMap.class);
        				resultData.setData(dataList);
        			} catch (ServiceException e1) { 
        				log.debug("transfer json used jsonString2BeanList failed ");
        			}
        		}
    		}else {
				log.debug("IS　NOT PAD SO NOT NEED Encryp DATA。");
				System.out.println("IS　NOT PAD SO NOT NEED Encryp DATA。");
	    	}
    	
    	}else {
			log.debug("NOT NEED Encryp DATA。");
			System.out.println("NOT NEED Encryp DATA。");
    	}
    	
    }
    
	/**
	 * 解密
	 * head = 	{
	 *	type = "pad",
	 *	userid = "A32",
	 *	storeID = "1001",
	 *	sessionID = "61ddadaf-0731-4c5b-8f46-9fc078a323fc",
	 *	dateTime = "2016-11-22 02:41:11 +0000",
	 *	cver = "20160830",
	 *  }
	 * @param Encrytp
	 * @return
	 */
	public String  getDecryptData(String data, Head head){
		//根据约定美克AES网络加密key  Key=MD5(UserID+"_"+YYYY-MM-DD HH:MM:SS+"_Markor")
		
		String dateStr=head.getDateTime();
		String keyStr="";
		if(dateStr!=null && !"".equals(dateStr.trim())){
			keyStr=""+head.getUserid()+"_"+dateStr.substring(0, 19)+"_Markor";
		}
        // 解密 
		String cKey= MD5Encryption.encrypt16(keyStr);
		String DeString="";
		try {
			log.debug("data="+data);
			log.debug("keyStr="+keyStr);
			log.debug("key="+cKey);
		    DeString = AES128CBCEncryption.Decrypt(data, cKey);
		} catch (Exception e) {
			log.debug("AES128CBCEncryption.Decrypt is fail !!!");
			e.printStackTrace();
		}  
        System.out.println("解密后的DATA == " + DeString); 
		log.debug("解密后的DATA == " + DeString);
		return DeString;
	}
	 private String getDefaultCharSet() {
	        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
	        String enc = writer.getEncoding();
	        return enc;
	    }

}
