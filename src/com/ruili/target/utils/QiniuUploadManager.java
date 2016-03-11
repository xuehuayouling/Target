package com.ruili.target.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

public class QiniuUploadManager {
	private static final String TAG = QiniuUploadManager.class.getSimpleName();
	public static final String AK = "LToJ1s_uUS302O9DLA1FPoFcjykHqVn2EY8OdH4c";
	public static final String SK = "a7Z-PWhNTn6TYl4ETmlzW-mCfFQZjXOCC1UQb1B1";
	public static final String BUCKET_NAME = "ruiliqc";
	/** 在网站上查看 */
	private static final String ACCESS_KEY = AK;
	/** 在网站上查看 */
	private static final String SECRET_KEY = SK;
	public static final String KEY_FILE_PATH = "file_path";
	public static final String KEY_SAVE_NAME = "save_name";
	private UploadManager uploadManager = new UploadManager();
	private ConcurrentHashMap<String, Boolean> mNeedLoadHashMap = new ConcurrentHashMap<>();

	public QiniuUploadManager() {
	}

	/**
	 * 多文件同时上传，如果都上传成功，则返回成功，否则返回失败
	 * @param files
	 *            map包含需要上传到服务器的文件路径（key:{@link #KEY_FILE_PATH}
	 *            }），和保存到服务器的名称（key:{@link #KEY_SAVE_NAME}， PS:无需包含服务器地址）
	 * @param managerListener 上传完成后的回调接口
	 */
	public void multipleUpload(List<Map<String, String>> files, IQiniuUploadManagerListener managerListener) {
		if (files != null && files.size() > 0) {
			for (Map<String, String> map : files) {
				if (mNeedLoadHashMap.containsKey(map.get(KEY_SAVE_NAME))) {
					callcellAllUpLoad(managerListener, "有文件保存到服务器上的名字相同！");
					return;
				} else {
					mNeedLoadHashMap.put(map.get(KEY_SAVE_NAME), true);
				}
			}
			for (Map<String, String> map : files) {
				uploadImage(map.get(KEY_FILE_PATH), map.get(KEY_SAVE_NAME), managerListener);
			}
		} else {
			callcellAllUpLoad(managerListener, "需要上传的列表为空!");
		}
	}

	private void uploadImage(String filePath, final String fileUrlUUID,
			final IQiniuUploadManagerListener managerListener) {
		String token = getUpToken();
		if (token == null) {
			callcellAllUpLoad(managerListener, "Token is null!");
			return;
		}
		uploadManager.put(filePath, fileUrlUUID, token, new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				Logger.debug(TAG, "uploadImage complete  -->  fileUrlUUID: " + fileUrlUUID);
				if (info != null && info.statusCode == 200) {// 上传成功
					synchronized (mNeedLoadHashMap) {
						if (mNeedLoadHashMap.containsKey(fileUrlUUID)) {
							mNeedLoadHashMap.remove(fileUrlUUID);
							if (mNeedLoadHashMap.isEmpty()) {
								managerListener.onMultipleUploadDone();
							}
						}
					}
				} else {
					Logger.debug(TAG, info.toString());
					callcellAllUpLoad(managerListener, info.toString());
					
				}
			}
		}, new UploadOptions(null, null, false, new UpProgressHandler() {
			public void progress(String key, double percent) {
			}
		}, new UpCancellationSignal() {

			@Override
			public boolean isCancelled() {
				return mNeedLoadHashMap.contains(fileUrlUUID);
			}
		}));

	}

	private void callcellAllUpLoad(IQiniuUploadManagerListener managerListener, String reason) {
		mNeedLoadHashMap.clear();
		managerListener.onMultipleUploadFail(reason);
	}

	/**
	 * 获取token 本地生成
	 * 
	 * @return
	 */
	private String getUpToken() {
		Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);
		PutPolicy putPolicy = new PutPolicy(BUCKET_NAME);
		putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
		try {
			String uptoken = putPolicy.token(mac);
			Logger.debug(TAG, "getToken -->  uptoken: " + uptoken);
			return uptoken;
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param name 上传图片时通过key指定的文件名字：{@link #KEY_SAVE_NAME}
	 * @return
	 */
	public static final String getFileHttpUrlByName(String name) {
		return "http://7xrluz.com2.z0.glb.qiniucdn.com/" + name;
	}
}
