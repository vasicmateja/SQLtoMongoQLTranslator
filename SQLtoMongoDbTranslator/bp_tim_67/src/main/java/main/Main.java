package main;

import database.MongoDB;
import test.DBTest;
import test.MongoDBTest;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
        AppCore appCore = new AppCore();
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.setAppCore(appCore);

        /*
        DBTest dbTest = new MongoDBTest(MongoDB.getConnection());
        dbTest.runTest();
        */


    }
}