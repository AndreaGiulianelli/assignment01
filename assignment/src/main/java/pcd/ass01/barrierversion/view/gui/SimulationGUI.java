package pcd.ass01.barrierversion.view.gui;

import pcd.ass01.barrierversion.controller.passive.Controller;
import pcd.ass01.barrierversion.model.Boundary;
import pcd.ass01.barrierversion.model.P2d;
import pcd.ass01.barrierversion.view.SimulationViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class SimulationGUI extends JFrame implements SimulationViewer {
    private final Controller controller;
    private final VisualiserPanel panel;
    private final JButton start;
    private final JButton stop;

    public SimulationGUI(final int w, final int h, final Controller controller) {
        this.controller = controller;
        this.setTitle("Bodies Simulation");
        this.setSize(w, h + 40);
        this.setResizable(false);

        panel = new VisualiserPanel(w, h);
        JPanel buttonsPanel = new JPanel();
        this.start = new JButton("Start");
        this.stop = new JButton("Stop");
        buttonsPanel.add(this.start);
        buttonsPanel.add(this.stop);

        JPanel mainPanel = new JPanel();
        LayoutManager layout = new BorderLayout();
        mainPanel.setLayout(layout);
        mainPanel.add(BorderLayout.CENTER, panel);
        mainPanel.add(BorderLayout.SOUTH, buttonsPanel);

        this.setContentPane(mainPanel);

        start.setEnabled(true);
        start.addActionListener(e -> {
            start.setEnabled(false);
            controller.notifyStart();
            start.setText("Resume");
            stop.setEnabled(true);
        });

        stop.setEnabled(false);
        stop.addActionListener(e -> {
            start.setEnabled(true);
            controller.notifyStop();
            stop.setEnabled(false);
        });
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
            public void windowClosed(WindowEvent ev){
                System.exit(-1);
            }
        });
    }

    @Override
    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    @Override
    public void update(List<P2d> positions, double vt, long iter, Boundary boundary) {
        SwingUtilities.invokeLater(() -> {
            panel.display(positions, vt, iter, boundary);
            repaint();
        });
    }

    @Override
    public void simulationEnd() {
        this.start.setEnabled(false);
        this.stop.setEnabled(false);
    }
}
