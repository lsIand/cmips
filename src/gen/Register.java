package gen;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cdubach
 */
public class Register {


    /*
     * definition of registers
     */

    public static final Register v0 = new Register(2,"v0");
    public static final Register[] paramRegs = {
            new Register(4,"a0"),
            new Register(5,"a1"),
            new Register(6,"a2"),
            new Register(7,"a3")};

    public static final List<Register> tmpRegs = new ArrayList<Register>();
    static {
        for (int i=8; i<=15; i++)
            tmpRegs.add(new Register(i,"t"+(i-8)));
        for (int i=16; i<=23; i++)
            tmpRegs.add(new Register(i,"s"+(i-16)));
        for (int i=24; i<=25; i++)
            tmpRegs.add(new Register(i,"t"+(i-24+8)));
    }

    
    public static final Register gp = new Register(28,"gp");
    public static final Register sp = new Register(29,"sp");
    public static final Register fp = new Register(30,"fp");
    public static final Register ra = new Register(31,"ra");


    private final int num;      // register number
    private final String name;  // register name


    private Register(int num, String name) {
        this.num = num;
        this.name = name;
    }

    public String toString() {
        return "$"+name;
    }

}
