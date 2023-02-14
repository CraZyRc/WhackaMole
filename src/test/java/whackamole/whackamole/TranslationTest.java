package whackamole.whackamole;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;


@RunWith(Parameterized.class)
public class TranslationTest {
    @Rule
    public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    @Parameters(name="{index}: Lang: {0}")
    public static List<Locale> getLanguages() {
        List<Locale> languages = new ArrayList<Locale>();
        File resourceDir = new File("src/main/resources");
        for (File resource : resourceDir.listFiles()) {
            if (resource.getName().startsWith("Lang_")) {
                String[] fields = resource.getName().substring(5, 10).split("_", 0);
                languages.add(new Locale(fields[0], fields[1]));
            }
        }
        return languages;
    }

    static ServerMock server = MockBukkit.mock();
    static PlayerMock testPlayer = server.addPlayer("Test Player");
    static Game testGame = new Game("Test Game", new Grid(), testPlayer);
    static YMLFile testFile = new YMLFile("/test/testoutput.txt");


    @Parameter
    public Locale language;

    
    @Test
    public void AllTranslationsPresent() {
        Config.AppConfig.Language = language;
        for (Translator item : Translator.values()) {
            softly.then(item.toString() != null).as("%s : %s", item.name(), item.key)
            .withFailMessage("Not found in resource file: Lang_%s.properties", language)
            .isTrue();
        }
    }


    @Test
    public void AllTanlationsFormatProperly() {
        Pattern testPattern = Pattern.compile("(\\{.*?\\})");

        Config.AppConfig.Language = language;
        Translator.onLoad();

        for (Translator item : Translator.values()) {

            List<Object> args = new ArrayList<Object>();
            for(Object i : item.requiredTypes) {
                if      (i == Game.class)    args.add(testGame);
                else if (i == Player.class)  args.add(testPlayer);
                else if (i == YMLFile.class) args.add(testFile);
                else if (i == String.class)  args.add("Test String");
                else {
                    softly.fail("Undefined test case: type [%s] not found in test item", i.getClass().getName(), item.name(), item.key);
                }
            }

            String result = item.Format(args.toArray());
            Matcher matcher = testPattern.matcher(result);

            if(matcher.find())  {
                softly.fail("[%s : %s]: formatting Failed: %s", item.name(), item.key, matcher.group(1));
            }
        }
    }
}
