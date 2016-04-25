package com.ruili.target.receivers;

import java.util.List;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.ruili.target.utils.ImmediatelyShowToast;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class BaiduPushMessageReceiver extends PushMessageReceiver {

	@Override
	public void onBind(Context arg0, int arg1, String arg2, String arg3, String arg4, String arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(Context context, String message, String customContentString) {
		ImmediatelyShowToast.getInstance((Application) context.getApplicationContext())
				.show("message:" + message + ", \ncustomContentString:" + customContentString);
	}

	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

}
