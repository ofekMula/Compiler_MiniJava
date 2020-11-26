package ex2.proj;

public class StateManager {
        private int registersCnt;
        private int labelsCnt;

        public StateManager() {
            this.registersCnt = 0;
            this.labelsCnt = 0;
        }

        public String getNewRegister() {
            String newRegString = "%_" + registersCnt;
            registersCnt++;
            return newRegString;
        }

        public String getNewLabel() {
            String newLabelString = "label_" + labelsCnt;
            labelsCnt++;
            return newLabelString;
        }

    }
}
