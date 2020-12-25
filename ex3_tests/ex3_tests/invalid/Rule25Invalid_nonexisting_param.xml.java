class Main {
	public static void main(String[] args) {
		System.out.println(new Arr().iota(false));
	}
}

class Arr {
	int iota(boolean size) {
		int[] arr;
		int i;
		arr = new int[false];
		i = 0;
		arr[i] = 0;
		return arr[0];
	}

}
