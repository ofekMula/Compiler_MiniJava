class Main {
	public static void main(String[] args) {
		System.out.println(new Classes().run());
	}
}

class Classes {
	int run() {
		Base b;
		Derived d;
		b = new Base();
		d = b.notExist();
		return 0;
	}

}
class Base {
	int data;

	int set(int x) {
		data = x;
		return data;
	}

	int get() {
		return data;
	}

}
class Derived extends Base {
	int set(int x) {
		data = x * 2;
		return data;
	}

}
