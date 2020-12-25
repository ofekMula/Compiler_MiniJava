class Main {
	public static void main(String[] args) {
		System.out.println(0);
	}
}

class Base {
}
class Parent {
	Parent run(int x, int y) {
		return new Parent();
	}

}
class Child extends Parent {
	Parent run(int x, int y) {
		return new Parent();
	}

}
