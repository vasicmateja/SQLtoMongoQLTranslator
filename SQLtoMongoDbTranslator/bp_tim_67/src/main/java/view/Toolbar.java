package view;

import controller.RunButtonController;

import javax.swing.*;

public class Toolbar extends JToolBar {
    private  JButton runBtn;

    Toolbar(){
        init();
    }

    private void init(){
        runBtn = new JButton("RUN");
        runBtn.addActionListener(new RunButtonController());

        this.add(runBtn);
    }


}
