package com.shofq.springcache.config.cluster;

import io.lettuce.core.resource.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * Reconnect delay based on business
 */
public class ReconnectDelay extends Delay {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectDelay.class);

	private List<Duration> durationList;

	public static final String DEFAULT_DURATION_LIST_STR = "1ms,2ms,4ms,8ms,16ms,32ms,64ms,128ms,256ms," +
			"1s,1s,1s,1s,500ms,500ms,500ms,500ms,1s,1s,1s,1s,1s,1s,5s,5s,5s,30s";

	/**
	 * Default constructor
	 *
	 * @param durationList Duration list
	 */
	@Autowired
	public ReconnectDelay(List<Duration> durationList)  {

		this.durationList = durationList;
	}

	@Override
	public Duration createDelay(long attempt) {
		int attemptInt = (int) attempt;

		if (attemptInt < 1) {
			attemptInt = 1;
		} else if (attemptInt > durationList.size()) {
			attemptInt = durationList.size();
		}

		LOGGER.info("Reconnect attempt: " + attempt);
		return durationList.get(attemptInt - 1);
	}

	/**
	 * Get reconnect delay list from properties
	 *
	 * @param reconnectDelayStr Reconnect delay list as string
	 * @return List<Duration>
	 */
	public static List<Duration> getReconnectDelayList(String reconnectDelayStr) {
		List<Duration> reconnectDelayList = new ArrayList<>();
		String[] splittedDelayArray = reconnectDelayStr.split(",");
		ApplicationConversionService applicationConversionService = new ApplicationConversionService();

		for (String splitedDelay : splittedDelayArray) {
			Duration duration = applicationConversionService.convert(splitedDelay.trim(), Duration.class);
			reconnectDelayList.add(duration);
		}
		return reconnectDelayList;
	}
}
