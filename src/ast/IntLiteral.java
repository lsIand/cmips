package ast;

public class IntLiteral extends Expr {
	
	public int i;
	
	public IntLiteral (int i) {
		
		this.i = i;
		
	}

	public String toString() {
		
		return "";
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitIntLiteral(this);
		
	}

	@Override
	public int eval() {
		
		return i;
		
	}

}
