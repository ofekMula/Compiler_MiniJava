class Main {
	public static void main(String[] a) {
	    System.out.println(new Simple().bar());
	}
}

class Simple {
	public int bar() {
	    boolean b;
	    boolean c;
	    int x;
	    
        b = false;
        c = true;

        if (b && c)
            x = 0;
        else
            x = 1;

	    System.out.println(x);

	    return 0;
	}
}
