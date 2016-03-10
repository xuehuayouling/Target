package com.ruili.target.utils;

public interface IQiniuUploadManagerListener {

	public void onMultipleUploadDone();
	public void onMultipleUploadFail(String reason);
}
