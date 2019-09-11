import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Compilador {
    public static void main(String[] args) {

        try {
            var file = new FileReader("src\\filee.txt"); //args[0] //"src\\filee.txt"
            var buff = new BufferedReader(file);
            List<String> conteudoArquivo = new ArrayList<>();
            String linha;
            do {
                linha = buff.readLine();
                if (linha != null) {
                    conteudoArquivo.add(linha);     //recebe o conteudo do arquivo
                }
            } while (linha != null);        //linha serve como intermedio

            var c = new Scanner();
            c.scan(conteudoArquivo);   //mandando um list com toodo o arquivo

        } catch (IOException e) {
            System.out.println("Erro: "+e);
        }
    }
}
