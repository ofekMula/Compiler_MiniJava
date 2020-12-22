class Main {
	public static void main(String[] args) {
		System.out.println(1);
	}
}

class A {
}

class B extends A {
	int theMethod() {
		return 1;
	}

}

class C extends A {
	int theMethod() {
		return 1;
	}

}

class D extends C {
	int anotherMethod(B b) {
		int max;
		int y;
		while (true) {			{
				if (true)
					y = 1;
				else
					y = 2;

				max = y;

			}

		}
		return y;
	}

}

