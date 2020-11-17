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
//    public int renamedMethod() {
//        return 1;
//    }
//}
//
//class C extends A {
//    public int theMethod() {
//        return 1;
//    }
//}
//
//class D extends C {
//    public int[] anotherMethod(B b) {
//        int[] max;
//
//        max = new int[this.theMethod() * b.renamedMethod()]
//
//        if (b.renamedMethod() < this.theMethod()) {
//            max[b.renamedMethod()] = this.theMethod();
//        } else {
//            max[this.theMethod()] = b.renamedMethod();
//        }
//
//        return max[this.theMethod() * b.renamedMethod()];
//    }
//}
