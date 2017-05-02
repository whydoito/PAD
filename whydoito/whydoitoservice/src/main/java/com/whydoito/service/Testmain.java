package com.whydoito.service;

import com.alibaba.fastjson.JSONObject;
import com.whydoito.common.entity.ResultData;
import com.whydoito.common.utils.GetResultData;
import com.whydoito.common.utils.JsonBeanUtil;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplateCus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @project:whydoito
 * @package:com.whydoito.service
 * @copyright: Copyright[2017-2999] [Markor Investment Group Co. LTD]. All Rights Reserved.
 * @filename: Testmain
 * @description:&lt;描述&gt;
 * @author: wangyunlei
 * @date: 17/4/12-下午9:00
 * @version: 1.0
 */
public class Testmain
{
	RestTemplateCus restTemplate = new RestTemplateCus();

	public static void main(String[] args)
	{
		getSerialCode();

	}


	private static ResultData getSerialCode(){
		RestTemplateCus restTemplate = new RestTemplateCus();
		String serviceAddress="http://124.117.209.133:29092/verificationInterface/redList/personRedList";
		try {
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("access_token", "123123");
			HttpHeaders headers = new HttpHeaders();
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.set("Encoding", "UTF-8");

			HttpEntity formEntity = new HttpEntity(headers);
			//String objString = JsonBeanUtil.bean2JsonString(obj);
			ResponseEntity<String> rsp = restTemplate.exchange(serviceAddress, HttpMethod.GET,formEntity, String.class, reqMap);
			String rspString = rsp.getBody();

			if(rsp.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || rsp.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR){
				String message ="123";
				return GetResultData.getResultData(ResultData.FAIL_STATUS, message);
			}else{
				JSONObject jsStr = JSONObject.parseObject(rspString);
				String strs = JsonBeanUtil.bean2JsonString(jsStr.get("serials"));

				if("".equals(strs) || null == strs){
					ResultData data = GetResultData.getResultData2(ResultData.SUCC_STATUS, ResultData.SUCC_MSG, strs);
					data.getResult().setCode("1");
					return data;
				}else{
					return null;
				}
			}

		} catch (Exception e) {
			return GetResultData.getResultData(ResultData.FAIL_STATUS, "Hybris getSerialCode interface is failed");
		}

	}
}
