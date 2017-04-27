package sem;

import java.util.ArrayList;
import java.util.LinkedList;

import ast.BaseType;
import ast.Block;
import ast.FunDecl;
import ast.PointerType;
import ast.Stmt;
import ast.VarDecl;

public class SemanticAnalyzer {
	
	public int analyze(ast.Program prog) {	
		
        
        prog.funDecls.add(new FunDecl(BaseType.VOID,"print_s",new LinkedList<VarDecl>() {{ add(new VarDecl(new PointerType(BaseType.CHAR), "s")); }},new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
    	
        prog.funDecls.add(new FunDecl(BaseType.VOID,"print_c",new LinkedList<VarDecl>() {{ add(new VarDecl(BaseType.CHAR, "c")); }},new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
        
        prog.funDecls.add(new FunDecl(BaseType.VOID,"print_i",new LinkedList<VarDecl>() {{ add(new VarDecl(BaseType.INT, "i")); }},new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
        
        prog.funDecls.add(new FunDecl(BaseType.CHAR,"read_c",new LinkedList<VarDecl>(),new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
        
        prog.funDecls.add(new FunDecl(new PointerType(BaseType.VOID),"mcmalloc",new LinkedList<VarDecl>() {{ add(new VarDecl(BaseType.INT, "size")); }},new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
        
        prog.funDecls.add(new FunDecl(BaseType.INT,"read_i",new LinkedList<VarDecl>(),new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
        
		
		// List of visitors
		ArrayList<SemanticVisitor> visitors = new ArrayList<SemanticVisitor>() {{
			add(new NameAnalysisVisitor(new Scope()));
			add(new TypeCheckVisitor());
		}};
		// Error accumulator
		int errors = 0;
		
		// Apply each visitor to the AST
		for (SemanticVisitor v : visitors) {
			prog.accept(v);
			errors += v.getErrorCount();
		}
		
		// Return the number of errors.
		return errors;
	}
}
