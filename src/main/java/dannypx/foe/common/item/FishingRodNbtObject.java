package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FishingRodNbtObject extends NbtObject {
    private static final String LINE = "line";
    private List<NbtObject> lineItem = List.of();
    private static final String POLE = "pole";
    private List<NbtObject> poleItem = List.of();
    private static final String REEL = "reel";
    private List<NbtObject> reelItem = List.of();
    private static final String TACKLEBOX = "tacklebox";
    private List<NbtObject> tackleBox = List.of();

    public FishingRodNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
        if(itemStack != ItemStack.EMPTY) {
            this.init();
        }
    }

    private void init() {
        this.lineItem = this.getItemStackList(LINE);
        this.poleItem = this.getItemStackList(POLE);
        this.reelItem = this.getItemStackList(REEL);
        this.tackleBox = this.getItemStackList(TACKLEBOX);
    }

    public List<NbtObject> getLineItem() {
        return this.lineItem;
    }

    public List<NbtObject> getPoleItem() {
        return this.poleItem;
    }

    public List<NbtObject> getReelItem() {
        return this.reelItem;
    }

    public List<NbtObject> getTackleBox() {
        return this.tackleBox;
    }

    public static FishingRodNbtObject of(@NotNull NbtCompound nbtCompound, @NotNull ItemStack itemStack) {
        return new FishingRodNbtObject(nbtCompound, itemStack);
    }

    public static FishingRodNbtObject empty() {
        return new FishingRodNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
