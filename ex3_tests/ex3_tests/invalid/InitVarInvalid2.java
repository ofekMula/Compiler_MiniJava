class Main {
	public static void main(String[] a) {
		System.out.println(3);
	}
}

class Simple {
	int fun() {
		int x;
		boolean a;
		boolean b;
		int[] arr;
		if (2 < 3) {
			{
				x = 5;
				while (x < 7) {
					a = false;

					if (x < 3) {
						b = a && true;
					} else {
						a = true;
					}
					b = a && true;
				}
			}
		} else {
			{
				a = true;
				if (a) {
					arr = new int[5];
				} else {
					arr = new int[7];
				}
				x = arr[3];
			}
		}
		return 0;
	}
}
