package springv2.demo.action;


import springv2.demo.service.IDemoService;
import springv2.farmework.annotation.Autowired;
import springv2.farmework.annotation.Controller;
import springv2.farmework.annotation.RequestMapping;
import springv2.farmework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/demo")
public class DemoAction {
	
	@Autowired
	private IDemoService demoService;
	
	@RequestMapping("/query.json")
	public void query(HttpServletRequest req, HttpServletResponse resp,
                      @RequestParam("name") String name){
		String result = demoService.get(name);
		System.out.println(result);
//		try {
//			resp.getWriter().write(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	@RequestMapping("/edit.json")
	public void edit(HttpServletRequest req, HttpServletResponse resp, Integer id){

	}
	
}
