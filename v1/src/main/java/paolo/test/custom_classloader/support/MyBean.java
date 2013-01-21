package paolo.test.custom_classloader.support;

public class MyBean {

	private static final long serialVersionUID = 1L;

	
	public static final String s = "v1";

	@Override
	public String toString() {
		return s;
	}
	
	
	
	public String extraMehtod(){
		return "extra_value";
	}

}
