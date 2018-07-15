package springv2.farmework.webmvc;

import java.util.Map;

public class ModelAndView {

    private  String view;
    private  Map model;

    public ModelAndView(String viewName, Map<String, ?> model) {
       this.view = viewName;
       this.model = model;
    }
}
