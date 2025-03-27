package edu.lab14;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/*
* @author Jakub Zieliński
* Lab 14, 12K3 K05
* */
public class CMainForm extends JFrame implements Runnable{

    private JGamePanel gamePanel;
    private JPanel mainPanel;
    private JPanel displayPanel;
    private JTextField ipTF;
    private JButton startBtn;
    private JButton restartBtn;

    ServerSocket serverSocket;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    Thread thread;

    @Override
    public void run() {
        while(CConfig.threadRunning) {
            if(CConfig.errors >= 10) CConfig.comError = true;
            if(!CConfig.yourTurn && !CConfig.comError) {
                try {
                    int space = dis.readInt();
                    if((space>=0) && (space < 9)) {
                        CConfig.board[space] = CConfig.circle ? "X" : "O";
                        CConfig.yourTurn = true;
                        if(checkWin(false) || checkTie()) {
                            restartBtn.setEnabled(true);
                            CConfig.yourTurn = false;
                        }
                    } else if (space == 999) {
                        CConfig.reset();
                        CConfig.yourTurn = true;
                        gamePanel.repaint();
                    }
                } catch (IOException io) {
                    CConfig.errors++;
                }
            }
            gamePanel.repaint();
            if (!CConfig.circle && !CConfig.accepted) listenForServerRequest();
        }
    }

    public CMainForm() {
        super("Kółko i krzyżyk");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        restartBtn.setEnabled(false);
        startBtn.addActionListener(actionEvent -> buttonStartClick());
        restartBtn.addActionListener(actionEvent -> buttonRestartClick());
    }

    private void createUIComponents() {
        displayPanel = new JGamePanel();
        gamePanel = (JGamePanel) displayPanel;
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedEvent(e);
            }
        });
    }

    private void initializeServer() {
        try {
            serverSocket = new ServerSocket(CConfig.port, 8, InetAddress.getByName(CConfig.ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        CConfig.yourTurn = true;
        CConfig.circle = false;
    }

    private void listenForServerRequest() {
        try {
            socket = serverSocket.accept();
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            CConfig.accepted = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean connectToServer() {
        try {
            socket = new Socket(CConfig.ip, CConfig.port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            CConfig.accepted = true;
            gamePanel.repaint();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean checkTie() {
        for (String s : CConfig.board) if (s == null) return false;
        CConfig.tie = true;
        return true;
    }

    private boolean checkWin(boolean myWin) {
        String str = myWin ? CConfig.circle ? "O" : "X" : CConfig.circle ? "X" : "O";
        for(int[] win : CConfig.wins) {
            if(CConfig.board[win[0]] == null || CConfig.board[win[1]] == null || CConfig.board[win[2]] == null) continue;
            if(CConfig.board[win[0]].equals(str) && CConfig.board[win[1]].equals(str) && CConfig.board[win[2]].equals(str)) {
                CConfig.line[0] = win[0];
                CConfig.line[1] = win[2];
                if (myWin) {
                    CConfig.won = true;
                } else {
                    CConfig.enemyWon = true;
                }
                return true;
            }
        }
        return false;
    }

    private void buttonStartClick() {
        CConfig.ip = ipTF.getText().trim();
        if(!connectToServer()) initializeServer();
        thread = new Thread(this, "lab14game");
        CConfig.threadRunning = true;
        thread.start();
        startBtn.setEnabled(false);
        setTitle(CConfig.circle ? "Kółko i krzyżyk: [O]" : "Kółko i krzyżyk: [X]");
    }

    private void buttonRestartClick() {
        CConfig.reset();
        Toolkit.getDefaultToolkit().sync();
        try {
            dos.writeInt(999);
            dos.flush();
        } catch (IOException e) { CConfig.errors++; }
        CConfig.yourTurn = false;
        gamePanel.repaint();
    }

    private void mousePressedEvent(MouseEvent e) {
        if(CConfig.accepted) {
            System.out.println("accepted");
            if(CConfig.yourTurn) {
                System.out.println("my turn");
                if(!CConfig.comError && !CConfig.won && !CConfig.enemyWon) {
                    System.out.println("pos log");
                    int x = 3 * e.getX() / CConfig.WIDTH;
                    int y = 3 * e.getY() / CConfig.HEIGHT;
                    int pos = x + 3 * y;
                    if(CConfig.board[pos] == null) {
                        CConfig.board[pos] = !CConfig.circle ? "X" : "O";
                        CConfig.yourTurn = false;
                        Toolkit.getDefaultToolkit().sync();

                        try {
                            dos.writeInt(pos);
                            dos.flush();
                        } catch (IOException io) { CConfig.errors++; }
                        if(checkWin(true) || checkTie()) restartBtn.setEnabled(true);
                        gamePanel.repaint();
                    }
                }
            }
        }
    }
}
