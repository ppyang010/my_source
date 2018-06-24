package com.ccy.springv1.demo.service.impl;


import com.ccy.springv1.demo.service.IDemoService;
import com.ccy.springv1.spring.annotation.Service;

@Service
public class DemoService implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}
