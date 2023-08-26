import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
// import javax.swing.text.AttributeSet.FontAttribute;

public class Minesweeper implements ActionListener {
    JFrame frame;
    JPanel textPanel;
    JPanel buttonPanel;
    JButton[][] buttons; // create double dimensional array of JButtons
    JButton resetButton;
    JButton flag;
    JLabel textfield;
    int[][] solution; // double d array to store solution
    Random random; // random class object
    int size; // size of grid
    int bombs; // num of bombs
    boolean flagging; // use to see if person is currently flagging button or not
    int count = 0;
    int lastXchecked;
    int lastYchecked;
    int xZero; // stores x position of the zero that is clicked and around whom all nums will
               // have to be displayed
    int yZero;
    boolean[][] flagged;

    ArrayList<Integer> xPositions; // array list of x positions
    ArrayList<Integer> yPositions; // array list of y positions

    public Minesweeper() {
        xPositions = new ArrayList<Integer>();
        yPositions = new ArrayList<Integer>();
        random = new Random();

        size = 9;
        bombs = 10;
        lastXchecked = size + 1;
        lastYchecked = size + 1;

        flagged = new boolean[size][size];

        // assign bomb to any position in grid
        for (int i = 0; i < bombs; i++) {
            xPositions.add(random.nextInt(size));
            yPositions.add(random.nextInt(size));
        }

        // making sure bombs dont overlap, if so find a different spot
        for (int i = 0; i < bombs; i++) {
            for (int j = i + 1; j < bombs; j++) {
                if (xPositions.get(i) == xPositions.get(j) && yPositions.get(i) == yPositions.get(j)) {
                    xPositions.set(j, random.nextInt(size));
                    yPositions.set(j, random.nextInt(size));
                    i = 0;
                    j = 0;
                }
            }
        }

        frame = new JFrame(); // creation of frame object
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ensures application closes when close is clicked
        frame.setVisible(true);
        frame.setLayout(new BorderLayout()); // layout where element can be in NSEW or center of frame

        textPanel = new JPanel();
        textPanel.setVisible(true);
        textPanel.setBackground(Color.BLACK);

        buttonPanel = new JPanel();
        buttonPanel.setVisible(true);
        buttonPanel.setLayout(new GridLayout(size, size)); // creates grid of arguments

        textfield = new JLabel();
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setFont(new Font("MV Boli", Font.BOLD, 20));
        textfield.setForeground(Color.BLUE);
        textfield.setText(bombs + " Bombs");

        resetButton = new JButton();
        resetButton.setForeground(Color.BLUE);
        resetButton.setBackground(Color.WHITE);
        resetButton.setText("Reset");
        resetButton.setFont(new Font("MV Boli", Font.BOLD, 20));
        resetButton.setFocusable(false);
        resetButton.addActionListener(this);

        flag = new JButton();
        flag.setForeground(Color.ORANGE);
        flag.setBackground(Color.WHITE);
        flag.setText("|>");
        flag.setFont(new Font("MV Boli", Font.BOLD, 20));
        flag.setFocusable(false);
        flag.addActionListener(this);

        solution = new int[size][size];

        buttons = new JButton[size][size];
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) { // going through each and every button in double dimensional
                                                          // array
                buttons[i][j] = new JButton();
                buttons[i][j].setFocusable(false);
                buttons[i][j].setFont(new Font("MV Boli", Font.BOLD, 12));
                buttons[i][j].addActionListener(this);
                buttons[i][j].setText(""); // empty string
                buttonPanel.add(buttons[i][j]);
            }
        }

        textPanel.add(textfield);
        frame.add(textPanel, BorderLayout.NORTH); // north of the frame
        frame.add(resetButton, BorderLayout.SOUTH);
        frame.add(flag, BorderLayout.WEST);
        frame.add(buttonPanel);

        frame.setSize(570, 570);
        frame.revalidate(); // used to refresh all components of GUI and display them with all assigned
                            // properties
        frame.setLocationRelativeTo(null); // creates frame in center of screen

        getSolution();

    }

    public void getSolution() {
        for (int y = 0; y < solution.length; y++) {
            for (int x = 0; x < solution.length; x++) {
                boolean changed = false; // to see if position being checked has a bomb and already been modified
                int bombsAround = 0;

                for (int i = 0; i < xPositions.size(); i++) {
                    if (x == xPositions.get(i) && y == yPositions.get(i)) {
                        solution[y][x] = size + 1; // indicating bombs with size of the grid+1
                        changed = true;
                    }
                }

                if (!changed) {
                    for (int i = 0; i < xPositions.size(); i++) {
                        if (x - 1 == xPositions.get(i) && y == yPositions.get(i)) // checking if the positions around
                                                                                  // have a bomb or not
                            bombsAround++;
                        if (x + 1 == xPositions.get(i) && y == yPositions.get(i)) // one position ahead
                            bombsAround++;
                        if (x == xPositions.get(i) && y - 1 == yPositions.get(i)) // one position above
                            bombsAround++;
                        if (x == xPositions.get(i) && y + 1 == yPositions.get(i)) // one position below
                            bombsAround++;
                        if (x + 1 == xPositions.get(i) && y + 1 == yPositions.get(i)) // bottom right
                            bombsAround++;
                        if (x - 1 == xPositions.get(i) && y - 1 == yPositions.get(i)) // top left
                            bombsAround++;
                        if (x - 1 == xPositions.get(i) && y + 1 == yPositions.get(i)) // bottom left
                            bombsAround++;
                        if (x + 1 == xPositions.get(i) && y - 1 == yPositions.get(i)) // top right
                            bombsAround++;
                    }
                    solution[y][x] = bombsAround;
                }
            }
        }

        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[0].length; j++) {
                System.out.print(solution[i][j] + " ");
            }
            System.out.println(" ");
        }
    }

    // when button is clicked this method is called and displays what is at that
    // position in the solution double d array
    public void check(int y, int x) {
        boolean over = false;

        if (solution[y][x] == (size + 1)) {
            gameOver(false); // if the bomb is clicked it calls gameOver method
            over = true;
        }

        if (!over) {
            getColor(y, x);
            if (solution[y][x] == 0) {
                xZero = x;
                yZero = y;

                count = 0;
                display();
            }
            checkWinner();
        }
    }

    public void checkWinner() {
        int buttonsLeft = 0;

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (buttons[i][j].getText() == "" || buttons[i][j].getText() == "|>") // if button empty, when person
                                                                                      // wins no bombs clicked, so if
                                                                                      // button
                    // is zero the buttons not clicked
                    buttonsLeft++;
            }
        }

        if (buttonsLeft == bombs)
            gameOver(true);

    }

    public void gameOver(boolean won) {
        if (!won) {
            textfield.setForeground(Color.RED);
            textfield.setText("Game Over!");
        } else {
            textfield.setForeground(Color.GREEN);
            textfield.setText("You Win!");
        }

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j].setBackground(null);
                buttons[i][j].setEnabled(false); // disabling all buttons

                // revealing all bombs
                for (int count = 0; count < xPositions.size(); count++) {
                    if (j == xPositions.get(count) && i == yPositions.get(count)) {
                        buttons[i][j].setBackground(Color.BLACK);
                        buttons[i][j].setText("*");
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == flag) {
            if (flagging) {
                flag.setBackground(Color.WHITE);
                flagging = false;
            } else {
                flag.setBackground(Color.RED);
                flagging = true;
            }
        }
        if (e.getSource() == resetButton) {
            frame.dispose();
            new Minesweeper();
        }
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (e.getSource() == buttons[i][j]) {
                    if (flagging && (buttons[i][j].getText() == "" || buttons[i][j].getText() == "|>")) {
                        if (flagged[i][j]) {
                            buttons[i][j].setText("");
                            buttons[i][j].setBackground(null);
                            flagged[i][j] = false;
                        } else {
                            buttons[i][j].setText("|>");
                            buttons[i][j].setBackground(Color.RED);
                            buttons[i][j].setForeground(Color.ORANGE);
                            flagged[i][j] = true;
                        }
                    } else {
                        if (!flagged[i][j]) {
                            check(i, j);
                        }

                    }
                    check(i, j);
                }
            }
        }
    }

    // recursive call to reveal zeros
    public void display() {
        if (count < 1) { // this block is for revealing everything around the zero
            if ((xZero - 1) >= 0) // prevents out of bounds
                getColor(yZero, xZero - 1);
            if ((xZero + 1) < size)
                getColor(yZero, xZero + 1);
            if ((yZero - 1) >= 0)
                getColor(yZero - 1, xZero);
            if ((yZero + 1) < size)
                getColor(yZero + 1, xZero);
            if ((yZero - 1) >= 0 && (xZero - 1) >= 0)
                getColor(yZero - 1, xZero - 1);
            if ((yZero + 1) < size && (xZero + 1) < size)
                getColor(yZero + 1, xZero + 1);
            if ((yZero - 1) >= 0 && (xZero + 1) < size)
                getColor(yZero - 1, xZero + 1);
            if ((yZero + 1) < size && (xZero - 1) >= 0)
                getColor(yZero + 1, xZero - 1);

            count++;
            display();

        } else { // to check if anymore zeros are left to be revealed around
            // iterate through all the buttons
            for (int y = 0; y < buttons.length; y++) {
                for (int x = 0; x < buttons[0].length; x++) {
                    if (buttons[y][x].getText().equals("0")) {
                        if (y - 1 >= 0) {
                            if (buttons[y - 1][x].getText().equals("") || buttons[y - 1][x].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if (x + 1 < size) {
                            if (buttons[y][x + 1].getText().equals("") || buttons[y][x + 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if (x - 1 >= 0) {
                            if (buttons[y][x - 1].getText().equals("") || buttons[y][x - 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if (x - 1 >= 0 && y - 1 >= 0) {
                            if (buttons[y - 1][x - 1].getText().equals("")
                                    || buttons[y - 1][x - 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if (x + 1 < size && y + 1 < size) {
                            if (buttons[y + 1][x + 1].getText().equals("")
                                    || buttons[y + 1][x + 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if (x - 1 >= 0 && y + 1 < size) {
                            if (buttons[y + 1][x - 1].getText().equals("")
                                    || buttons[y + 1][x - 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if (x + 1 < size && y - 1 >= 0) {
                            if (buttons[y - 1][x + 1].getText().equals("")
                                    || buttons[y - 1][x + 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                    }
                }
            }
            if (lastXchecked < size + 1 && lastYchecked < size + 1) {
                xZero = lastXchecked;
                yZero = lastYchecked;

                count = 0;
                lastXchecked = size + 1;
                lastYchecked = size + 1;
                display();
            }
        }
    }

    public void getColor(int y, int x) {
        // max number of bombs surrounding a place is 8
        if (solution[y][x] == 0)
            buttons[y][x].setEnabled(false);
        if (solution[y][x] == 1)
            buttons[y][x].setForeground(Color.BLUE);
        if (solution[y][x] == 2)
            buttons[y][x].setForeground(Color.GREEN);
        if (solution[y][x] == 3)
            buttons[y][x].setForeground(Color.RED);
        if (solution[y][x] == 4)
            buttons[y][x].setForeground(Color.MAGENTA);
        if (solution[y][x] == 5)
            buttons[y][x].setForeground(new Color(128, 0, 128));
        if (solution[y][x] == 6)
            buttons[y][x].setForeground(Color.CYAN);
        if (solution[y][x] == 7)
            buttons[y][x].setForeground(new Color(42, 13, 93));
        if (solution[y][x] == 8)
            buttons[y][x].setForeground(Color.lightGray);

        buttons[y][x].setBackground(null); // null sets default background
        buttons[y][x].setText(String.valueOf(solution[y][x])); // does everything instead of writing multi lines
    }
}