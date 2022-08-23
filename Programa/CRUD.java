/*Trabalho Prático AED III
Inserção, edição, remoção e impressão

Autor: João Paulo Maia de Paula - Matricula 702056
*/

import java.io.*;
import java.util.*;

public class CRUD {

    //Manipulação do tamanho das Strings
    public static String fixaString (String inStr, int tam) {
        //Caso a String for maior que tamanho máximo, corta o fim
        if (inStr.length() > tam)
            inStr = inStr.substring(0, tam);
        //Caso a String seja menor, preenche o restante com espaços                
        else
            while (inStr.length() < tam)
                inStr += " ";
        return inStr;
    }
    /*public static String fixedLengthString(String inString, int tam) {
        return String.format("%1$" + tam + "s", inString);
    }*/

    ////Variaveis do cabeçalho
    //Diretório, tamanho = 4
    static int cabDiretorio = 4; //Tamanho do cabeçalho do diretório
    static int pfGlobal = -1;
    //Índice, tamanho = 12
    static int cabBucket = 12; //Tamanho do cabeçalho do índice
    static int pfLocal = -1;
    static int qtdPorBucket = -1;
    static int qtdSBCheios = -1;
    //Arquivo mestre, tamanho = 12
    static int qtdID = -1;
    static int tamAnota = -1;
    static int qtdProntRemov = -1; 
    
    ////Variaveis de input, controle e manipulação
    static Scanner sc = new Scanner(System.in);    
    static RandomAccessFile raf;
    //Diretorio
    static int tamSubDir = 8; //Tamanho de cada subdiretório = 8 (2 int)
    static int tamDiretorio = -1;
    static int qtdPag = -1;
    //Indice
    static int tamSubBuc = 9; //Tamanho de cada subbucket = 9 (1 bool, 2 int)
    static int tamBucket = -1;
    //Arquivo mestre
    static int tamLapi = 1;
    static int tamCPF = 4;
    static int tamNome = 60; //+2 por ser String
    static int tamNasc = 10; //+2 por ser String
    static int tamSexo = 1; //+2 por ser String
    static int tamReg = -1; 
    static int qtdAutoInsere = 5000000; //Tamanho de quantos registros serão automaticamente inseridos  
    
