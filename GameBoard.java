import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Класс GameBoard представляет игровую панель для игры "Змейка".
 * Он отображает поле, змейку, яблоко и яды, а также обрабатывает пользовательский ввод.
 */
public class GameBoard extends JPanel implements ActionListener {

  private static final int BOARD_WIDTH = 600; // Ширина игрового поля
  private static final int BOARD_HEIGHT = 600; // Высота игрового поля
  private static final int DOT_SIZE = 10; // Размер одного элемента (ячейки) на поле
  private static final int ALL_DOTS = (BOARD_WIDTH * BOARD_HEIGHT) / (DOT_SIZE * DOT_SIZE); // Общее количество элементов на поле
  private static final int DELAY = 40; // Задержка в миллисекундах между обновлениями игрового состояния

  private final int x[] = new int[ALL_DOTS]; // Координаты x каждого элемента змейки
  private final int y[] = new int[ALL_DOTS]; // Координаты y каждого элемента змейки

  private int dots; // Текущая длина змейки

  private int appleX; // Координата x яблока
  private int appleY; // Координата y яблока

  private int[] poisonX = new int[3]; // Координаты x ядов
  private int[] poisonY = new int[3]; // Координаты y ядов
  private boolean[] hasPoison = new boolean[3]; // Флаги для отслеживания наличия яда

  private boolean leftDirection = false; // Направление движения влево
  private boolean rightDirection = true; // Направление движения вправо
  private boolean upDirection = false; // Направление движения вверх
  private boolean downDirection = false; // Направление движения вниз

  private boolean inGame = true; // Флаг для отслеживания состояния игры

  private Timer timer; // Таймер для обновления игрового состояния

  /**
   * Конструктор класса GameBoard.
   * Инициализирует игровую панель и начинает новую игру.
   */
  public GameBoard() {
    initBoard();
  }

  /**
   * Инициализация игровой панели.
   * Устанавливает размеры, фон, фокус и добавляет обработчик клавиш.
   */
  private void initBoard() {
    setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    setBackground(Color.black);
    setFocusable(true);

    addKeyListener(new GameKeyListener());

    initGame();
  }

  /**
   * Инициализация новой игры.
   * Устанавливает начальные значения и запускает таймер.
   */
  private void initGame() {
    dots = 3;

    for (int i = 0; i < dots; i++) {
      x[i] = 100 - i * DOT_SIZE;
      y[i] = 100;
    }

    placeApple();
    placePoison();

    timer = new Timer(DELAY, this);
    timer.start();
  }

  /**
   * Размещает яблоко на поле в случайном месте.
   */
  private void placeApple() {
    int r = (int) (Math.random() * (BOARD_WIDTH / DOT_SIZE));
    appleX = r * DOT_SIZE;

    r = (int) (Math.random() * (BOARD_HEIGHT / DOT_SIZE));
    appleY = r * DOT_SIZE;
  }

  /**
   * Размещает яды на поле в случайных местах.
   */
  private void placePoison() {
    for (int i = 0; i < 3; i++) {
      int r = (int) (Math.random() * (BOARD_WIDTH / DOT_SIZE));
      poisonX[i] = r * DOT_SIZE;

      r = (int) (Math.random() * (BOARD_HEIGHT / DOT_SIZE));
      poisonY[i] = r * DOT_SIZE;

      hasPoison[i] = true;
    }
  }

  /**
   * Перемещает змейку на одну ячейку в соответствии с текущим направлением.
   */
  private void move() {
    for (int i = dots; i > 0; i--) {
      x[i] = x[i - 1];
      y[i] = y[i - 1];
    }

    if (leftDirection) {
      x[0] -= DOT_SIZE;
    }

    if (rightDirection) {
      x[0] += DOT_SIZE;
    }

    if (upDirection) {
      y[0] -= DOT_SIZE;
    }

    if (downDirection) {
      y[0] += DOT_SIZE;
    }
  }

  /**
   * Проверяет столкновение змейки с краем поля или самой собой.
   * Если столкновение произошло, игра завершается.
   */
  private void checkCollision() {
    for (int i = dots; i > 0; i--) {
      if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
        inGame = false;
      }
    }

    if (y[0] >= BOARD_HEIGHT) {
      inGame = false;
    }

    if (y[0] < 0) {
      inGame = false;
    }

    if (x[0] >= BOARD_WIDTH) {
      inGame = false;
    }

    if (x[0] < 0) {
      inGame = false;
    }

    if (!inGame) {
      timer.stop();
    }
  }

  /**
   * Проверяет, поймала ли змейка яблоко.
   * Если поймала, увеличивает длину змейки и размещает новое яблоко на поле.
   */
  private void checkApple() {
    if ((x[0] == appleX) && (y[0] == appleY)) {
      dots++;
      placeApple();
    }
  }

  /**
   * Проверяет, попала ли змейка на яд.
   * Если попала, игра завершается.
   */
  private void checkPoison() {
    for (int i = 0; i < 3; i++) {
      if (hasPoison[i] && (x[0] == poisonX[i]) && (y[0] == poisonY[i])) {
        inGame = false;
      }
    }
  }

  /**
   * Отрисовка элементов игры.
   * Отображает поле, змейку, яблоко и яды.
   */
  private void draw(Graphics g) {
    if (inGame) {
      g.setColor(Color.green);
      g.fillOval(appleX, appleY, DOT_SIZE * 2, DOT_SIZE * 2);

      for (int i = 0; i < dots; i++) {
        if (i == 0) {
          g.setColor(Color.red);
        } else {
          g.setColor(Color.white);
        }

        g.fillRect(x[i], y[i], DOT_SIZE * 2, DOT_SIZE * 2);
      }

      for (int i = 0; i < 3; i++) {
        if (hasPoison[i]) {
          g.setColor(Color.orange);
          g.fillOval(poisonX[i], poisonY[i], DOT_SIZE * 2, DOT_SIZE * 2);
        }
      }

      Toolkit.getDefaultToolkit().sync();
    } else {
      gameOver(g);
    }
  }

  /**
   * Отображение сообщения о завершении игры.
   */
  private void gameOver(Graphics g) {
    String message = "Game Over";
    Font font = new Font("Helvetica", Font.BOLD, 24);
    FontMetrics metrics = getFontMetrics(font);

    g.setColor(Color.white);
    g.setFont(font);
    g.drawString(message, (BOARD_WIDTH - metrics.stringWidth(message)) / 2, BOARD_HEIGHT / 2);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (inGame) {
      checkApple();
      checkPoison();
      checkCollision();
      move();
    }

    repaint();
  }

  private class GameKeyListener extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();

      if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
        leftDirection = true;
        upDirection = false;
        downDirection = false;
      }

      if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
        rightDirection = true;
        upDirection = false;
        downDirection = false;
      }

      if ((key == KeyEvent.VK_UP) && (!downDirection)) {
        upDirection = true;
        rightDirection = false;
        leftDirection = false;
      }

      if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
        downDirection = true;
        rightDirection = false;
        leftDirection = false;
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  /**
   * Точка входа в программу.
   * Создает экземпляр класса GameBoard и отображает его на экране.
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("Snake Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.add(new GameBoard());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

