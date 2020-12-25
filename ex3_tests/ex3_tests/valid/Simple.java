class Main {
	public static void main(String[] a) {
	    System.out.println(new Simple().bar());
	}
}

class Simple {
	public int bar() {
	    int x;

        x = 10;

	    System.out.println(x);

	    return 0;
	}
}
