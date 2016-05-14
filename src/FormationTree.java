import javax.swing.*;
import java.awt.*;

/**
 * Created by zeling on 16/5/14.
 *
 * @author zeling
 */
public class FormationTree extends JPanel {


    public static void main(String[] args) {
        PropositionParser parser = new PropositionParser();
        JFrame frame = new JFrame();
        String proposition = JOptionPane.showInputDialog("Your proposition here");
        try {
            FormationTree view = new FormationTree(parser.parse(proposition));
            frame.add(view);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            frame.pack();
            int x = (int) ((d.getWidth() - frame.getWidth()) / 2);
            int y = (int) ((d.getHeight() - frame.getHeight()) / 2);
            frame.setLocation(x, y);
            frame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid proposition");
            e.printStackTrace();
            System.exit(1);
        }
    }


    Proposition proposition;
    int width;
    int height;
    Font font = new Font(Font.SANS_SERIF, Font.ITALIC, 20);
    FontMetrics fm = getFontMetrics(font);

    public FormationTree(Proposition proposition) {
        this.proposition = proposition;
        this.width = width(proposition);
        this.height = height(proposition);
        setSize(width, height);
    }

    private int width(Proposition proposition) {
        return fm.stringWidth(proposition.toString());
    }

    private int height(Proposition proposition) {
        int fheight = fm.getHeight();
        int fdescent = fm.getDescent();
        return proposition.apply(
                letter -> fheight + fdescent,
                unary -> height(unary.getChild()) + fheight * 2,
                binary -> Math.max(height(binary.getLeft()), height(binary.getRight())) + fheight * 2
        ) + fdescent;
    }

    public void paintPropositon(Graphics g, Proposition p, int x1, int x2, int y) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int length = fontMetrics.stringWidth(p.toString());
        int height = fontMetrics.getHeight();
        int descent = fontMetrics.getDescent();
        /* the f*cking ugly java pattern match :) */
        p.apply(
        letter -> {
            g.drawString(letter.toString(), (x1 + x2 - length) / 2, y);
            return null;
        }, unary -> {
            g.drawString(p.toString(), (x1 + x2 - length) / 2, y);
            g.drawLine((x1 + x2) / 2, y + descent, (x1 + x2) / 2, y + height + descent);
            paintPropositon(g, unary.getChild(), x1, x2, y + height * 2);
            return null;
        }, binary -> {
            int lwidth = width(binary.getLeft());
            int rwidth = width(binary.getRight());
            g.drawString(p.toString(), (x1 + x2 - length) / 2, y);
            g.drawLine((x1 + x2) / 2, y + descent, x1 + lwidth / 2, y + height + descent);
            g.drawLine((x1 + x2) / 2, y + descent, x2 - rwidth / 2, y + height + descent);
            paintPropositon(g, binary.getLeft(), x1, x1 + lwidth, y + height * 2);
            paintPropositon(g, binary.getRight(), x2 - rwidth, x2, y + height * 2);
            return null;
        });

    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(font);
        ((Graphics2D) g).setStroke(new BasicStroke(2));
        /* open up antialising to get rid of the shit of bloody ugly fonts */
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        paintPropositon(g, proposition, 10, width + 10, g.getFontMetrics().getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width + 20, height);
    }
}

