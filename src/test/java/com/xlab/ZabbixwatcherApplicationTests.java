package com.xlab;

import com.xlab.dao.InfluxRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZabbixwatcherApplicationTests {

	@Autowired
	private InfluxRepository influxRepository;
	@Test
	public void contextLoads() {
	}

	@Test
	public void readCpu(){
		influxRepository.readcpu("zabbix");
	}
}
