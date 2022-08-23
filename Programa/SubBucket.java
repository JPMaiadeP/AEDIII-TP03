/*Trabalho Prático AED III
SubBucket

Autor: João Paulo Maia de Paula - Matricula 702056
*/

import java.io.*;

public class SubBucket { //Entradas individuais dentro do bucket
    protected Boolean lapide; //1 = Entrada existe, 0 = Entrada vazia
    protected int CPF; //CPF do registro, usado pra busca interna do bucket
    protected int endereco; //Endereço do registro

    //Construtor vazio
    public SubBucket() {
        lapide = false;
        CPF = -1;
        endereco = -1;
    }

    //Construtor com entradas
    public SubBucket(Boolean b, int c, int e) {
        lapide = b;
        CPF = c;
        endereco = e;
    }

    //Leitura em console
    public String toString() {         
        return  "\nLapide: " + lapide +
                "\nCPF: " + CPF + 
                "\nEndereço: " + endereco;
    }

    //Escrita em arquivo
    public byte[] getByteArray() { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeBoolean(lapide);
            dos.writeInt(CPF);
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
            lapide = dis.readBoolean();
            CPF = dis.readInt();
            endereco = dis.readInt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }        
    }

    //Inserção de nova entrada
    public void Inserir(RandomAccessFile arq, int pos, int tam) {
        try {
            arq.seek((pos * tam));
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
            arq.seek((pos * tam));
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

    //Leitura de entrada em endereço
    public int LerEndereco(RandomAccessFile arq, int end, int tam) {
        int nBytes = 0;
        try {
            byte[] buffer = new byte[tam];
            arq.seek(end);
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
