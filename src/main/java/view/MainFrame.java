package view;

import lombok.Data;
import main.AppCore;
import observer.Notification;
import observer.Subscriber;
import resource.data.Row;
import view.table.TableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;


@Data
public class MainFrame extends JFrame implements Subscriber {

    private static MainFrame instance = null;

    private AppCore appCore;
    private JTable jTable;
    private Toolbar toolBar;
    private JTextPane jTextPane = new JTextPane();
    private TableModel tableModel = new TableModel();


    private MainFrame(){}

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
            instance.initalise();
        }
        return instance;
    }

    private void initalise() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        this.setSize(screenSize.width/2, screenSize.height/2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolBar = new Toolbar();
        this.add(toolBar,BorderLayout.NORTH);

        jTextPane = new JTextPane();
        jTextPane.setSize(screenSize.width/2, screenSize.height/10);
        jTextPane.setBackground(Color.gray);
        this.add(jTextPane,BorderLayout.CENTER);



        jTable = new JTable(tableModel);
        jTable.setPreferredScrollableViewportSize(new Dimension(screenSize.width/2,screenSize.height/5 ));
        jTable.setFillsViewportHeight(true);
        jTable.setBackground(Color.cyan);
        this.add(new JScrollPane(jTable),BorderLayout.SOUTH);
        this.setVisible(true);


    }

    public String getQuerry(){

        return this.getJTextPane().getText();
    }

    public void update(Vector dataVector, Vector columnVector){

        //tableModel.setDataVector(dataVector, columnVector);
        this.getJTable().updateUI();
    }

    public void setAppCore(AppCore appCore){
        this.appCore = appCore;
        //this.appCore.addSubscriber(this);
        this.jTable.setModel(appCore.getTableModel());
    }

    @Override
    public void update(Notification notification) {
        System.out.println("UPDATEE");
        this.appCore.getTableModel().setRows((List<Row>) notification.getData());
        this.jTable.updateUI();
    }
}
