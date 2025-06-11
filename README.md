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

## Arquitetura do Projeto

O projeto FileScript possui duas implementações principais:

1. Analisador Léxico e Sintático (Flex + Bison + C)
2. FileScript Virtual Machine (JVM) - Interpretador Java

## Análise Léxica e Sintática

Via **Linux** ou **WSL** no Windows, as ferramentas **Flex** (para análise léxica) e **Bison** (para análise sintática) podem ser utilizadas para validar os scripts escritos na linguagem.

- Os arquivos de código (`scanner.l`, `parser.y`, `main.c`) estão localizados em `analysis/`
- O `Makefile` está na raiz do projeto (`FILESCRIPT/`)
- O executável gerado será `analysis/analyzer`

### ➔ Pré requisitos para a análise

Para garantir suporte adequado às ferramentas utilizadas, instale suas dependências executando os comandos abaixo:

```bash
sudo apt update
sudo apt install flex bison gcc build-essential
```

## FileScript Virtual Machine (JVM)

A FileScript VM é um interpretador completo implementado em Java que não apenas valida a sintaxe, mas executa efetivamente os programas FileScript, realizando as operações de arquivo especificadas.

### ➔ Características da VM

- **Interpretação em tempo real:** Executa comandos de arquivo durante a interpretação
- **Gerenciamento de variáveis:** Suporte completo a declaração e atribuição de variáveis
- **Estruturas de controle:** Implementação funcional de if/else e loops for
- **Operações de arquivo seguras:** Tratamento de erros e validação de paths
- **Feedback em tempo real:** Logs detalhados das operações executadas

### ➔ Pré requisitos para VM

Java JDK 8+ instalado no sistema.

## Compilação e Execução

O Makefile fornece vários comandos para diferentes necessidades:

- **Compilar tudo**

```bash
make all
```

Compila tanto o analisador quanto a VM.

- **Executar apenas análise léxica/sintática**

```bash
make analysis-run file=exemplo.txt
```

Valida a sintaxe do arquivo sem executar as operações.

- **Executar apenas na VM**

```bash
make jvm-run file=exemplo.txt
```

Interpreta e executa o programa FileScript, realizando as operações de arquivo.

- **Executar análise + VM (recomendado)**

```bash
make run file=exemplo.txt
```

Primeiro valida a sintaxe e, se válida, executa o programa na VM.

- **Limpeza dos arquivos gerados**

```bash
make clean
```

Remove todos os arquivos intermediários gerados.

- **Ajuda**

```bash
make help
```

Exibe todos os comandos disponíveis.
