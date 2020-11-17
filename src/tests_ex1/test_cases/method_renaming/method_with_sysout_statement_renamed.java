//package tests_ex1.test_cases.method_renaming;
//
//class Main {
//    public static void main(String[] args) {
//        System.out.println(1);
//    }
//}
//
//class A {}
//
//class B extends A {
//    public int theMethod() {
//        return 1;
//    }
//}
//
//class C extends A {
//    public int renamedMethod() {
//        return 1;
//    }
//}
//
//class D extends C {
//    public int anotherMethod(B b) {
//        System.out.println(this.renamedMethod() - b.theMethod());
//        return this.renamedMethod() - b.theMethod();
//    }
//}
