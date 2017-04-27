package ast;

import java.util.LinkedList;

public class Block extends Stmt {

    public LinkedList<VarDecl> varDecls;
    public LinkedList<Stmt> statements;
    public Block father;
    
    public Block(LinkedList<VarDecl> vd, LinkedList<Stmt> s) {
    	
    	this.varDecls = vd;
    	this.statements = s;
    	this.varSize = 0;
    	this.father = null;
    }
    public int varSize;

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }
}
