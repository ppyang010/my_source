package springv2.demo.service.impl;


import springv2.demo.service.IDemoService;
import springv2.farmework.annotation.Service;

@Service
public class DemoService implements IDemoService {

	public String get(String name) {
		System.out.println("invoker get ");
		return "My name is " + name;
	}

}
