package springv2.demo.action;


import springv2.demo.service.IDemoService;
import springv2.farmework.annotation.Autowired;
import springv2.farmework.annotation.Controller;
import springv2.farmework.annotation.RequestMapping;

@Controller("my")
public class MyAction {

		@Autowired
		IDemoService demoService;
	
		@RequestMapping("/first.html")
		public void query(){
			System.out.println("query");
		}

		public void test(){
			System.out.println(demoService);
		}
	
}
