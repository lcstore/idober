package com.lezo.idober.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TaskUtils {

	public static JSONObject withParam(JSONObject taskObject, String key, String value) {
		if (StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("param key can not be empty");
		}
		JSONObject argsObject = taskObject.getJSONObject("args");
		if (argsObject == null) {
			argsObject = new JSONObject();
			taskObject.put("args", argsObject);
		}
		argsObject.put(key, value);
		return taskObject;
	}

	public static int createTasks(JSONArray taskArray) throws Exception {
		if (taskArray == null || taskArray.size() < 1) {
			return 400;
		}
		String url = "http://www.lezomao.com:8090/taskmgr/createtasks";
		String referrer = "http://www.lezomao.com/idober";
		Response resp =
				Jsoup.connect(url).referrer(referrer).method(Method.POST).data("tasks", taskArray.toJSONString())
						.ignoreContentType(true).execute();
		return resp.statusCode();
	}
}
