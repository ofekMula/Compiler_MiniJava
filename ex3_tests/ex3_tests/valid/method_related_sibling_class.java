class Main {
	public static void main(String[] args) {
		System.out.println(1);
	}
}

class Shared {
	int theMethod() {
		return 1;
	}

}
class A extends Shared {
}
class B extends A {
	int theMethod() {
		return 2;
	}

}
class C extends A {
	int theMethod() {
		return 3;
	}

}
