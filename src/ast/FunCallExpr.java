package ast;

import java.util.LinkedList;

public class FunCallExpr extends Expr {
	
	public FunDecl decl;
	
	public String name;
	public LinkedList<Expr> expressions;
	
	public FunCallExpr (String n, LinkedList<Expr> expr) {
		
		this.name = n;
		this.expressions = expr;
		decl = null;
		
	}
	
	public String toString() {
		
		return "";
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		
		return v.visitFunCallExpr(this);
		
	}

	@Override
	public int eval() {
		
		return 0;
				
	}

}
