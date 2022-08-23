/*Trabalho Prático AED III
Criação dos arquivos

Autor: João Paulo Maia de Paula - Matricula 702056
*/

import java.io.*;
import java.util.*;

public class CArq {
    public static void criaArquivo() {        

        //Variaveis de input, controle e manipulação
        Scanner sc = new Scanner(System.in);
        int inptPront = 0;
        int inptInd = 0;
        int inptDir = 0;
        int tempPG = 0;
        boolean loop = true;

        //Cria e deleta os arquivos anteriores
        File dir = new File("dados/diretorio.db");
        if (dir.delete())
            System.out.println("Diretorio anterior deletado");
        else
            System.out.println("Não existe diretorio anterior");
        File ind = new File("dados/indice.db");
        if (ind.delete())
            System.out.println("Indice anterior deletado");
        else
            System.out.println("Não existe indice anterior");
        File prt = new File("dados/prontuarios.db");
        if (prt.delete())
            System.out.println("Prontuário anterior deletado");
        else
            System.out.println("Não existe prontuário anterior");
        File tmp = new File("dados/logTempo.db");
        

        ////Input de valores        
        //Loop de input para tamanho da profundidade global
        while (loop){
            try {
                System.out.println("Qual a profundidade global do diretório?");
                inptDir = sc.nextInt();
                tempPG = inptDir;
                loop = false;            
            } catch (InputMismatchException e) { //Testa se o input é um número
                System.out.println("Não é um número!");
                sc.next();
            }
        }
        loop=true;
        //Loop de input para a quantidade de entradas 
        while (loop){
            try {
                System.out.println("Qual a quantidade de entradas por bucket?");
                inptInd = sc.nextInt();
                loop = false;            
            } catch (InputMismatchException e) { //Testa se o input é um número
                System.out.println("Não é um número!");
                sc.next();
            }
        }
        loop=true; 
        //Loop de input para tamanho das anotações        
        while (loop){
            try {
                System.out.println("Qual o tamanho das anotações do prontuário?");
                inptPront = sc.nextInt();
                loop = false;            
            } catch (InputMismatchException e) { //Testa se o input é um número
                System.out.println("Não é um número!");
                sc.next();
            }
        }
        loop=true;


        //-----Arq Mestre INICIO-----
        try {            
            RandomAccessFile raf = new RandomAccessFile("dados/prontuarios.db", "rw");
            //Inicio cabeçalho
            raf.writeInt(0); //Número de registros já inseridos, ID
            raf.writeInt(inptPront); //Tamanho fixo das anotações do prontuário
            raf.writeInt(0); //Quantidade de registros removidos   
            //Fim cabeçalho
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }            
        //-----Arq Mestre FIM-----

        //-----Indice INICIO-----
        List<Integer> enderBucket = new ArrayList<>();
        try {            
            RandomAccessFile raf = new RandomAccessFile("dados/indice.db", "rw");            
            raf.seek(0);
            int qtdPag = Diretorio.calculaQtdPaginas(tempPG);
            for (int i=0; i<qtdPag; i++) {  
                //Armazena o endereço inicial do bucket
                enderBucket.add((int) raf.getFilePointer());
                //Escreve o cabeçalho do bucket
                raf.writeInt(tempPG); //Profundidade local
                raf.writeInt(inptInd); //Quantidade de entradas por bucket  
                raf.writeInt(0); //Quantidade de entradas cheias                           
                //Escreve os subbuckets
                for (int j=0; j<inptInd; j++) {                                        
                    raf.writeBoolean(false);          
                    raf.writeInt(-1);
                    raf.writeInt(-1);
                }                
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }    

        //-----Indice FIM-----        

        //-----Diretorio INICIO-----
        try {            
            RandomAccessFile raf = new RandomAccessFile("dados/diretorio.db", "rw");
            //Inicio cabeçalho
            raf.writeInt(tempPG); //Tamanho da profundidade global
            //Inicio páginas     
            int qtdPag = Diretorio.calculaQtdPaginas(tempPG);
            for (int i=0; i<qtdPag; i++) {             
                raf.writeInt(i);
                raf.writeInt(enderBucket.get(i));
            }
            //Fim páginas
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }            
        //-----Diretorio FIM-----
    }   
}
