package whackamole.whackamole.DB.Model;

import java.util.List;

public interface TableModel<T extends RowModel> {
    
    void Create();

    List<T> Select();
    List<T> Select(String whereStatement, Object... whereValues);

    T Insert(T row);

    void Update(T row);
    void Update(T row, String whereStatement, Object... whereValues);

    void Delete(T row);
    void Delete(String whereStatement, Object... whereValues);

}
