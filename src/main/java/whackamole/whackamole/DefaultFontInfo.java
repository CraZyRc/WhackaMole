package whackamole.whackamole;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DefaultFontInfo {
    SPACE                   (' ',4),
    EXCLAMATION_MARK        ('!', 2),
    QUOTATION_MARK          ('"', 5),
    DOLLAR_SIGN             ('$', 6),
    PERCENT_SIGN            ('%', 6),
    AMPERSAND               ('&', 6),
    APOSTROPHE              ('\'', 3),
    COLON                   (':', 5),
    PARENTHESIS_RIGHT       (')', 5),
    PARENTHESES_LEFT        ('(', 5),
    ASTERISK                ('*', 5),
    PLUS_SIGN               ('+', 6),
    COMMA                   (',', 2),
    MINUS_SIGN              ('-', 6),
    DOT                     ('.', 2),
    SLASH                   ('/', 6),
    NUM_0                   ('0', 6),
    NUM_1                   ('1', 6),
    NUM_2                   ('2', 6),
    NUM_3                   ('3', 6),
    NUM_4                   ('4', 6),
    NUM_5                   ('5', 6),
    NUM_6                   ('6', 6),
    NUM_7                   ('7', 6),
    NUM_8                   ('8', 6),
    NUM_9                   ('9', 6),
    SEMICOLON               (';', 2),
    ARROW_LEFT              ('<', 5),
    EQUAL_SIGN              ('=', 6),
    ARROW_RIGHT             ('>', 5),
    QUESTION_MARK           ('?', 6),
    AT_SIGN                 ('@', 7),
    A                       ('A', 6),
    B                       ('B', 6),
    C                       ('C', 6),
    D                       ('D', 6),
    E                       ('E', 6),
    F                       ('F', 6),
    G                       ('G', 6),
    H                       ('H', 6),
    I                       ('I', 4),
    J                       ('J', 6),
    K                       ('K', 6),
    L                       ('L', 6),
    M                       ('M', 6),
    N                       ('N', 6),
    O                       ('O', 6),
    P                       ('P', 6),
    Q                       ('Q', 6),
    R                       ('R', 6),
    S                       ('S', 6),
    T                       ('T', 6),
    U                       ('U', 6),
    V                       ('V', 6),
    W                       ('W', 6),
    X                       ('X', 6),
    Y                       ('Y', 6),
    Z                       ('Z', 6),
    SQUARE_BRACKET_LEFT     ('[', 4),
    BACKSLASH               ('\\', 6),
    SQUARE_BRACKET_RIGHT    (']', 4),
    CIRCUMFLEX              ('^', 6),
    UNDERSCORE              ('_', 6),
    ACCENT_GRAVE            ('`', 0),
    a                       ('a', 6),
    b                       ('b', 6),
    c                       ('c', 6),
    d                       ('d', 6),
    e                       ('e', 6),
    f                       ('f', 5),
    g                       ('g', 6),
    h                       ('h', 6),
    i                       ('i', 2),
    j                       ('j', 6),
    k                       ('k', 5),
    l                       ('l', 3),
    m                       ('m', 6),
    n                       ('n', 6),
    o                       ('o', 6),
    p                       ('p', 6),
    q                       ('q', 6),
    r                       ('r', 6),
    s                       ('s', 6),
    t                       ('t', 4),
    u                       ('u', 6),
    v                       ('v', 6),
    w                       ('w', 6),
    x                       ('x', 6),
    y                       ('y', 6),
    z                       ('z', 6),
    CURLY_BRACKET_LEFT      ('{', 5),
    VERTICAL_BAR            ('|', 2),
    CURLY_BRACKET_RIGHT     ('}', 5),
    TILDE                   ('~', 7),
    DEFAULT                 ('a', 4);

    private final char character;
    private final int length;

    DefaultFontInfo(char character, int length) {
        this.character = character;
        this.length = length;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }

    public int getBoldLength() {
        return this == SPACE ? this.getLength() : this.length + 1;
    }

    public static DefaultFontInfo getDefaultFontInfo(char c) {
        DefaultFontInfo[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DefaultFontInfo dFI = var1[var3];
            if (dFI.getCharacter() == c) {
                return dFI;
            }
        }

        return DEFAULT;
    }

    public static int getMessageLength(String message){

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§' || c == '&'){
                previousCode = true;
                continue;
            }else if(previousCode){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
            }
        }
        return messagePxSize;
    }

    public static String Color(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String padRight(String message, int length) {
        int messageLength = getMessageLength(message);
        int padding = length - messageLength;
        if (padding < 4) return message;
        Logger.info(String.format("message: %s\nmessageLength: %s\nspaces: %s\npadding: %s", message, messageLength, Math.floorDiv(padding, 4), padding));
        return message + " ".repeat(Math.floorDiv(padding, 4));
    }
}


