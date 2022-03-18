package cfpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IDE{
    private JTextArea inputTxtArea;
    public JTextArea outputTxtArea;
    private JButton runButton;
    private JPanel panelMain;

    public IDE(){
        setComponents();
    }


    public void setComponents(){
        panelMain.setPreferredSize(new Dimension(1000,750));
        panelMain.revalidate();
        panelMain.repaint();
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputTxtArea.setText("");
                Program.runProgram(inputTxtArea.getText());
                setOutputTxt(Program.getOutput()+"\n\n"+"Success...");
            }
        });
    }

    public static void main(String[] args) {
        JFrame jframe = new JFrame("IDE");
        jframe.setContentPane(Program.ide.panelMain);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);
    }

    public void setOutputTxt(String output){
        outputTxtArea.setText(output);
    }

}
