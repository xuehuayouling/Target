package com.ruili.target.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description: fastjson alibaba 将对象转换为Json对象
 * @author: YangShengqing
 * @createTime: 2016年2月22日 下午3:09:44
 * @Company: 奥克斯集团有限公司
 * @reviser: YangShengqing
 * @reviseTime: 2016年2月22日 下午3:09:44
 * @version: v1.0
 */
public class JsonUtil {

	/**
	 * 将对象转换json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJSONString(Object obj) {
		String json = "";
		if (obj instanceof ArrayList) {
			json = JSONArray.toJSONString(obj);
		} else {
			json = JSON.toJSONString(obj);
		}
		return json;
	}

	/**
	 * 将JSON串转换成List,JSON串格式如：[{"id":1,"name":"LiLei"}]，不能缺少'[ ]',否则会报异常
	 * 
	 * @param text
	 *            JSON串
	 * @param clazz
	 *            目标Class
	 * @return 如果text为null或者""或者clazz为null，则返回null，否则返回List。
	 */
	public static <T> List<T> parseArray(String text, Class<T> clazz) {
		if (TextUtil.isEmpty(text) || null == clazz) {
			return null;
		}
		return JSON.parseArray(text, clazz);
	}

	/**
	 * 将JSON串中的部分数据转换成List,JSON串格式如：
	 * {"id":1,"name":"LiLei","scores":[{"name":"语文","result":85}]}，
	 * 不能最外围不能用'[ ]'包含,否则会报异常
	 * 
	 * @param text
	 *            JSON串
	 * @param specialKey
	 *            指定的key名称
	 * @param clazz
	 *            目标Class
	 * @return 如果出现一下情况则返回null：1.text为null或者""; 2.@specialKey为null或者"";
	 *         3.clazz为null.
	 * 
	 */
	public static <T> List<T> parseSpecialArray(String text, String specialKey, Class<T> clazz) {
		if (TextUtil.isEmpty(text) || TextUtil.isEmpty(specialKey) || null == clazz) {
			return null;
		}
		JSONArray result = JSON.parseObject(text).getJSONArray(specialKey);
		if (null == result) {
			return null;
		}
		return JSON.parseArray(result.toJSONString(), clazz);
	}

	/**
	 * 转成具体实体对象
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> T parseObject(String text, Class<T> clazz) {
		if (TextUtil.isEmpty(text) || null == clazz) {
			return null;
		}
		return JSON.parseObject(text, clazz);
	}
	
	/**
	 * 转成具体实体对象集 格式：[model.PatientYdhlDTO@12a0f6c]
	 * @param <T>
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> T parseSpecialObject(String text, String specialKey, Class<T> clazz) {
		if (TextUtil.isEmpty(text) || TextUtil.isEmpty(specialKey) || null == clazz) {
			return null;
		}
		JSONObject result = JSON.parseObject(text).getJSONObject(specialKey);
		if (null == result) {
			return null;
		}
		return JSON.parseObject(result.toJSONString(), clazz);
	}

	/**
	 * 转成对象数据集 数据集格式如：[{"age":0,"charge":10}, {"age":9,"charge":10}]
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List toList(String json) {
		if (TextUtil.isEmpty(json)) {
			return null;
		}
		return JSONObject.parseArray(json, List.class);
	}

}
