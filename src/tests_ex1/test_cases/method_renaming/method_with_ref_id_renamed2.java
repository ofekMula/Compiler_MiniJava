//class Main {
//	public static void main(String[] args) {
//		System.out.println(1);
//	}
//}
//
//class A {
//}
//
//class B extends A {
//	int renamedMethod() {
//		return 1;
//	}
//
//}
//
//class C extends A {
//	int theMethod() {
//		return 1;
//	}
//
//}
//
//class D extends C {
//}
//
//class E {
//	D d;
//
//	int anotherMethod(B b) {
//		d = new D();
//		return ((b).renamedMethod()) + ((d).theMethod());
//	}
//
//}
//
