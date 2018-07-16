package springv2.farmework.webmvc;

import java.util.Map;

public class ModelAndView {

    private  String view;
    private  Map model;

    public ModelAndView(String viewName, Map<String, ?> model) {
       this.view = viewName;
       this.model = model;
    }


    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map getModel() {
        return model;
    }

    public void setModel(Map model) {
        this.model = model;
    }
}
