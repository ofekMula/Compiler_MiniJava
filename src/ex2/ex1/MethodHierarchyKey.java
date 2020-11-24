package ex2.ex1;

public class MethodHierarchyKey {
    private final String classHierarchyRoot;
    private final String methodID;

    public MethodHierarchyKey(String classHierarchyRoot, String methodID) {
        this.classHierarchyRoot = classHierarchyRoot;
        this.methodID = methodID;
    }

    public String getClassHierarchyRoot() {
        return classHierarchyRoot;
    }

    public String getMethodID() {
        return methodID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodHierarchyKey)) return false;
        MethodHierarchyKey key = (MethodHierarchyKey) o;
        return methodID.equals(key.methodID) && classHierarchyRoot.equals(key.classHierarchyRoot);
    }

    @Override
    public int hashCode() {
        return methodID.hashCode() + classHierarchyRoot.hashCode();
    }
}
