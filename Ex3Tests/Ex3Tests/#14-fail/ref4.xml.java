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
		int[] x;
		Tree t;
		t = (new Tree()).getLeft();
		return (x)[0];
	}

}

