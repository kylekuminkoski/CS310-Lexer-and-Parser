package unit;

import java.util.HashMap;
import java.util.Queue;

import framework.Token;
import framework.TokenName;

/**
* @author Kyle Kuminkoski
* @co_author: Grant Stumpf
* CS310 Section 001
* Unit 2
* March 3rd 2023
* I agree to the WVU academic integrity policy and verify that all work follows this policy.
*/

/**
 * Contributions: 
 * 
 * Kyle Kuminkoski: implication, parserBoolean, evaluation, negation, expression, variable
 * 
 * Grant Stumpf: program, equivalence, conjunction, disjunction, literal
 * 
 * Shared: analyze, assignment, accept, peek, expect
 * 
 *
 */

public class Parser {
	public static void main(String[] args) {	
		String sentence = "let X = 0, let Y = 1, let Z = 0, eval X -> Y -> Z?";
		boolean value = new Parser().analyze(sentence);
		System.out.println(value);
	}
	
	private Queue<Token> tokens;
	 static HashMap<String, Boolean> lookupTable;

	public boolean analyze(String input) {
		tokens = new Lexer().tokenize(input);
		
		boolean value = program();
		expect(TokenName.END_OF_INPUT);
		
		return value;
	}
	
	private boolean accept(TokenName name) {
		if(tokens.peek().name != name)
			return false;
		
		tokens.remove();
		return true;
	}

	private boolean peek(TokenName name) {
		return tokens.peek().name == name;
	}

	private Object expect(TokenName name) {
		if (tokens.peek().name != name)
			throw new RuntimeException("Expected: " + name + ", but found: " + tokens.peek().name);
		
		return tokens.remove().value;
	}
	
	/*
	 * BNF: <program> -> <assignment> <evaluation>
	 * 					| <evaluation>
	 * 
	 * EBNF: <program> -> {<assignment}* <evaluation> 
	 */
	
	private boolean program() {
	  lookupTable = new HashMap<>(8);
		while (peek(TokenName.LET_KEYWORD)) { 
			assignment();
		}
		
		return evaluation();
	}
	
	/*
	 * BNF: <assignment> -> 'let' <variable> '=' <equivalence> ','
	 */
	
	private void assignment() {
		accept(TokenName.LET_KEYWORD);
		String tokenName = (String) expect(TokenName.IDENTIFIER);
		
		
			
			if(lookupTable.get(tokenName) != null)
				throw new RuntimeException("Identifier Already Exists.");
		
		if(accept(TokenName.EQUAL)) {
			boolean value = equivalence();
			lookupTable.put(tokenName, value);
		}
		
		if(lookupTable.size() > 8)
			throw new RuntimeException("Variable Limit Exceeded.");
		
		expect(TokenName.COMMA);
		
	}
	
	/*
	 * BNF: <evaluation> -> 'eval' <equivalence> '?'
	 */
	
	private boolean evaluation() {
		expect(TokenName.EVAL_KEYWORD);
		boolean value = equivalence();
		expect(TokenName.QUESTION);
		
		return value;
	}
	
	/*
	 * BNF: <equivalence> -> <implication> '<->' <implication>
	 * 						| <implication>
	 * 
	 * EBNF: <equivalence> -> <implication> { '<->' <implication> }*
	 */
	
	private boolean equivalence() {
		boolean value = implication();
		while(accept(TokenName.DOUBLE_ARROW)) {
			if(value == implication())
				value = true;
			else
				value = false;
		}
		
		return value;
	}
	
	/*
	 * BNF: <implication> -> <disjunction> '->' <disjunction>
	 * 						| <disjunction>
	 * 
	 * EBNF: <implication> -> <disjunction> { '->' <disjunction> }*
	 */
	
	private boolean implication() {
		boolean value = disjunction();
		if(accept(TokenName.ARROW)) {
			if((value == true & implication() == false))
				value = false;
			else
				value = true;
		}
		
		return value;
	}
	
	/*
	 * BNF: <disjunction> -> <conjunction> 'v' <conjunction>
	 * 						| <conjunction>
	 * 
	 * EBNF: <disjunction> -> <conjunction> { 'v' <conjunction }*
	 */
	
	private boolean disjunction() {
		boolean value = conjunction();
		while(accept(TokenName.VEE)) {
			if(value == false & conjunction() == false)
				value = false;
			else
				value = true;
		}
		
		return value;
	}
	
	/*
	 * BNF: <conjunction> -> <negation> '^' <negation>
	 * 						| <negation>
	 * 
	 * EBNF: <conjunction -> <negation> { '^' <negation>}*
	 */
	
	private boolean conjunction() {
		boolean value = negation();
		while(accept(TokenName.CARET)) {
			if(value == true & negation() == true)
				value = true;
			else
				value = false;
		}
		
		return value;
	}
	
	/*
	 * BNF: <negation> -> <expression> '
	 * 					| <expression>
	 * 
	 * EBNF: <negation> -> expression [']
	 */
	
	private boolean negation() {
		boolean value = expression();
		
		if(accept(TokenName.APOSTROPHE)) {
			if(value == true)
				value = false;
			else
				value = true;
		}
		
		return value;
	}
	
	/*
	 * BNF: <expression> -> '(' <equivalence> ')' 
	 * 						| <boolean>
	 * 
	 * EBNF: <expression> -> '(' <equivalence> ')' | <boolean>
	 */
	
	private boolean expression() {
		boolean value = false;
		
		if(accept(TokenName.OPEN_PAREN)) {
			value = equivalence();
			expect(TokenName.CLOSE_PAREN);
		} else {
			value = parserBoolean();
		}
		
		return value;
	}
	
	/*
	 * BNF: <boolean> -> <literal>
	 * 					| <variable>
	 * 
	 * EBNF: <boolean> -> <literal> | <variable>
	 */
	
	private boolean parserBoolean() {
		boolean value;
		
		if(peek(TokenName.BOOL_LITERAL)) {
			value = literal();
		}
		else {
			value = variable();
		}
		
		
		return value;
	}
	
	/*
	 * <literal> -> 1 | 0
	 * 
	 */
	
	private boolean literal() {
		return (Boolean) expect(TokenName.BOOL_LITERAL);
	}
	
	/*
	 * <variable> -> variable identifier
	 */
	
	private boolean variable() {
		String value = (String) expect(TokenName.IDENTIFIER);
					
		return lookupTable.get(value);
	}
	
}

