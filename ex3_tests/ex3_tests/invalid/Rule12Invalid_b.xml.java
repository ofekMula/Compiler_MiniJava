class Main {
	public static void main(String[] args) {
		System.out.println(1);
	}
}

class A {
	int run() {
		return new A().run().run();
	}

}
