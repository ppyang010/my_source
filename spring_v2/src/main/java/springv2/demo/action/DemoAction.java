package springv2.demo.action;


import springv2.demo.service.IDemoService;
import springv2.farmework.annotation.Autowired;
import springv2.farmework.annotation.Controller;
import springv2.farmework.annotation.RequestMapping;
import springv2.farmework.annotation.RequestParam;
import springv2.farmework.webmvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/demo")
public class DemoAction {
	
	@Autowired
	private IDemoService demoService;
	
	@RequestMapping("/first.html")
	public ModelAndView query(HttpServletRequest req, HttpServletResponse resp,
							  @RequestParam("name") String name){
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("teacher", "ccy");
		model.put("data", "2018年7月16日 23:56:04");
		model.put("token", "123456");
		return new ModelAndView("first.html",model);
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
