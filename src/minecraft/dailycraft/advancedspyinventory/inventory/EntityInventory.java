package minecraft.dailycraft.advancedspyinventory.inventory;

import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static minecraft.dailycraft.advancedspyinventory.inventory.InventoryUtils.InformationItems;

public class EntityInventory extends AbstractInventory
{
    private final LivingEntity entity;

    public EntityInventory(Player sender, LivingEntity entity)
    {
        this(sender, entity, 2);
    }

    public EntityInventory(Player sender, LivingEntity entity, int inventoryColumns)
    {
        super(new TranslationUtils(sender), inventoryColumns);
        this.entity = entity;
    }

    @Override
    public List<ItemStack> getContents()
    {
        List<ItemStack> result = new ArrayList<>();
        ((CraftLivingEntity) entity).getHandle().aQ().forEach(result::add);
        return result;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index >= getSize() - 17 && index <= getSize() - 14)
            return InformationItems.getItem(getContents().get(-(index - (-12 + getSize()))), InventoryUtils.InformationItems.addWarning(informationItems.items()[-(index - (-12 + getSize()))], translation));
        else if (index >= getSize() - 12 && index <= getSize() - 11)
            return InformationItems.getItem(getContents().get(index - (-12 + getSize())), InventoryUtils.InformationItems.addWarning(informationItems.items()[index - (-12 + getSize())], translation));
        else if (index == getSize() - 8)
            return InventoryUtils.setItemWithDisplayName(new Potion(PotionType.INSTANT_HEAL).toItemStack(1), translation.format("interface.inventory.health", new DecimalFormat("#.#").format(entity.getHealth()), entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        else if (index == getSize() - 7)
            return InventoryUtils.setItemWithDisplayName(Material.ARROW, translation.format("interface.inventory.location"),
                    translation.format("interface.inventory.world", entity.getWorld().getName(), entity.getWorld().getEnvironment()),
                    "X : " + new DecimalFormat("#.##").format(entity.getLocation().getX()),
                    "Y : " + new DecimalFormat("#.##").format(entity.getLocation().getY()),
                    "Z : " + new DecimalFormat("#.##").format(entity.getLocation().getZ()), "",
                    "-> " + translation.format("interface.inventory.teleport"));
        else if (index == getSize() - 5)
            return InventoryUtils.setItemWithDisplayName(Material.BARRIER, translation.format("interface.inventory.clear"));
        else
            return InventoryUtils.getVoidItem();
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index >= getSize() - 17 && index <= getSize() - 14)
        {
            if (!stack.equals(InventoryUtils.InformationItems.addWarning(informationItems.items()[-(index - (-12 + getSize()))], translation)))
            {
                getContents().set(-(index - (-12 + getSize())), stack);
                ((CraftLivingEntity) entity).getHandle().setSlot(EnumItemSlot.values()[-(index - (-12 + getSize()))], stack);
            }
        }
        else if (index >= getSize() - 12 && index <= getSize() - 11)
        {
            if (!stack.equals(InventoryUtils.InformationItems.addWarning(informationItems.items()[index - (-12 + getSize())], translation)))
            {
                getContents().set(index - (-12 + getSize()), stack);
                ((CraftLivingEntity) entity).getHandle().setSlot(EnumItemSlot.values()[index - (-12 + getSize())], stack);
            }
        }
    }

    @Override
    public String getName()
    {
        return translation.format("interface.entity.name", entity.getName());
    }
}