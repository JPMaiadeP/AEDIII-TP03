/*Trabalho Prático AED III
Prontuário

Autor: João Paulo Maia de Paula - Matricula 702056
*/

import java.io.*;

public class Pront {
    protected Boolean lapide; //1 = Registro existe, 0 = Registro vazio
    protected int CPF;
    protected String nome;
    protected String dataNasc;
    protected String sexo;
    protected String anotacoes;

    //Construtor vazio
    public Pront() { 
        lapide = false;
        CPF = -1;
        nome = "";
        dataNasc = "";
        sexo = "";
        anotacoes = "";
    }

    //Construtor com entradas
    public Pront(Boolean l, int c, String n, String dn, String s, String a) { 
        lapide = l;
        CPF = c;
        nome = n;
        dataNasc = dn;
        sexo = s;
        anotacoes = a;
    }

    //Leitura em console
    public String toString() { 
        
        return  "\nLapide: " + lapide +
                "\nCPF: " + CPF + 
                "\nNome: " + nome + 
                "\nData de Nascimento: " + dataNasc + 
                "\nSexo: " + sexo + 
                "\nAnotações: " + anotacoes;
    }

    //Escrita em arquivo
    public byte[] getByteArray() { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeBoolean(lapide);
            dos.writeInt(CPF);
            dos.writeUTF(nome);
            dos.writeUTF(dataNasc);
            dos.writeUTF(sexo);
            dos.writeUTF(anotacoes);
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
            nome = dis.readUTF();
            dataNasc = dis.readUTF();
            sexo = dis.readUTF();
            anotacoes = dis.readUTF();
        }
        catch (IOException e) {
            e.printStackTrace();
        }        
    }

    //Inserção de novos registros
    public void Inserir(RandomAccessFile arq, long pos, int tam) {
        try {
            arq.seek(12 + (pos * tam));
            arq.write(getByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Leitura de registros
    public int Ler(RandomAccessFile arq, long pos, int tam) {
        int nBytes = 0;
        try {
            byte[] buffer = new byte[tam];
            arq.seek(12 + (pos * tam));
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