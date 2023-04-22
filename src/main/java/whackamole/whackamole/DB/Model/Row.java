package whackamole.whackamole.DB.Model;

import java.lang.reflect.Field;

import whackamole.whackamole.Logger;

public class Row implements RowModel {
    public Row() {
    }

    public static void upCast(Row a, Row b)  {
        Field[] AFields;
        try {
            AFields = a.getClass().getDeclaredFields();
        } catch (Exception e) {
            Logger.error(e.getMessage());
            assert false : e.getMessage();
            return;
        }
        try {
            for (var field : AFields) {
                var BField = Table.findDeclaredField(b.getClass(), field.getName());
                BField.set(b, field.get(a));
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
            assert false : e.getMessage();
        }
    }
}
