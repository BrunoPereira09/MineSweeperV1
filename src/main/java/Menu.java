import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Classe que representa o menu principal do jogo.
 * A classe lida com a exibição do menu de opções ao usuário e permite ao jogador escolher entre iniciar um novo jogo,
 * visualizar os últimos 10 vencedores ou sair do jogo.
 * Essa classe também implementa a lógica para processar as opções do jogador.
 */
public class Menu {
    private static Game newGame; //Instância do jogo

    /**
     * *Inicia o menu do jogo e aguarda a escolha do usuário.
     * O usuário pode escolher entre iniciar um novo jogo, ver os últimos 10 vencedores ou sair do jogo.
     * A opção escolhida é processada, e se a escolha for válida, a ação correspondente é executada.
     * Caso uma escolha inválida seja feita, o menu é reapresentado até que uma opção válida seja selecionada.
     */
    public static void start(){
        Scanner sc = new Scanner(System.in);
        boolean choiceMade = false;
        while (!choiceMade) {
            System.out.println("MineSweeper Game");
            System.out.println("----------------");
            System.out.println("1. New Game");
            System.out.println("2. Last 10 Wins");
            System.out.println("3. Exit Game");
            System.out.print("Option> ");
            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        startGame();
                        choiceMade = true;
                        break;
                    case 2:
                        String [] winners = Game.getWinners();
                        if (winners[0] == null) {
                            System.out.println("\nNo winners yet.");
                        }
                        else {
                            System.out.println("\nLast 10 wins:");
                            for (String winner : winners) {
                                if (winner == null) {
                                    continue;
                                }
                                System.out.println(winner);
                            }
                        }
                        System.out.println();
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid option, please choose a number between 1 and 3.\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid option, please choose a valid number.\n");
                sc.next(); // limpa o valor que está no scanner para que o ciclo while não repita.
            }
        }
    }

    /**
     * Inicia um novo jogo de MineSweeper.
     * Um novo tabuleiro é criado com as dimensões e o número de bombas especificados.
     * O jogador é solicitado a fornecer seu nome e então pode começar a jogar
     * atraves de comandos especificos.
     */
    public static void startGame() {
        newGame = new Game(9, 9, 10); //Cria o tabuleiro
        newGame.initializeGame(); //Inicializa o tabuleiro
        newGame.fillBombs(); //Preenche o tabuleiro com bombas
        String nome = newGame.setNome(); //Define o nome do jogador
        newGame.setStartTime(System.currentTimeMillis()); //Define o tempo de inicio (começa o cronometro)
        newGame.printBoard(); //Imprime o tabuleiro
        commands(nome); //Processa os comandos do jogador
    }

    /**
     * Processa os comandos do jogador durante o jogo. Os comandos disponíveis incluem:
     * <ul>
     *   <li>/help - Exibe uma lista de comandos disponíveis.</li>
     *   <li>/quit - Sai do jogo e retorna ao menu.</li>
     *   <li>/open <linha> <coluna> - Abre uma célula nas coordenadas especificadas.</li>
     *   <li>/flag <linha> <coluna> - Marca uma célula nas coordenadas especificadas. Se já estiver marcada, a marcação é removida.</li>
     *   <li>/hint <linha> <coluna> - Revela uma célula aleatória sem bomba.</li>
     *   <li>/cheat - Revela todas as bombas.</li>
     *   <li>/win - Vence o jogo.</li>
     * </ul>
     * Nos comandos /open e /flag é verificado se o jogador ganhou o jogo, ao não ter celulas sem bombas restantes e
     * ter adivinhado todas as bombas ao colocar flags.
     * @param nome Nome do jogador
     */
    public static void commands(String nome) {;
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome, " + nome + "!");
        while (!Game.isGameOver()) {
            int guessedBombs = newGame.getGuessedBombs();
            System.out.println("\nAvailable flags: " + newGame.getTotalFlags());
            System.out.println("\nElapsed time: " + Game.time());
            System.out.println("\n[Type /help for assistance]");
            System.out.print("Command> ");
            String[] command = sc.nextLine().split(" ");
            switch (command[0]) {
                case "/help":
                    System.out.println("Available commands:");
                    System.out.println("/help - Displays a list of available commands.");
                    System.out.println("/quit - Quits the game.");
                    System.out.println("/open <row> <column> - Opens a cell at the specified coordinates.");
                    System.out.println("/flag <row> <column> - Flags a cell at the specified coordinates. If the cell is already flagged, it will be unflagged.");
                    System.out.println("/hint <row> <column> - Reveals a random cell without a bomb.");
                    System.out.println("/cheat - Reveals the entire board.");
                    System.out.println("/win - Reveals the entire board and wins the game.");
                    newGame.printBoard();
                    break;
                case "/quit":
                    System.out.println("Returning to the menu...");
                    Menu.start();
                    break;
                case "/open":
                    if(command.length != 3) {
                        System.out.println("Invalid command, please use /open <row> <column>.");
                        continue;
                    }
                    int row = command[1].charAt(0) - 'A';
                    int col = Integer.parseInt(command[2]) - 1;
                    if (newGame.validatePosition(row, col)) {
                        newGame.playGame(row, col);
                    }
                    if (guessedBombs == newGame.getAmountBombs() && newGame.getPositionsWithoutBombs() == 0) {
                        System.out.println("You win! time: " + Game.time() + "\nReturning to the menu...\n");
                        Game.addWinners();
                        Game.setIsGameOver(true);
                        Menu.start();
                    }
                    break;
                case "/flag":
                    if (command.length != 3) {
                        System.out.println("Invalid command, please use /flag <row> <column>.");
                        continue;
                    }
                    int flagRow = command[1].charAt(0) - 'A';
                    int flagCol = Integer.parseInt(command[2]) - 1;
                    if (newGame.validatePosition(flagRow, flagCol)) {
                       newGame.placeFlag(flagRow, flagCol);
                       newGame.checkFlag(flagRow, flagCol);
                       newGame.printBoard();
                    }
                    if (guessedBombs == newGame.getAmountBombs() && newGame.getPositionsWithoutBombs() == 0) {
                        System.out.println("You win! time: " + Game.time() + "\nReturning to the menu...\n");
                        Game.addWinners();
                        Game.setIsGameOver(true);
                        Menu.start();
                    }
                    break;
                case "/hint":
                    newGame.hint();
                    newGame.printBoard();
                    break;
                case "/cheat":
                    newGame.revealBombs();
                    break;
                case "/win":
                    newGame.revealBombs();
                    System.out.println("You win! time: " + Game.time() + "\nReturning to the menu...\n");
                    Game.addWinners();
                    Game.setIsGameOver(true);
                    Menu.start();
                    break;
                default:
                    System.out.println("Invalid command! To see the list of available commands, type /help.");
                    newGame.printBoard();
            }
        }
    }

    /**
     * Inicia o menu do jogo e aguarda a escolha do usuário.
     */
    public static void main(String[] args) {
        Menu.start();
    }
}
