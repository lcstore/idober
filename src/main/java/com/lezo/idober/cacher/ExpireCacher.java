package com.lezo.idober.cacher;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import com.google.common.collect.Maps;

@Log4j
public class ExpireCacher {
	private static final ExpireCacher instance = new ExpireCacher();
	private Map<String, ExpireValue> expireMap = Maps.newConcurrentMap();

	public static ExpireCacher getInstance() {
		return instance;
	}

	private ExpireCacher() {
		Timer timer = new Timer("expireTrigger", true);
		long delay = 1000;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Iterator<Entry<String, ExpireValue>> it = expireMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, ExpireValue> entry = it.next();
					if (entry.getValue().getExpire() <= System.currentTimeMillis()) {
						it.remove();
						log.info("expire key:" + entry.getKey());
					}
				}
			}
		}, delay);
	}

	public Object getValue(String key) {
		ExpireValue value = expireMap.get(key);
		if (value != null && value.getExpire() >= System.currentTimeMillis()) {
			return value.getValue();
		}
		return null;
	}

	public Object putValue(String key, Object value, long expire) {
		if (key == null || expire < System.currentTimeMillis()) {
			return null;
		}
		ExpireValue valObj = new ExpireValue();
		valObj.setValue(value);
		valObj.setExpire(expire);
		return expireMap.put(key, valObj);
	}

	public Object removeValue(String key) {
		return expireMap.remove(key);
	}

	@Getter
	@Setter
	private class ExpireValue {
		private Object value;
		private long expire;
	}
}
