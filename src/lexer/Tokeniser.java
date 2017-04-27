package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int holds = 0;
    public String checker;
    
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    /*
     * To be completed
     */
    private Token next() throws IOException,EOFException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();

        if (c == '/') {
        	if (scanner.peek() == '/') {
        		scanner.next();
        		while (scanner.peek() != '\n') {
        			c = scanner.next();
        		}
        		scanner.next();
        		return next();
        	}
        	else if (scanner.peek() == '*') {
        		boolean b = false;
        		boolean b2 = false;
        		scanner.next();            		
        		while (b != true) { 
        			
            		if (scanner.peek() == '*') {
            			b2 = true;
            			scanner.next();            			            			
            		}
            		else {
            			scanner.next();
            		}
            		if (scanner.peek() != '*') {
                		if (scanner.peek() == '/' && b2) {
                			scanner.next();
                			
                			b = true;
                			return next();
                		}
                		else {
                			scanner.next();
                		}
            		}

            		
            		b2 = false;
        		}

        	}
        	else {
                return new Token(TokenClass.DIV, line, column);
        	}
        }
        
        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        try {
        	
            if (Character.isDigit(c)) {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
        		while (Character.isDigit(scanner.peek())) {
        			sb.append(scanner.next());
        		}

        		return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);     		
        	}
        	
        	else if (c == '\"') {
        		StringBuilder sb = new StringBuilder();
        		while (scanner.peek() != '\"') {
        			if (scanner.peek() == '\\') {
        				scanner.next();
        				if (scanner.peek() != 't' && scanner.peek() != 'b' && scanner.peek() != 'n' && scanner.peek() != 'r' && scanner.peek() != 'f' && scanner.peek() != '\"' && scanner.peek() != '\'' && scanner.peek() != '\\') {
        					sb.append('\\');
        					sb.append(scanner.next());
        				}
        				else {
        					if (scanner.peek() == 't') {
        						sb.append('\t');
        					}
        					else if (scanner.peek() == 'b') {
        						sb.append('\b');
        					}
        					else if (scanner.peek() == 'n') {
        						sb.append('\n');
        					}
        					else if (scanner.peek() == 'r') {
        						sb.append('\r');
        					}
        					else if (scanner.peek() == 'f') {
        						sb.append('\f');
        					}
        					else if (scanner.peek() == '\\') {
        						sb.append('\\');
        					}
        					else if (scanner.peek() == '\"') {
        						sb.append('\"');
        					}
        					else if (scanner.peek() == '\'') {
        						sb.append('\'');
        					}
        					scanner.next();
        					
        				}
        			}
        			else {
        				sb.append(scanner.next());
        			}
        			
        		}
        		scanner.next();
        		return new Token(TokenClass.STRING_LITERAL, sb.toString(), line, column);     		
        	}
        	
        	//Start language keyword matching.
        	else if (c == 'i') {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
        		if (scanner.peek() == 'f') {
        			sb.append(scanner.next());
        			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
        				return new Token(TokenClass.IF, line, column);
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        		}
        		
        		else if (scanner.peek() == 'n') {
        			sb.append(scanner.next());;
        			if (scanner.peek() == 't') {
        				sb.append(scanner.next());
            			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
            				return new Token(TokenClass.INT, line, column);
            			}
            			else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            					sb.append(scanner.next());
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            			}
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        			
        		}
        		else {
    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
    					sb.append(scanner.next());
    				}
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			
        		}
        	}
        	
        	
        	else if (c == 's') {
        		StringBuilder sb = new StringBuilder();
        		
        		sb.append(c);
        		if (scanner.peek() == 't') {
        			sb.append(scanner.next());
        			if (scanner.peek() == 'r') {
        				sb.append(scanner.next());
        				if (scanner.peek() == 'u') {
        					sb.append(scanner.next());
            				if (scanner.peek() == 'c') {
            					sb.append(scanner.next());
                				if (scanner.peek() == 't') {
                					sb.append(scanner.next());
                        			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
                        				return new Token(TokenClass.STRUCT, line, column);
                        			}
                        			else {
                        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                        					sb.append(scanner.next());
                        				}
                        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                        			}
                				}
                				else {
                    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                    					sb.append(scanner.next());
                    				}
                    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                				}
            				}
            				else {
                				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                					sb.append(scanner.next());
                				}
                				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            				}
        				}
        				else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            					sb.append(scanner.next());
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        				}
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        		}
        		
        		else if (scanner.peek() == 'i') {
        			sb.append(scanner.next());        			        			
        			if (scanner.peek() == 'z') {
            			sb.append(scanner.next());   
        				if (scanner.peek() == 'e') {
                			sb.append(scanner.next());   
            				if (scanner.peek() == 'o') {
                    			sb.append(scanner.next());    
                				if (scanner.peek() == 'f') {
                        			sb.append(scanner.next());   
                        			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
                        				return new Token(TokenClass.SIZEOF, line, column);
                        			}
                        			else {
                        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                                			sb.append(scanner.next());   
                        				}
                        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                        			}                					                					
                				}
                				else {
                    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                            			sb.append(scanner.next());   
                    				}
                    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                				}            					            					
            				}
            				else {
                				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                        			sb.append(scanner.next());   
                				}
                				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            				}        					        					
        				}
        				else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                    			sb.append(scanner.next());   
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        				}        				
        			}        		        			
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                			sb.append(scanner.next());   
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}        			
        		}
        		else {
    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            			sb.append(scanner.next());   
    				}
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);        			
        		}
        	}
        	
        	
        	else if (c == 'v') {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
        		if (scanner.peek() == 'o') {
        			sb.append(scanner.next());;
        			if (scanner.peek() == 'i') {
        				sb.append(scanner.next());;
        				if (scanner.peek() == 'd') {
            				sb.append(scanner.next());;
                			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
                				return new Token(TokenClass.VOID, line, column);
                			}
                			else {
                				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                					sb.append(scanner.next());;
                				}
                				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                			}
        				}
        				else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            					sb.append(scanner.next());;
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        				}
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());;
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        		}
        		else {
    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
    					sb.append(scanner.next());
    				}
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);        			
        		}
        	}
        	
        	else if (c == 'c') {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
        		if (scanner.peek() == 'h') {
        			sb.append(scanner.next());
        			if (scanner.peek() == 'a') {
        				sb.append(scanner.next());
        				if (scanner.peek() == 'r') {
            				sb.append(scanner.next());
                			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
                				return new Token(TokenClass.CHAR, line, column);
                			}
                			else {
                				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                					sb.append(scanner.next());
                				}
                				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                			}
        				}
        				else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            					sb.append(scanner.next());
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        				}
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        		}
        		else {
    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
    					sb.append(scanner.next());
    				}
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);        			
        		}
        	}
        	
        	else if (c == 'e') {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
        		if (scanner.peek() == 'l') {
        			sb.append(scanner.next());
        			if (scanner.peek() == 's') {
        				sb.append(scanner.next());
        				if (scanner.peek() == 'e') {
            				sb.append(scanner.next());
                			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
                				return new Token(TokenClass.ELSE, line, column);
                			}
                			else {
                				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                					sb.append(scanner.next());
                				}
                				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                			}
        				}
        				else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            					sb.append(scanner.next());
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        				}
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        		}
        		else {
    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
    					sb.append(scanner.next());
    				}
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);        			
        		}
        	}
        	
        	
        	else if (c == 'w') {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
        		if (scanner.peek() == 'h') {
        			sb.append(scanner.next());
        			if (scanner.peek() == 'i') {
        				sb.append(scanner.next());
        				if (scanner.peek() == 'l') {
            				sb.append(scanner.next());
            				if (scanner.peek() == 'e') {
            					sb.append(scanner.next());
                    			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
                    				return new Token(TokenClass.WHILE, line, column);
                    			}
                    			else {
                    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                    					sb.append(scanner.next());
                    				}
                    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                    			}
            				}
            				else {
                				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                					sb.append(scanner.next());
                				}
                				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            				}
        				}
        				else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            					sb.append(scanner.next());
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        				}
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        		}
        		else {
    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
    					sb.append(scanner.next());
    				}
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);        			
        		}
        	}
        	
        	
        	else if (c == 'r') {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
        		if (scanner.peek() == 'e') {
        			sb.append(scanner.next());
        			if (scanner.peek() == 't') {
        				sb.append(scanner.next());
        				if (scanner.peek() == 'u') {
            				sb.append(scanner.next());
            				if (scanner.peek() == 'r') {
            					sb.append(scanner.next());
            					if (scanner.peek() =='n') {
            						sb.append(scanner.next());
                        			if (!Character.isAlphabetic(scanner.peek()) && scanner.peek() != '_' && !Character.isDigit(scanner.peek())) {
                        				return new Token(TokenClass.RETURN, line, column);
                        			}
                        			else {
                        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                        					sb.append(scanner.next());
                        				}
                        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
                        			}
            					}
            					else {
                    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                    					sb.append(scanner.next());
                    				}
                    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            					}
            				}
            				else {
                				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
                					sb.append(scanner.next());
                				}
                				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            				}
        				}
        				else {
            				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
            					sb.append(scanner.next());
            				}
            				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        				}
        			}
        			else {
        				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
        					sb.append(scanner.next());
        				}
        				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        			}
        		}
        		else {
    				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
    					sb.append(scanner.next());
    				}
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);        			
        		}
        	}
        	//End language keyword matching.
        	
        	//Identifier matching block.
        	else if (c == '_' || Character.isAlphabetic(c)) {
        		StringBuilder sb = new StringBuilder();
        		sb.append(c);
				while (scanner.peek() == '_' || Character.isDigit(scanner.peek()) || Character.isAlphabetic(scanner.peek())) {
					sb.append(scanner.next());
				}
				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
			
        	}
        	//End Identifier matching block.
        	
        	
        	else if (c == '\'') {
        		StringBuilder sc = new StringBuilder();
        		if (scanner.peek() == '\\') {
        			scanner.next();
        			if (scanner.peek() == '\'') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\'');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				}
        				 
        				
        			}
        			
        			else if (scanner.peek() == '\"') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\"');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				}
        				
        			}
        			
        			else if (scanner.peek() == '\\') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\\');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				}  
        				
        			}
        			
        			else if (scanner.peek() == 't') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\t');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				}  
        				
        			}
        			
        			else if (scanner.peek() == 'b') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\b');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				} 
        				
        			}
        			else if (scanner.peek() == 'n') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\n');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				} 
        				
        			}
        			else if (scanner.peek() == 'r') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\r');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				}  
        				
        			}
        			else if (scanner.peek() == 'f') {
        				
        				scanner.next();
        				if (scanner.peek() == '\'') {
        					sc.append('\f');
        					scanner.next();
        					return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column); 
        				}  
        				
        			}
        		}
        		else {
        			sc.append(scanner.next());
        			if (scanner.peek() == '\'') {
        				scanner.next();
        				return new Token(TokenClass.CHAR_LITERAL, sc.toString(), line, column);
        			}
        		}
        	}
        	        				        		
        	else if (c == '+') {
                return new Token(TokenClass.PLUS, line, column);
            }
            else if (c == '-') {
                return new Token(TokenClass.MINUS, line, column);
            }
            else if (c == '*') {
                return new Token(TokenClass.ASTERIX, line, column);
            }
        	
            else if (c == '%') {
                return new Token(TokenClass.REM, line, column); 
            }
            else if (c == ';') {
                return new Token(TokenClass.SC, line, column);
            }
            else if (c == ',') {
                return new Token(TokenClass.COMMA, line, column);
            }
            else if (c == '.') {
                return new Token(TokenClass.DOT, line, column);
            }
            else if (c == '{') {
                return new Token(TokenClass.LBRA, line, column);
            }
            else if (c == '}') {
                return new Token(TokenClass.RBRA, line, column);
            }
            else if (c == '(') {
                return new Token(TokenClass.LPAR, line, column);
            }
            else if (c == ')') {
                return new Token(TokenClass.RPAR, line, column);
            }
            else if (c == '[') {
                return new Token(TokenClass.LSBR, line, column);
            }
            else if (c == ']') {
                return new Token(TokenClass.RSBR, line, column);
            }
                
            else if (c == '<') {
                if (scanner.peek() == '=') {
                	scanner.next();
                	return new Token(TokenClass.LE, line, column);
            }
                else {
                	return new Token(TokenClass.LT, line, column);
                }
            }
                
            else if (c == '>') {
                if (scanner.peek() == '=') {
                	scanner.next();
                	return new Token(TokenClass.GE, line, column);
                }
                else {
                	return new Token(TokenClass.GT, line, column);
                }
            }
                
            else if (c == '=') { 
                if (scanner.peek() == '=') {
                	scanner.next();
                	return new Token(TokenClass.EQ, line, column);
                }
                else {
                	return new Token(TokenClass.ASSIGN, line, column);
                }
            }

            else if (c == '#') {
                if (scanner.peek() == 'i') {
                    scanner.next();
                    if (scanner.peek() == 'n') {
                    	scanner.next();
                        if (scanner.peek() == 'c') {
                        	scanner.next();
                            if (scanner.peek() == 'l') {
                            	scanner.next();
                                if (scanner.peek() == 'u') {
                                	scanner.next();
                                    if (scanner.peek() == 'd') {
                                    	scanner.next();
                                        if (scanner.peek() == 'e') {
                                        	scanner.next();
                                        	checker = "";  
                                        	return new Token(TokenClass.INCLUDE, line, column);
                    							
                }}}}}}}
            }
                	
            else if (c == '=') { 
                if (scanner.peek() == '=') { 
                    scanner.next();
                    return new Token(TokenClass.EQ, line, column);
                }
                else {
                    return new Token(TokenClass.ASSIGN, line, column);
                }
            }
                
            else if (c == '!') { 
                if (scanner.peek() == '=') { 
                    scanner.next();
                    return new Token(TokenClass.NE, line, column);
                }
            }
        	
            
        else if (c == '&') { 
            if (scanner.peek() == '&') { 
                scanner.next();
                return new Token(TokenClass.AND, line, column);
            }
        }
        	
            
        else if (c == '|') { 
            if (scanner.peek() == '|') { 
                scanner.next();
                return new Token(TokenClass.OR, line, column);
            }
        }
                
                
        } catch(EOFException e) {
                
        }
          


        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }

    public static void main() {
    	System.out.println("hi");
    }
    
}


