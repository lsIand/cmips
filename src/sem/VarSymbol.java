package sem;

import ast.*;

public class VarSymbol extends Symbol {
	public VarSymbol(VarDecl vd) {
		super(vd.varName);
		super.vd = vd;
	}

	public String name;
	
}
