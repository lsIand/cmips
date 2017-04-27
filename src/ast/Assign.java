package ast;

public class Assign extends Stmt {
	
	public Expr lhs;
	public Expr rhs;
	
	public Assign (Expr e1, Expr e2) {
		
		this.lhs = e1;
		this.rhs = e2;
		
	}

	public String toString() {
		
		return "";
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitAssign(this);
		
	}


}
