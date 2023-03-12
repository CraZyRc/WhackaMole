package whackamole.whackamole.DB;

import java.util.List;

public interface Table {

    void Create();

    List<? extends Row> Select();

    int Insert(Row row);
    void Update(Row row);
}
