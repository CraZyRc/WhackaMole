package whackamole.whackamole.DB.Model;

import java.util.List;

public interface TableModel<T extends RowModel> {
    
    void Create();

    /**
     * Selects All Rows from Table
     * @return List Of < Rows extends RowModel >
     */
    List<T> Select();

    int Insert(T row);

    void Update(T row);
}
