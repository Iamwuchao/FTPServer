package lifecycle;

public class LifecycleEvent {
	private Object data;
	private String type;
	public LifecycleEvent(Object data,String type){
		this.data = data;
		this.type = type;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
