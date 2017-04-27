package sem;

import ast.*;

public class ProcSymbol extends Symbol {
	
	public ProcSymbol(FunDecl fd) {
		super(fd.name);
		super.fd = fd;
	}

	public String name;
	
}
