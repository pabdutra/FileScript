%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

extern FILE *yyin;
int yylex(void);
int yyerror(const char *s);
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
    VAR IDENTIFIER ASSIGN expression
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
      STRING
    | NUMBER
    | IDENTIFIER
    | file_expression
    | expression PLUS expression
    | expression MINUS expression
    ;


file_expression:
      COUNTFILES   LPAREN expression RPAREN
    | CHECKSPACE   LPAREN expression RPAREN
    ;


%%

int yyerror(const char *msg) {
    fprintf(stderr, "Erro de sintaxe: %s\n", msg);
    return 0;
}
