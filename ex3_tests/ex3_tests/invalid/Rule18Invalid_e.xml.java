class Main {
	public static void main(String[] args) {
		System.out.println(1);
	}
}

class A {
	int test() {
		return 1;
	}

}
class C {
	int test() {
		return 1;
	}

}
class B extends A {
	B theVar;

	A foo() {
		C c;
		c = new C();
		return c;
	}

}
