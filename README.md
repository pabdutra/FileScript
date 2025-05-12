# FileScript

**FileScript** é uma linguagem de programação projetada para **organizar, gerenciar e renomear arquivos em massa** de maneira simples e programável. A intenção é viabilizar a automação de tarefas como mover, copiar, renomear, listar e deletar arquivos, usando uma sintaxe clara e objetiva.

## Funcionalidades principais

- Declaração de variáveis (`var`)
- Atribuição de valores
- Condicionais (`if/else`)
- Laços de repetição (`for`)
- Operações diretas com arquivos:
  - Mover (`MoveFile`)
  - Copiar (`CopyFile`)
  - Renomear (`RenameFile`)
  - Deletar (`DeleteFile`)
  - Listar arquivos (`ListFiles`)
  - Contar arquivos (`CountFiles`)
  - Verificar espaço ocupado (`CheckSpace`)
  - Esperar tempo (`Wait`)

## Exemplos de código

### ➔ Uso de variáveis

```plaintext
{
    var origem = "C:/Downloads"
    var destino = "C:/Imagens"
}
```

### ➔ Condicional simples

```plaintext
{
    var tamanho = CheckSpace("C:/Downloads")
    
    if tamanho > 100000000 {
        ListFiles("C:/Downloads")
    } else {
        ListFiles("C:/Desktop")
    }
}
```

### ➔ Loop para mover e renomear arquivos

```plaintext
{
    var origem = "C:/Downloads"
    var destino = "C:/Organizado"
    var prefixo = "arquivo_"

    for CountFiles(origem) > 0 {
        MoveFile(origem + "/foto.jpg", destino)
        RenameFile(destino + "/foto.jpg", prefixo + "foto.jpg")
    }
}
```

## Análise Léxica e Sintática

Via **Linux** ou **WSL** no Windows, as ferramentas **Flex** (para análise léxica) e **Bison** (para análise sintática) podem ser utilizadas para validar os scripts escritos na linguagem.

- Os arquivos de código (`scanner.l`, `parser.y`, `main.c`) estão localizados em `analysis/`
- O `Makefile` está na raiz do projeto (`FILESCRIPT/`)
- O executável gerado será `analysis/analyzer`

### ➔ Pré requisitos

Para garantir suporte adequado às ferramentas utilizadas, instale suas dependências executando os comandos abaixo:

```bash
sudo apt update
sudo apt install flex bison gcc build-essential
```

### ➔ Compilação e execução

Para compilar e executar a verificação léxica e sintática, basta ter um arquivo como `entrada.txt` em FileScript na raiz do projeto e executar:

```bash
make run-entrada.txt
```

Este comando:

1. Compila automaticamente os arquivos com Flex e Bison
2. Gera o executável `analysis/analyzer`
3. Executa `./analysis/analyzer < entrada.txt`

### ➔ Limpeza dos arquivos gerados

Para remover todos os arquivos intermediários gerados (scanner, parser e binário), voltando `analysis/` ao estado original, execute:

```bash
make clean
```
