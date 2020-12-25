class Main {
	public static void main(String[] args) {
		System.out.println(0);
	}
}

class Parent {
	int run() {
		return new Parent();
	}

}
