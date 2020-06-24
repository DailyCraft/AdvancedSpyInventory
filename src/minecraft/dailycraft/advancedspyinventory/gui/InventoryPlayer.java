package minecraft.dailycraft.advancedspyinventory.gui;

import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import static minecraft.dailycraft.advancedspyinventory.gui.InventoryUtils.*;

public class InventoryPlayer implements PlayerInventory
{
    private final PlayerInventory playerInv;
    private final TranslationUtils translation;

    public InventoryPlayer(Player sender, PlayerInventory playerInv)
    {
        this.playerInv = playerInv;
        translation = new TranslationUtils(sender);
    }

    @Override
    public ItemStack[] getArmorContents()
    {
        return playerInv.getArmorContents();
    }

    @Override
    public ItemStack[] getExtraContents()
    {
        return playerInv.getExtraContents();
    }

    @Override
    public ItemStack getHelmet()
    {
        return playerInv.getHelmet();
    }

    @Override
    public ItemStack getChestplate()
    {
        return playerInv.getChestplate();
    }

    @Override
    public ItemStack getLeggings()
    {
        return playerInv.getLeggings();
    }

    @Override
    public ItemStack getBoots()
    {
        return playerInv.getBoots();
    }

    @Override
    public int getSize()
    {
        return 9 * 6;
    }

    @Override
    public int getMaxStackSize()
    {
        return playerInv.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int amount)
    {
        playerInv.setMaxStackSize(amount);
    }

    @Override
    public String getName()
    {
        return translation.translate("§4§l" + getHolder().getName() + "§r§e's Inventory", "§eInventaire de §4§l" + getHolder().getName());
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index <= 26)
        {
            return playerInv.getItem(index + 9);
        }

        if (index <= 35)
        {
            return playerInv.getItem(index - 27);
        }

        switch (index)
        {
            case 36:
                return getHelmet();

            case 37:
                return getChestplate();

            case 38:
                return getLeggings();

            case 39:
                return getBoots();

            case 44:
                return getItemInOffHand();

            case 46:
                return setItemWithDisplayName(new Potion(PotionType.INSTANT_HEAL).toItemStack(1), translation.translate("Health", "Vie") + " : " + new DecimalFormat(".#").format(getHolder().getHealth()).replace(',', '.'));

            case 47:
                if (translation.getSender().hasPermission("advancedspyinventory.inventory.location.view"))
                {
                    return setItemWithDisplayName(Material.ARROW, "Location",
                            translation.translate("World", "Monde") + " : " + getHolder().getWorld().getName(),
                            "X : " + new DecimalFormat(".##").format(getHolder().getLocation().getX()).replace(',', '.'),
                            "Y : " + new DecimalFormat(".##").format(getHolder().getLocation().getY()).replace(',', '.'),
                            "Z : " + new DecimalFormat(".##").format(getHolder().getLocation().getZ()).replace(',', '.'), "",
                            "-> " + translation.translate("Click to teleport", "Cliquer pour se téléporter"));
                }

                break;

            case 49:
                return setItemWithDisplayName(Material.BARRIER, translation.translate("Clear Inventory", "Vider l'inventaire"));

            case 51:
                return setItemWithDisplayName(Material.EXP_BOTTLE, "Experience : " + ((Player) getHolder()).getTotalExperience() + " points");

            case 52:
                return setItemWithDisplayName(Material.COOKED_BEEF, translation.translate("Food", "Nourriture") + " : " + ((Player) getHolder()).getFoodLevel());
        }

