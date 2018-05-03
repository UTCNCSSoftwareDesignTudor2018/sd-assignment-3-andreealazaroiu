package presentation;

import entities.Article;
import entities.Writer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class WriterForm2 extends JFrame{
    private JPanel panel;
    private JTextField abstr;
    private JTextArea content;
    private JButton button1;
    private JTextField thetitle;
    private JTextArea articlesArea;
    private JTextField deleteField;
    private JButton delete;
    private Controller controller;
    private Writer writer;

    public WriterForm2(Controller controller)
    {
        super.setSize(900,900);
        super.getDefaultCloseOperation();
        this.panel=new JPanel();
        this.abstr=new JTextField("Abstract ",20);
        this.thetitle=new JTextField("Title",20);
        this.content=new JTextArea(20,75);
        this.button1=new JButton("OK");
        button1.addActionListener(e->{
            controller.createRequest();
        });
        this.articlesArea=new JTextArea(10,75);
        this.deleteField=new JTextField("Delete Article",20);
        this.delete=new JButton("Delete ");
        delete.addActionListener(e->{
            controller.deleteRequest();
        });
        panel.add(thetitle);
        panel.add(abstr);
        panel.add(content);
        panel.add(button1);
        panel.add(articlesArea);
        panel.add(deleteField);
        panel.add(delete);
        super.add(panel);
        super.setVisible(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.controller=controller;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                controller.disconnectClient();
            }
        });

    }
    public void displayAllArticles(List<Article> list)
    {
        String text = list.stream().map(a->a.getTitle()).reduce("", (s1,s2)->s1+"\n"+s2);
        //System.out.println(text);
        articlesArea.setText(text);
    }

    public Article createArticle()
    {
        return new Article(thetitle.getText(),abstr.getText(),articlesArea.getText(),writer);
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public String getSelectedArticle() {
        return deleteField.getText();
    }
}
