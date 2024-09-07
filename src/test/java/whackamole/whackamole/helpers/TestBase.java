package whackamole.whackamole.helpers;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import whackamole.whackamole.Utils.YMLFile;

import whackamole.whackamole.GS.*;
import whackamole.whackamole.Grid;


@ExtendWith(MockitoExtension.class)
public class TestBase {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();
    
    public static Grid gridMock = mock(Grid.class);
    public static Game gameMock = mock(Game.class);
    public static GameRunner gameRunnerMock = mock(GameRunner.class);
    public static Settings gameSettingsMock = mock(Settings.class);

    public static File fileMock = mock(File.class);
    public static YMLFile YMLfileMock = mock(YMLFile.class);
    
    public static Player playerMock = mock(Player.class);
    public static World worldMock = mock(World.class);
    public static Location locationMock = mock(Location.class);
    public static BlockFace blockFaceMock = mock(BlockFace.class);
    public static Block blockMock = mock(Block.class);
    
    static MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class);

    @BeforeAll
    public static void setupMocks() {

        gameRunnerMock.missed = 0;
        gameRunnerMock.score = 2;
        when(gameMock.getRunning()).thenReturn(Optional.of(gameRunnerMock));
        
        gameSettingsMock.missCount = 3;
        gameSettingsMock.world = worldMock;
        when(worldMock.getName()).thenReturn("Test World");

        when(locationMock.getWorld()).thenReturn(worldMock);
        when(locationMock.getX()).thenReturn(1d);
        when(locationMock.getY()).thenReturn(1d);
        when(locationMock.getZ()).thenReturn(1d);
        when(locationMock.getYaw()).thenReturn(1f);
        when(locationMock.getPitch()).thenReturn(1f);

        gameSettingsMock.spawnRotation = blockFaceMock;
        when(blockFaceMock.name()).thenReturn("WEST");
        
        gameSettingsMock.ID = 3;
        gameSettingsMock.Name = "Test Game"; 
        gameSettingsMock.Cooldown = 86400000L;
        gameSettingsMock.hasJackpot = true;
        gameSettingsMock.difficultyScore = 1;
        gameSettingsMock.scorePoints = 1;
        gameSettingsMock.missCount = 3;
        gameSettingsMock.spawnTimer = 1;
        gameSettingsMock.spawnChance = 1;
        gameSettingsMock.difficultyScale = 10;
        gameSettingsMock.moleSpeed = 2;
        gameSettingsMock.teleportLocation = new Location(worldMock, 10, 20, 30, 0.0f, 2.0f);
        gameSettingsMock.scoreLocation = new Location(worldMock, 100, 200, 300, 10.0f, 20.0f);
        
        when(gameMock.getSettings()).thenReturn(gameSettingsMock);

        when(gameMock.getName()).thenReturn(gameSettingsMock.Name);
        when(gameMock.getID()).thenReturn(gameSettingsMock.ID);

        when(playerMock.getName()).thenReturn("test player");
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());

        YMLfileMock.file = fileMock;
        when(fileMock.getName()).thenReturn("test file");

        when(blockMock.getX()).thenReturn(1);
        when(blockMock.getY()).thenReturn(1);
        when(blockMock.getZ()).thenReturn(1);

        gridMock.world = worldMock;
        gridMock.grid = new ArrayList<>() {
            {
                add(blockMock);
            }
        };

        bukkitMock.when(() -> Bukkit.getWorld(worldMock.getName())).thenReturn(worldMock);
    }

    @AfterEach
    public void assertAll() {
        softly.assertAll();
    }
}