    //Carrega profundidade global em pfGlobal
    public void profGlobalInicial() {
        try {
            raf = new RandomAccessFile("dados/diretorio.db", "rw");
            raf.seek(0);  
            pfGlobal = raf.readInt();  
            qtdPag = Diretorio.calculaQtdPaginas(pfGlobal); //Calcula a quantidade de páginas e carrega em qtdPag
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo prontuarios.db não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    //Função hash
    public static int vHash (int inK) {
        //int temp = (inK + (inStr.hashCode() & 0x7fffffff)) % Diretorio.calculaQtdPaginas(pfGlobal);
        return inK % (int)Math.pow(2, Diretorio.calculaQtdPaginas(pfGlobal));
    }

    //C - Insere o registro
    public static void InserirReg() {
        List<Integer> enderPront = new ArrayList<Integer>();
        //int enderPront = -1;     
        int enderBucket = -1;   

        //-----Arquivo mestre INICIO-----
        //Leitura do cabeçalho
        try {      
            raf = new RandomAccessFile("dados/prontuarios.db", "rw");
            raf.seek(0);                                      
            qtdID = raf.readInt();
            tamAnota = raf.readInt();
            qtdProntRemov = raf.readInt();
            tamReg = tamLapi + tamCPF + tamNome+2 + tamNasc+2 + tamSexo+2 + tamAnota+2; //Tamanho total do registro
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Inserção do Registro
        Pront bufferPront = new Pront();
        try {              
            raf = new RandomAccessFile("dados/prontuarios.db", "rw");  
            bufferPront.lapide = true;                         
            bufferPront.CPF = qtdID + 1;
            System.out.println("Digite as informações do paciente");
            System.out.println("Nome:");
            bufferPront.nome = fixaString(sc.nextLine(), tamNome);
            System.out.println("Data de nascimento (DD/MM/AAAA):");
            bufferPront.dataNasc = fixaString(sc.nextLine(), tamNasc);
            System.out.println("Sexo (M/F/O):");
            bufferPront.sexo = fixaString(sc.nextLine(), tamSexo);
            String anot = "Vazio";
            bufferPront.anotacoes = fixaString(anot, tamAnota);

            //Teste se existem registros vazios
            if (qtdProntRemov >= 1){
                int pos = 0;
                int loop = 0;
                raf.seek(12);
                //Teste sequencial das lapides
                Pront lePront = new Pront();
                do {
                    //Teste se o registro foi deletado e atingiu o fim do arquivo  
                    loop = lePront.Ler(raf, pos, tamReg);             
                    if (!lePront.lapide && lePront.CPF != -1){ 
                        //Insere o registro no primeiro campo vazio   
                        enderPront.add((int) raf.getFilePointer());
                        bufferPront.Inserir(raf, pos, tamReg);
                        loop = -1;
                        //Atualiza no cabeçalho a quantidade de registros removidos
                        raf.seek(8);
                        raf.writeInt(--qtdProntRemov);
                    }
                    pos++;
                } while (loop != -1 && pos < qtdID);
            }
            //Caso não hajam registros vazio, insere no final do arquivo
            else {
                enderPront.add((int) raf.getFilePointer());
                bufferPront.Inserir(raf, qtdID, tamReg);
            }
            
            //Atualiza no cabeçalho o último ID usado
            raf.seek(0);            
            raf.writeInt(++qtdID);
            raf.close();    
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo prontuarios.db não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }    
        //-----Arquivo mestre FIM----- 

        //-----Diretório INICIO-----  
        try {
            raf = new RandomAccessFile("dados/diretorio.db", "rw");
            //Guarda o valor hash do prontuario criado  
            int tempHash = vHash(bufferPront.CPF); 
            int pos = 0;
            int loop = 0;
            int valor = -1;
            
            //Leitura do cabeçalho
            raf.seek(0);  
            pfGlobal = raf.readInt();  
            
            //Busca qual bucket o prontuário pertence
            do {
                valor = raf.readInt();
                enderBucket = raf.readInt();
                if (tempHash == valor)
                    loop = -1;
                pos++;
            } while (loop != -1 && pos < qtdPag);
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo diretorio.db não encontrado. Crie o arquivo primeiro!");
        } catch (Exception e) {
            e.printStackTrace();
        }        
        //-----Diretório FIM-----
        
        //-----Índice INICIO-----
        try {
            raf = new RandomAccessFile("dados/indice.db", "rw");
            int pos = 0;
            int loop = 0;
            Boolean lap;
            //Pula para o endereço do bucket correto
            raf.seek(enderBucket);
            //Leitura do cabeçalho
            pfLocal = raf.readInt(); 
            qtdPorBucket = raf.readInt(); 
            qtdSBCheios = raf.readInt();        
            
            //Checa se o bucket está cheio 
            if(qtdSBCheios < qtdPorBucket){          
                do {     
                    lap = raf.readBoolean();
                    if (lap == false) {
                        //Escreve o novo SubBucket
                        SubBucket tempSB = new SubBucket(true, bufferPront.CPF, enderPront.get(0));                    
                        tempSB.InserirEndereco(raf, (enderBucket+12+(pos*tamSubBuc)));
                        //Atualiza a quantidade de SubBuckets em uso
                        raf.seek(8+enderBucket);
                        raf.writeInt(++qtdSBCheios);
                        loop = -1;
                    }
                    else {
                        raf.readInt();
                        raf.readInt();
                    }
                    pos++;
                } while (loop != -1 && pos < qtdPorBucket);
            }
            else {
                pos = 0;
                loop = 0;
                //Pula para o endereço do próximo bucket
                raf.seek(enderBucket+enderBucket);
                //Leitura do cabeçalho
                pfLocal = raf.readInt(); 
                qtdPorBucket = raf.readInt(); 
                qtdSBCheios = raf.readInt();
                do {     
                    lap = raf.readBoolean();
                    if (lap == false) {
                        //Escreve o novo SubBucket
                        SubBucket tempSB = new SubBucket(true, bufferPront.CPF, enderPront.get(0));                    
                        tempSB.InserirEndereco(raf, (enderBucket+12+(pos*tamSubBuc)));
                        //Atualiza a quantidade de SubBuckets em uso
                        raf.seek(8+enderBucket);
                        raf.writeInt(++qtdSBCheios);
                        loop = -1;
                    }
                    else {
                        raf.readInt();
                        raf.readInt();
                    }
                    pos++;
                } while (loop != -1 && pos < qtdPorBucket);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo indice.db não encontrado. Crie o arquivo primeiro!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-----Índice FIM-----  
    }

    //// AutoInsere o registro
    public static void AutoInserirReg() {
        long tempoInicial = System.currentTimeMillis();
        List<Integer> enderPront = new ArrayList<Integer>();
        //int enderPront = -1;     
        int enderBucket = -1;   

        //-----Arquivo mestre INICIO-----
        //Leitura do cabeçalho
        try {      
            raf = new RandomAccessFile("dados/prontuarios.db", "rw");
            raf.seek(0);                                      
            qtdID = raf.readInt();
            tamAnota = raf.readInt();
            qtdProntRemov = raf.readInt();
            tamReg = tamLapi + tamCPF + tamNome+2 + tamNasc+2 + tamSexo+2 + tamAnota+2; //Tamanho total do registro
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Inserção do Registro
        Pront bufferPront = new Pront();         
        String bufferString = "";
        String auxString;
        for (int i=0; i<qtdAutoInsere;i++){
            try {              
                raf = new RandomAccessFile("dados/prontuarios.db", "rw");  
                bufferPront.lapide = true;                         
                bufferPront.CPF = qtdID + 1;
                auxString = "" + i;
                bufferString = "Nome" + auxString;
                bufferPront.nome = fixaString(bufferString, tamNome);
                bufferString = "00/00/000" + auxString;
                bufferPront.dataNasc = fixaString(bufferString, tamNasc);
                bufferString = "O";
                bufferPront.sexo = fixaString(bufferString, tamSexo);
                String anot = "Vazio";
                bufferPront.anotacoes = fixaString(anot, tamAnota);

                //Teste se existem registros vazios
                if (qtdProntRemov >= 1){
                    int pos = 0;
                    int loop = 0;
                    raf.seek(12);
                    //Teste sequencial das lapides
                    Pront lePront = new Pront();
                    do {
                        //Teste se o registro foi deletado e atingiu o fim do arquivo  
                        loop = lePront.Ler(raf, pos, tamReg);             
                        if (!lePront.lapide && lePront.CPF != -1){ 
                            //Insere o registro no primeiro campo vazio   
                            enderPront.add((int) raf.getFilePointer());
                            bufferPront.Inserir(raf, pos, tamReg);
                            loop = -1;
                            //Atualiza no cabeçalho a quantidade de registros removidos
                            raf.seek(8);
                            raf.writeInt(--qtdProntRemov);
                        }
                        pos++;
                    } while (loop != -1 && pos < qtdID);
                }
                //Caso não hajam registros vazio, insere no final do arquivo
                else {
                    enderPront.add((int) raf.getFilePointer());
                    bufferPront.Inserir(raf, qtdID, tamReg);
                }
                
                //Atualiza no cabeçalho o último ID usado
                raf.seek(0);            
                raf.writeInt(++qtdID);
                raf.close();    
            } catch (FileNotFoundException e) {
                System.out.println("Arquivo prontuarios.db não encontrado. Crie o arquivo primeiro!");                            
            } catch (Exception e) {
                e.printStackTrace();
            }    
            //-----Arquivo mestre FIM----- 

            //-----Diretório INICIO-----  
            try {
                raf = new RandomAccessFile("dados/diretorio.db", "rw");
                //Guarda o valor hash do prontuario criado  
                int tempHash = vHash(bufferPront.CPF); 
                int pos = 0;
                int loop = 0;
                int valor = -1;
                
                //Leitura do cabeçalho
                raf.seek(0);  
                pfGlobal = raf.readInt();  
                
                //Busca qual bucket o prontuário pertence
                do {
                    valor = raf.readInt();
                    enderBucket = raf.readInt();
                    if (tempHash == valor)
                        loop = -1;
                    pos++;
                } while (loop != -1 && pos < qtdPag);
                raf.close();
            } catch (FileNotFoundException e) {
                System.out.println("Arquivo diretorio.db não encontrado. Crie o arquivo primeiro!");
            } catch (Exception e) {
                e.printStackTrace();
            }        
            //-----Diretório FIM-----
            
            //-----Índice INICIO-----
            try {
                raf = new RandomAccessFile("dados/indice.db", "rw");
                int pos = 0;
                int loop = 0;
                Boolean lap;
                //Pula para o endereço do bucket correto
                raf.seek(enderBucket);
                //Leitura do cabeçalho
                pfLocal = raf.readInt(); 
                qtdPorBucket = raf.readInt(); 
                qtdSBCheios = raf.readInt();
                
                //Checa se o bucket está cheio 
            if(qtdSBCheios < qtdPorBucket){          
                do {     
                    lap = raf.readBoolean();
                    if (lap == false) {
                        //Escreve o novo SubBucket
                        SubBucket tempSB = new SubBucket(true, bufferPront.CPF, enderPront.get(0));                    
                        tempSB.InserirEndereco(raf, (enderBucket+12+(pos*tamSubBuc)));
                        //Atualiza a quantidade de SubBuckets em uso
                        raf.seek(8+enderBucket);
                        raf.writeInt(++qtdSBCheios);
                        loop = -1;
                    }
                    else {
                        raf.readInt();
                        raf.readInt();
                    }
                    pos++;
                } while (loop != -1 && pos < qtdPorBucket);
            }
            else {
                pos = 0;
                loop = 0;
                //Pula para o endereço do próximo bucket
                raf.seek(enderBucket+enderBucket);
                //Leitura do cabeçalho
                pfLocal = raf.readInt(); 
                qtdPorBucket = raf.readInt(); 
                qtdSBCheios = raf.readInt();
                do {     
                    lap = raf.readBoolean();
                    if (lap == false) {
                        //Escreve o novo SubBucket
                        SubBucket tempSB = new SubBucket(true, bufferPront.CPF, enderPront.get(0));                    
                        tempSB.InserirEndereco(raf, (enderBucket+12+(pos*tamSubBuc)));
                        //Atualiza a quantidade de SubBuckets em uso
                        raf.seek(8+enderBucket);
                        raf.writeInt(++qtdSBCheios);
                        loop = -1;
                    }
                    else {
                        raf.readInt();
                        raf.readInt();
                    }
                    pos++;
                } while (loop != -1 && pos < qtdPorBucket);
            }
            } catch (FileNotFoundException e) {
                System.out.println("Arquivo indice.db não encontrado. Crie o arquivo primeiro!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //-----Índice FIM-----
        }
        //Calcula o tempo de execução em millisegundos e armazena no arquivo log
        long tempFinal = System.currentTimeMillis() - tempoInicial;        
        try {
            raf = new RandomAccessFile("dados/logTempo.db", "rw");
            raf.writeLong(tempFinal);
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo logTempo.db não encontrado. Crie o arquivo primeiro!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Tempo de inserção: " + tempFinal + " ms (" + (tempFinal/1000) + " s)");
    }
    

    //U - Busca e edita o registro
    public static void EditarReg() {
        //Leitura do cabeçalho
        try {      
            raf = new RandomAccessFile("dados/prontuarios.db", "rw");
            raf.seek(0);                                      
            qtdID = raf.readInt();
            tamAnota = raf.readInt();
            qtdProntRemov = raf.readInt();
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Variaveis de controle
        Boolean loop = true;
        tamReg = tamLapi + tamCPF + tamNome+2 + tamNasc+2 + tamSexo+2 + tamAnota+2; //Tamanho total do registro

        //Testa se existem prontuários cadastrados
        if (qtdID > 0) {
            //Loop enquanto o input não for um número 
            while (loop){            
                try {
                    raf = new RandomAccessFile("dados/prontuarios.db", "rw");
                    raf.seek(12);

                    //Variaveis
                    int inpt = -1;
                    long pos = 0;
                    Boolean achou = false;
                    Pront bufferPront = new Pront();
                    
                    System.out.println("Número de prontuários registrados: " + (qtdID - qtdProntRemov));
                    System.out.println("Digite o código para a busca:");
                    inpt = sc.nextInt();
                    sc.nextLine();
                    do {    
                        bufferPront.Ler(raf, pos, tamReg);
                        pos++;
                        if (bufferPront.lapide && inpt == bufferPront.CPF)
                            achou = true;
                    } while (!achou && pos <= qtdID);
                    if (achou) { 
                        System.out.println(bufferPront.toString());
                        System.out.println("Prontuário localizado, digite as anotações (até " + tamAnota + " caracteres):");
                        String inStr = sc.nextLine();
                        bufferPront.anotacoes = fixaString(inStr, tamAnota);
                        bufferPront.Inserir(raf, --pos, tamReg);
                        loop = false;
                    }
                    else {
                        System.out.println("Prontuário não localizado");   
                        loop = false;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Não é um número!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
        }
        else {
            System.out.println("Não existem prontuários registrados");
        }   
    }

    //D - Deleta o registro
    public static int RemoverReg() {
        //Leitura do cabeçalho
        try {      
            raf = new RandomAccessFile("dados/prontuarios.db", "rw");
            raf.seek(0);                                      
            qtdID = raf.readInt();
            tamAnota = raf.readInt();
            qtdProntRemov = raf.readInt();            
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Variaveis de controle
        Boolean loop = true;
        tamReg = tamLapi + tamCPF + tamNome+2 + tamNasc+2 + tamSexo+2 + tamAnota+2; //Tamanho total do registro

        //Testa se existem prontuários cadastrados
        if (qtdID > 0) {
            //Loop enquanto o input não for um número 
            while (loop){            
                try {
                    raf = new RandomAccessFile("dados/prontuarios.db", "rw");
                    raf.seek(12);

                    //Variaveis
                    int inpt = -1;
                    long pos = 0;
                    Boolean achou = false;
                    Pront bufferPront = new Pront();
                    String confirma;
                    
                    System.out.println("Número de prontuários registrados: " + qtdID);
                    System.out.println("Digite o código para a busca:");
                    inpt = sc.nextInt();
                    sc.nextLine();
                    //Testa se achou o prontuário
                    do {    
                        bufferPront.Ler(raf, pos, tamReg);                        
                        if (bufferPront.lapide && inpt == bufferPront.CPF)
                            achou = true;
                        pos++;
                    } while (!achou && pos <= qtdID);

                    //Se achou, deleta com confirmação
                    if (achou) { 
                        System.out.println(bufferPront.toString());
                        System.out.println("Prontuário localizado, deletar? (S/N)");
                        confirma = sc.nextLine();
                        if (confirma.startsWith("S") || confirma.startsWith("s")) {                                                    
                            bufferPront.lapide = false;                            
                            bufferPront.Inserir(raf, --pos, tamReg);
                            System.err.println("Deletado");
                            //Atualiza no cabeçalho a quantidade de registros removidos
                            raf.seek(8);                            
                            raf.writeInt(++qtdProntRemov);
                            raf.close();
                            //Retorna o valor CPF do registro removido
                            return bufferPront.CPF;
                        } 
                        else if (confirma.startsWith("N") || confirma.startsWith("n"))
                            System.out.println("Deleção cancelada");
                        else
                            System.out.println("Opção inválida");                        
                    }
                    else {
                        System.out.println("Prontuário não localizado");                          
                    }
                    loop = false;
                    //Retorna 0 caso não tenham sido feitas alterações
                    return 0;
                } catch (InputMismatchException e) {
                    System.out.println("Não é um número!");
                } catch (Exception e) {
                    e.printStackTrace();
                }                
            } 
        }
        else {
            System.out.println("Não existem prontuários registrados");
            //Retorna 0 caso não tenham sido feitas alterações
            return 0;
        }  
        //Retorna -1 caso ocorra erro
        return -1;        
    }

    //R - Impressão dos arquivos
    public static void ImprimirArq() {
        long tempoInicial = System.currentTimeMillis();
        //Variaveis        
        int pos = 0;
        int loop = 0;

        //Impressão do diretório
        try {      
            SubDiretorio bufferSubDir = new SubDiretorio();
            raf = new RandomAccessFile("dados/diretorio.db", "rw");
            raf.seek(0);  
            pfGlobal = raf.readInt();  
            qtdPag = Diretorio.calculaQtdPaginas(pfGlobal);

            //Impressão do cabeçalho
            System.out.println("\n---Cabeçalho diretório---" + 
                            "\nProfundidade global: " + pfGlobal);

            //Impressão das páginas        
            while (loop != -1 && pos < qtdPag) {                                     
                loop = bufferSubDir.Ler(raf, pos, tamSubDir);
                System.out.println(bufferSubDir.toString());        
                pos++;     
            } 
            System.out.println("\nFim do arquivo");           
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }
        pos = 0;
        loop = 0;

        //Impressão do índice
        try {                  
            raf = new RandomAccessFile("dados/indice.db", "rw");
            SubBucket bufferSubBuc = new SubBucket();             
            raf.seek(4);  
            qtdPorBucket = raf.readInt(); 
           
            tamBucket = cabBucket + (qtdPorBucket * tamSubBuc);

            raf.seek(0);
            //Impressão dos buckets
            for (int i=0; i<qtdPag; i++) {                
                pfLocal = raf.readInt(); 
                qtdPorBucket = raf.readInt(); 
                qtdSBCheios = raf.readInt();
                //Impressão do cabeçalho
                System.out.println("\n---Cabeçalho índice " + i + "---" +  
                "\nProfundidade local: " + pfLocal + 
                "\nQuantidade de entradas: " + qtdPorBucket +
                "\nQuantidade de entradas utilizadas: " + qtdSBCheios);
                //Impressão dos subbuckets
                for (int j=0; j<qtdPorBucket; j++) {
                    //Testa se o registro não foi deletado  
                    bufferSubBuc.lapide = raf.readBoolean();
                    bufferSubBuc.CPF = raf.readInt();
                    bufferSubBuc.endereco = raf.readInt();                              
                    if (bufferSubBuc.lapide && bufferSubBuc.CPF != -1){                    
                        System.out.println(bufferSubBuc.toString());
                    } 
                }
            }      
            System.out.println("\nFim do arquivo");           
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }
        pos = 0;
        loop = 0;
        

        //Impressão do arquivo mestre
        try {   
            Pront bufferPront = new Pront();   
            raf = new RandomAccessFile("dados/prontuarios.db", "rw");
            raf.seek(0);                                      
            qtdID = raf.readInt();
            tamAnota = raf.readInt();
            qtdProntRemov = raf.readInt();
            tamReg = tamLapi + tamCPF + tamNome+2 + tamNasc+2 + tamSexo+2 + tamAnota+2; //Tamanho total do registro

            //Impressão do cabeçalho
            System.out.println("\n---Cabeçalho prontuários---" + 
                            "\nID: " + qtdID + 
                            "\nTamanho: " + tamAnota + 
                            "\nQuantidade remov: " + qtdProntRemov);

            //Impressão dos registros
            while (loop != -1 && pos <= qtdID) {
                //Testa se o registro não foi deletado                                
                if (bufferPront.lapide && bufferPront.CPF != -1){                    
                    System.out.println(bufferPront.toString());
                }      
                loop = bufferPront.Ler(raf, pos, tamReg);        
                pos++;     
            } 
            System.out.println("\nFim do arquivo");           
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
        } catch (Exception e) {
            e.printStackTrace();
        }
        long tempoFinalImp = System.currentTimeMillis() - tempoInicial;
        long tempoFinalIns;
        //Calcula o tempo de execução em millisegundos e armazena no arquivo log
        try {
            raf = new RandomAccessFile("dados/logTempo.db", "rw");
            tempoFinalIns = raf.readLong();
            raf.writeLong(tempoFinalImp);
            raf.close();
            System.out.println("\n---Arquivo Log---");
            System.out.println("Tempo de inserção: " + tempoFinalIns + " ms (" + (tempoFinalIns/1000) + " s)");
            System.out.println("Tempo de busca e impressão: " + tempoFinalImp + " ms (" + (tempoFinalImp/1000) + " s)");
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo logTempo.db não encontrado. Crie o arquivo primeiro!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}