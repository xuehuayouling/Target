package com.ruili.target.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import android.test.AndroidTestCase;

public class QiniuUploadManagerTest extends AndroidTestCase {

	private static final String TAG = QiniuUploadManagerTest.class.getSimpleName();
	private QiniuUploadManager mManager;
	private CountDownLatch signal = new CountDownLatch(1);
	private static final String KEY_RESULT_ACTUAL = "actual";
	private static final String KEY_RESULT_DONE = "done";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mManager = new QiniuUploadManager();
	}

	 public void testMultipleUpload1() {
		 ConcurrentHashMap<String, String> result = multipleUpload(null);
			String expected = "需要上传的列表为空!";
			if (result.containsKey(KEY_RESULT_ACTUAL)) {
				AndroidTestCase.assertEquals(expected, result.get(KEY_RESULT_ACTUAL));
			} else if (result.containsKey(KEY_RESULT_DONE)) {
				AndroidTestCase.fail("应该上传失败的，而不是返回成功");
			} else {
				AndroidTestCase.fail("测试案例有bug，请检查");
			}
	 }
	
	public void testMultipleUpload2() {
		List<Map<String, String>> list = new ArrayList<>();
		ConcurrentHashMap<String, String> result = multipleUpload(list);
		String expected = "需要上传的列表为空!";
		if (result.containsKey(KEY_RESULT_ACTUAL)) {
			AndroidTestCase.assertEquals(expected, result.get(KEY_RESULT_ACTUAL));
		} else if (result.containsKey(KEY_RESULT_DONE)) {
			AndroidTestCase.fail("应该上传失败的，而不是返回成功");
		} else {
			AndroidTestCase.fail("测试案例有bug，请检查");
		}
	}

	public void testMultipleUpload3() {
		List<Map<String, String>> list = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put(QiniuUploadManager.KEY_FILE_PATH, "file1");
		map.put(QiniuUploadManager.KEY_SAVE_NAME, "savename1");
		list.add(map);
		list.add(map);
		ConcurrentHashMap<String, String> result = multipleUpload(list);
		String expected = "有文件保存到服务器上的名字相同！";
		if (result.containsKey(KEY_RESULT_ACTUAL)) {
			AndroidTestCase.assertEquals(expected, result.get(KEY_RESULT_ACTUAL));
		} else if (result.containsKey(KEY_RESULT_DONE)) {
			AndroidTestCase.fail("应该上传失败的，而不是返回成功");
		} else {
			AndroidTestCase.fail("测试案例有bug，请检查");
		}
	}

	public void testMultipleUpload4() {
		List<Map<String, String>> list = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		String path = Constant.EXTERNAL_STORAGE_DIRECTORY + "1457503645407.jpg";
		File dir = new File(path);
		if (!dir.exists()) {
			AndroidTestCase.fail("文件不存在，请先创建文件");
		}
		map.put(QiniuUploadManager.KEY_FILE_PATH, path);
		map.put(QiniuUploadManager.KEY_SAVE_NAME, String.valueOf(System.currentTimeMillis()));
		list.add(map);
		ConcurrentHashMap<String, String> result = multipleUpload(list);
		if (result.containsKey(KEY_RESULT_ACTUAL)) {
			AndroidTestCase.fail("应该上传成功的，但是却调用了上传失败的回调函数");
		} else if (!result.containsKey(KEY_RESULT_DONE)) {
			AndroidTestCase.fail("应该上传成功的，但是没有调用上传成功的回调函数");
		}
	}

	private ConcurrentHashMap<String, String> multipleUpload(List<Map<String, String>> list) {
		final ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
		mManager.multipleUpload(list, new IQiniuUploadManagerListener() {

			@Override
			public void onMultipleUploadFail(String reason) {
				String actual = reason;
				result.put(KEY_RESULT_ACTUAL, actual);
				signal.countDown();
			}

			@Override
			public void onMultipleUploadDone() {
				result.put(KEY_RESULT_DONE, "yes");
				signal.countDown();
			}
		});
		try {
			signal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

}
