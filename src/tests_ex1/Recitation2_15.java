//class Main {
//	public static void main(String[] args) {
//		System.out.println((new A()).bar());
//	}
//}
//
//class A {
//	int bar(int x, int y) {
//		A e;
//		e = new A();
//		return (e).bar();
//	}
//
//}
//
//class B extends A {
//	int bar(int x, int y) {
//		return (new B()).bar();
//	}
//
//}
//
//class C extends B {
//	int bar() {
//		return (new B()).bar();
//	}
//
//}
//
//class otherClass {
//	int foo() {
//		B x;
//		return (x).bar(1, 2);
//	}
//
//}
//
