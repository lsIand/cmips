package ast;


public class SizeOfExpr extends Expr {
	
	public Type ty;
	
	
	public SizeOfExpr (Type t) {
		
		this.ty = t;
				
	}
	
	public String toString() {
		
		return "";
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitSizeOfExpr(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
				
	}

}
