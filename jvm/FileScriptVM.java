package jvm;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class FileScriptVM {
    private Map<String, Object> variables = new HashMap<>();
    private String input;
    private int position = 0;
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java jvm.FileScriptVM <arquivo.txt>");
            System.exit(1);
        }
        
        try {
            String content = Files.readString(Paths.get(args[0]));
            FileScriptVM vm = new FileScriptVM();
            vm.execute(content);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Erro na execução: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void execute(String program) {
        this.input = program.trim();
        this.position = 0;
        parseBlock();
    }
    
    private void parseBlock() {
        skipWhitespace();
        if (!consume("{")) {
            throw new RuntimeException("Esperado '{'");
        }
        
        while (position < input.length() && peek() != '}') {
            parseStatement();
            skipWhitespace();
        }
        
        if (!consume("}")) {
            throw new RuntimeException("Esperado '}'");
        }
    }
    
    private void parseStatement() {
        skipWhitespace();
        
        if (consumeKeyword("var")) {
            parseVarDeclaration();
        } else if (consumeKeyword("if")) {
            parseIfStatement();
        } else if (consumeKeyword("for")) {
            parseForStatement();
        } else if (isFileOperation()) {
            parseFileOperation();
        } else if (isIdentifier()) {
            parseAssignment();
        } else {
            throw new RuntimeException("Statement inválido na posição " + position);
        }
    }
    
    private void parseVarDeclaration() {
        skipWhitespace();
        String varName = parseIdentifier();
        skipWhitespace();
        if (!consume("=")) {
            throw new RuntimeException("Esperado '=' após nome da variável");
        }
        Object value = parseExpression();
        variables.put(varName, value);
    }
    
    private void parseAssignment() {
        String varName = parseIdentifier();
        skipWhitespace();
        if (!consume("=")) {
            throw new RuntimeException("Esperado '=' na atribuição");
        }
        Object value = parseExpression();
        variables.put(varName, value);
    }
    
    private void parseIfStatement() {
        boolean condition = parseCondition();
        parseBlock();
        
        skipWhitespace();
        if (consumeKeyword("else")) {
            if (!condition) {
                parseBlock();
            } else {
                skipBlock();
            }
        }
    }
    
    private void parseForStatement() {
        while (parseCondition()) {
            int savedPos = position;
            parseBlock();
            position = savedPos;
            skipWhitespace();
            if (consumeKeyword("for")) {
            } else {
                break;
            }
        }
        if (!consumeKeyword("for")) {
            parseBlock();
        }
    }
    
    private boolean parseCondition() {
        Object left = parseExpression();
        skipWhitespace();
        
        String operator = "";
        if (consume("==")) operator = "==";
        else if (consume("!=")) operator = "!=";
        else if (consume(">")) operator = ">";
        else if (consume("<")) operator = "<";
        else throw new RuntimeException("Operador de comparação esperado");
        
        Object right = parseExpression();
        
        return evaluateCondition(left, operator, right);
    }
    
    private boolean evaluateCondition(Object left, String op, Object right) {
        if (left instanceof Number && right instanceof Number) {
            double l = ((Number) left).doubleValue();
            double r = ((Number) right).doubleValue();
            switch (op) {
                case "==": return l == r;
                case "!=": return l != r;
                case ">": return l > r;
                case "<": return l < r;
            }
        } else {
            String l = String.valueOf(left);
            String r = String.valueOf(right);
            switch (op) {
                case "==": return l.equals(r);
                case "!=": return !l.equals(r);
                case ">": return l.compareTo(r) > 0;
                case "<": return l.compareTo(r) < 0;
            }
        }
        return false;
    }
    
    private Object parseExpression() {
        skipWhitespace();
        Object left = parsePrimary();
        
        while (position < input.length()) {
            skipWhitespace();
            if (consume("+")) {
                Object right = parsePrimary();
                if (left instanceof Number && right instanceof Number) {
                    left = ((Number) left).doubleValue() + ((Number) right).doubleValue();
                } else {
                    left = String.valueOf(left) + String.valueOf(right);
                }
            } else if (consume("-")) {
                Object right = parsePrimary();
                if (left instanceof Number && right instanceof Number) {
                    left = ((Number) left).doubleValue() - ((Number) right).doubleValue();
                } else {
                    throw new RuntimeException("Operação de subtração inválida");
                }
            } else {
                break;
            }
        }
        
        return left;
    }
    
    private Object parsePrimary() {
        skipWhitespace();
        
        if (peek() == '"') {
            return parseString();
        } else if (Character.isDigit(peek())) {
            return parseNumber();
        } else if (isFileFunction()) {
            return executeFileFunction();
        } else if (isIdentifier()) {
            String id = parseIdentifier();
            return variables.getOrDefault(id, id);
        }
        
        throw new RuntimeException("Expressão inválida na posição " + position);
    }
    
    private void parseFileOperation() {
        executeFileFunction();
    }
    
    private Object executeFileFunction() {
        skipWhitespace();
        String function = parseIdentifier();
        skipWhitespace();
        
        if (!consume("(")) {
            throw new RuntimeException("Esperado '(' após função");
        }
        
        List<Object> args = new ArrayList<>();
        
        if (peek() != ')') {
            args.add(parseExpression());
            while (consume(",")) {
                args.add(parseExpression());
            }
        }
        
        if (!consume(")")) {
            throw new RuntimeException("Esperado ')' após argumentos");
        }
        
        return executeFileCommand(function, args);
    }
    
    private Object executeFileCommand(String command, List<Object> args) {
        try {
            switch (command) {
                case "MoveFile":
                    if (args.size() != 2) throw new RuntimeException("MoveFile requer 2 argumentos");
                    moveFile(String.valueOf(args.get(0)), String.valueOf(args.get(1)));
                    return null;
                    
                case "CopyFile":
                    if (args.size() != 2) throw new RuntimeException("CopyFile requer 2 argumentos");
                    copyFile(String.valueOf(args.get(0)), String.valueOf(args.get(1)));
                    return null;
                    
                case "RenameFile":
                    if (args.size() != 2) throw new RuntimeException("RenameFile requer 2 argumentos");
                    renameFile(String.valueOf(args.get(0)), String.valueOf(args.get(1)));
                    return null;
                    
                case "DeleteFile":
                    if (args.size() != 1) throw new RuntimeException("DeleteFile requer 1 argumento");
                    deleteFile(String.valueOf(args.get(0)));
                    return null;
                    
                case "ListFiles":
                    if (args.size() != 1) throw new RuntimeException("ListFiles requer 1 argumento");
                    listFiles(String.valueOf(args.get(0)));
                    return null;
                    
                case "CountFiles":
                    if (args.size() != 1) throw new RuntimeException("CountFiles requer 1 argumento");
                    return countFiles(String.valueOf(args.get(0)));
                    
                case "CheckSpace":
                    if (args.size() != 1) throw new RuntimeException("CheckSpace requer 1 argumento");
                    return checkSpace(String.valueOf(args.get(0)));
                    
                case "Wait":
                    if (args.size() != 1) throw new RuntimeException("Wait requer 1 argumento");
                    waitTime(((Number) args.get(0)).longValue());
                    return null;
                    
                default:
                    throw new RuntimeException("Função desconhecida: " + command);
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar " + command + ": " + e.getMessage());
            return command.equals("CountFiles") ? 0 : (command.equals("CheckSpace") ? 0L : null);
        }
    }
    
    private void moveFile(String source, String dest) throws IOException {
        Path src = Paths.get(source);
        Path dst = Paths.get(dest);
        if (Files.exists(src)) {
            Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Movido: " + source + " -> " + dest);
        } else {
            System.err.println("Arquivo não encontrado: " + source);
        }
    }
    
    private void copyFile(String source, String dest) throws IOException {
        Path src = Paths.get(source);
        Path dst = Paths.get(dest);
        if (Files.exists(src)) {
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copiado: " + source + " -> " + dest);
        } else {
            System.err.println("Arquivo não encontrado: " + source);
        }
    }
    
    private void renameFile(String oldName, String newName) throws IOException {
        Path old = Paths.get(oldName);
        Path newPath = Paths.get(newName);
        if (Files.exists(old)) {
            Files.move(old, newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Renomeado: " + oldName + " -> " + newName);
        } else {
            System.err.println("Arquivo não encontrado: " + oldName);
        }
    }
    
    private void deleteFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            Files.delete(path);
            System.out.println("Deletado: " + filename);
        } else {
            System.err.println("Arquivo não encontrado: " + filename);
        }
    }
    
    private void listFiles(String directory) throws IOException {
        Path dir = Paths.get(directory);
        if (Files.exists(dir) && Files.isDirectory(dir)) {
            System.out.println("Arquivos em " + directory + ":");
            Files.list(dir).forEach(path -> System.out.println("  " + path.getFileName()));
        } else {
            System.err.println("Diretório não encontrado: " + directory);
        }
    }
    
    private int countFiles(String directory) throws IOException {
        Path dir = Paths.get(directory);
        if (Files.exists(dir) && Files.isDirectory(dir)) {
            int count = (int) Files.list(dir).count();
            System.out.println("Arquivos em " + directory + ": " + count);
            return count;
        } else {
            System.err.println("Diretório não encontrado: " + directory);
            return 0;
        }
    }
    
    private long checkSpace(String directory) throws IOException {
        Path dir = Paths.get(directory);
        if (Files.exists(dir)) {
            long size = Files.walk(dir)
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        return 0L;
                    }
                })
                .sum();
            System.out.println("Espaço ocupado por " + directory + ": " + size + " bytes");
            return size;
        } else {
            System.err.println("Diretório não encontrado: " + directory);
            return 0L;
        }
    }
    
    private void waitTime(long milliseconds) {
        try {
            System.out.println("Aguardando " + milliseconds + "ms...");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private String parseString() {
        if (!consume("\"")) {
            throw new RuntimeException("Esperado '\"'");
        }
        
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && peek() != '"') {
            if (peek() == '\\') {
                position++;
                if (position < input.length()) {
                    sb.append(input.charAt(position++));
                }
            } else {
                sb.append(input.charAt(position++));
            }
        }
        
        if (!consume("\"")) {
            throw new RuntimeException("String não fechada");
        }
        
        return sb.toString();
    }
    
    private double parseNumber() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && Character.isDigit(peek())) {
            sb.append(input.charAt(position++));
        }
        return Double.parseDouble(sb.toString());
    }
    
    private String parseIdentifier() {
        StringBuilder sb = new StringBuilder();
        if (position < input.length() && (Character.isLetter(peek()) || peek() == '_')) {
            sb.append(input.charAt(position++));
            while (position < input.length() && 
                   (Character.isLetterOrDigit(peek()) || peek() == '_')) {
                sb.append(input.charAt(position++));
            }
        }
        return sb.toString();
    }
    
    private boolean isIdentifier() {
        skipWhitespace();
        return position < input.length() && (Character.isLetter(peek()) || peek() == '_');
    }
    
    private boolean isFileOperation() {
        int saved = position;
        skipWhitespace();
        String id = parseIdentifier();
        position = saved;
        return Arrays.asList("MoveFile", "CopyFile", "RenameFile", "DeleteFile", 
                           "ListFiles", "CountFiles", "CheckSpace", "Wait").contains(id);
    }
    
    private boolean isFileFunction() {
        int saved = position;
        skipWhitespace();
        String id = parseIdentifier();
        position = saved;
        return Arrays.asList("CountFiles", "CheckSpace").contains(id);
    }
    
    private boolean consumeKeyword(String keyword) {
        skipWhitespace();
        if (position + keyword.length() <= input.length() &&
            input.substring(position, position + keyword.length()).equals(keyword) &&
            (position + keyword.length() >= input.length() || 
             !Character.isLetterOrDigit(input.charAt(position + keyword.length())))) {
            position += keyword.length();
            return true;
        }
        return false;
    }
    
    private boolean consume(String str) {
        skipWhitespace();
        if (position + str.length() <= input.length() &&
            input.substring(position, position + str.length()).equals(str)) {
            position += str.length();
            return true;
        }
        return false;
    }
    
    private char peek() {
        return position < input.length() ? input.charAt(position) : '\0';
    }
    
    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }
    
    private void skipBlock() {
        skipWhitespace();
        if (!consume("{")) return;
        
        int braceCount = 1;
        while (position < input.length() && braceCount > 0) {
            if (peek() == '{') {
                braceCount++;
            } else if (peek() == '}') {
                braceCount--;
            }
            position++;
        }
    }
}