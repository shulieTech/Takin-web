/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package io.shulie.takin.web.biz.pojo.request.scriptmanage;

/**
 * @author hengyu
 */
public class WebPartRequest {

	/**
	 * 文件路径
	 */
	private String filePath = null;

	/**
	 * 推送服务器 takin URL地址
	 */
	private String takinWebUrl = null;

	/**
	 * 用户上传 key值
	 */
	private String userAppKey = null;

	/**
	 * 场景ID
	 */
	private Long sceneId = null;

	/**
	 * 原始上传文件名称
	 */
	private String originalName;

	/**
	 * 脚本ID
	 */
	private Long takinScriptId = null;

	/**
	 * 是否按照顺序拆分
	 */
	private Long isOrderSplit = null;
	/**
	 * 是否需要拆分
	 */
	private Long isSplit = null;
	/**
	 * 拆分文件块数
	 */
	private Long dataCount = null;


	/**
	 * 文件块长度
	 */
	private long fileLength;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTakinWebUrl() {
		return takinWebUrl;
	}

	public void setTakinWebUrl(String takinWebUrl) {
		this.takinWebUrl = takinWebUrl;
	}

	public String getUserAppKey() {
		return userAppKey;
	}

	public void setUserAppKey(String userAppKey) {
		this.userAppKey = userAppKey;
	}

	public Long getSceneId() {
		return sceneId;
	}

	public void setSceneId(Long sceneId) {
		this.sceneId = sceneId;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public Long getIsOrderSplit() {
		return isOrderSplit;
	}

	public void setIsOrderSplit(Long isOrderSplit) {
		this.isOrderSplit = isOrderSplit;
	}

	public Long getIsSplit() {
		return isSplit;
	}

	public void setIsSplit(Long isSplit) {
		this.isSplit = isSplit;
	}

	public Long getDataCount() {
		return dataCount;
	}

	public void setDataCount(Long dataCount) {
		this.dataCount = dataCount;
	}

	public Long getTakinScriptId() {
		return takinScriptId;
	}

	public void setTakinScriptId(Long takinScriptId) {
		this.takinScriptId = takinScriptId;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
}
