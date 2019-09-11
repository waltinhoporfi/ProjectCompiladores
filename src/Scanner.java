import java.util.List;

public class Scanner {

    TokenLexema tk = new TokenLexema();
    private char lookahead = '\n';
    StringBuilder lexe = new StringBuilder();

    private List<String> arquivo;
    private int linha = 0, coluna = 0, colunaReal = 0;

    public void scan(List arquivo) {

        this.arquivo = arquivo;                //recebe o conteudo do arquivo

        while (tk != null) { // *--em testeeeee--*
            tk = scan();                      // chama o scan e retorna o token se formar...
//            if (tk == null) {
//                System.out.println("[-------------------------FIM DE ARQUIVO---------------------]");
//            } else {
//                System.out.println("< Token: " + tk.getToken() + ", Lexema: " + tk.getLexema() + " >");
//            }
            lexe.setLength(0);       //zerando o lexe p receber o prox
        }

    }

    public TokenLexema scan() {
        if (lookahead == '\n' || lookahead == ' ') {
            while (lookahead == '\n' || lookahead == ' ') {
                lookahead = proxCharact();
            }
        }
        if (verificaEspecial()) {
            lookahead = proxCharact();
            return tk;
        } else if (verificaLetra()) {
            return tk;
        } else if (verificaDigito()) {
            return tk;
        } else if (verificaOpRelacional()) {
            return tk;
        } else if (verificaOpArit()) {
            //lookahead = proxCharact();        //engolindo o prox no caso de ser so /
            return tk;
        } else {
            if (lookahead == '\u0000') {
                //return null  //fim de arquivo
            } else {
                linha++;
                mgsErr("caracter invalido");
            }
        }
        return null;
    }

    public char proxCharact() {  //tratamento de fim de linha e fim de arq
        if (linha < arquivo.size()) {
            if (coluna < arquivo.get(linha).length()) {
                if ((arquivo.get(linha).charAt(coluna)) == '\t') { //se for tab col = +4
                    while ((arquivo.get(linha).charAt(coluna)) == '\t') {
                        coluna = coluna + 1; //em testeeeee
                        colunaReal += 4;
                    }
                    colunaReal += 1;
                    return arquivo.get(linha).charAt(coluna++);
                } else {
                    colunaReal += 1;
                    return arquivo.get(linha).charAt(coluna++);

                }
            } else { //linha acabou
                linha++;
                coluna = 0;
                colunaReal = 0;
                return '\n';  // informar q acabou a linha
            }
        } else {
            return (char) 0;   //fim de arquivo \u0000
        }

    }

    private boolean verificaDigito() {
        if (Character.isDigit(lookahead)) {
            while (Character.isDigit(lookahead)) {
                insereLexe();
                lookahead = proxCharact();
            }
            if (lookahead == '.') {  //verifica se é float (so pode vim digito)
                insereLexe();
                lookahead = proxCharact();
                if (Character.isDigit(lookahead)) {
                    while (Character.isDigit(lookahead)) {
                        insereLexe();
                        lookahead = proxCharact();
                    }
                    preencheTk(TipoToken.TIPO_FLOAT);
                    return true;
                } else { ///em testeeeeeeeeee
                    linha++;      //pq começa em zero
                    mgsErr("float mal formado");
                }
            }
            preencheTk(TipoToken.TIPO_INT);
            return true;
        } else {     //caso comece com ponto
            if (lookahead == '.') {
                lexe.append('0');
                lexe.append(lookahead);
                lookahead = proxCharact();
                if (Character.isDigit(lookahead)) {
                    while (Character.isDigit(lookahead)) {
                        insereLexe();
                        lookahead = proxCharact();
                    }
                    preencheTk(TipoToken.TIPO_FLOAT);
                    return true;
                } else { ///em testeeeeeeeeee
                    mgsErr("float mal formado");
                }
            }
        }

        return false;
    }

    private boolean verificaLetra() {

        if (Character.isAlphabetic(lookahead) || lookahead == '_') {  //id

            while (Character.isAlphabetic(lookahead) || lookahead == '_'
                    || Character.isDigit(lookahead)) {
                insereLexe();
                lookahead = proxCharact();
            }
            //verifica se é palavra reservada, se n for é id
            if (verificaPalReservada()) {
                return true;
            } else {
                //id
                preencheTk(TipoToken.TIPO_IDENTIFICADOR);
                return true;
            }
        }

        return false;
    }

    private void preencheTk(TipoToken tipo) {
        tk.setToken(tipo);
        tk.setLexema(lexe.toString());
    }

    private boolean verificaEspecial() {

        if (lookahead == ';') {
            lexe.append(lookahead);
            preencheTk(TipoToken.CARACTER_ESPECIAL_PONTO_VIRGULA);
            return true;
        } else if (lookahead == '\'') {
            lexe.append(lookahead);
            lookahead = proxCharact();
            if (Character.isDigit(lookahead) || Character.isAlphabetic(lookahead)) {
                lexe.append(lookahead);
                lookahead = proxCharact();
                if (lookahead == '\'') {
                    lexe.append(lookahead);
                    preencheTk(TipoToken.TIPO_CHAR);
                    return true;
                } else {
                    linha++;
                    mgsErr("char mal formado");
                }
            } else {
                linha++;
                mgsErr("char mal formado");
            }
        } else if (lookahead == '(') {
            lexe.append(lookahead);
            preencheTk(TipoToken.CARACTER_ESPECIAL_ABRE_PARENTESES);
            return true;
        } else if (lookahead == ')') {
            lexe.append(lookahead);
            preencheTk(TipoToken.CARACTER_ESPECIAL_FECHA_PARENTESES);
            return true;
        } else if (lookahead == '{') {
            lexe.append(lookahead);
            preencheTk(TipoToken.CARACTER_ESPECIAL_ABRE_CHAVES);
            return true;
        } else if (lookahead == '}') {
            lexe.append(lookahead);
            preencheTk(TipoToken.CARACTER_ESPECIAL_FECHA_CHAVES);
            return true;
        } else if (lookahead == ',') {
            lexe.append(lookahead);
            preencheTk(TipoToken.CARACTER_ESPECIAL_VIRGULA);
            return true;
        }
        return false;
    }

