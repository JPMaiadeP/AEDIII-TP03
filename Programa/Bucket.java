/*Trabalho Prático AED III
Bucket

Autor: João Paulo Maia de Paula - Matricula 702056
*/

import java.io.*;

public class Bucket {
    //Cabeçalho, tamanho = 12
    private int profLocal; //Profundidade local
    private int qtdPorBucket; //Quantidade de entradas no bucket
    private int qtdSBUsados = 0; //Quantidade de entradas removidas

    //Variaveis de controle e manipulação   
    static int tamCab = 12;
    static int tamSB = 1 + 4 + 4;
    static int qtdSB = buscaQtdSB();
    static int tamBucket = tamCab + (tamSB * qtdSB);       

    //Vetor para carregar os SubBuckets em memória
    private static SubBucket vetSB[] = new SubBucket[qtdSB];

    //Construtor vazio
    public Bucket(){
        profLocal = -1;
        qtdPorBucket = qtdSB;
        qtdSBUsados = 0;
        iniciaVetorSB(qtdSB);
    }

    //Construtor com entrada de profundidade local
    public Bucket(int p){
        profLocal = p;
        qtdPorBucket = qtdSB;
        qtdSBUsados = 0;
        iniciaVetorSB(qtdSB);
    }

    //Construtor com 2 entradas
    public Bucket(int p, int qsb){
        profLocal = p;
        qtdPorBucket = qtdSB;
        qtdSBUsados = qsb;
        iniciaVetorSB(qtdSB);
    }
    

    //Busca a quantidade de entradas por bucket no cabeçalho
    public static int buscaQtdSB () { 
        try {                   
            int tempSB;           
            RandomAccessFile raf = new RandomAccessFile("dados/indice.db", "rw");
            raf.seek(4);
            tempSB = raf.readInt();    
            raf.close(); 
            return tempSB;               
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");   
            return -2;                         
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }        
    }
    
    /*
    //Inicializa o vetor de SubBuckets
    public static void iniciaVetorSB() {
        for (int i=0; i<qtdSB; i++) {
            vetSB[i] = new SubBucket();
        }
    }*/

    //Inicializa o vetor de SubBuckets com entrada
    public static SubBucket[] iniciaVetorSB(int inQtdSB) {        
       SubBucket vetSB[] = new SubBucket[inQtdSB]; //Vetor para carregar os SubBuckets em memória
       for (int i=0; i<inQtdSB; i++) {
           vetSB[i] = new SubBucket();
       }
       return vetSB;
   }

    //Leitura em console
    public String toString() { 
        String str = "\n---Cabeçalho---" + 
                    "\nProfundidade local: " + profLocal + 
                    "\nQuantidade de SubBuckets vazios: " + qtdPorBucket + 
                    "\nQuantidade de SubBuckets removidos: " + qtdSBUsados;

        for (int i = 0; i<qtdSB; i++) {
            if (vetSB[i].lapide == true && vetSB[i].CPF != -1) {
                str += "\nSubBucket " + (i+1) + 
                "\nLapide: " + vetSB[i].lapide + 
                "\nCPF: " + vetSB[i].CPF + 
                "\nEndereço do registro: " + vetSB[i].endereco;
            }
        }
        return  str;
    }

    //Escrita em arquivo
    public byte[] getByteArray() { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(profLocal);
            dos.writeInt(qtdPorBucket);
            dos.writeInt(qtdSBUsados);
            for(int i = 0; i<qtdSB; i++) {
                dos.writeBoolean(vetSB[i].lapide);                
                dos.writeInt(vetSB[i].CPF);
                dos.writeInt(vetSB[i].endereco);
            }
            dos.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    //Leitura do arquivo
    public void setByteArray(byte [] aByte) { 
        ByteArrayInputStream bais = new ByteArrayInputStream(aByte);
        DataInputStream dis = new DataInputStream(bais);

        try {
            profLocal = dis.readInt();
            qtdPorBucket = dis.readInt();
            qtdSBUsados = dis.readInt();
            for(int i = 0; i<qtdSB; i++) {
                vetSB[i].lapide = dis.readBoolean();
                vetSB[i].CPF = dis.readInt();
                vetSB[i].endereco = dis.readInt();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }        
    }

    //Inserção de nova entrada
    public void Inserir(RandomAccessFile arq, int pos, int tam) {
        try {
            arq.seek(tamCab + (pos * tam));
            arq.write(getByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Inserção de nova entrada em endereço
    public void InserirEndereco(RandomAccessFile arq, int end) {
        try {
            arq.seek(end);
            arq.write(getByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Leitura de entrada
    public int Ler(RandomAccessFile arq, int pos, int tam) {
        int nBytes = 0;
        try {
            byte[] buffer = new byte[tam];
            arq.seek(tamCab + (pos * tam));
            nBytes = arq.read(buffer);
            if (nBytes > 0)
                setByteArray(buffer);            
        } catch (EOFException e) {
            //Retorna -1 se atingiu o fim do arquivo
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nBytes;        
    }
    
}