        return getVoidItem();
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index <= 26)
        {
            playerInv.setItem(index + 9, stack);
            return;
        }

        if (index <= 35)
        {
            playerInv.setItem(index - 27, stack);
            return;
        }

        switch (index)
        {
            case 36:
                setHelmet(stack);
                return;

            case 37:
                setChestplate(stack);
                return;

            case 38:
                setLeggings(stack);
                return;

            case 39:
                setBoots(stack);
                return;

            case 44:
                setItemInOffHand(stack);
        }
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... stacks) throws IllegalArgumentException
    {
        return playerInv.addItem(stacks);
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... stacks) throws IllegalArgumentException
    {
        return playerInv.removeItem(stacks);
    }

    @Override
    public ItemStack[] getContents()
    {
        return playerInv.getContents();
    }

    @Override
    public void setContents(ItemStack[] stacks) throws IllegalArgumentException
    {
        playerInv.setContents(stacks);
    }

    @Override
    public ItemStack[] getStorageContents()
    {
        return playerInv.getStorageContents();
    }

    @Override
    public void setStorageContents(ItemStack[] stacks) throws IllegalArgumentException
    {
        playerInv.setStorageContents(stacks);
    }

    @Deprecated
    @Override
    public boolean contains(int materialId)
    {
        return playerInv.contains(materialId);
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException
    {
        return playerInv.contains(material);
    }

    @Override
    public boolean contains(ItemStack stack)
    {
        return playerInv.contains(stack);
    }

    @Deprecated
    @Override
    public boolean contains(int materialId, int amount)
    {
        return playerInv.contains(materialId, amount);
    }

    @Override
    public boolean contains(Material material, int amount) throws IllegalArgumentException
    {
        return playerInv.contains(material, amount);
    }

    @Override
    public boolean contains(ItemStack stack, int amount)
    {
        return playerInv.contains(stack, amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack stack, int amount)
    {
        return playerInv.containsAtLeast(stack, amount);
    }

    @Deprecated
    @Override
    public HashMap<Integer, ? extends ItemStack> all(int materialId)
    {
        return playerInv.all(materialId);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException
    {
        return playerInv.all(material);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack stack)
    {
        return playerInv.all(stack);
    }

    @Deprecated
    @Override
    public int first(int materialId)
    {
        return playerInv.first(materialId);
    }

    @Override
    public int first(Material material) throws IllegalArgumentException
    {
        return playerInv.first(material);
    }

    @Override
    public int first(ItemStack stack)
    {
        return playerInv.first(stack);
    }

    @Override
    public int firstEmpty()
    {
        return playerInv.firstEmpty();
    }

    @Deprecated
    @Override
    public void remove(int materialId)
    {
        playerInv.remove(materialId);
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException
    {
        playerInv.remove(material);
    }

    @Override
    public void remove(ItemStack stack)
    {
        playerInv.remove(stack);
    }

    @Override
    public void clear(int index)
    {
        playerInv.clear(index);
    }

    @Override
    public void clear()
    {
        playerInv.clear();
    }

    @Override
    public List<HumanEntity> getViewers()
    {
        return playerInv.getViewers();
    }

    @Override
    public String getTitle()
    {
        return playerInv.getTitle();
    }

    @Override
    public InventoryType getType()
    {
        return playerInv.getType();
    }

    @Override
    public void setArmorContents(ItemStack[] stacks)
    {
        playerInv.setArmorContents(stacks);
    }

    @Override
    public void setExtraContents(ItemStack[] stacks)
    {
        playerInv.setExtraContents(stacks);
    }

    @Override
    public void setHelmet(ItemStack stack)
    {
        playerInv.setHelmet(stack);
    }

    @Override
    public void setChestplate(ItemStack stack)
    {
        playerInv.setChestplate(stack);
    }

    @Override
    public void setLeggings(ItemStack stack)
    {
        playerInv.setLeggings(stack);
    }

    @Override
    public void setBoots(ItemStack stack)
    {
        playerInv.setBoots(stack);
    }

    @Override
    public ItemStack getItemInMainHand()
    {
        return playerInv.getItemInMainHand();
    }

    @Override
    public void setItemInMainHand(ItemStack stack)
    {
        playerInv.setItemInMainHand(stack);
    }

    @Override
    public ItemStack getItemInOffHand()
    {
        return playerInv.getItemInOffHand();
    }

    @Override
    public void setItemInOffHand(ItemStack stack)
    {
        playerInv.setItemInOffHand(stack);
    }

    @Deprecated
    @Override
    public ItemStack getItemInHand()
    {
        return playerInv.getItemInHand();
    }

    @Deprecated
    @Override
    public void setItemInHand(ItemStack stack)
    {
        playerInv.setItemInHand(stack);
    }

    @Override
    public int getHeldItemSlot()
    {
        return playerInv.getHeldItemSlot();
    }

    @Override
    public void setHeldItemSlot(int slot)
    {
        playerInv.setHeldItemSlot(slot);
    }

    @Deprecated
    @Override
    public int clear(int id, int data)
    {
        return playerInv.clear(id, data);
    }

    @Override
    public HumanEntity getHolder()
    {
        return playerInv.getHolder();
    }

    @Override
    public ListIterator<ItemStack> iterator()
    {
        return playerInv.iterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int index)
    {
        return playerInv.iterator(index);
    }

    @Override
    public Location getLocation()
    {
        return playerInv.getLocation();
    }
}
