package com.test.routes;

import com.test.service.TestService;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Log4j2
@Path("/resource")
public class TestResource {

	@Inject
	private TestService testService;


	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testMethod () {

		logger.info("Received request to get Test Resource");

		return testService.getMessage();
	}
}
