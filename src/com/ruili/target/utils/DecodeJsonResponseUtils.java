package com.ruili.target.utils;

import com.ruili.target.R;
import com.ruili.target.entity.ResponseDTO;

import android.content.Context;
import android.util.Log;

public class DecodeJsonResponseUtils {

	public static Object decode(String response, String tag, String mark, ImmediatelyShowToast toast, Context context, Class<? extends ResponseDTO> clazz) {
		Log.d(tag, mark + " --> " + response);
		try {
			ResponseDTO dto = JsonUtil.parseObject(response, clazz);
			if (dto.isValid()) {
				return dto.getData();
			} else {
				Logger.debug(tag,  mark + " --> " + context.getString(R.string.no_valid_data) + response);
				toast.show(context.getString(R.string.no_valid_data));
			}
		} catch (Exception e) {
			Logger.debug(tag, mark + " --> " + context.getString(R.string.can_not_decode_data) + response);
			toast.show(context.getString(R.string.service_fail));
			e.printStackTrace();
		}
		return null;
	}
}
