package ast;

/**
 * @author cdubach
 */
public interface Type extends ASTNode {
	
	public boolean equals1(Type t);

    public <T> T accept(ASTVisitor<T> v);

}
