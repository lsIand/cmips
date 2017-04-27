package sem;

import ast.*;

public abstract class Symbol {
	public String name;
	
	public FunDecl fd;
	
	public VarDecl vd;
	
	public Symbol(String name) {
		this.name = name;
		fd = null;
		vd = null;
	}
	
	public boolean isVar() {
		
		return (this instanceof VarSymbol);
		
	}
	
	public boolean isProc() {
		
		return (this instanceof ProcSymbol);
		
	}
	
	
}
