package ast;

public class ExprStmt extends Stmt {
	
	public Expr e;
	
	public ExprStmt (Expr e) {
		
		this.e = e;
		
	}

	public String toString() {
		
		return e.toString();
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitExprStmt(this);
		
	}


}
