package se.inera.statistikapi;

import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.test.context.web.WebAppConfiguration;
import se.inera.statistikapi.Application;

/**
 * Test class for Application
 *
 * @author Pär Lindkvist
 */
@ActiveProfiles(profiles = "dev")
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes =Application.class)
@WebAppConfiguration
public class ApplicationTest {

	@Test
	public void contextLoads() {
	}

}
