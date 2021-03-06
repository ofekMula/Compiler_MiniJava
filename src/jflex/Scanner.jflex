/***************************/
/* Based on a template by Oren Ish-Shalom */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;



/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/

/*****************************************************/
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */
/*****************************************************************************/
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine()    { return yyline + 1; }
	public int getCharPos() { return yycolumn;   }
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace		= [\t ] | {LineTerminator}
INTEGER			= 0 | [1-9][0-9]*
IDENTIFIER				= [a-zA-Z][a-zA-Z0-9_]*
MultiComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
SingleComment     = "//" [^\r\n]* {LineTerminator}?
/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {
"public"            { return symbol(sym.PUBLIC); }
"class"             { return symbol(sym.CLASS); }
"main"        { return symbol(sym.MAIN); }
"extends"           { return symbol(sym.EXTENDS); }
"static"            {return symbol(sym.STATIC);}
"void"              {return symbol(sym.VOID);}
","			        { return symbol(sym.COMMA); }
"+"                 { return symbol(sym.PLUS); }
"-"                 { return symbol(sym.MINUS); }
"*"                 { return symbol(sym.MULT); }
/*"/"                 { return symbol(sym.DIV); }*/
"<"                 { return symbol(sym.LT); }
"("                 { return symbol(sym.LPAREN); }
")"                 { return symbol(sym.RPAREN); }
"{"                 { return symbol(sym.L_CUR_PAREN); }
"}"                 { return symbol(sym.R_CUR_PAREN); }
";"                 { return symbol(sym.END_SENT); }
"!"                  { return symbol(sym.NOT); }
"&&"                { return symbol(sym.AND); }
"int"               { return symbol(sym.INT); }
"String"          {return symbol(sym.STRING);}
"["                { return symbol(sym.L_ARRAY); }
"]"                { return symbol(sym.R_ARRAY); }
"boolean"           { return symbol(sym.BOOLEAN); }
"false"             { return symbol(sym.FALSE); }
"true"                 { return symbol(sym.TRUE); }
"."                 { return symbol(sym.DOT); }
"new"               { return symbol(sym.NEW); }
"="                 { return symbol(sym.ASSIGN); }
"if"             { return symbol(sym.IF); }
"else"             { return symbol(sym.ELSE); }
"while"             { return symbol(sym.WHILE); }
"length"             { return symbol(sym.LENGTH); }
"this"                 {return symbol(sym.THIS);}
"return"                {return symbol(sym.RETURN);}
"System.out.println"    { return symbol(sym.PRINT); }
{IDENTIFIER}		        { return symbol(sym.IDENTIFIER, new String(yytext())); }
{INTEGER}           { return symbol(sym.INTEGER, Integer.parseInt(yytext())); }
{WhiteSpace}        { /* do nothing */ }
{SingleComment}     { /* do nothing */ }
{MultiComment}     { /* do nothing */ }
<<EOF>>				{ return symbol(sym.EOF); }
}