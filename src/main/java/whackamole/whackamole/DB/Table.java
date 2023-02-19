package whackamole.whackamole.DB;

import java.util.List;

public interface Table {

    void create();

    List<? extends Row> select();

    int insert(Row row);
}
