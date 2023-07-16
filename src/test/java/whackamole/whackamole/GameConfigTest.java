package whackamole.whackamole;

import org.junit.jupiter.api.Test;

public class GameConfigTest extends TestBase{
    static YMLFile OldConfigFile = new YMLFile("test/config_1.3.yml");
    static YMLFile NewConfigFile = new YMLFile("test/config_1.5.yml");

    @Test
    void Loading_new_config_should_succeed() {
        softly.then(Config.LoadConfig(NewConfigFile)).isEqualTo(true);
        softly.then(Config.Game.HAMMER_ITEM).isEqualTo("GOLDEN_AXE");
        softly.then(Config.Game.PLAYER_AXE).isNotNull();
        softly.then(Config.Game.FiELD_MARGIN_Y).isEqualTo(2.5);
        softly.then(Config.Permissions.PERM_TICKET_USE).isEqualTo("WAM.Use");
        softly.then(Config.Permissions.PERM_PLAY).isEqualTo("WAM.Play");
        softly.then(Config.Permissions.PERM_BUY).isEqualTo("WAM.Buy");
        softly.then(Config.Permissions.PERM_SETTINGS).isEqualTo("WAM.Settings");
        softly.then(Config.Permissions.PERM_RELOAD).isEqualTo("WAM.Reload");
        softly.then(Config.Permissions.PERM_CREATE).isEqualTo("WAM.create");
        softly.then(Config.Permissions.PERM_REMOVE).isEqualTo("WAM.Remove");
        softly.then(Config.Permissions.PERM_POSITIONS).isEqualTo("WAM.Positions");
        softly.then(Config.Permissions.PERM_TOP).isEqualTo("WAM.Top");
    }

    @Test
    void Loading_old_config_should_succeed_with_warnings() {
        softly.then(Config.LoadConfig(OldConfigFile)).isEqualTo(false);
        softly.then(Config.Game.HAMMER_ITEM).isNull();
        softly.then(Config.Game.PLAYER_AXE).isNull();
        softly.then(Config.Game.FiELD_MARGIN_Y).isEqualTo(2.0);
        softly.then(Config.Permissions.PERM_TICKET_USE).isNull();
        softly.then(Config.Permissions.PERM_PLAY).isNull();
        softly.then(Config.Permissions.PERM_BUY).isNull();
        softly.then(Config.Permissions.PERM_SETTINGS).isNull();
        softly.then(Config.Permissions.PERM_RELOAD).isNull();
        softly.then(Config.Permissions.PERM_CREATE).isNull();
        softly.then(Config.Permissions.PERM_REMOVE).isNull();
        softly.then(Config.Permissions.PERM_POSITIONS).isNull();
        softly.then(Config.Permissions.PERM_TOP).isNull();
    }
}
