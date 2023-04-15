package whackamole.whackamole;

import org.bukkit.entity.Player;

import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

public class TranslationTest extends TestBase{

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

    @MethodSource("getLanguages")
    @ParameterizedTest(name = "{index} Lang: {0}")
    public void AllTranslationsPresent(Locale language) {
        Config.AppConfig.Language = language;
        for (Translator item : Translator.values()) {
            softly.then(item.toString().isEmpty()).as("%s : %s", item.name(), item.key)
                    .withFailMessage("Not found in resource file: Lang_%s.properties", language)
                    .isFalse();
        }
    }

    @MethodSource("getLanguages")
    @ParameterizedTest(name = "{index} Lang: {0}")
    public void AllTanlationsFormatProperly(Locale language) {

        try (MockedStatic<Logger> logger = mockStatic(Logger.class)) {
            var testPattern = Pattern.compile("(\\{.*?\\})");

            Config.AppConfig.Language = language;
            Translator.onLoad();

            for (Translator item : Translator.values()) {

                var args = new ArrayList<Object>();
                for (var i : item.requiredTypes) {
                    if (i == Game.class)        args.add(gameMock);
                    else if (i == Player.class) args.add(playerMock);
                    else if (i == YMLFile.class)args.add(YMLfileMock);
                    else if (i == String.class) args.add("Test String");
                    else {
                        softly.fail("Undefined test case: type [%s] not found in test item",
                                ((Class<?>) i).getName(),
                                item.name(), item.key);
                        return;
                    }
                }

                var result = item.Format(args.toArray());
                var matcher = testPattern.matcher(result);

                if (matcher.find()) {
                    softly.fail("[%s : %s]: formatting Failed: %s", item.name(), item.key, matcher.group(1));
                }
            }
        }
    }
    
    @Test
    public void EmptyFormat() {
        softly.then(Translator.MAIN_OLDVERSION.Format())
        .as("empty format")
        .isNotNull();
    }
    
    @Test
    public void StaticEmptyFormat() {
        softly.then(Translator.Format(Translator.MAIN_OLDVERSION))
        .as("empty format")
        .isNotNull();
    }

    @Test
    public void NullParameterShouldFail() {
        Assert.assertThrows(java.lang.AssertionError.class, () -> {
            Translator.MANAGER_NAMEEXISTS.Format();
        });
    }
}
