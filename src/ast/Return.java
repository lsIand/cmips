package ast;

public class Return extends Stmt {
	
	public Expr e;
	
	public Return (Expr e) {
		
		this.e = e;
		
	}
	
	public Return () {
		
		this.e = null;
		
	}

	public String toString() {
		
		return e.toString();
		
	}
	
	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitReturn(this);
		
	}


}
