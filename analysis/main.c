#include <stdio.h>
#include <stdlib.h>

extern int yyparse(void);
extern FILE *yyin;
int yyerror(const char *s);

int main(void) {
    int result = yyparse();

    if (result == 0) {
        printf("Programa válido!\n");
        return EXIT_SUCCESS;
    } else {
        printf("Erro na análise, programa não é válido.\n");
        return EXIT_FAILURE;
    }
}
