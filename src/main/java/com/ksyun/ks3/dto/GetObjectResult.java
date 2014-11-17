package com.ksyun.ks3.dto;

import com.ksyun.ks3.utils.StringUtils;

/**
 * @author lijunwei[13810414122@163.com]  
 * 
 * @date 2014年11月10日 下午6:09:15
 * 
 * @description 
 **/
public class GetObjectResult {
	private Ks3Object object = new Ks3Object();
	/**
	 * false 
	 * object为null
	 * false 即 http 304  一般用于缓存控制
	 */
	private boolean ifModified = true;
	/**
	 * false
	 * object为null
	 * false 即http 412 一般用于缓存控制
	 */
	private boolean ifPreconditionSuccess = true;
	public String toString()
	{
		return StringUtils.object2string(this);
	}
	public Ks3Object getObject() {
		return object;
	}
	public void setObject(Ks3Object object) {
		this.object = object;
	}
	public boolean isIfModified() {
		return ifModified;
	}
	public void setIfModified(boolean ifModified) {
		this.ifModified = ifModified;
	}
	public boolean isIfPreconditionSuccess() {
		return ifPreconditionSuccess;
	}
	public void setIfPreconditionSuccess(boolean ifPreconditionSuccess) {
		this.ifPreconditionSuccess = ifPreconditionSuccess;
	}
	
}
