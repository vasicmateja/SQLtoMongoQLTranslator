package main;

import database.MongoDB;
import lombok.Getter;
import lombok.Setter;
import view.table.TableModel;

@Setter
@Getter
public class AppCore {

    private MongoDB mongoDB;


    private TableModel tableModel;

    public AppCore(){
        this.mongoDB = new MongoDB();
        this.tableModel = new TableModel();
    }


}
