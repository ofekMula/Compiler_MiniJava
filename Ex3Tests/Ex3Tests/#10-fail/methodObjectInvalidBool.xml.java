class Main {
	public static void main(String[] a) {
		System.out.println(3);
	}
}

class Tree {
	Tree left;

	Tree right;

	Tree getLeft() {
		return left;
	}

	Tree getRight() {
		return right;
	}

	int num() {
		return 2;
	}

	int fun() {
		boolean x;
		Tree t;
		x = 5;
		t = (new Tree()).getLeft();
		return (x).fun();
	}

}

