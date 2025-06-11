# Diretórios
ANALYSIS_DIR = analysis
JVM_DIR = jvm

# Ferramentas
FLEX = flex
BISON = bison
GCC = gcc
JAVAC = javac
JAVA = java

# Arquivos
SCANNER = $(ANALYSIS_DIR)/scanner.l
PARSER = $(ANALYSIS_DIR)/parser.y
MAIN = $(ANALYSIS_DIR)/main.c
ANALYZER = $(ANALYSIS_DIR)/analyzer
VM_SOURCE = $(JVM_DIR)/FileScriptVM.java
VM_CLASS = $(JVM_DIR)/FileScriptVM.class

# Alvos padrão
.PHONY: all clean run analysis-run vm-run help

all: $(ANALYZER) $(VM_CLASS)

# Compilação do analisador (Flex + Bison)
$(ANALYZER): $(SCANNER) $(PARSER) $(MAIN)
	@echo "Compilando analisador léxico e sintático..."
	cd $(ANALYSIS_DIR) && $(FLEX) scanner.l
	cd $(ANALYSIS_DIR) && $(BISON) -d parser.y
	cd $(ANALYSIS_DIR) && $(GCC) -o analyzer lex.yy.c parser.tab.c main.c

# Compilação da VM Java
$(VM_CLASS): $(VM_SOURCE)
	@echo "Compilando VM Java..."
	$(JAVAC) $(VM_SOURCE)

# Executar apenas análise
analysis-run:
ifndef file
	$(error Você deve passar o caminho do arquivo com 'file=exemplo.txt')
endif
	@echo "Executando análise léxica e sintática de $(file)..."
	@if [ -f "$(file)" ]; then \
		./$(ANALYZER) < $(file); \
	else \
		echo "Arquivo $(file) não encontrado!"; \
		exit 1; \
	fi

# Executar apenas a VM
jvm-run:
ifndef file
	$(error Você deve passar o caminho do arquivo com 'file=exemplo.txt')
endif
	@echo "Executando $(file) na FileScript VM..."
	@if [ -f "$(file)" ]; then \
		$(JAVA) jvm.FileScriptVM $(file); \
	else \
		echo "Arquivo $(file) não encontrado!"; \
		exit 1; \
	fi

# Executar análise + VM
run: $(ANALYZER) $(VM_CLASS)
ifndef file
	$(error Você deve passar o caminho do arquivo com 'file=exemplo.txt')
endif
	@echo "=== Análise Léxica e Sintática ==="
	@if [ -f "$(file)" ]; then \
		./$(ANALYZER) < $(file); \
		if [ $$? -eq 0 ]; then \
			echo "=== Execução na VM ==="; \
			$(JAVA) jvm.FileScriptVM $(file); \
		else \
			echo "Programa inválido - não executando na VM"; \
		fi \
	else \
		echo "Arquivo $(file) não encontrado!"; \
		exit 1; \
	fi

# Limpeza
clean:
	@echo "Limpando arquivos gerados..."
	rm -f $(ANALYSIS_DIR)/lex.yy.c $(ANALYSIS_DIR)/parser.tab.c $(ANALYSIS_DIR)/parser.tab.h
	rm -f $(ANALYZER)
	rm -f $(VM_CLASS)

# Ajuda
help:
	@echo "FileScript - Comandos disponíveis:"
	@echo "  make all                        - Compila analisador e VM"
	@echo "  make analysis-run file=ARQUIVO - Executa análise léxica/sintática"
	@echo "  make jvm-run file=ARQUIVO       - Executa programa na VM"
	@echo "  make run file=ARQUIVO          - Executa análise + VM"
	@echo "  make clean                     - Remove arquivos gerados"