    private void insereLexe() {
        lexe.append(lookahead);
    }

    private boolean verificaPalReservada() {

        if ((lexe.toString()).equals("int")) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_INT);
            return true;
        } else if ((lexe.toString()).equals("main")) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_MAIN);
            return true;
        } else if ((lexe.toString().equals("char"))) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_CHAR);
            return true;
        } else if ((lexe.toString().equals("float"))) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_FLOAT);
            return true;
        } else if ((lexe.toString().equals("if"))) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_IF);
            return true;
        } else if ((lexe.toString().equals("else"))) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_ELSE);
            return true;
        } else if ((lexe.toString().equals("do"))) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_DO);
            return true;
        } else if ((lexe.toString().equals("while"))) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_WHILE);
            return true;
        } else if ((lexe.toString().equals("for"))) {
            preencheTk(TipoToken.PALAVRA_RESERVADA_FOR);
            return true;
        }

        return false;
    }

    private boolean verificaOpRelacional() {

        if (lookahead == '<') {
            lexe.append(lookahead);
            lookahead = proxCharact();
            if (lookahead == '=') {
                lexe.append(lookahead);
                preencheTk(TipoToken.OPERADOR_RELACIONAL_MENOR_IGUAL);
                lookahead = proxCharact();
                return true;
            }
            lexe.append(lookahead);
            preencheTk(TipoToken.OPERADOR_RELACIONAL_MENOR);
            return true;
        } else if (lookahead == '>') {
            lexe.append(lookahead);
            lookahead = proxCharact();
            if (lookahead == '=') {
                lexe.append(lookahead);
                preencheTk(TipoToken.OPERADOR_RELACIONAL_MAIOR_IGUAL);
                lookahead = proxCharact();
                return true;
            }
            lexe.append(lookahead);
            preencheTk(TipoToken.OPERADOR_RELACIONAL_MAIOR);
            return true;
        } else if (lookahead == '!') {
            lexe.append(lookahead);
            lookahead = proxCharact();
            if (lookahead == '=') {
                lexe.append(lookahead);
                preencheTk(TipoToken.OPERADOR_RELACIONAL_DIFERENTE);
                lookahead = proxCharact();
                return true;
            } else {
                linha++;
                mgsErr("operador diferente mal formado");
            }
        } else if (lookahead == '=') {
            lexe.append(lookahead);
            lookahead = proxCharact();
            if (lookahead == '=') {
                lexe.append(lookahead);
                preencheTk(TipoToken.OPERADOR_RELACIONAL_IGUALDADE);
                lookahead = proxCharact();
                return true;
            }
            preencheTk(TipoToken.OPERADOR_ARITIMETICO_ATRIBUICAO);
            return true;
        }

        return false;
    }

    private boolean verificaOpArit() {

        if (lookahead == '+') {
            lexe.append(lookahead);
            preencheTk(TipoToken.OPERADOR_ARITIMETICO_SOMA);
            lookahead = proxCharact();
            return true;
        } else if (lookahead == '-') {
            lexe.append(lookahead);
            preencheTk(TipoToken.OPERADOR_ARITIMETICO_SUBTRACAO);
            lookahead = proxCharact();
            return true;
        } else if (lookahead == '*') {
            lexe.append(lookahead);
            preencheTk(TipoToken.OPERADOR_ARITIMETICO_MULTIPLICACAO);
            lookahead = proxCharact();
            return true;
        } else if (lookahead == '/') {          //erro se for so /
            lexe.append(lookahead);
            lookahead = proxCharact();               //chamando o prox pra saber se é // ou /*
            if (lookahead == '/') {         //comentario
                while (lookahead != '\n') {
                    lexe.append(lookahead);
                    lookahead = proxCharact();
                }
                preencheTk(TipoToken.COMENTARIO);
                return true;
            } else if (lookahead == '*') {
                lexe.append(lookahead);
                lookahead = proxCharact();
                while (lookahead != '\u0000') {
                    if (lookahead == '*') {
                        while (lookahead == '*') {
                            lexe.append(lookahead);
                            lookahead = proxCharact();
                        }
                        if (lookahead == '/') {
                            lexe.append(lookahead);
                            preencheTk(TipoToken.COMENTARIO);
                            lookahead = proxCharact();
                            return true;
                        }
                    }
                    lexe.append(lookahead); //se quiser ignorar o comments so tirar essa linha
                    lookahead = proxCharact();
                }
                mgsErr("comentario multilinha não fechado");
            }
            preencheTk(TipoToken.OPERADOR_ARITIMETICO_DIVISAO);
            return true;

        } else if (lookahead == '=') {
            lexe.append(lookahead);
            preencheTk(TipoToken.OPERADOR_ARITIMETICO_ATRIBUICAO);
            return true;
        }
        return false;
    }

    private void mgsErr(String msg) {
        colunaReal -= 1;
        System.out.println("ERRO na linha " + linha + ", coluna " + colunaReal + ", ultimo token lido " + tk.getToken()
                + ": " + msg);
        System.exit(0);
    }


}