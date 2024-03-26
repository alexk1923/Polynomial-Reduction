import java.io.*;
import java.util.ArrayList;


class Retele extends Task{
    int n; // numar de noduri
    int m; // numar de muchii
    int[][] links; // matricea de adiacenta
    int k; // dimensiunea clicii
    int[][] y; // matricea clauzelor
    String result; // rezultat (True/False)
    ArrayList<Integer> solution; // solutie data de oracol
    ArrayList<Integer> finalResult; // solutie interpretata
    public static void main(String[] args) throws IOException, InterruptedException {
        Retele r = new Retele();
        r.solve();
    }

    // Initializeaza o matrice cu 1 pe diagonala principala
    private void initMat(int[][] mat) {
        for(int i = 1; i < mat.length; i++ ){
            for(int j = 1; j < mat.length; j++) {
                if(i == j) {
                    mat[i][j] = 1;
                }
            }
        }
    }


    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        // Initializeaza matricea de clauze cu numere consecutive
        int count = 1;
        for(int i = 1; i <= k; i++) {
            for(int j = 1; j <= n; j++) {
                y[i][j] = count++;
            }
        }
        
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        InputStreamReader in= new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
        String line = br.readLine();

        String[] args = line.split(" ");

        // Parseaza inputul
        n = Integer.parseInt(args[0]);
        m = Integer.parseInt(args[1]);
        k = Integer.parseInt(args[2]);

        links = new int[n + 1][n + 1];
        initMat(links);

        // Completeaza matricea de adiacenta
        for(int i = 0; i < m; i++) {
            line = br.readLine();
            args = line.split(" ");
            int u = Integer.parseInt(args[0]);
            int v = Integer.parseInt(args[1]);
            links[u][v] = links[v][u] = 1;
        }

        y = new int[k + 1][n + 1];
    }


    @Override
    public void formulateOracleQuestion() throws IOException {
    	FileWriter myWriter = new FileWriter("sat.cnf");
        myWriter.write("p cnf ");
        myWriter.write(n * k + " ");
   
        int noConditions = k + (n * (n - 1) / 2 - m) * k * (k - 1) + k * (k - 1) * n / 2;
        myWriter.write(noConditions + "\n");
 
        // Conditia a (minim un nod pe fiecare index)
        for(int idx = 1; idx <= k; idx++) {
            for(int v = 1; v <= n; v++) {
                myWriter.write(y[idx][v] + " ");
            }
            myWriter.write("0\n");
        }


        // Conditia b (2 noduri intre care nu exista muchii, nu pot fi ambele in clica)
        for(int v = 1; v <= n; v++) {
            for(int w = v + 1; w <= n; w++) {
                if(links[v][w] == 0) { // non-edge
                    for(int first_idx = 1; first_idx <= k; first_idx++) {
                        for(int sec_idx = 1; sec_idx <= k; sec_idx++) {
                            if(first_idx != sec_idx) {
                                myWriter.write(-y[first_idx][v] + " " + -y[sec_idx][w] + " 0\n");
                            }
                        }
                    }
                }
            }
        }

        // Conditia c  (pe pozitia i si pozitia j se afla 2 noduri diferite)
        for(int first_idx = 1; first_idx <= k; first_idx++) {
            for(int second_idx = first_idx + 1; second_idx <=k; second_idx++) {
                for(int v = 1; v <= n; v++) {
                    myWriter.write(-y[first_idx][v] + " " + -y[second_idx][v] + " 0\n");
                }
            }
        }

        myWriter.close();

    }


     @Override
    public void decipherOracleAnswer() throws IOException {
        File file = new File("sat.sol");
        BufferedReader br;
        br = new BufferedReader(new FileReader(file));
        result = String.valueOf(br.readLine());

        // Citeste numarul de variabile ale formulei
        br.readLine();

        if(result.equals("False")) {
            return;
        }

        // Retine rezultatul nedescifrat intr-un vector
        String[] arr_result = br.readLine().split(" ");
        solution = new ArrayList<>();
        int dim = 0;
        for (String s : arr_result) {
            if (Integer.parseInt(s) > 0) {
                // Daca valoarea clauzei este true, adaug-o in vectorul de solutii
                solution.add(Integer.parseInt(s));
            }
        }

        // Interpreteaza clauzele adevarate si adauga nodul in lista finala
        finalResult = new ArrayList<>();
        for(int i = 0; i <= k; i++) {
            for(int j = 0; j <= n; j++) {
                if(solution.contains(y[i][j])) {
                    finalResult.add(j);
                }
            }
        }

    }


    @Override
    public void writeAnswer() throws IOException {
        // Afiseaza rezultatul
       System.out.println(result);
       if(finalResult != null)
       for(Integer i : finalResult) {
           System.out.print(i + " ");
       }
        System.out.println();
    }


}
