//package tests_ex1.test_cases.method_renaming;
//
//class Main {
//    public static void main(String[] args) {
//        System.out.println(1);
//    }
//}
//
//class Shared {
//    int theThing;
//
//    public int renamedThing() {
//        return theThing;
//    }
//}
//
//class A extends Shared { }
//
//class B extends A {
//    public int renamedThing() {
//        return theThing;
//    }
//}
//
//class C extends A {
//    public int renamedThing() {
//        return theThing;
//    }
//}
//
//class D extends A {
//    public int renamedThing() {
//        int theThing;
//
//        return theThing;
//    }
//}
//
//class E extends A {
//    public int renamedThing(int theThing) {
//        return theThing;
//    }
//}
