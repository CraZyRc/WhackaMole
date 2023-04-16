package whackamole.whackamole;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestBase {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    public static Game.GameRunner gameRunnerMock = mock(Game.GameRunner.class);
    public static Game.Settings gameSettingsMock = mock(Game.Settings.class);
    public static Game.Scoreboard.Score gameScoreBoardScoreMock = mock(Game.Scoreboard.Score.class);
    public static Game gameMock = mock(Game.class);
    public static YMLFile YMLfileMock = mock(YMLFile.class);
    public static File fileMock = mock(File.class);
    
    public static Player playerMock = mock(Player.class);
    public static World worldMock = mock(World.class);
    public static BlockFace blockFaceMock = mock(BlockFace.class);

    @BeforeAll
    public static void setupMocks() {

        gameScoreBoardScoreMock.player = playerMock;
        gameScoreBoardScoreMock.score = 10;
        gameScoreBoardScoreMock.timestamp = 1234567890L;

        gameRunnerMock.missed = 0;
        gameRunnerMock.score = 2;
        when(gameMock.getRunning()).thenReturn(gameRunnerMock);
        
        gameSettingsMock.maxMissed = 3;
        gameSettingsMock.world = worldMock;
        when(worldMock.getName()).thenReturn("Test World");

        gameSettingsMock.spawnRotation = blockFaceMock;
        when(blockFaceMock.name()).thenReturn("WEST");
        
        gameSettingsMock.Cooldown = 86400000L;
        gameSettingsMock.Jackpot = true;
        gameSettingsMock.jackpotSpawn = 1;
        gameSettingsMock.difficultyScore = 1;
        gameSettingsMock.pointsPerKill = 1;
        gameSettingsMock.maxMissed = 3;
        gameSettingsMock.Interval = 1;
        gameSettingsMock.spawnChance = 1;
        gameSettingsMock.difficultyScale = 10;
        gameSettingsMock.moleSpeed = 2;
        when(gameMock.getSettings()).thenReturn(gameSettingsMock);

        gameMock.ID = 3;
        gameMock.name = "Test Game"; 

        when(playerMock.getName()).thenReturn("test player");
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());

        YMLfileMock.file = fileMock;
        when(fileMock.getName()).thenReturn("test file");
    }

    @AfterEach
    public void assertAll() {
        softly.assertAll();
    }
}

