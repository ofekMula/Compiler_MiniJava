class Main {
	public static void main(String[] args) {
		System.out.println(0);
	}
}

class Parent {
	int run() {
		return 0;
	}

}
class Child extends Parent {
}
class GrandChild extends Child {
	boolean run() {
		return false;
	}

}
