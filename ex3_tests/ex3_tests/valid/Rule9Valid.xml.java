class Main {
	public static void main(String[] args) {
		System.out.println(0);
	}
}

class Base {
	Child c;

	int run() {
		c = new Child();
		return 0;
	}

}
class Parent {
}
class Child extends Parent {
}
