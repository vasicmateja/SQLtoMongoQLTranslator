package view.table;

import resource.data.Row;
import view.MainFrame;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

public class TableModel extends DefaultTableModel {
    private List<Row> rows;

    private void updateModel(){

       int columnCount = rows.get(0).getFields().keySet().size();


       Vector columnVector = DefaultTableModel.convertToVector(rows.get(0).getFields().keySet().toArray());
       Vector dataVector = new Vector(columnCount);


       for (int i=0; i<rows.size(); i++){
           dataVector.add(DefaultTableModel.convertToVector(rows.get(i).getFields().values().toArray()));
       }
       setDataVector(dataVector, columnVector);




        MainFrame.getInstance().update(dataVector,columnVector);

    }

    public void setRows(List<Row> rows) {
        //System.out.println(rows);
        this.rows = rows;
        updateModel();
        MainFrame.getInstance().getJTable().repaint();
    }

}
