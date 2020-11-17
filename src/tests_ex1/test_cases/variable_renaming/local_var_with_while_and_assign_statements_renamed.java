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
//        int renamedVar;
//        int max;
//
//        while (!(anotherVar && renamedVar)) {
//            renamedVar = renamedVar + anotherVar;
//            max = 2 * renamedVar;
//        }
//
//        return max;
//    }
//}
