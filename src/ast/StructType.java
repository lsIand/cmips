package ast;

/**
 * @author cdubach
 */

import java.util.List;

public class StructType implements Type {

	public StructType refersto;
	public int structSize;
	
	public String name;
	public final List<VarDecl> varDecls;
    
	public StructType(String name, List<VarDecl> varDecls) {
		
		this.name = name;
		this.varDecls = varDecls;
		
		this.structSize = 0;
		
	}
	
	public String toString() {
		
		return ("structholder");
		
	}
	
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

	@Override
	public boolean equals1(Type t) {
		if (t instanceof StructType) {
			StructType s = (StructType) t;
						
			return (this.name.equals(s.name));
		
		}
		return false;
	}

}
