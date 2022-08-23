/*Trabalho Prático AED III
Diretorio

Autor: João Paulo Maia de Paula - Matricula 702056
*/

import java.io.*;

public class Diretorio {
    //Cabeçalho, tamanho = 4
    private int profGlobal; //Profundidade global
    
    //Variaveis de controle e manipulação   
    static int tamCab = 4;
    static int tamSD = 4 + 4;
    //static int pGlobal = -1;
    int qtdPag = calculaQtdPaginas(profGlobal);         
    int tamDiretorio = tamCab + (tamSD * qtdPag);   
    
    //Vetor para carregar os SubDiretorios em memória
    private SubDiretorio vetSD[] = new SubDiretorio[qtdPag];
    
    
    //Construtor vazio
    public Diretorio() {
        profGlobal = -1;     
        iniciaVetorSD();   
    }

    //Construtor com entrada
    public Diretorio(int p) {
        profGlobal = p;
        iniciaVetorSD();
    }

    //Calcula o número de páginas
    public static int calculaQtdPaginas(int pg) { 
        int temp = 2;
        for (int i=1; i<pg; i++)
        temp *= 2;
        return temp;
    }

    /*//Busca a profundidade global no cabeçalho
    public static int buscaProfGlobal () {
        try {                  
            RandomAccessFile raf = new RandomAccessFile("dados/diretorio.db", "rw");
            raf.seek(0);
            pGlobal = raf.readInt();    
            raf.close();                
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return pGlobal;
    }*/
    
    //Inicializa o vetor de SubBuckets com entrada
    public SubDiretorio[] iniciaVetorSD() {
        int numPag = calculaQtdPaginas(profGlobal);
        
        SubDiretorio vetSD[] = new SubDiretorio[numPag]; //Vetor para carregar os SubDiretorios em memória
        for (int i=0; i<numPag; i++) {
           vetSD[i] = new SubDiretorio();
           vetSD[i].valor = i;
        }
        return vetSD;
   }

    //Leitura em console
    public String toString() { 
        String str = "\n---Cabeçalho---" + 
                    "\nProfundidade global: " + profGlobal;
        for (int i = 0; i<qtdPag; i++) {
            str += "\nSubDiretorio " + vetSD[i].valor + 
            "\nEndereço do bucket: " + vetSD[i].endereco;            
        }
        return  str;
    }

    //Escrita em arquivo
    public byte[] getByteArray() { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(profGlobal);
            for(int i = 0; i<qtdPag; i++) {                
                dos.writeInt(vetSD[i].valor);
                dos.writeInt(vetSD[i].endereco);
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
            profGlobal = dis.readInt();
            for(int i = 0; i<qtdPag; i++) {
                vetSD[i].valor = dis.readInt();
                vetSD[i].endereco = dis.readInt();
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
