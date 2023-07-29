package io.github.adainish.ifuckinghatepixelmonbreeding;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.daycare.DayCareCondition;
import com.pixelmonmod.pixelmon.api.daycare.DayCareConditionRegistry;
import com.pixelmonmod.pixelmon.api.daycare.DayCareDuration;
import com.pixelmonmod.pixelmon.api.daycare.event.DayCareEvent;
import com.pixelmonmod.pixelmon.api.daycare.impl.requirement.PokeDollarsRequirement;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("ifuckinghatepixelmonbreeding")
public class IFuckingHatePixelmonBreeding {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public IFuckingHatePixelmonBreeding() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartedEvent event) {
        DayCareConditionRegistry.getAllConditions().forEach(dayCareCondition -> {
            dayCareCondition.getRequirements().clear();
            dayCareCondition.getRequirements().add(new PokeDollarsRequirement());
        });

    }

    @SubscribeEvent
    public void onEggCalculated(DayCareEvent.PreTimerBegin event) {
        int cost = 1000;
        if (event.getBox().getParentOne() == null || event.getBox().getParentTwo() == null) {
            event.setCanceled(true);
            return;
        }
        if (!EconomyUtil.canAfford(event.getBox().getParentOne().getOwnerPlayerUUID(), cost)) {
            event.getBox().getParentOne().getOwnerPlayer().sendMessage(new StringTextComponent("You can't afford the $1000 cost").setStyle(Style.EMPTY.setColor(Color.fromInt(4))), event.getBox().getParentOne().getUUID());
            event.setCanceled(true);
        } else {
            EconomyUtil.takeBalance(event.getBox().getParentOne().getOwnerPlayerUUID(), cost);
            event.getBox().getParentOne().getOwnerPlayer().sendMessage(new StringTextComponent("You were charged $1000 for breeding").setStyle(Style.EMPTY.setColor(Color.fromInt(2))), event.getBox().getParentOne().getUUID());
        }
    }


}
