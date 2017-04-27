package ast;

public class While extends Stmt {
	
	public Expr e;
	public Stmt t;
	
	public While (Expr e, Stmt t) {
		
		this.e = e;
		this.t = t;
		
	}

	public String toString() {
		
		return e.toString();
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitWhile(this);
		
	}


}
