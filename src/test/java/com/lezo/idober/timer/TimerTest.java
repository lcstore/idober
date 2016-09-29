package com.lezo.idober.timer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.idober.config.AppConfig;
import com.lezo.idober.solr.BaseTest;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class TimerTest extends BaseTest {
	@Autowired
	private FillTorrent2MovieTimer fillTorrent2MovieTimer;
	@Autowired
	private FillOldId2MovieTimer fillOldId2MovieTimer;
	@Autowired
	private UnifyRegionTimer unifyRegionTimer;
	@Autowired
	private OnlineTorrentMovieTimer onlineTorrentMovieTimer;
	@Autowired
	private AssembleIdMovieTimer assembleIdMovieTimer;
	@Autowired
	private QueryTorrent4NewMovieTimer queryTorrent4NewMovieTimer;

	@Test
	public void testTimer() {
		AppConfig appConfig = SpringBeanUtils.getBean(AppConfig.class);
		appConfig.setEnvName("dev");
		fillTorrent2MovieTimer.run();
		// fillOldId2MovieTimer.run();
		// unifyRegionTimer.run();
//		 onlineTorrentMovieTimer.run();
		// assembleIdMovieTimer.run();
		// queryTorrent4NewMovieTimer.run();
	}

}
