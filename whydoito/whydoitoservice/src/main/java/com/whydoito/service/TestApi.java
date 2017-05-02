package com.whydoito.service;


import com.whydoito.common.entity.Result;
import com.whydoito.common.entity.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * 品牌渠道接口,品牌商品
 * @author Administrator
 *
 */
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Service
public class TestApi
{
	private static final Logger LOG = LoggerFactory.getLogger(TestApi.class);



	@GET
	@Path("/getTest")
	public ResultData getTest(@QueryParam("code") String code) {
		ResultData data = new ResultData();
		Result result = new Result();
		result.setCode(code);
		result.setMessage("成功！！！");
		data.setResult(result);
		return data;
	}

}
