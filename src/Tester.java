import com.zero1.realjavascript.RJSBridge;

public class Tester {

	int b, c;
	private double a;
	private double d;
	private boolean bool = true;
	private String n = null;
	private static Tester t = new Tester();
	private static Tester t2 = new Tester();
	private String string = "sanju rocks! :D";
	private static Tester[] array = { t, t2 };
	private static String[] sa = { "Hello", "Sanju", "Nalla", "Irukiya" };
	private static String[] sa2 = { "Hello", null, "Nalla", "Irukiya" };
	private static int[] ia = { 1, 2, 34 };
	private static boolean[] ba = { false, true };

	public static void main(String[] args) throws Exception {
		test();
		t.a = 13;
		t.b = 4;
		t.c = 2;
		t2.a = 5;
		t.runRJS();
	}

	private static void test() {
	}

	private boolean getBool(boolean b) {
		return !b;
	}

	private String getBoolString() {
		return "Bool";
	}

	private String getSeString() {
		return "se";
	}

	private String getFalString() {
		return "fal";
	}

	private void runRJS() {
		String command = "var v={11}:{compute (call getNum c,b)- b} and call show &v<1>";
		long time1 = System.currentTimeMillis();
		RJSBridge.interpret(this, command);
		long time2 = System.currentTimeMillis();
		long diff1 = time2 - time1;
		System.out.println("\nTook " + diff1 / 1000d + " seconds...\n");
	}

	private static void hi(String name) {
		System.out.println("Hi " + name);
	}

	private void printHello() {
		System.out.println("Hello World!");
	}

	private void print(Object num) {
		System.out.println("Hello " + num);
	}

	private void show(Object obj) {
		System.out.println(obj);
	}

	private int getNum(int a, int b) {
		return a * b;
	}

	private String getToString(Object obj) {
		return obj.toString();
	}

	public Tester(double a) {
		// TODO Auto-generated constructor stub
		this.a = a;
	}

	public Tester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + a;
	}

}
