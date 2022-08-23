/*Trabalho Prático AED III
Menu de seleção

Autor: João Paulo Maia de Paula - Matricula 702056
*/
import java.util.*;

public class Menu {
        public static void menu() {      
            //Variaveis de input, controle e manipulação
            Scanner sc = new Scanner(System.in);
            int inpt;
            boolean loop = true;             
        
            //Variaveis do cabeçalho
            /*int qtdID = -1;
            int tamAnota = -1;
            int qtdRemov = -1;*/

            while (loop){ //Loop só se encerra quando a opção 7-Saída é selecionada
                System.out.println("\n---- Menu ----");
                System.out.println("1 - Criar arquivo");
                System.out.println("2 - Inserir registro");
                System.out.println("3 - Editar registro");
                System.out.println("4 - Remover registro");
                System.out.println("5 - Imprimir arquivos");
                System.out.println("6 - Simulação");
                System.out.println("7 - Sair");
                //System.out.println("8 - Auto insere");
                //System.out.println("99 - Teste cabeçalho");
                try {
                    inpt = sc.nextInt(); //Input menu
                    sc.nextLine();
                    switch (inpt) {
                        case 1: //Criar arquivo
                        CArq.criaArquivo();
                        System.out.println("Arquivo criado");                
                            break;
                    
                        case 2: //Inserir registro
                        CRUD.InserirReg();
                            break;
            
                        case 3: //Editar Registro
                        CRUD.EditarReg();
                            break;
            
                        case 4: //Remover registro
                        CRUD.RemoverReg();
                        //System.out.println("WIP");            
                            break;
            
                        case 5: //Imprimir arquivos
                        CRUD.ImprimirArq();            
                            break;
            
                        case 6: //Simulação
                        CArq.criaArquivo();
                        System.out.println("Arquivo criado");
                        CRUD.AutoInserirReg();
                        System.out.println("Registros auto inseridos");
                        CRUD.ImprimirArq();
                            break;
        
                        case 7: //Sair
                        System.out.println("Encerrando");
                        loop = false;      
                        sc.close();  
                            break;  
                            
                        /*case 8: //Autoinsere
                        CRUD.AutoInserirReg();
                            break;*/
                        
                        /*case 99: //teste  
                        RandomAccessFile raf;  
                        try {      
                            raf = new RandomAccessFile("dados/prontuarios.db", "rw");
                            raf.seek(0);                                      
                            qtdID = raf.readInt();
                            tamAnota = raf.readInt();
                            qtdRemov = raf.readInt();
                            raf.close();
                        } catch (FileNotFoundException e) {
                            System.out.println("Arquivo não encontrado. Crie o arquivo primeiro!");                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }                   
                        System.out.println("\nID: " + qtdID + 
                                            "\nTamanho: " + tamAnota + 
                                            "\nQuantidade remov: " + qtdRemov);
                            break;*/
                        
                        default: 
                            System.out.println("Opção inválida!"); //Retorna erro caso o número não exista no menu
                            break;
                    }
                } catch (InputMismatchException e) { //Testa se o input é um número
                    System.out.println("Não é um número!");
                    sc.next();
                }
            }                 
        }        
}
