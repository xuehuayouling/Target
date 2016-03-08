package com.ruili.target.utils;

public class Constant {

//	public static final String BASE_URL = "http://wqjms4gkkh.proxy.qqbrowser.cc/ruiliqc";
	public static final String BASE_URL = "http://121.40.224.107/ruiliqc";
	public static final String USER_LOGIN = BASE_URL + "/api/v1/user/login";
	public static final String SonSDCardD = "/storage/emulated/0/DCIM/Camera/";
	public static final String getPicUrl(String url) {
		if (TextUtil.isEmpty(url)) {
			return url;
		}
		return url.replace("http://ruiliqc.qiniudn.com/", "http://7xrluz.com2.z0.glb.qiniucdn.com/");
	}
}
