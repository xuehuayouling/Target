package com.ruili.target.utils;

public interface IQiniuUploadUitlsListener {
	public void onSucess(String fileUrl);

	public void onError(int errorCode, String msg);

	public void onProgress(int progress);

}
