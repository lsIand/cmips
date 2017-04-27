package sem;

import java.util.Map;
import java.util.HashMap;

public class Scope {
	private Scope outer;
	private Map<String, Symbol> symbolTable;
	
	public Scope(Scope outer) { 
		this.outer = outer; 
		symbolTable = new HashMap<String, Symbol>();
	}
	
	public Scope() { this(null); }
	
	public Symbol lookup(String name) {
		
		if (!symbolTable.isEmpty()) {
			if (symbolTable.containsKey(name)) {
				return symbolTable.get(name);
			}
		}
		
		if (outer != null) {
			if (outer.lookup(name) != null) {				
				return outer.lookup(name);				
			}
		}
		
		return null;
	}
	
	public Symbol lookupCurrent(String name) {
		
		if (!symbolTable.isEmpty()) {
			if (symbolTable.containsKey(name)) {
				return symbolTable.get(name);
			}
		}
		
		
		return null;
	}
	
	public Scope getOuter() {
		return this.outer;
	}
	
	public void put(Symbol sym) {
		
		symbolTable.put(sym.name, sym);
	}
}
