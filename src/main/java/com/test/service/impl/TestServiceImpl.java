package com.test.service.impl;

import com.test.service.TestService;
import org.jvnet.hk2.annotations.Service;

@Service
public class TestServiceImpl implements TestService {

	@Override
	public String getMessage () {
		return "Hello World!";
	}
}
