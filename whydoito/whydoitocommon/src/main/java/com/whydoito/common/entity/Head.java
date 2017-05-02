package com.whydoito.common.entity;

import java.io.Serializable;


public class Head implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7058443398113601766L;

	private String userid = "";
	
	private String userName = "";
	
	private String storeID = "";
	
	private String storeName = "";

	private String sessionID = "";

	private String tranID = "";
	
	private String dateTime = "";
	
	private String cver = "";  //版本号
	
	private String updateUrl = ""; //版本更新url
	
	private String type = "";
	
	private String deviceId = "";
	
	private String url = "";
	
	private String urlhost = "";
	
	private String urlpath = "";
	
	private String urlparam = "";
	
	private String clientip = "";
	
	private String netkind = "";
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCver() {
		return cver;
	}

	public void setCver(String cver) {
		this.cver = cver;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getTranID() {
		return tranID;
	}

	public void setTranID(String tranID) {
		this.tranID = tranID;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUrlhost() {
		return urlhost;
	}

	public void setUrlhost(String urlhost) {
		this.urlhost = urlhost;
	}

	public String getUrlpath() {
		return urlpath;
	}

	public void setUrlpath(String urlpath) {
		this.urlpath = urlpath;
	}

	public String getUrlparam() {
		return urlparam;
	}

	public void setUrlparam(String urlparam) {
		this.urlparam = urlparam;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public String getNetkind() {
		return netkind;
	}

	public void setNetkind(String netkind) {
		this.netkind = netkind;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
