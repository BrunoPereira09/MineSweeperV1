import java.util.Random;
import java.util.Scanner;

/**
 * Classe que representa o tabuleiro do jogo MineSweeper.
 * A classe lida com a criação e manipulação do tabuleiro, incluindo a colocação de bombas e revelação de células.
 * Ela também implementa a lógica para interações do jogador, como abrir células,
 * colocar bandeiras e verificar se o jogo foi ganho ou perdido.
 */
public class Game {
    //Atributos de instância
    private int rows; //Linhas
    private int cols; //Colunas
    private String[][] board; //Tabuleiro
    private boolean[][] bombs; //Se o valor for true, a celula do tabuleiro tem uma bomba.
    private int amountBombs; //Quantidade de bombas
    private boolean[][] checked; //Se o valor for true, a celula do tabuleiro foi verificada e revelada
    private int positionsWithoutBombs; //Quantidade de celulas sem bombas disponiveis
    private int totalFlags; //Quantidade de bandeiras
    private int guessedBombs;//Quantidade de bombas que o jogador adivinhou
    //Atributos de classe
    private static boolean isGameOver = false; //Se o valor for true, o jogo acabou
    private static long startTime; //Tempo inicial do jogo
    private static int anonymousCount = 0; //Contador de utilizadores anônimos
    private static String name; //Nome do jogador
    private static String[] winners = new String[10]; //Guarda os ultimos 10 vencedores
    private static int winnerCount = 0; //Contador de utilizadores que ganharam

    /**
     * Constroi uma nova instância do jogo com as dimensões e número de bombas especificados.
     * @param rows o número de linhas no tabuleiro do jogo
     * @param cols o número de colunas no tabuleiro do jogo
     * @param amountBombs o número total de bombas a serem colocadas no tabuleiro
     * Também inicializa os outros atributos do jogo.
     */
    public Game(int rows, int cols, int amountBombs) {
        this.rows = rows;
        this.cols = cols;
        this.amountBombs = amountBombs;
        this.positionsWithoutBombs = rows * cols - amountBombs;
    }

    /**
     * Retorna o estado atual do jogo (se acabou ou não).
     * (utilizado como getter)
     * @return verdadeiro se o jogo acabou, caso contrário falso.
     */
    public static boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Retorna os nomes dos vencedores.
     * @return um array de strings com os nomes dos vencedores
     */
    public static String[] getWinners() {
        return winners;
    }

    /**
     * @return o número de bandeiras restantes
     */
    public int getTotalFlags() {
        return totalFlags;
    }

    /**
     * @return a quantidade de bombas no tabuleiro
     */
    public int getAmountBombs() {
        return amountBombs;
    }

    /**
     * @return a quantidade de celulas sem bombas
     */
    public int getPositionsWithoutBombs() {
        return positionsWithoutBombs;
    }

    /**
     * @return a quantidade de bombas que o jogador adivinhou
     */
    public int getGuessedBombs() {
        return guessedBombs;
    }

    /**
     * Setter para a variavel isGameOver
     * @param gameOver verdadeiro se o jogo acabou, caso contrário falso
     */
    public static void setIsGameOver(boolean gameOver) {
        Game.isGameOver = gameOver;
    }

    /**
     * Começa o cronometro
     * @param startTime o tempo inicial em milisegundos
     */
    public void setStartTime(long startTime) {
        Game.startTime = startTime;
    }

