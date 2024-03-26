import java.io.*;
import java.util.ArrayList;

class Registre extends Task{
    int n; // numarul de noduri (variabile)
    int m; // numarul de legaturi
    int[][] links; // matricea de adiacenta
    int k; // numarul de culori (registre) cautate
    int[][] y; // matricea de clauze
    String result; // rezultat (True/False)
    ArrayList<Integer> solution; // / solutie data de oracol
    ArrayList<Integer> finalResult; // solutie interpretata

    public static void main(String[] args) throws IOException, InterruptedException {
        Registre reg = new Registre();
        reg.solve();
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
        for(int i = 1; i <= n; i++) {
            for(int j = 1; j <= k; j++) {
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
        for(int i = 0; i < m; i++) {
            line = br.readLine();
            args = line.split(" ");
            int u = Integer.parseInt(args[0]);
            int v = Integer.parseInt(args[1]);
            links[u][v] = links[v][u] = 1;
        }

        y = new int[n + 1][k + 1];
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        FileWriter myWriter = new FileWriter("sat.cnf");
        myWriter.write("p cnf ");
        myWriter.write(n * k + " ");

        int noConditions = n + n * k * (k - 1) / 2 + k * m;
        myWriter.write(noConditions + "\n");

        // Verific ca fiecare nod sa fie colorat cu minim o culoare
        for(int i = 1; i <= n; i++) { 
            for(int color = 1; color <= k; color++) {
                myWriter.write(y[i][color] + " ");
            }
            myWriter.write("0\n");
        }

        // Verific sa nu existe acelasi nod colorat cu 2 culori diferite
        for(int colorC = 1; colorC <= k; colorC++) {
            for(int colorD = colorC + 1; colorD <= k; colorD++) {
                for(int i = 1; i <= n; i++) {
                    myWriter.write(-y[i][colorC] + " " + -y[i][colorD] + " 0\n");
                }
            }
        }


       	// Verific ca 2 noduri care au muchie sa nu aiba aceeasi culoare
        for(int v = 1; v <= n; v++) {
            for(int w = v + 1; w <= n; w++) {
                if(links[v][w] == 1) {
                    for(int color = 1; color <= k; color++) {
                        myWriter.write(-y[v][color] + " " + -y[w][color]+ " 0\n");
                    }
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

        // Retine rezultatul nedescifrat intr-un vector
        String[] arr_result = br.readLine().split(" ");
        solution = new ArrayList<>();
        for (String s : arr_result) {
            if (Integer.parseInt(s) > 0) {
            	 // Daca valoarea clauzei este true, adaug-o in vectorul de solutii
                solution.add(Integer.parseInt(s));
            }
        }

        // Interpreteaza clauzele adevarate si adauga nodul in lista finala
        finalResult = new ArrayList<>();
        for(int i = 0; i <= n; i++) {
            for(int j = 0; j <= k; j++) {
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
        if (finalResult != null) {
            for (Integer i : finalResult) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }
}