//package tests_ex1.test_cases.variable_renaming;
//
//class Main {
//    public static void main(String[] args) {
//        System.out.println(1);
//    }
//}
//
//class A { }
//
//class B extends A {
//    int[] theVar;
//
//    public int foo() {
//        return theVar.length;
//    }
//}
//
//class C extends A {
//    int[] renamedVar;
//
//    public int foo() {
//        return renamedVar.length;
//    }
//}
//
//class D extends C {
//    public int bar(int anotherVar) {
//        System.out.println(renamedVar[renamedVar.length] - anotherVar);
//        return renamedVar.length - anotherVar;
//    }
//
//}
