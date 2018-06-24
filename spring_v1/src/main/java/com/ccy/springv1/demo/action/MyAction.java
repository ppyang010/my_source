package com.ccy.springv1.demo.action;

import com.ccy.springv1.demo.service.IDemoService;
import com.ccy.springv1.spring.annotation.Autowired;
import com.ccy.springv1.spring.annotation.Controller;
import com.ccy.springv1.spring.annotation.RequestMapping;


@Controller("my")
public class MyAction {

		@Autowired
		IDemoService demoService;
	
		@RequestMapping("/index.html")
		public void query(){

		}

		public void test(){
			System.out.println(demoService);
		}
	
}
