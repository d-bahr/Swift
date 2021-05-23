package swiftmod.common;

public class SwiftTextUtils
{
    public static final String PINK = "\u00A7c";
    public static final String RED = "\u00A74";
    public static final String ORANGE = "\u00A76";
    public static final String YELLOW = "\u00A7e";
    public static final String LIME = "\u00A7a";
    public static final String GREEN = "\u00A72";
    public static final String AQUA = "\u00A7b";
    public static final String DARK_AQUA = "\u00A73";
    public static final String LIGHT_BLUE = "\u00A79";
    public static final String BLUE = "\u00A71";
    public static final String MAGENTA = "\u00A7d";
    public static final String PURPLE = "\u00A75";
    public static final String WHITE = "\u00A7f";
    public static final String LIGHT_GRAY = "\u00A77";
    public static final String GRAY = "\u00A78";
    public static final String BLACK = "\u00A70";

    public static final String BOLD = "\u00A7l";
    public static final String ITALIC = "\u00A7o";
    public static final String UNDERLINE = "\u00A7n";
    public static final String STRIKE = "\u00A7m";
    public static final String RANDOM = "\u00A7k";

    public static final String RESET = "\u00A7r";

    public static String color(String text, Color color)
    {
        switch (color)
        {
        case Transparent:
            return color(text, WHITE);
        case Black:
            return color(text, BLACK);
        case Gray:
            return color(text, GRAY);
        case LightGray:
            return color(text, LIGHT_GRAY);
        case White:
            return color(text, WHITE);
        case Pink:
            return color(text, PINK);
        case Red:
            return color(text, RED);
        case Orange:
            return color(text, ORANGE);
        case Yellow:
            return color(text, YELLOW);
        case Lime:
            return color(text, LIME);
        case Green:
            return color(text, GREEN);
        case LightBlue:
            return color(text, AQUA); // Closest color; can't get exact match.
        case Cyan:
            return color(text, DARK_AQUA); // Closest color; can't get exact match.
        case Blue:
            return color(text, BLUE);
        case Magenta:
            return color(text, MAGENTA);
        case Purple:
            return color(text, PURPLE);
        case Brown:
            return color(text, ORANGE); // Closest color; can't get exact match.
        default:
            return color(text, WHITE);
        }
    }

    public static String color(String text, String color)
    {
        return color + text + color;
    }

    public static String bold(String text)
    {
        return BOLD + text + BOLD;
    }

    public static String italic(String text)
    {
        return ITALIC + text + ITALIC;
    }

    public static String underline(String text)
    {
        return UNDERLINE + text + UNDERLINE;
    }

    public static String strike(String text)
    {
        return STRIKE + text + STRIKE;
    }

    public static String colorBoolean(boolean value)
    {
        return colorBoolean(value, "true", "false");
    }

    public static String colorBoolean(boolean value, String trueText, String falseText)
    {
        if (value)
            return LIME + trueText + LIME;
        else
            return PINK + falseText + PINK;
    }
}
