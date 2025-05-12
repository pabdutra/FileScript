%{
#include "parser.tab.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
extern int yylineno;
%}

%option noyywrap
%option noinput nounput

DIGIT           [0-9]
LETTER          [a-zA-Z]
ID_START        ({LETTER}|_)
ID_CHAR         ({LETTER}|{DIGIT}|_)
IDENTIFIER      {ID_START}{ID_CHAR}*
NUMBER          {DIGIT}+
STRING_LITERAL  \"([^\"\\]|\\.)*\"

%%

"var"               { return VAR; }
"if"                { return IF; }
"else"              { return ELSE; }
"for"               { return FOR; }

"MoveFile"          { return MOVEFILE; }
"CopyFile"          { return COPYFILE; }
"RenameFile"        { return RENAMEFILE; }
"DeleteFile"        { return DELETEFILE; }
"ListFiles"         { return LISTFILES; }
"CountFiles"        { return COUNTFILES; }
"CheckSpace"        { return CHECKSPACE; }
"Wait"              { return WAIT; }

"=="                { return EQ; }
"!="                { return NEQ; }
">"                 { return GT; }
"<"                 { return LT; }
"+"                 { return PLUS; }
"-"                 { return MINUS; }
"="                 { return ASSIGN; }

"{"                 { return LBRACE; }
"}"                 { return RBRACE; }
"("                 { return LPAREN; }
")"                 { return RPAREN; }
","                 { return COMMA; }

{NUMBER}            { yylval.num = atoi(yytext); return NUMBER; }

{STRING_LITERAL}    {
                      yytext[yyleng - 1] = '\0';
                      yylval.str = strdup(yytext + 1);
                      return STRING;
                   }

{IDENTIFIER}        { yylval.id = strdup(yytext); return IDENTIFIER; }

[ \t\r\n]+          { }

.                   {
                      fprintf(stderr, "Erro léxico na linha %d: caractere inválido '%s'\n", yylineno, yytext);
                      exit(1);
                   }

%%
