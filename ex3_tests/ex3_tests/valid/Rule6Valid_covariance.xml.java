class Main {
	public static void main(String[] args) {
		System.out.println(0);
	}
}

class Base {
}
class Parent {
	Child run(int x, int y) {
		return new Child();
	}

}
class Child extends Parent {
	Child run(int x, int y) {
		return new Child();
	}

}
