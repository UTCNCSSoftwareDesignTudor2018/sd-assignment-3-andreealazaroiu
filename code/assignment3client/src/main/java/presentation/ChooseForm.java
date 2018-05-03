package presentation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChooseForm extends JFrame {
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
    private Controller controller;

    public ChooseForm(Controller controller)
    {   super.setSize(200,200);
        this.panel1=new JPanel();
        this.button2=new JButton("Reader");
        this.button1=new JButton("Writer");
        this.panel1.add(button1);
        this.panel1.add(button2);
        super.add(panel1);
        super.getDefaultCloseOperation();
        super.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.controller=controller;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                controller.disconnectClient();
            }
        });
    }


    public void runFrame(WriterForm writerForm,ReaderForm readerForm)
    {
        button1.addActionListener(e-> {
            if (button1.isEnabled()) {
                writerForm.setVisible(true);
            }
        });
        button2.addActionListener(e->{
                if(button1.isEnabled()) {
                    readerForm.setVisible(true);
                }
        });


    }
}
