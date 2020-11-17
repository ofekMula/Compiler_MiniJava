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
//    int theVar;
//
//    public int foo() {
//        return theVar;
//    }
//}
//
//class C extends A {
//    int theVar;
//
//    public int foo() {
//        return theVar;
//    }
//}
//
//class D extends C {
//    public int bar(int anotherVar) {
//        int max;
//
//        while (!(anotherVar && theVar)) {
//            theVar = theVar + anotherVar;
//            max = 2 * theVar;
//        }
//
//        return max;
//    }
//}
