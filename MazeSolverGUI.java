import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MazeSolverGUI extends JFrame {
    private static final int CELL_SIZE = 40;
    private int[][] maze;
    private List<int[]> path;
    private int startX = 1, startY = 1, endX = 11, endY = 8;
    private MazeSolverLogic.Algorithm algorithm = MazeSolverLogic.Algorithm.DFS;

    private boolean solveClicked = false;
    private boolean pathFound = false;
    private int animationStep = 0;
    private Timer animationTimer;
    private boolean isPaused = false;

    private MazePanel mazePanel;
    private JButton pauseResumeBtn;

    public MazeSolverGUI() {
        setTitle("Animated Maze Solver with Walker");
        setSize(620, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        maze = getMaze();
        path = null;

        // Maze drawing panel
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        // Controls
        JComboBox<MazeSolverLogic.Algorithm> algoBox = new JComboBox<>(MazeSolverLogic.Algorithm.values());
        algoBox.addActionListener(e -> algorithm = (MazeSolverLogic.Algorithm) algoBox.getSelectedItem());

        JButton solveBtn = new JButton("Solve");
        solveBtn.addActionListener(e -> {
            solveClicked = true;
            path = MazeSolverLogic.solve(maze, startX, startY, endX, endY, algorithm);
            pathFound = (path != null && !path.isEmpty());

            animationStep = 0;
            if (pathFound) {
                animationTimer.start();
                pauseResumeBtn.setEnabled(true);
                pauseResumeBtn.setText("Pause");
                isPaused = false;
            } else {
                mazePanel.repaint(); // Show "Path not found"
            }
        });

        pauseResumeBtn = new JButton("Pause");
        pauseResumeBtn.setEnabled(false);
        pauseResumeBtn.addActionListener(e -> {
            if (isPaused) {
                animationTimer.start();
                pauseResumeBtn.setText("Pause");
            } else {
                animationTimer.stop();
                pauseResumeBtn.setText("Resume");
            }
            isPaused = !isPaused;
        });

        animationTimer = new Timer(150, e -> {
            if (animationStep < path.size()) {
                playSound("step_sound.wav"); // Add sound effect on each step
            }

            animationStep++;
            if (animationStep >= path.size()) {
                animationTimer.stop();
                pauseResumeBtn.setEnabled(false);
            }
            mazePanel.repaint();
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Algorithm:"));
        panel.add(algoBox);
        panel.add(solveBtn);
        panel.add(pauseResumeBtn);
        add(panel, BorderLayout.SOUTH);
    }

    private class MazePanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Draw maze
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[0].length; j++) {
                    if (maze[i][j] == 1) g2.setColor(Color.BLACK);
                    else if (i == startY && j == startX) g2.setColor(Color.RED);
                    else if (i == endY && j == endX) g2.setColor(Color.GREEN);
                    else g2.setColor(Color.WHITE);

                    g2.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g2.setColor(Color.GRAY);
                    g2.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }

            // Draw visited path up to current step
            if (solveClicked && pathFound) {
                g2.setColor(new Color(173, 216, 230)); // light blue for trail

                for (int i = 0; i < animationStep && i < path.size(); i++) {
                    int[] p = path.get(i);
                    if ((p[0] == startX && p[1] == startY) || (p[0] == endX && p[1] == endY)) continue;
                    g2.fillRect(p[0] * CELL_SIZE, p[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }

                // Draw the walking circle
                if (animationStep < path.size()) {
                    int[] current = path.get(animationStep);
                    int x = current[0] * CELL_SIZE + CELL_SIZE / 4;
                    int y = current[1] * CELL_SIZE + CELL_SIZE / 4;

                    g2.setColor(Color.MAGENTA);
                    g2.fillOval(x, y, CELL_SIZE / 2, CELL_SIZE / 2);
                }

                if (animationStep >= path.size()) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    g2.drawString("Path is found", 10, maze.length * CELL_SIZE + 20);
                }
            } else if (solveClicked && !pathFound) {
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString("Path is not found", 10, maze.length * CELL_SIZE + 20);
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(maze[0].length * CELL_SIZE, maze.length * CELL_SIZE + 40);
        }
    }

    private int[][] getMaze() {
        return new int[][] {
            {1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,1,0,1,0,1,0,0,0,0,0,1},
            {1,0,1,0,0,0,1,0,1,1,1,0,1},
            {1,0,1,1,1,1,1,0,0,0,0,0,1},
            {1,0,0,1,0,0,0,0,1,1,1,0,1},
            {1,0,1,0,1,1,1,0,1,0,0,0,1},
            {1,0,1,0,1,0,0,0,1,1,1,0,1},
            {1,0,1,0,1,1,1,0,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
    }

    private void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + filePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // 🔊 Increase volume: value between -80.0 (mute) to 6.0 (max)
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-5.0f);  // Increase volume (use -5.0 for louder sound)

            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeSolverGUI().setVisible(true));
    }
}
