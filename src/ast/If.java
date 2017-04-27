package ast;

public class If extends Stmt {
	
	public Expr e;
	public Stmt tif;
	public Stmt telse;
	
	public If (Expr e, Stmt t1, Stmt t2) {
		
		this.e = e;
		this.tif = t1;
		this.telse = t2;
		
	}

	public String toString() {
		
		return e.toString();
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitIf(this);
		
	}


}
