package springv2.demo.action;


import springv2.demo.service.IDemoService;
import springv2.farmework.annotation.Autowired;
import springv2.farmework.annotation.Controller;
import springv2.farmework.annotation.RequestMapping;
import springv2.farmework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller("my")
public class MyAction {

		@Autowired
		IDemoService demoService;
	
		@RequestMapping("/query")
		public void query(HttpServletRequest req, HttpServletResponse resp,
						  String test,@RequestParam("name") String name,@RequestParam("text") String t){
			System.out.println("query");
			String s = demoService.get(name) + " is "+t;
			out(resp,s);
		}

		public void test(){
			System.out.println(demoService);
		}

	private String out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}



}
