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

	int foo() {
		theVar = new A();
		return 1;
	}

}
