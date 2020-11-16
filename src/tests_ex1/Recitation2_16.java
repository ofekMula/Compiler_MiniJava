//class Main {
//	public static void main(String[] args) {
//		System.out.println((new A()).bar());
//	}
//}
//
//class A {
//	int bar() {
//		A e;
//		e = new A();
//		return (e).bar();
//	}
//
//}
//
//class B extends A {
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
//	int bar() {
//		B x;
//		return (x).bar();
//	}
//
//}
//
