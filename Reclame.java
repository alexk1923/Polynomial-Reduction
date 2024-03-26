import java.io.*;
import java.util.ArrayList;

class Reclame extends Task{

    int n; // numar de noduri
    int m; // numar de muchii
    int[][] links; // matricea de adiacenta
    int[][] y; // matricea clauzelor
    int k; // dimensiunea minima a grupului esential
    String result; // rezultat (True/False)
    ArrayList<Integer> solution; // solutie data de oracol
    ArrayList<Integer> finalResult; // solutie interpretata

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

    public static void main(String[] args) throws IOException, InterruptedException {
        Reclame reclame = new Reclame();
        reclame.solve();
    }


    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        k = 1;
        while(k <= n) {
            k++;
            // Initializeaza matricea de clauze cu numere consecutive
            y = new int[k + 1][n + 1];
            int count = 1;
            for(int i = 1; i <= k; i++) {
                for(int j = 1; j <= n; j++) {
                    y[i][j] = count++;
                }
            }
            formulateOracleQuestion();
            askOracle();
            decipherOracleAnswer();
            if(result.equals("True")) { // Daca s-a gasit grupul minim -> s-a gasit un rezultat
                break;
            }
        }
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        InputStreamReader in= new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
        String line = br.readLine();

        String[] args = line.split(" ");

        // Parseaza input-ul
        n = Integer.parseInt(args[0]);
        m = Integer.parseInt(args[1]);

        
        links = new int[n + 1][n + 1];
        initMat(links);

        // Creeaza matricea de adiacenta
        for(int i = 0; i < m; i++) {
            line = br.readLine();
            args = line.split(" ");
            int u = Integer.parseInt(args[0]);
            int v = Integer.parseInt(args[1]);
            links[u][v] = links[v][u] = 1;
        }

    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        FileWriter myWriter = new FileWriter("sat.cnf");
        myWriter.write("p cnf ");
        myWriter.write(n * k + " ");

        int noConditions = k + n + m + k * (k - 1) * n / 2; // Calculeaza numarul de conditii
        myWriter.write(noConditions + "\n");

        // Conditia a (minim un element pe fiecare index)
        for(int idx = 1; idx <= k; idx++) {
            for(int v = 1; v <= n; v++) {
                myWriter.write(y[idx][v] + " ");
            }
            myWriter.write("0\n");
        }


        // Conditia de a se afla cel mult o data pe un index
        for(int v = 1; v <= n; v++) {
            for(int idx = 1; idx <= k; idx++) {
                myWriter.write(-y[idx][v] + " ");
            }
            myWriter.write("0\n");
        }

        // Muchiile trebuie sa aiba minim un capat in acoperire
        for(int v = 1; v <= n; v++) {
            for(int w = v + 1; w <= n; w++) {
                if(links[v][w] == 1){
                    for(int idx = 1; idx <= k; idx++) {
                        myWriter.write(y[idx][v] + " " + y[idx][w] + " ");
                    }
                    myWriter.write("0\n");
                }
            }
        }


        // (Doua noduri distincte nu pot fi pe aceeasi pozitie)
        for(int first_idx = 1; first_idx <= k; first_idx++) {
            for(int v = 1; v <= n; v++) {
                for(int w = v + 1; w <= n; w++) {
                    myWriter.write(-y[first_idx][v] + " " + -y[first_idx][w] + " 0\n");
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
        br.readLine();

        if(result.equals("False")) {
            return;
        }

        String[] arr_result = br.readLine().split(" ");
        solution = new ArrayList<>();

        // Adauga clauzele adevarate in vectorul solution
        for (String s : arr_result) {
            if (Integer.parseInt(s) > 0) {
                solution.add(Integer.parseInt(s));
            }
        }

        finalResult = new ArrayList<>();

        // Interpreteaza conditiile adevarate
        for(int i = 0; i <= k; i++) {
            for(int j = 0; j <= n; j++) {
                if(solution.contains(y[i][j])) {
                    finalResult.add(j);
                }
            }
        }
    }

    @Override
    public void writeAnswer() {
    	// Afiseaza rezultatul
        if(finalResult != null)
            for(Integer i : finalResult) {
                System.out.print(i + " ");
            }
        System.out.println();
    }
}