    /**
     * Inicializa o tabuleiro do jogo as matrizes de bomba e células verificadas.
     * A cada célula é atribuído o símbolo inicial "■  ".
     * Será o estado inicial do jogo.
     */
    public void initializeGame() {
        checked = new boolean[rows][cols];
        board = new String[rows][cols];
        bombs = new boolean[rows][cols];
        isGameOver = false;
        totalFlags = amountBombs;
        guessedBombs = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = "■  ";
            }
        }
    }

    /**
     * Exibe o tabuleiro atual na consola.
     */
    public void printBoard() {
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println("| " + (char)('A' + i));
        }
        for (int i = 0; i < rows; i++) {
            System.out.print((i + 1) + "| ");
        }
        System.out.println();
    }

    /**
     * Preenche o tabuleiro com as bombas aleatoriamente, sem repetir posições.
     */
    public void fillBombs() {
        Random random = new Random();
        int bombsPlaced = 0;
        while (bombsPlaced < amountBombs) {
            int bombRow = random.nextInt(rows);
            int bombCol = random.nextInt(cols);
            if (!bombs[bombRow][bombCol]) {
                bombs[bombRow][bombCol] = true;
                bombsPlaced++;
            }
        }
    }

    /**
     * Fornece uma dica ao jogador, revelando uma célula aleatória sem bomba, com número de bombas adjacentes maior que 0
     * que não tenha sido aberta ainda.
     * Utilizado no Menu para o comando /hint
     */
    public void hint() {
        Random random = new Random();
        while (true) {
            int hintRow = random.nextInt(rows);
            int hintCol = random.nextInt(cols);
            int bombCount = countBombs(hintRow, hintCol);
            if (!bombs[hintRow][hintCol] && !checked[hintRow][hintCol] && bombCount > 0) {
                board[hintRow][hintCol] = bombCount + "  ";
                checked[hintRow][hintCol] = true;
                break;
            }
        }
    }

    /**
     * Valida se uma posição (linha e coluna) fornecida está dentro dos limites do tabuleiro.
     * Esta função é chamada no Menu para validar as coordenadas fornecidas pelo jogador.
     * @param row a linha a ser validada
     * @param col a coluna a ser validada
     * @return verdadeiro se a posição for válida, falso caso contrário
     */
    public boolean validatePosition(int row, int col) {
        try {
            if (row < 0 || row >= rows) {
                char lastRow = (char) ('A' + rows - 1);
                System.out.println("Invalid row! Please enter a letter between A and " + lastRow + ".");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Invalid row! Please enter a valid number.");
        }
        try {
            if (col < 0 || col >= cols) {
                System.out.println("Invalid column! Please enter a number between 1 and " + cols + ".");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Invalid column! Please enter a valid number.");
        }
        if (checked[row][col]) {
            System.out.println("Cell already opened!");
            return false;
        }
        if (board[row][col].equals("#  ")) {
            System.out.println("Flag already placed!");
            return false;
        }
        return true;
    }

    /**
     * Inicia a jogada de abrir uma célula no tabuleiro.
     * Se uma bomba for revelada, o jogo é perdido.
     * @param row a linha da célula a ser aberta
     * @param col a coluna da célula a ser aberta
     * A linha e coluna a serem abertas devem ser fornecidas pelo jogador com o comando /open <row> <col>
     */
    public void playGame(int row, int col) {
        if (bombs[row][col]) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (bombs[i][j]) {
                        board[i][j] = "B  ";
                    }
                }
            }
            board[row][col] = "X  ";
            printBoard();
            isGameOver = true;
            System.out.println("You lose! time: " + time() + "\nReturning to menu...\n");
            Menu.start();
        } else {
            revealAround(row, col);
            printBoard();
        }
    }

    /**
     * Conta o número de bombas ao redor de uma célula.
     * @param row a linha da célula
     * @param col a coluna da célula
     * @return o número de bombas ao redor da célula
     */
    public int countBombs(int row, int col) {
        int count = 0;
        int[] directions = {-1, 0, 1};
        for (int dirRow : directions) {
            for (int dirCol : directions) {
                int newRow = row + dirRow;
                int newCol = col + dirCol;
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && bombs[newRow][newCol]) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Revela as células ao redor de uma célula aberta, caso não tenha bomba e esteja dentro dos limites do tabuleiro.
     * Se a célula aberta não tiver bombas ao redor, a recursão propaga-se para as células adjacentes.
     * Assim criando um algoritmo de busca em profundidade ao utilizar a recursão.
     * @param row a linha da célula
     * @param col a coluna da célula
     */
    public void revealAround(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || checked[row][col] || bombs[row][col]) {
            return;
        }
        checked[row][col] = true;
        positionsWithoutBombs--;
        int bombCount = countBombs(row, col);
        board[row][col] = bombCount + "  ";
        if (bombCount == 0) {
            int[] directions = {-1, 0, 1};
            for (int dirRow : directions) {
                for (int dirCol : directions) {
                    if (dirRow != 0 || dirCol != 0) {
                        revealAround(row + dirRow, col + dirCol); //chama recursivamente a função
                    }
                }
            }
        }
    }

    /**
     * Revela todas as bombas no tabuleiro.
     * Utilizada quando o jogador perde o jogo.
     */
    public void revealBombs() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (bombs[i][j]) {
                    board[i][j] = "B  ";
                }
                System.out.print(board[i][j]);
            }
            System.out.println("| " + (char)('A' + i));
        }
        for (int i = 0; i < rows; i++) {
            System.out.print((i + 1) + "| ");
        }
        System.out.println();
    }

    /**
     * Coloca ou remove uma bandeira na célula especificada.
     * Validando se a bandeira pode ser colocada ou removida.
     * Se tentar colocar a bandeira numa celula que ja tenha flag, ira ser removida.
     * @param row a linha da célula onde a bandeira será colocada ou removida
     * @param col a coluna da célula onde a bandeira será colocada ou removida
     */
    public void placeFlag(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            System.out.println("Coordenadas inválidas!");
            return;
        }

        if (checked[row][col]) {
            System.out.println("Posição inválida!");
        } else if (board[row][col].equals("■  ")) {
            board[row][col] = "#  ";
            totalFlags--;
        } else if (board[row][col].equals("#  ")) {
            board[row][col] = "■  ";
            totalFlags++;
        } else if (board[row][col].equals("B  ")) {
            board[row][col] = "#  ";
            totalFlags--;
        } else if (totalFlags == 0) {
            System.out.println("Limite de flags atingido!");
        } else {
            System.out.println("Posição inválida!");
        }
    }

    /**
     * Verifica se uma bandeira foi colocada corretamente em uma célula com bomba.
     * Se sim, incrementa o contador de bandeiras corretas.
     * @param row a linha da célula onde a bandeira foi colocada
     * @param col a coluna da Celtula onde a bandeira foi colocada
     */
    public void checkFlag(int row, int col) {
        if (board[row][col].equals("#  ") && bombs[row][col]) {
            guessedBombs++;
        }
    }

    /**
     * Define o nome do jogador, pedindo a entrada do usuário.
     * Se o nome for vazio, o nome é definido como "Anonymous" seguido pelo número de anonymousCount.
     * @return o nome do jogador
     */
    public String setNome() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Username> ");
                name = sc.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a valid name.\n");
                sc.next();
            }
        }
        if (name.isEmpty()) {
            anonymousCount++;
            name = "Anonymous " + (anonymousCount); ;
        }
        return name;
    }

    /**
     * Remove ao tempo atual o tempo inicial (em milisegundos) para obter o tempo decorrido.
     * @return o tempo decorrido em formato de horas, minutos e segundos
     */
    public static String time(){
        long Time = System.currentTimeMillis() - startTime;
        return String.format("%02dh:%02dm:%02ds", Time / 3600000, (Time % 3600000) / 60000, (Time % 60000) / 1000);
    }

    /**
     * Adiciona o nome do jogador e o tempo decorrido ao array de vencedores.
     * Para ser vizualizado no menu dos 10 ultimos vencedores.
     */
    public static void addWinners(){
        winnerCount++;
        if (winnerCount > 10) {
            winnerCount = 1;
        }
        winners[winnerCount - 1] = name + " --> " + time();
    }
}

