package com.ruili.target.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class QiniuUploadUitls {
	// public static final String AK =
	// "WEl_LrIf2e5-SZe3Sx7uMzIbwbiezXFvQmZC-KiU";
	// public static final String SK =
	// "AEySYVswzW4xlhxIExwT8Xco1lkJmdlXkrR9e35F";
	// public static final String BUCKET_NAME = "wangdajiu";
	public static final String AK = "LToJ1s_uUS302O9DLA1FPoFcjykHqVn2EY8OdH4c";
	public static final String SK = "a7Z-PWhNTn6TYl4ETmlzW-mCfFQZjXOCC1UQb1B1";
	public static final String BUCKET_NAME = "ruiliqc";

	/** 在网站上查看 */
	private static final String ACCESS_KEY = AK;
	/** 在网站上查看 */
	private static final String SECRET_KEY = SK;
	/** 你所创建的空间的名称 */
	private static final String bucketName = BUCKET_NAME;

	private static final String fileName = ".jpg";

	private static final String tempJpeg = Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + fileName;
	private static QiniuUploadUitls qiniuUploadUitls = null;
	private UploadManager uploadManager = new UploadManager();
	private int maxWidth = 720;
	private int maxHeight = 1080;

	private QiniuUploadUitls() {

	}

	public static QiniuUploadUitls getInstance() {
		if (qiniuUploadUitls == null) {
			qiniuUploadUitls = new QiniuUploadUitls();
		}
		return qiniuUploadUitls;
	}

	public boolean saveBitmapToJpegFile(Bitmap bitmap, String filePath) {
		return saveBitmapToJpegFile(bitmap, filePath, 75);
	}

	public boolean saveBitmapToJpegFile(Bitmap bitmap, String filePath, int quality) {
		try {
			BufferedOutputStream bufOutStr = new BufferedOutputStream(new FileOutputStream(filePath));
			resizeBitmap(bitmap).compress(CompressFormat.JPEG, quality, bufOutStr);
			bufOutStr.flush();
			bufOutStr.close();
		} catch (Exception exception) {
			return false;
		}
		return true;
	}

	/**
	 * 缩小图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public Bitmap resizeBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			if (width > maxWidth) {
				int pWidth = maxWidth;
				int pHeight = maxWidth * height / width;
				Bitmap result = Bitmap.createScaledBitmap(bitmap, pWidth, pHeight, false);
				bitmap.recycle();
				return result;
			}
			if (height > maxHeight) {
				int pHeight = maxHeight;
				int pWidth = maxHeight * width / height;
				Bitmap result = Bitmap.createScaledBitmap(bitmap, pWidth, pHeight, false);
				bitmap.recycle();
				return result;
			}
		}
		return bitmap;
	}
	
	public void uploadImage(Bitmap bitmap, IQiniuUploadUitlsListener listener) {

		saveBitmapToJpegFile(bitmap, tempJpeg);
		uploadImage(tempJpeg, listener);
	}

	public void uploadImage(Bitmap bitmap, String imgUrl, IQiniuUploadUitlsListener listener) {

		saveBitmapToJpegFile(bitmap, imgUrl);
		uploadImage(imgUrl, listener);
	}

	public void uploadImage(String filePath, final IQiniuUploadUitlsListener listener) {
		final String fileUrlUUID = filePath.replace(Constant.SonSDCardD, "");// getFileUrlUUID();
		String token = getToken();
		if (token == null) {
			if (listener != null) {
				listener.onError(-1, "token is null");
			}
			return;
		}
		uploadManager.put(filePath, fileUrlUUID, token, new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				System.out.println("debug:info = " + info + ",response = " + response);
				if (info != null && info.statusCode == 200) {// 上传成功
					String fileRealUrl = getRealUrl(fileUrlUUID);
					if (listener != null) {
						listener.onSucess(fileRealUrl);
					}
				} else {
					if (listener != null) {
						listener.onError(info.statusCode, info.error);
					}
				}
			}
		}, new UploadOptions(null, null, false, new UpProgressHandler() {
			public void progress(String key, double percent) {
				if (listener != null) {
					listener.onProgress((int) (percent * 100));
				}
			}
		}, null));

	}

	/**
	 * 生成远程文件路径（全局唯一）
	 * 
	 * @return
	 */
	public static String getFileUrlUUID() {
		String filePath = "_" + System.currentTimeMillis() + "_" + (new Random().nextInt(500000)) + "_"
				+ (new Random().nextInt(10000));
		String temp = filePath.replace(".", "0");
		return temp + ".jpg";
	}

	private String getRealUrl(String fileUrlUUID) {
		String filePath = "http://" + bucketName + ".qiniudn.com/" + fileUrlUUID;
		return filePath;
	}

	/**
	 * 获取token 本地生成
	 * 
	 * @return
	 */
	private String getToken() {
		Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);
		PutPolicy putPolicy = new PutPolicy(bucketName);
		putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
		try {
			// http://7vikpk.com1.z0.glb.clouddn.com/MI%20PAD__1423892819777__103202_8092
			String uptoken = putPolicy.token(mac);
			System.out.println("debug:uptoken = " + uptoken);
			return uptoken;
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
