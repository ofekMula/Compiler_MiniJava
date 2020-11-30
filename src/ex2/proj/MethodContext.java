package ex2.proj;

import java.util.HashMap;
import java.util.Map;

public class MethodContext {
    private int registerCnt;
    private int labelCnt;
    Map<String, String> regTypesMap; // < %_0 : i32 >

    MethodContext(){
        registerCnt = 0;
        labelCnt = 0;
        regTypesMap = new HashMap<>();
    }

    String getNewReg() {
        int currCnt = registerCnt;
        registerCnt++;
        return "%_" + currCnt;
    }

    String getNewLabel(String name) {
        int currCnt = labelCnt;
        labelCnt++;
        return name+ "_" + currCnt;
    }
}
