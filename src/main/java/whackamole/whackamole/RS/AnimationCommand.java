package whackamole.whackamole.RS;

import whackamole.whackamole.Utils.Logger;

public class AnimationCommand {
    public Float colorRed;
    public Float colorGreen;
    public Float colorBlue;
    public Float Delta1;
    public Float Delta2;
    public Float Delta3;
    public Float offSetX;
    public Float offSetY;
    public Float offSetZ;
    public Float Scale;
    public Float Speed;
    public int Count;

    AnimationCommand(String commandString, String Animation) {
        try {
            String[] Array      =       commandString.split(" ");
            this.colorRed       =       Float.valueOf(Array[0]);
            this.colorGreen     =       Float.valueOf(Array[1]);
            this.colorBlue      =       Float.valueOf(Array[2]);
            this.Scale          =       Float.valueOf(Array[3]);
            this.Delta1         =       Float.valueOf(Array[4]);
            this.Delta2         =       Float.valueOf(Array[5]);
            this.Delta3         =       Float.valueOf(Array[6]);
            this.offSetX        =       Float.valueOf(Array[7]);
            this.offSetY        =       Float.valueOf(Array[8]);
            this.offSetZ        =       Float.valueOf(Array[9]);
            this.Speed          =       Float.valueOf(Array[10]);
            this.Count          =       Integer.valueOf(Array[11]);
        } catch (Exception e) {
            Logger.error("Could now Load " + Animation + " Due to invalid command"); // TODO: make this work....
        }
    }
}
