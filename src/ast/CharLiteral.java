package ast;

public class CharLiteral extends Expr {
	
	public char c;
	
	public CharLiteral (char c) {
		
		this.c = c;
		
	}

	public String toString() {
		
		return "";
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitCharLiteral(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
		
	}

}
