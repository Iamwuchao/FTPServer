package lifecycle;

public interface Lifecycle {
	void start();
	void stop();
	public LifecycleListener[] findLifecycleListeners();
	
}
