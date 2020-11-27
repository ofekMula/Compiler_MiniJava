package ex2.proj;

import java.util.HashMap;
import java.util.Map;

public class MethodContext {
    private int registerCnt;
    private int labelCnt;
    Map<String, String> RegTypesMap; // < %_0 : i32 >

    MethodContext(){
        registerCnt = 0;
        labelCnt = 0;
        RegTypesMap = new HashMap<>();
    }

    public String getNewReg() {
        int currCnt = registerCnt;
        registerCnt++;
        return "%_" + currCnt;
    }
}
