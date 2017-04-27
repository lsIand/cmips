package ast;

public class StringLiteral extends Expr {
	
	public String s;
	
	public StringLiteral (String s) {
		
		this.s = s;
		
	}

	public String toString() {
		
		return s;
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitStringLiteral(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
		
	}

}
