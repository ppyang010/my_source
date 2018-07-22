package springv2.farmework.context;

public abstract class AbstractApplicationContext {


    //提供给子类重写
    protected void onRefresh(){
        // For subclasses: do nothing by default.
    }

    protected abstract void refreshBeanFactory();

}
