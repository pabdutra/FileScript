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
