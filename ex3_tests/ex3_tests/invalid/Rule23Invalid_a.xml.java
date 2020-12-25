class Main {
	public static void main(String[] args) {
		System.out.println(new Arr().iota(false));
	}
}

class Arr {
	int iota(boolean size) {
		int[] arr;
		int i;
		arr = new int[2];
		i = 0;
		arr[size] = i + 1;
		return arr[0];
	}

}
