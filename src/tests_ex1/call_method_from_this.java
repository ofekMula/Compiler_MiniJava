//class Main {
//	public static void main(String[] args) {
//		System.out.println((new class1()).super_method());
//	}
//}
//
//class class1 {
//	int other_method(int num) {
//		return 1;
//	}
//
//	int super_method(int num) {
//		class1 e;
//		int x;
//		e = new class1();
//		x = (this).other_method(1);
//		return x;
//	}
//
//}
//
//class class2 extends class1 {
//	int other_method() {
//		int x;
//		x = (this).other_method(1);
//		return 1;
//	}
//
//}
//
