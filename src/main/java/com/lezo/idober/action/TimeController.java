package com.lezo.idober.action;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.timer.FillTorrent2MovieTimer;
import com.lezo.idober.timer.OnlineTorrentMovieTimer;
import com.lezo.idober.timer.QueryTorrent4NewMovieTimer;
import com.lezo.idober.timer.UnifyRegionTimer;

@Log4j
@Controller
@RequestMapping("time")
public class TimeController extends BaseController {
	@Autowired
	private FillTorrent2MovieTimer fillTorrent2MovieTimer;
	@Autowired
	private UnifyRegionTimer unifyRegionTimer;
	@Autowired
	private OnlineTorrentMovieTimer onlineTorrentMovieTimer;
	@Autowired
	private QueryTorrent4NewMovieTimer queryTorrent4NewMovieTimer;

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
	@RequestMapping(value = { "query" },
			method = RequestMethod.GET)
	public String unifyRegionTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		queryTorrent4NewMovieTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,queryTorrent4NewMovieTimer,cost:" + costMills);
		return "OK";
	}

	@ResponseBody
	@RequestMapping(value = { "move" },
			method = RequestMethod.GET)
	public String onlineTorrentMovieTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		onlineTorrentMovieTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,onlineTorrentMovieTimer,cost:" + costMills);
		return "OK";
	}
}
