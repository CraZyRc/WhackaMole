package whackamole.whackamole.DB.Model;

import java.util.List;

public interface TableModel<T extends RowModel> {
    
    void Create();

    List<T> Select();

    void Insert(T row);

    void Update(T row);
}
