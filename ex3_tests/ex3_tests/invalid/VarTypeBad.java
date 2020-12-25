class Main {
	public static void main(String[] a) {
	    System.out.println(new Simple().bar());
	}
}

class Simple {
	public int bar() {
	    int x;
	    boolean b;

        x = 10;
        b = 20;

	    System.out.println(x);

	    return 0;
	}
}
