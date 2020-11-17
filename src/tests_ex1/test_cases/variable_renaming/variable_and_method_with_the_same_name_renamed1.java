//package tests_ex1.test_cases.variable_renaming;
//
//class Main {
//    public static void main(String[] args) {
//        System.out.println(1);
//    }
//}
//
//class Shared {
//    int renamedThing;
//
//    public int theThing() {
//        return renamedThing;
//    }
//}
//
//class A extends Shared { }
//
//class B extends A {
//    public int theThing() {
//        return renamedThing;
//    }
//}
//
//class C extends A {
//    public int theThing() {
//        return renamedThing;
//    }
//}
//
//class D extends A {
//    public int theThing() {
//        int theThing;
//
//        return theThing;
//    }
//}
//
//class E extends A {
//    public int theThing(int theThing) {
//        return theThing;
//    }
//}
