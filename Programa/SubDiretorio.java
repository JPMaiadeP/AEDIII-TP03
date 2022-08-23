/*Trabalho Prático AED III
SubDiretorio

Autor: João Paulo Maia de Paula - Matricula 702056
*/

import java.io.*;

public class SubDiretorio {
    protected int tamCabecalho = 4;

    //Variaveis de controle e manipulação
    protected int valor;
    protected int endereco;

    //Construtor vazio
    public SubDiretorio() {
        valor = -1;
        endereco = -1;
    }

    //Construtor com entrada de endereço
    public SubDiretorio(int e) {
        valor = -1;
        endereco = e;
    }
    

    //Construtor com entradas
    public SubDiretorio(int p, int e) {
        valor = p;
        endereco = e;
    }

    //Leitura em console
    public String toString() {         
        return  "\nValor: " + valor +
                "\nEndereço: " + endereco;
    }

    //Escrita em arquivo
    public byte[] getByteArray() { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(valor);
            dos.writeInt(endereco);
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
            valor = dis.readInt();
            endereco = dis.readInt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }        
    }

    //Inserção de nova entrada
    public void Inserir(RandomAccessFile arq, int pos, int tam) {
        try {
            arq.seek(tamCabecalho + (pos * tam));
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
            arq.seek(tamCabecalho + (pos * tam));
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
