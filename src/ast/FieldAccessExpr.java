package ast;


public class FieldAccessExpr extends Expr {
	
	public Expr exp;
	public Expr name;
	
	public FieldAccessExpr (Expr e, Expr rhs) {
		
		this.exp = e;
		this.name = rhs;
		
	}
	
	public String toString() {
		
		return "";
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitFieldAccessExpr(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
				
	}

}
