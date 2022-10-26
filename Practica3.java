import java.net.Socket;
import java.net.ServerSocket;
import java.nio.ByteBuffer; 
import java.lang.Thread;
import java.io.DataOutputStream; 
import java.io.DataInputStream;

class Practica3{
    static int N = 12;
    static int  ax,bx, numNod;
    static long cx,checksum = 0;
    static int[][] A = new int[N][N];
    static int[][] B = new int[N][N];
    static long[][] C = new long[N][N];
    static int[][] AX = new int[N/2][N];
    static int[][] BX = new int[N/2][N];
    static long[][] CX = new long[N/2][N/2];

    static class Worker extends Thread{
        Socket conexion;
        int node; 
        Worker(Socket conexion,int node){
            this.conexion = conexion;
            this.node = node;
        }

        public void run(){
            try {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                int node = entrada.readInt();
       
                if (node == 1){
                    //Se envia A1
                    for (int i = 0; i < N/2; i++)
                        for (int j = 0; j < N; j++){
                            ax = A[i][j];
                            salida.writeInt(ax);
                        }
                   //Matriz A1 ENVIADa Enviar B1  
                    for (int i = 0; i < N/2; i++)
                        for (int j = 0; j < N; j++){
                            bx= B[i][j];
                            salida.writeInt(bx);
                        }
                    //Matriz B1 ENVIADA, Recibe C1
                    for (int i = 0; i < N/2; i++)
                        for (int j = 0; j < N/2; j++){
                            C[i][j] = entrada.readLong();
                        }
                   
                    System.out.println("Nodo 1 recibida correctamente");
                }

                else if (node == 2) {
                  
                    for (int i = 0; i < N/2; i++)
                        for (int j = 0; j < N; j++){
                            ax = A[i][j];
                            salida.writeInt(ax);
                        }
			  //////////////////
                    
                    for (int i = N/2; i < N; i++)				//Matriz A1 ENVIAr Enviar B2  
                        for (int j = 0; j < N; j++){
                            bx= B[i][j];
                            salida.writeInt(bx);
                        } 

                    for (int i = 0; i < N/2; i++)                       // Matriz B2 ENVIADA,   Recibe C2
                        for (int j = N/2; j < N; j++){
                            C[i][j] = entrada.readLong();
                        }   
                    System.out.println("Nodo 2 recibida correctamente");
                }

                else if (node == 3){
                    for (int i = N/2; i < N; i++)                        // Enviar A2
                        for (int j = 0; j < N; j++){
                            ax= A[i][j];
                            salida.writeInt(ax);
                        }
                    for (int i = 0; i < N/2; i++)                         //Matriz A2 ENVIADA    Enviar B1  
                        for (int j = 0; j < N; j++){
                            bx= B[i][j];
                            salida.writeInt(bx);
                        }
                    for (int i = N/2; i < N; i++)                      //Recibe C3, enviar Matriz b1
                        for (int j = 0; j < N/2; j++){
                            C[i][j] = entrada.readLong();
                        }     
                    System.out.println("Nodo 3 recibida correctamente");
                }
                else if (node == 4) {
                    // Enviar A2
                    for (int i = N/2; i < N; i++)
                        for (int j = 0; j < N; j++){
                            ax= A[i][j];
                            salida.writeInt(ax);
                        }

                   // ENVIAR MATRIZ B2
                    for (int i = N/2; i < N; i++)
                        for (int j = 0; j < N; j++){
                            bx= B[i][j];
                            salida.writeInt(bx);
                        }
                   
                    for (int i = N/2; i < N; i++) //MATRIZ 2 NODO 4 RECIBIDA
                        for (int j = N/2; j < N; j++){
                            C[i][j] = entrada.readLong();
                        } 
                    System.out.println("Nodo 4 recibida correctamente"); 
                }
                salida.close();
                entrada.close();
                conexion.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void nodeServer() throws Exception{
        System.out.println("Esperando coneccion.... ");
        //llenado de matrices A,B,C
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++){
                A[i][j] = i + 3*j;
                B[i][j] = 2 * i - j;
                C[i][j] = 0;
            }
      //trasnpuesta matriz B
      for ( int i = 0; i < N; i++)
        for ( int j = 0; j < i; j++){
          int x = B[i][j];
          B[i][j] = B[j][i];
          B[j][i] = x;
        }
	 System.out.println("Imprimir Matriz A \n");
	 printMatrix(A, N, N);
	 System.out.println("Imprimir matriz B\n");
	 printMatrix(B, N, N);




        ServerSocket servidor = new ServerSocket(50000);
        Worker w[] = new Worker[4];

        //Aceptamos nodos
        for (int i = 0; i < 4; ++i){
            w[i] = new Worker(servidor.accept(), i);
            w[i].start();
        }
       
        for (int i = 0; i < 4; ++i)w[i].join();

        //Calculamos Checksum e imprimos
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)          
                checksum +=  C[i][j];

      //imprime checksum
      System.out.println("");
      System.out.println("  CHECKSUME : "+checksum);
    
      if(N == 12){
        System.out.print("  Matriz C:\n");
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++)
                System.out.print(" "+C[i][j]);
        System.out.println("");
        }
      }
        servidor.close();
    }

    public static void nodeCliente(int node) throws Exception{ //NODO CLIENTE
        System.out.println("Nodo: "+node + " Conectado correctamente ");
        Socket conexion = null;
       for(;;)
            try{
                conexion = new Socket("localhost",50000);
                break;
            }
            catch (Exception e){
                Thread.sleep(100);
            }        



        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        salida.writeInt(node);

        //Ax
        for (int i = 0; i < N/2; i++)
            for (int j = 0; j < N; j++)
                AX[i][j] = entrada.readInt();
        //Bx
        for (int i = 0; i < N/2; i++)
            for (int j = 0; j < N; j++)
                BX[i][j] = entrada.readInt();
   
        for (int i = 0; i < N/2; i++)
            for (int j = 0; j < N/2; j++)
                for (int k = 0; k < N; k++)
                    CX[i][j] += AX[i][k] * BX[j][k];
        for (int i = 0; i < N/2; i++)
            for (int j = 0; j < N/2; j++){
                cx = CX[i][j];
                salida.writeLong(cx);
        }
        salida.close();
        entrada.close();
        conexion.close();
    }

    public static void main(String [] args)throws Exception{
        if (args.length < 1 || args.length > 2){
            System.err.println("java Practica3 <nodo>");
            System.exit(0);
        }
        int node = Integer.valueOf(args[0]);

        if(node == 0){
            nodeServer();
        }
        else{
            nodeCliente(node);
        }
    }

    static void printMatrix(int matrix[][], int rows, int cols){
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.printf("%4d", matrix[i][j]);
			}
			System.out.println("");
		}
	}




}