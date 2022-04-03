package pcd.ass01.barrierversion.view.gui;

import pcd.ass01.barrierversion.controller.passive.Controller;
import pcd.ass01.barrierversion.model.Boundary;
import pcd.ass01.barrierversion.model.P2d;
import pcd.ass01.barrierversion.view.SimulationViewer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class SimulationGUI extends JFrame implements SimulationViewer {
    private final Controller controller;
    private final VisualiserPanel panel;

    public SimulationGUI(final int w, final int h, final Controller controller) {
        this.controller = controller;
        this.setTitle("Bodies Simulation");
        this.setSize(w, h);
        this.setResizable(false);
        panel = new VisualiserPanel(w,h);
        this.getContentPane().add(panel);
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
}
