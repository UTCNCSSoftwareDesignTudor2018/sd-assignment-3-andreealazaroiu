package presentation;

import entities.Article;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ReaderForm extends JFrame{
    private JPanel panel1;
    private JTextArea seeArticle;
    private JButton ok;
    private JTextField enterArticle;
    private Controller controller;

    public ReaderForm(Controller controller)
    {
        this.setSize(500,500);
        this.panel1=new JPanel();
        this.seeArticle =new JTextArea("");
        this.ok=new JButton("OK");
        this.controller = controller;
        ok.addActionListener(e->{
            controller.getArticleRequest();
        });
        this.enterArticle=new JTextField(20);
        this.panel1.add(seeArticle);
        this.panel1.add(enterArticle);
        this.panel1.add(ok);
        this.add(panel1);
        this.setVisible(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                controller.disconnectClient();
            }
        });
    }

    public void displayArticle( Article a)
    {
      seeArticle.setText(a.getTitle() + "\nBy " + a.getWriter().getName()+"\n"+a.getBody());
    }
    public void displayAllArticles(List<Article> list)
    {
        System.out.println("reader form display all");
        String text = list.stream().map(a->a.getTitle()).reduce("", (s1,s2)->s1+"\n"+s2);
        //System.out.println(text);
        seeArticle.setText(text);
    }

    public String getArticleTitle() {
        return enterArticle.getText();
    }
}
