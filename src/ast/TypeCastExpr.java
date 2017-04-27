package ast;


public class TypeCastExpr extends Expr {
	
	public Type ty;
	public Expr exp;
	
	
	public TypeCastExpr (Type t, Expr e) {
		
		this.ty = t;
		this.exp = e;
				
	}
	
	public String toString() {
		
		return "";
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitTypeCastExpr(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
				
	}

}
