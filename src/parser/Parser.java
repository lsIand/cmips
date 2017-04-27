package parser;

import ast.*;

import java.util.ArrayList;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import ast.Program;
import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

public class Parser {

	public int indexOfExpr;
	
	public ArrayList<Boolean> instanceOfExpr;

    private Token token;
    
    public boolean isTrue;
    
    

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<Token>();

    private final Tokeniser tokeniser;

    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;

        instanceOfExpr = new ArrayList<Boolean>(0);

        isTrue = true;
        
        indexOfExpr = -1;
    }

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
        
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }
    

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }
    
    //Includes are ignored
    private void parseIncludes() {
    	
	    if (accept(TokenClass.INCLUDE)) {
	    	
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
            
        }
	    
    }
    
    //START
    
    public Program parseProgram() {
    	
    	LinkedList<StructType> structTypes = new LinkedList<StructType>();
    	LinkedList<VarDecl> varDecls = new LinkedList<VarDecl>();
    	LinkedList<FunDecl> funDecls = new LinkedList<FunDecl>();
    	    	
    	parseIncludes();//Ignored
    	
        while (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.LBRA) {
            //System.out.println("congo!");
        	structTypes.add(parseStructDecls());
        }
        
        while (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT) && lookAhead(2).tokenClass != TokenClass.LPAR &&lookAhead(3).tokenClass != TokenClass.LPAR && lookAhead(4).tokenClass != TokenClass.LPAR) {        	
        	varDecls.add(parseStructVarDecls());
        }
           
        
        while (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT)) {
        	
            funDecls.add(parseFunDecls());

        }
        
        

        expect(TokenClass.EOF);
    	
        return new Program(structTypes, varDecls, funDecls);
        
    }    
    
    
    //STRUCTS
    private StructType parseStructDecls() {
    	
    	String structName;
    	LinkedList<VarDecl> varDecls = new LinkedList<VarDecl>();
    	    	
        expect(TokenClass.STRUCT);
        Token t = expect(TokenClass.IDENTIFIER);
        
        structName = t.data;
        
        expect(TokenClass.LBRA);
                
        varDecls.add(parseStructVarDecls());        
        while (accept(TokenClass.INT) || accept(TokenClass.CHAR) || accept(TokenClass.VOID) || accept(TokenClass.STRUCT)) {
            
        	varDecls.add(parseStructVarDecls());
        	
        }	        
        expect(TokenClass.RBRA);
        expect(TokenClass.SC);
        return new StructType(structName, varDecls);
        
}
	@SuppressWarnings("all")
    private VarDecl parseStructVarDecls() {
    	
    	String name;
    	String structName;
    	
    	if (accept(TokenClass.INT)) {
    		expect(TokenClass.INT);
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new PointerType(BaseType.INT), name);
    	        }
    	        else {
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(new PointerType(BaseType.INT), i), name);
    	        }
    		}
    		else {
    	        name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(BaseType.INT, name);
    	        }
    	        else {
    	        	
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(BaseType.INT, i), name);
    	        }
    		}
    	}
    	else if (accept(TokenClass.CHAR)) {
    		expect(TokenClass.CHAR);
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new PointerType(BaseType.CHAR), name);
    	        }
    	        else {
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(new PointerType(BaseType.CHAR), i), name);
    	        }
    		}
    		else {
    			name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(BaseType.CHAR, name);
    	        }
    	        else {
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(BaseType.CHAR, i), name);
    	        }
    		}
    	}
    	else if (accept(TokenClass.VOID)) {
    		expect(TokenClass.VOID);
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new PointerType(BaseType.VOID), name);
    	        }
    	        else {
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(new PointerType(BaseType.VOID), i), name);
    	        }
    		}
    		else {
    			name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(BaseType.VOID, name);
    	        }
    	        else {
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(BaseType.VOID, i), name);
    	        }
    		}
    	}
    	else {
    		expect(TokenClass.STRUCT);
    		LinkedList<VarDecl> structUseEmptyDecls = new LinkedList<VarDecl>();
    		structName = expect(TokenClass.IDENTIFIER).data;
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new PointerType(new StructType(structName, structUseEmptyDecls)), name);
    	        }
    	        else {
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(new PointerType(new StructType(structName, structUseEmptyDecls)), i), name);
    	        }
    		}
    		else {
    			name = expect(TokenClass.IDENTIFIER).data;
    	        if (accept(TokenClass.SC)) {
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new StructType(structName, structUseEmptyDecls), name);
    	        }
    	        else {
    	        	expect(TokenClass.LSBR);
    	        	int i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    	        	expect(TokenClass.RSBR);
    	        	expect(TokenClass.SC);
    	        	return new VarDecl(new ArrayType(new StructType(structName, structUseEmptyDecls), i), name);
    	        }
    		}
    	}
    }
    //STRUCTSOVER
    
    
    //FUNCTION DECLARATIONS
    private FunDecl parseFunDecls() {
    	
    	Type t;
    	String s;
    	
    	LinkedList<VarDecl> params;
    	
    	Block b;
    	
    	t = parseType();
    	s = expect(TokenClass.IDENTIFIER).data;
        expect(TokenClass.LPAR);        
        params = parseParams();        
        expect(TokenClass.RPAR);        
        b = parseBlock();      
        
        return new FunDecl(t, s, params, b);
    }    
    
    
    
    private LinkedList<VarDecl> parseParams() {   
    	LinkedList<VarDecl> decls = new LinkedList<VarDecl>();
    	
    	int isComma = 0;
    	
    	while (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT,TokenClass.COMMA)) {
    		
    		Type t;
    		String s;
    					
        	if (accept(TokenClass.COMMA) && isComma == 1) {
        		expect(TokenClass.COMMA);        
        	}   
        	t = parseType();
        	s = expect(TokenClass.IDENTIFIER).data;    
        	
        	decls.add(new VarDecl(t, s));
        	
        	isComma = 1;
    	}	
    	return decls;
    }
    
    
    private Block parseBlock() {
    	
    	expect(TokenClass.LBRA);
    	
    	LinkedList<VarDecl> varDecls = new LinkedList<VarDecl>();
        LinkedList<Stmt> statements = new LinkedList<Stmt>();
    	
    	if (accept(TokenClass.RBRA)) {
    		expect(TokenClass.RBRA);
    	}
    	else if (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT)) {
    		while (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT)) {
    			
    			varDecls.add(parseStructVarDecls());
    		}

    		while (!accept(TokenClass.RBRA)) {
    			statements.add(parseStmt());
    		}
    		expect(TokenClass.RBRA);
        	
    	}
    	else {
    		while (!accept(TokenClass.RBRA)) {
    			statements.add(parseStmt());
    		}    		
    		expect(TokenClass.RBRA);
    		
    	}	
    	
    	return new Block(varDecls, statements);
    	
    }
    
    
    private Stmt parseStmt() {
    	
    	Stmt t = null;
    	
    	if (accept(TokenClass.LBRA)) {
    		Block b;
    		b = parseBlock();
    		t = b;
    	}
    	else if (accept(TokenClass.WHILE)) {
    		Expr e;
    		Stmt twhile;
    		
    		expect(TokenClass.WHILE);
    		expect(TokenClass.LPAR);
    		
    		indexOfExpr++;
    		
    		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
    		
    		instanceOfExpr.add(indexOfExpr, true);
    		
    		e = parseExp();
    		expect(TokenClass.RPAR);

    		twhile = parseStmt();
    		
    		t = new While(e, twhile);
    		
    	}
    	else if (accept(TokenClass.IF)) {
    		Expr e;
    		Stmt tif;
    		Stmt telse = null;
    		
    		
    		expect(TokenClass.IF);
    		expect(TokenClass.LPAR);
    		
    		indexOfExpr++;
    		
    		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
    		
    		instanceOfExpr.add(indexOfExpr, true);
    		
    		e = parseExp();
    		expect(TokenClass.RPAR);

    		tif = parseStmt();
    		if (accept(TokenClass.ELSE)) {
    			expect(TokenClass.ELSE);
    			telse = parseStmt();
    		}
    		
    		t = new If(e, tif, telse);
    		
    	}
    	else if (accept(TokenClass.RETURN)) {
    		Expr e;
    		
    		expect(TokenClass.RETURN);
    		if (accept(TokenClass.SC)) {
    			expect(TokenClass.SC);
    			t = new Return();
    		}
    		else {
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, true);
    			
    			e = parseExp();
    			expect(TokenClass.SC);
    			t = new Return(e);
    		}
    	}
    	else {
    		Expr lhs;
    		Expr rhs;
    		
    		indexOfExpr++;
    		
    		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
    		
    		instanceOfExpr.add(indexOfExpr, true);
    		
    		lhs = parseExp();
    		if (accept(TokenClass.SC)) {
    			expect(TokenClass.SC);
    			t = new ExprStmt(lhs);
    		}
    		else {
    			
    			expect(TokenClass.ASSIGN);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, true);
    			
    			rhs = parseExp();
    			
    			t = new Assign(lhs, rhs);
    			
    			expect(TokenClass.SC);
    			
    		}   		
    	}      

    	return t;
   	
    }
    
    
    //EXPRESSIONS
    private Expr parseExp() {
    	if (accept(TokenClass.LPAR, TokenClass.MINUS, TokenClass.ASTERIX, TokenClass.SIZEOF, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
    		
    		LinkedList<Op> ops = new LinkedList<Op>();
    		LinkedList<Expr> exprs = new LinkedList<Expr>();
    		
    		Expr baseExpression;
    		
    		//EXPRESSION BEGINNING WITH LEFT PARANTHESIS	
    		if (accept(TokenClass.LPAR)) {
    			expect(TokenClass.LPAR);
    			if (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT)) {
    				
    				Type t;
    				Expr e;
    				
    				t = parseType();
    				expect(TokenClass.RPAR);
    				
    	    		indexOfExpr++;
    	    		
    	    		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
    	    		
    	    		instanceOfExpr.add(indexOfExpr, true);
    				
    				e = parseExp();
    				
    				baseExpression = new TypeCastExpr(t, e);
    				
    			}
    			else {
    				
    	    		indexOfExpr++;
    	    		
    	    		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
    	    		
    	    		instanceOfExpr.add(indexOfExpr, true);
    				
        			baseExpression = parseExp();
        			expect(TokenClass.RPAR);
    			}

    		}
    		
    		//EXPRESSIONS BEGINNING WITH "-" (ND)
    		else if (accept(TokenClass.MINUS)) {
    			expect(TokenClass.MINUS);
    			if (accept(TokenClass.INT_LITERAL)) {
    				
    				int i;
    				
    				i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    				
    				baseExpression = new BinOp(Op.SUB, new IntLiteral(0), new IntLiteral(i));
    				
    			}
    			else {
    				
    				String s;
    				
    				s = expect(TokenClass.IDENTIFIER).data;
    				
    				baseExpression = new BinOp(Op.SUB, new IntLiteral(0), new VarExpr(s));
    				
    			}
    		}
    		
    		//EXPRESSIONS BEGINNING WITH AN INT LITERAL OR IDENTIFIER
    		else if (accept(TokenClass.INT_LITERAL,TokenClass.IDENTIFIER)) {
    			if (accept(TokenClass.INT_LITERAL)) {
    				int i;
    				
    				i = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    				
    				baseExpression = new IntLiteral(i);
    			}
    			else {
    				String s;
    				
    				s = expect(TokenClass.IDENTIFIER).data;
    				if (accept(TokenClass.LPAR)) {
    					int checkComma = 0;
    					
    					LinkedList<Expr> functionCallVars = new LinkedList<Expr>();
    					
    					expect(TokenClass.LPAR);
    					while (accept(TokenClass.LPAR, TokenClass.MINUS, TokenClass.ASTERIX, TokenClass.SIZEOF, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.COMMA)) {
    						
    						if (checkComma == 1 && accept(TokenClass.COMMA)) {
    							expect(TokenClass.COMMA);    							
    						}
    						
    						indexOfExpr++;
    						
    		        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
    		        		
    		        		instanceOfExpr.add(indexOfExpr, true);
    						
    						functionCallVars.add(parseExp());
    						checkComma = 1;

    					}
    					expect(TokenClass.RPAR);

    					baseExpression = new FunCallExpr(s, functionCallVars);
    				}
    				else {
    					baseExpression = new VarExpr(s);
    				}
    			}
    		}
    		
    		
    		else if (accept(TokenClass.CHAR_LITERAL)) {
    			char c;
    			c = expect(TokenClass.CHAR_LITERAL).data.charAt(0);
    			baseExpression = new CharLiteral(c);
    		}
    		
    		
    		else if (accept(TokenClass.STRING_LITERAL)) {
    			String s;
    			s = expect(TokenClass.STRING_LITERAL).data;
    			baseExpression = new StringLiteral(s);
    		}
    		
    		
    		else if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			
    			indexOfExpr++;
    			
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, true);
    			
    			baseExpression = new ValueAtExpr(parseExp());
    			
    		}
    		
    		
    		else if (accept(TokenClass.SIZEOF)) {
    			Type t;
    			expect(TokenClass.SIZEOF);
    			expect(TokenClass.LPAR);
    			t = parseType();
    			expect(TokenClass.RPAR);
    			baseExpression = new SizeOfExpr(t);
    		}
    		else {
    			baseExpression = null;
    			
    		}
		
		
    		exprs.add(baseExpression);

    		    		

    		
    		
    		
    		if (instanceOfExpr.get(indexOfExpr)) {
    			
    			instanceOfExpr.set(indexOfExpr, false);
    			
	    	while (accept(TokenClass.LT,TokenClass.GT,TokenClass.LE,TokenClass.GE,TokenClass.NE,TokenClass.EQ,TokenClass.PLUS,TokenClass.MINUS,TokenClass.DIV,TokenClass.ASTERIX,TokenClass.REM,TokenClass.AND,TokenClass.OR,TokenClass.DOT,TokenClass.LSBR)) {
	    		
	    		Pair<Op, Expr> ab = parseExp2();
	    		
	    		exprs.add(ab.getRight());
	    		ops.add(ab.getLeft());

	    		
	    		
	    	}
	    	
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.RESERVEDARRAY) {	    				
	    				Expr e;
	    				
	    				e = new ArrayAccessExpr(exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				i--;
	    				
	    			}	    			
	    		}	    		
	    	}
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.RESERVEDFIELDACCESS) {	    				
	    				Expr e;
	    				
	    				e = new FieldAccessExpr(exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				i--;
	    				
	    			}	    			
	    		}	    		
	    	}
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.DIV || ops.get(i) == Op.MUL || ops.get(i) == Op.MOD) {	    				
	    				
	    				Op op;
	    				
	    				op = ops.get(i);
	    				
		    			Expr e;
		    				
	    				e = new BinOp(op,exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				i--;
	    				
	    			}	    			
	    		}	    		
	    	}
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.ADD || ops.get(i) == Op.SUB) {
	    				//System.out.println(ops.size());
    				
	    				Op op;
	    				
	    				op = ops.get(i);
	    				
		    			Expr e;
		    				
	    				e = new BinOp(op,exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				i--;
	    			}	    			
	    		}	    		
	    	}
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.LT || ops.get(i) == Op.LE || ops.get(i) == Op.GT || ops.get(i) == Op.GE) {	    				
	    				
	    				Op op;
	    				
	    				op = ops.get(i);
	    				
		    			Expr e;
		    				
	    				e = new BinOp(op,exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				i--;
	    				
	    			}	    			
	    		}	    		
	    	}
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.EQ || ops.get(i) == Op.NE) {	    				
	    				
	    				Op op;
	    				
	    				op = ops.get(i);
	    				
		    			Expr e;
		    				
	    				e = new BinOp(op,exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				i--;
	    				
	    			}	    			
	    		}	    		
	    	}	    	
	    	
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.AND) {	    				
	    				
	    				Op op;
	    				
	    				op = ops.get(i);
	    				
		    			Expr e;
		    				
	    				e = new BinOp(op,exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				
	    				i--;
	    				
	    			}	    			
	    		}	    		
	    	}
	    	
	    	if (!ops.isEmpty()) {	    		
	    		for (int i = 0; i < ops.size(); i++) {	    			
	    			if (ops.get(i) == Op.OR) {	    				
	    				
	    				Op op;
	    				
	    				op = ops.get(i);
	    				
		    			Expr e;
		    				
	    				e = new BinOp(op,exprs.get(i),exprs.get(i+1));
	    				
	    				exprs.set(i, e);
	    				exprs.remove(i+1);
	    				ops.remove(i);
	    				i--;
	    				
	    			}	    			
	    		}	    		
	    	}
	    	
	    	
	    	
    		}
    		
	    	
	    	baseExpression = exprs.getFirst();
	    	
	    	instanceOfExpr.remove(indexOfExpr);
	    	
	    	indexOfExpr--;
	    	
	    	return baseExpression;
	    	
	    	
	    	
    	
    	}
    	else {
    		error(TokenClass.LPAR, TokenClass.MINUS, TokenClass.ASTERIX, TokenClass.SIZEOF, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL);
    		return null;
    	}
    	
    }
    	
    
    
     
    private Pair<Op,Expr> parseExp2 () {
    	
    	Expr baseExpression2 = null;
		Expr rhs = null;
		Op op = null;
    	if (accept(TokenClass.LT, TokenClass.GT, TokenClass.LE, TokenClass.GE, TokenClass.NE, TokenClass.EQ, TokenClass.PLUS, TokenClass.MINUS, TokenClass.DIV, TokenClass.ASTERIX, TokenClass.REM, TokenClass.AND, TokenClass.OR)) {

    		
    		
    		if (accept(TokenClass.LT)) {
			
    			expect(TokenClass.LT);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.LT;
    		}
    		else if (accept(TokenClass.GT)) {

    			expect(TokenClass.GT);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.GT;
    		}
    		else if (accept(TokenClass.LE)) {

    			expect(TokenClass.LE);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.LE;
    		}
    		else if (accept(TokenClass.GE)) {
    			
    			expect(TokenClass.GE);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.GE;
    		}
    		else if (accept(TokenClass.NE)) {
    			expect(TokenClass.NE);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.NE;
    		}
    		else if (accept(TokenClass.EQ)) {
    			expect(TokenClass.EQ);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.EQ;
    		}
    		else if (accept(TokenClass.PLUS)) {
    			expect(TokenClass.PLUS);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.ADD;
    		}
    		else if (accept(TokenClass.MINUS)) {
    			expect(TokenClass.MINUS);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.SUB;
    		}
    		else if (accept(TokenClass.DIV)) {
    			expect(TokenClass.DIV);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.DIV;
    		}
    		else if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.MUL;
    		}
    		else if (accept(TokenClass.REM)) {
    			expect(TokenClass.REM);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 =rhs;
    			op = Op.MOD;
    		}
    		else if (accept(TokenClass.AND)) {
    			expect(TokenClass.AND);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.AND;
    		}
    		else if (accept(TokenClass.OR)) {
    			expect(TokenClass.OR);
    			
        		indexOfExpr++;
        		
        		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
        		
        		instanceOfExpr.add(indexOfExpr, false);
    			
    			rhs = parseExp();
    			
    			baseExpression2 = rhs;
    			op = Op.OR;
    		}
    	}
    	
    	else if (accept(TokenClass.LSBR)) {
    		
    		expect(TokenClass.LSBR);
    		
    		indexOfExpr++;
    		
    		instanceOfExpr.ensureCapacity(indexOfExpr + 10);
    		
    		instanceOfExpr.add(indexOfExpr, true);
    		
    		rhs = parseExp();
    		expect(TokenClass.RSBR);
    		
    		baseExpression2 = rhs;
    		op = Op.RESERVEDARRAY;
    	}
    	else if (accept(TokenClass.DOT)) {
    		
    		expect(TokenClass.DOT);
    		rhs = new StringLiteral(expect(TokenClass.IDENTIFIER).data);
    		
    		baseExpression2 = rhs;
    		op = Op.RESERVEDFIELDACCESS;
    	}
    	else {
    		error(TokenClass.LT, TokenClass.GT, TokenClass.LE, TokenClass.GE, TokenClass.NE, TokenClass.EQ, TokenClass.PLUS, TokenClass.MINUS, TokenClass.DIV, TokenClass.ASTERIX, TokenClass.REM, TokenClass.AND, TokenClass.OR);
    		baseExpression2 = null;
    	}
    	 
    	
    	return new Pair<>(op, baseExpression2);
    }
    
    
    
    
     
    private Type parseType() {
    	if (accept(TokenClass.INT)) {
    		expect(TokenClass.INT);
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			return new PointerType(BaseType.INT);
    		}
    		else {
    			return BaseType.INT;
    		}
    	}
    	else if (accept(TokenClass.CHAR)) {
    		expect(TokenClass.CHAR);
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			return new PointerType(BaseType.CHAR);
    		}
    		else {
    			return BaseType.CHAR;
    		}
    	}
    	else if (accept(TokenClass.VOID)) {
    		expect(TokenClass.VOID);
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			return new PointerType(BaseType.VOID);
    		}
    		else {
    			return BaseType.VOID;
    		}
    	}
    	else {
    		LinkedList<VarDecl> structUseEmptyDecls = new LinkedList<VarDecl>();
    		expect(TokenClass.STRUCT);
    		String structName = expect(TokenClass.IDENTIFIER).data;
    		if (accept(TokenClass.ASTERIX)) {
    			expect(TokenClass.ASTERIX);
    			return new PointerType(new StructType(structName, structUseEmptyDecls));
    		}
    		else {
    			return new StructType(structName, structUseEmptyDecls);
    		}
    	}
    }
}