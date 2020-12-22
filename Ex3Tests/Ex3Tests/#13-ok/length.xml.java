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
		int[] z;
		int y;
		x = new B();
		y = 1;
		return (z).length;
	}

}

