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
//    int renamedVar;
//
//    public int foo() {
//        return renamedVar;
//    }
//}
//
//class D extends C {
//    public int bar(int anotherVar) {
//        int[] max;
//
//        max = new int[renamedVar * anotherVar]
//
//        if (anotherVar < renamedVar) {
    //        if (anotherVar < renamedVar) {
    //            max[anotherVar] = renamedVar;
    //            x = 5;
    //        } else {
    //            max[renamedVar] = anotherVar;
    //        }
//        } else {
                //x = 6;
//            max[renamedVar] = anotherVar;
//        }
//
//        return max[renamedVar * anotherVar];
//    }
//}
