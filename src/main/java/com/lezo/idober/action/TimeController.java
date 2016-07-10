package com.lezo.idober.action;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.timer.FillTorrent2MovieTimer;
import com.lezo.idober.timer.UnifyRegionTimer;

@Log4j
@Controller
@RequestMapping("time")
public class TimeController extends BaseController {
	@Autowired
	private FillTorrent2MovieTimer fillTorrent2MovieTimer;
	@Autowired
	private UnifyRegionTimer unifyRegionTimer;

	@ResponseBody
	@RequestMapping(value = { "torrent" },
			method = RequestMethod.GET)
	public String fillTorrent2MovieTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		fillTorrent2MovieTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,fillTorrent2MovieTimer,cost:" + costMills);
		return "OK";
	}

	@ResponseBody
	@RequestMapping(value = { "region" },
			method = RequestMethod.GET)
	public String unifyRegionTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		unifyRegionTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,unifyRegionTimer,cost:" + costMills);
		return "OK";
	}
}
