%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

extern FILE *yyin;
extern int yylineno;
extern char *yytext;
int yylex(void);
int yyerror(const char *msg);
%}

%union {
    int num;
    char *str;
    char *id;
}

%token <num> NUMBER
%token <str> STRING
%token <id> IDENTIFIER

%token VAR IF ELSE FOR
%token MOVEFILE COPYFILE RENAMEFILE DELETEFILE LISTFILES COUNTFILES CHECKSPACE WAIT

%token EQ NEQ GT LT PLUS MINUS ASSIGN
%token LBRACE RBRACE LPAREN RPAREN COMMA

%type <num> expression condition file_expression
%type <id> var_declaration assignment

%%

program:
    block
    ;

block:
    LBRACE statement_list RBRACE
    ;

statement_list:
      statement
    | statement_list statement
    ;

statement:
      var_declaration
    | assignment
    | file_operation
    | if_statement
    | while_statement
    ;

var_declaration:
    VAR IDENTIFIER ASSIGN expression { $$ = $2; }
    ;

assignment:
    IDENTIFIER ASSIGN expression
    ;

file_operation:
      MOVEFILE    LPAREN expression COMMA expression RPAREN
    | COPYFILE    LPAREN expression COMMA expression RPAREN
    | RENAMEFILE  LPAREN expression COMMA expression RPAREN
    | DELETEFILE  LPAREN expression RPAREN
    | LISTFILES   LPAREN expression RPAREN
    | COUNTFILES  LPAREN expression RPAREN
    | CHECKSPACE  LPAREN expression RPAREN
    | WAIT        LPAREN expression RPAREN
    ;

if_statement:
      IF condition block
    | IF condition block ELSE block
    ;

while_statement:
    FOR condition block
    ;

condition:
      expression EQ  expression
    | expression NEQ expression
    | expression GT  expression
    | expression LT  expression
    ;

expression:
      STRING                      { $$ = 0; }
    | NUMBER                      { $$ = $1; }
    | IDENTIFIER                  { $$ = 0; }
    | file_expression             { $$ = $1; }
    | expression PLUS expression  { $$ = $1 + $3; }
    | expression MINUS expression { $$ = $1 - $3; }
    ;


file_expression:
      COUNTFILES   LPAREN expression RPAREN   { $$ = 0; }
    | CHECKSPACE   LPAREN expression RPAREN   { $$ = 0; }
    ;


%%

int yyerror(const char *msg) {
    fprintf(stderr, "Erro de sintaxe na linha %d próximo a '%s': %s\n", yylineno, yytext, msg);
    return 0;
}
