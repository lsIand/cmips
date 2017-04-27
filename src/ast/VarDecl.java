package ast;

import java.util.ArrayList;

public class VarDecl implements ASTNode {
    public Type type;
    public final String varName;
    
    public String funname;
    
    public int offset;
    public ArrayList<Integer> address;
    public int size;

    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
	    offset = 0;
	    address = new ArrayList<Integer>();
	    size = 0;
    }

     public <T> T accept(ASTVisitor<T> v) {
	return v.visitVarDecl(this);
    }
}
