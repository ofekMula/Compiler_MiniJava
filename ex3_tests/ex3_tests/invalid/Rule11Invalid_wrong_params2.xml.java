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
		d = new Derived();
		System.out.println(b.set(1));
		b = d;
		System.out.println(b.set());
		System.out.println(d.get());
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
