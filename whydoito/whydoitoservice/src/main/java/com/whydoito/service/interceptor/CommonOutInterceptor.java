package com.whydoito.service.interceptor;



import com.whydoito.common.entity.*;
import com.whydoito.common.exception.ServiceException;
import com.whydoito.common.utils.JsonBeanUtil;
import com.whydoito.common.utils.security.AES128CBCEncryption;
import com.whydoito.common.utils.security.MD5Encryption;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


@Component
public class CommonOutInterceptor extends AbstractPhaseInterceptor<Message> {

	 @Value("${default.listPicture.address}")
	 private String listPictureAddress;
	 
	private String[] noEncrypService = {"pictureClickService","getChannelfirstPicture","pictureUploadService","addAndUpdateForumMain"};
	private static final Logger log = LoggerFactory.getLogger(CommonOutInterceptor.class);
	
	private String[] noEncrytpService = {"refreshProductData","getPictureUrl","logAnalysisService","pictureClickService","getChannelfirstPicture","pictureUploadService","addAndUpdateForumMain","addAndUpdateForumReply"};

	public CommonOutInterceptor(String phase) {
		super(phase);
	}
	
	public CommonOutInterceptor() {  
        super(Phase.PRE_STREAM);  
    }

	@SuppressWarnings({ "rawtypes", "static-access" })
	@Override
	public void handleMessage(Message message) throws Fault {
		
		System.out.println("===============CommonOutInterceptor=====================");

		HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
		String service=""+response.getHeader("WEBSERVICE-SNAME");
		//判断数据是否需要进行解密
		//待定 pictureUploadService
		System.out.println("serviceName ="+service);
		ResultData resultData=new ResultData();
    	Head head=new Head();
    	
		try{
			Object objContent = message.getContent(List.class).get(0);
	    	resultData = (ResultData) objContent;
	    	head=resultData.getHead();
	    	head.setUserid(response.getHeader("USERID-IN-HEAD"));
	    	head.setCver(response.getHeader("WEBSERVICE-CVER-ADDRESS"));
	    	head.setUpdateUrl(response.getHeader("WEBSERVICE-UPDATEURL-ADDRESS"));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!Arrays.asList(noEncrytpService).contains(service)){
	    	if("pad".equals(response.getHeader("ACESSTYPE-IN-HEAD"))){
	    		try {
					Object dt = resultData.getData();
			    	String data= JsonBeanUtil.bean2JsonString(dt);
					log.debug(service+" encryp start 服务加密开始 ！！！");
					Object EnStr = getEncrypData(data,head);
					log.debug(service+"encryp over 服务加密结束！！！");
			    	resultData.setData(EnStr);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
	    	}else {
				log.debug("IS　NOT PAD SO NOT NEED Encryp DATA。");
	    	}
		}else{
			log.debug("NOT NEED Encryp DATA。");
			if("getPictureUrl".equals(service)){ 
				//图片流处理 
				String picUrl=""+resultData.getData();
				log.debug("picUl=="+picUrl);
				showPicture(picUrl,response);
			}
		}
	}
	
	public void showPicture(String picUrl ,HttpServletResponse response){
		OutputStream output = null;//得到输出流
		BufferedInputStream bis=null;
		BufferedOutputStream bos=null;
		try {
			output=response.getOutputStream();
			if("".equals(picUrl)){//本地默认图片
				String path= this.getClass().getResource("/").getPath()+"/nopic.png";
				FileInputStream fileInput=new FileInputStream(path);
				bis=new BufferedInputStream(fileInput); 
				bos = new BufferedOutputStream(output);
			
			}else{
				URL url = new URL(picUrl);
				//http://ip:portg/markorhome-service/services/picture/getPictureInputStream?skuCode=1234
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5 * 1000);
				InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
				bis = new BufferedInputStream(inStream);// 输入缓冲流
				bos = new BufferedOutputStream(output);// 输出缓冲流
			
			}
			byte data[] = new byte[4096];// 缓冲字节数
			int size = 0;
			size = bis.read(data);
			while (size != -1) {
				bos.write(data, 0, size);
				size = bis.read(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug(e.getMessage());
		}finally{
				try {
					if(bis!=null) bis.close();
					if(bos!=null) {
						bos.flush();// 清空输出缓冲流
						bos.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} 
	}
	/**
	 * 加密
	 * @param
	 * @return
	 */
	public static Object  getEncrypData(String data,Head head){

		//根据约定美克AES网络加密key 
		//Key=MD5(UserID+"_"+YYYY-MM-DD HH:MM:SS+"_Markor")
		String dateStr=head.getDateTime();
		String keyStr="";
		if(dateStr!=null && !"".equals(dateStr.trim())){
			
			keyStr+=head.getUserid()+"_"+dateStr.substring(0, 19)+"_Markor";
		} 
		System.out.println("keyStr="+keyStr);
	  // 加密 
		String cKey= MD5Encryption.encrypt16(keyStr);
        String enString="";
		try {
			enString = AES128CBCEncryption.Encrypt((String)data, cKey);
		} catch (Exception e) {
			System.out.println("AES128CBCEncryption.Encrypt is fail !!!");
			e.printStackTrace();
		} 
		return enString;
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
	 * @param
	 * @return
	 */
	public String  getDecrytptData(Object data, Head head){
		//根据约定美克AES网络加密key  Key=MD5(UserID+"_"+YYYY-MM-DD HH:MM:SS+"_Markor")
		String dateStr=head.getDateTime();
		String keyStr="";
		if(dateStr!=null && !"".equals(dateStr.trim())){
			keyStr=""+head.getUserid()+"_"+dateStr.substring(0, 19)+"_Markor";
		}
        // 解密 
		String cKey=MD5Encryption.encrypt16(keyStr);
		String DeString="";
		try {
			System.out.println(data);
			System.out.println(cKey);
		    DeString = AES128CBCEncryption.Decrypt(""+data, cKey); 
		} catch (Exception e) {
			System.out.println("AES128CBCEncryption.Decrypt is fail !!!");
			e.printStackTrace();
		}  
        System.out.println("解密后的DATA == " + DeString); 
		return DeString;
	}
	
}
