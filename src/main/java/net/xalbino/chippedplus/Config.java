package net.xalbino.chippedplus;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue logDirtBlock;
    public static final ForgeConfigSpec.ConfigValue<String> magicNumberIntroduction;
    public static final ForgeConfigSpec.IntValue magicNumber;
    public static final ForgeConfigSpec.ConfigValue<java.util.List<String>> items;

    static {
        BUILDER.push("general");
        logDirtBlock = BUILDER.comment("Log the dirt block key on startup").define("logDirtBlock", false);
        magicNumberIntroduction = BUILDER.comment("Intro text").define("magicNumberIntroduction", "Magic number: ");
        magicNumber = BUILDER.comment("A number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
        items = BUILDER.comment("Sample list").define("items", java.util.List.of("a","b","c"));
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
