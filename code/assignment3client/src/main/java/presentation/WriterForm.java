package presentation;

import entities.Writer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WriterForm extends JFrame{
    private JPanel panel1;
    private JTextField username;
    private JTextField password;
    private JButton login;
    private Controller controller;
    private Label lblError;

  public WriterForm(Controller controller)
  {
      super.setSize(300,300);
      super.getDefaultCloseOperation();
      this.panel1=new JPanel();
      this.username=new JTextField("USERNAME",20);
      this.password=new JTextField("PASSWORD",20);
      this.login=new JButton("Login");
      this.controller=controller;
      this.lblError=new Label();
      login.addActionListener(e->{
          if(login.isEnabled())
          {
              controller.loginRequest();
          }
      });
      this.panel1.add(username);
      this.panel1.add(password);
      this.panel1.add(login);
      super.add(panel1);
      this.setDefaultCloseOperation(EXIT_ON_CLOSE);

      this.controller=controller;
      addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent arg0) {
              controller.disconnectClient();
          }
      });
  }

  public Writer getWriter() {
      return new Writer("", username.getText(), password.getText());
  }


    public void displayError(String msg) {
        lblError.setText(msg);
    }
}
