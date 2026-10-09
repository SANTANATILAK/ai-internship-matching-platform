package com.tilak.internship_platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "opportunities.scheduler.enabled=false")
class InternshipPlatformApplicationTests {

	@Test
	void contextLoads() {
	}

}
