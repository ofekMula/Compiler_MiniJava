class Main {
	public static void main(String[] args) {
		System.out.println(1);
	}
}

class A {
}

class B extends A {
	int bar(boolean x, int y) {
		return 0;
	}

}

class otherClass {
	int foo() {
		B x;
		boolean z;
		int y;
		x = new B();
		z = true;
		y = 1;
		y = (x).bar(z, y);
		return (x).bar(true, 2);
	}

}

