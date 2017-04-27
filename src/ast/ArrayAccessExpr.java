package ast;


public class ArrayAccessExpr extends Expr {
	
	public Expr exp1;
	public Expr exp2;
	
	public ArrayAccessExpr (Expr e1, Expr e2) {
		
		this.exp1 = e1;
		this.exp2 = e2;
		
	}
	
	public String toString() {
		
		return "";
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitArrayAccessExpr(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
				
	}

}
