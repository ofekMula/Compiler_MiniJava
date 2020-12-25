class Main {
	public static void main(String[] a) {
		System.out.println(0);
	}
}

class Base {
	boolean run(boolean x) {
		return x < new Base();
	}

}
