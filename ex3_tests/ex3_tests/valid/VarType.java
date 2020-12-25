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
        b = true;

	    System.out.println(x);

	    return 0;
	}
}
