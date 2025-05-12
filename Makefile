SRC_DIR = analysis

EXEC = $(SRC_DIR)/analyzer

FLEX_SRC = $(SRC_DIR)/scanner.l
BISON_SRC = $(SRC_DIR)/parser.y
MAIN_SRC = $(SRC_DIR)/main.c

FLEX_OUT = $(SRC_DIR)/lex.yy.c
BISON_OUT = $(SRC_DIR)/parser.tab.c
BISON_HDR = $(SRC_DIR)/parser.tab.h

CFLAGS = -Wall -Wextra
LDFLAGS = -lfl

.PHONY: all clean

all: $(EXEC)

$(EXEC): $(MAIN_SRC) $(BISON_OUT) $(FLEX_OUT)
	gcc $(CFLAGS) -o $(EXEC) $(MAIN_SRC) $(BISON_OUT) $(FLEX_OUT) $(LDFLAGS)

$(BISON_OUT) $(BISON_HDR): $(BISON_SRC)
	bison -d -o $(BISON_OUT) $(BISON_SRC)

$(FLEX_OUT): $(FLEX_SRC) $(BISON_HDR)
	flex -o $(FLEX_OUT) $(FLEX_SRC)

run:
	@echo "Uso: make run ARQUIVO=entrada.txt"
	@false

run-%: all
	@$(EXEC) < $*

clean:
	rm -f $(EXEC) $(FLEX_OUT) $(BISON_OUT) $(BISON_HDR)
