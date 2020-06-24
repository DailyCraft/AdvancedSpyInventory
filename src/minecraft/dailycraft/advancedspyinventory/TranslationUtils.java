package minecraft.dailycraft.advancedspyinventory;

import org.bukkit.entity.Player;

public class TranslationUtils
{
    private final Player sender;

    public TranslationUtils(Player sender)
    {
        this.sender = sender;
    }

    public String translate(String english, String french)
    {
        final String locale = sender.getLocale().toLowerCase();

        switch (locale)
        {
            case "fr_fr":
                return french;

            default:
                return english;
        }
    }

    public Player getSender()
    {
        return sender;
    }


}
