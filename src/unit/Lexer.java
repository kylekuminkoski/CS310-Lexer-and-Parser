// Author: Kyle Kuminkoski
// I agree to the WVU academic integrity policy and verify that all work is my own.

package unit;

import java.util.LinkedList;
import java.util.Queue;

import framework.Token;
import framework.TokenName;

public class Lexer {
	public static void main(String[] args) {
		String sentence = "^eval 1?";
		Queue<Token> tokens = new Lexer().tokenize(sentence);
		while (!tokens.isEmpty())
			System.out.println(tokens.remove());
	}

	public Queue<Token> tokenize(String input) {
		Queue<Token> tokens = new LinkedList<Token>();
		
		for(int i = 0; i < input.length(); i++) {
			if (Character.isWhitespace(input.charAt(i)))
				continue;
			
			if (input.charAt(i) == '=') {
				tokens.add(new Token(TokenName.EQUAL));
			}
			else if(input.charAt(i) == ',') {
				tokens.add(new Token(TokenName.COMMA));
			}
			else if(input.charAt(i) == '?') {
				tokens.add(new Token(TokenName.QUESTION));
			}
			else if(input.charAt(i) == '^') {
				tokens.add(new Token(TokenName.CARET));
			}
			else if(input.charAt(i) == '\'') {
				tokens.add(new Token(TokenName.APOSTROPHE));
			}
			else if(input.charAt(i) == '(') {
				tokens.add(new Token(TokenName.OPEN_PAREN));
			}
			else if(input.charAt(i) == ')') {
				tokens.add(new Token(TokenName.CLOSE_PAREN));
			}
			else if(input.charAt(i) == '0' || input.charAt(i) == '1' ) {
				if(input.charAt(i) == '0') {
					tokens.add(new Token(TokenName.BOOL_LITERAL, false));
				}
				else {
					tokens.add(new Token(TokenName.BOOL_LITERAL, true));
				}
			}
			else if(input.charAt(i) == '<') {
				if(i+1 < input.length() && input.charAt(i+1) == '-') {
					if(i+2 < input.length() && input.charAt(i+2) == '>') {
				tokens.add(new Token(TokenName.DOUBLE_ARROW));
					i += 2;
					} else {
						tokens.add(new Token(TokenName.UNEXPECTED_INPUT));
						return tokens;
					}
				} else {
					tokens.add(new Token(TokenName.UNEXPECTED_INPUT));
					return tokens;
				}
			}
			else if(input.charAt(i) == '-') {
				if(i+1 < input.length() && input.charAt(i+1) == '>') {
				tokens.add(new Token(TokenName.ARROW));
				i += 1;
				} else {
					tokens.add(new Token(TokenName.UNEXPECTED_INPUT));
					return tokens;
				}
			}
			else if (Character.isLetter(input.charAt(i))) {
				int len = 1;
				while (
					i+len < input.length() &&
					Character.isLetter(input.charAt(i+len))
						) len++;
				String lexeme = input.substring(i, i+len);

				switch (lexeme.toLowerCase()) {
				case "v" :
					tokens.add(new Token(TokenName.VEE));
					continue;
				case "let" :
					tokens.add(new Token(TokenName.LET_KEYWORD));
					i +=2;
					continue;
				case "eval" :
					tokens.add(new Token(TokenName.EVAL_KEYWORD));
					i += 3;
					continue;
				default :
					tokens.add(new Token(TokenName.IDENTIFIER, lexeme));
					i += len - 1;
					continue;
				}
				
			}
			else {
				tokens.add(new Token(TokenName.UNEXPECTED_INPUT));
				return tokens;
			}
			

		}

		tokens.add(new Token(TokenName.END_OF_INPUT));
		
		return tokens;
	}
}