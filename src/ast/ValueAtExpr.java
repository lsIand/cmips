package ast;


public class ValueAtExpr extends Expr {
	
	public Expr exp;
	
	
	public ValueAtExpr (Expr e) {
		
		this.exp = e;
				
	}
	
	public String toString() {
		
		return "";
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitValueAtExpr(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
				
	}

}
