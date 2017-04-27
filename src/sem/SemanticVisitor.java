package sem;

/**
 * @author dhil
 * An interface for semantic visitors.
 * Exposes a method for extracting the number of errors encountered during an AST traversal.
 */
public interface SemanticVisitor<T> extends ast.ASTVisitor<T> {
	/**
	 * Returns a count of errors determined by the visitor during a single AST traversal.
	 * @return number of errors
	 */
	public int getErrorCount();
}
