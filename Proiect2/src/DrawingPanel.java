import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class DrawingPanel extends JPanel {

    public double Input=Double.NaN;
    private String info="";
    private Perceptron paintTarget;

    public DrawingPanel() {
    }

    public void setPaintTarget(Perceptron perceptron){
        this.paintTarget=perceptron;
        this.repaint();
    }
    public void repaintWithInfo(String info){
        this.info=info;
        this.repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.paintTarget == null) return;
        synchronized (paintTarget) {
            int perceptronX = 250;
            int perceptronY = 160;
            int inputX = 50;
            int inputY = 200;
            int lossX = 450;
            int lossY = 200;


            g.setColor(Color.BLACK);
            g.fillOval(perceptronX, perceptronY, 120, 120);

            g.setColor(Color.BLACK);
            g.fillOval(inputX, inputY, 60, 60);

            g.setColor(Color.BLACK);
            g.fillRect(lossX, lossY, 60, 60);

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.drawString(this.info, 380, 50);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString("Loss:", lossX + 15, lossY + 25);
            g.drawString(String.format("[%.2f]",paintTarget.getOutput()),perceptronX+60,perceptronY+60);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.drawString(String.format("%.3f", paintTarget.LossNumerical), lossX + 15, lossY + 40);

            g.setColor(Color.BLACK);
            g.drawLine(perceptronX + 25, perceptronY + 50, inputX + 25, inputY + 25);

            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(String.format("W: [%.5f]", paintTarget.getWeight_vector()[0]), perceptronX - 90, perceptronY + 50);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            if(paintTarget.getLastInput() != null) {
                g.drawString(String.format("[%.2f]", paintTarget.getLastInput()[0]), inputX + 10, inputY + 35);
            }else{
                g.drawString(String.format(""), inputX + 10, inputY + 35);
            }

        }
    }

}
