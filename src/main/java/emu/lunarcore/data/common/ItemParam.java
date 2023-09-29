package emu.lunarcore.data.common;

import com.google.gson.annotations.SerializedName;

import emu.lunarcore.proto.ItemCostOuterClass.ItemCost;
import lombok.Getter;

@Getter
public class ItemParam {
    @SerializedName(value = "id", alternate = {"ItemId", "ItemID"})
    private int id;

    @SerializedName(value = "count", alternate = {"ItemCount", "ItemNum"})
    private int count;

    private ItemParamType type = ItemParamType.PILE;

    public ItemParam() {
        // Gson
    }

    public ItemParam(ItemParamType type, int id, int count) {
        this.type = type;
        this.id = id;
        this.count = count;
    }

    public ItemParam(ItemCost itemCost) {
        if (itemCost.hasPileItem()) {
            this.id = itemCost.getPileItem().getItemId();
            this.count = itemCost.getPileItem().getItemNum();
        } else if (itemCost.hasEquipmentUniqueId()) {
            this.type = ItemParamType.UNIQUE;
            this.id = itemCost.getEquipmentUniqueId();
            this.count = 1;
        } else if (itemCost.hasRelicUniqueId()) {
            this.type = ItemParamType.UNIQUE;
            this.id = itemCost.getRelicUniqueId();
            this.count = 1;
        }
    }

    public static enum ItemParamType {
        UNKNOWN, PILE, UNIQUE;
    }
}