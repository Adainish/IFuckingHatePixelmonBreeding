package io.github.adainish.ifuckinghatepixelmonbreeding;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.daycare.DayCareCondition;
import com.pixelmonmod.pixelmon.api.daycare.DayCareConditionRegistry;
import com.pixelmonmod.pixelmon.api.daycare.DayCareDuration;
import com.pixelmonmod.pixelmon.api.daycare.event.DayCareEvent;
import com.pixelmonmod.pixelmon.api.daycare.impl.requirement.PokeDollarsRequirement;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.api.events.npc.NPCEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.entities.npcs.registry.NPCRegistryData;
import com.pixelmonmod.pixelmon.entities.npcs.registry.NPCRegistryTrainers;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import java.util.concurrent.TimeUnit;
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
    public void onTrainerEvent(BeatTrainerEvent event)
    {
        int winMoney = event.trainer.winMoney;
        if (winMoney > 0)
        {
            int adjusted = (winMoney / 100) * 60;
            EconomyUtil.takeBalance(event.player.getUniqueID(), adjusted);
        }

    }

    @SubscribeEvent
    public void onEggCalculated(DayCareEvent.PreTimerBegin event) {
        int cost = 1000;
        if (event.getBox().getParentOne() == null || event.getBox().getParentTwo() == null) {
            event.setCanceled(true);
            return;
        }
        if (!EconomyUtil.canAfford(event.getBox().getParentOne().getOwnerPlayerUUID(), cost)) {
            sendMessage(event.getBox().getParentOne().getOwnerPlayer(), "&7You can't afford the &a$%cost% &7breeding cost".replace("%cost%", String.valueOf(cost)));
            event.setCanceled(true);
        } else {
            EconomyUtil.takeBalance(event.getBox().getParentOne().getOwnerPlayerUUID(), cost);
            event.setDuration(TimeUnit.MINUTES.toMillis(45));
            sendMessage(event.getBox().getParentOne().getOwnerPlayer(), "&7You were charged &a$%cost% &7for breeding".replace("%cost%", String.valueOf(cost)));
        }
    }

    public void sendMessage(ServerPlayerEntity serverPlayer, String message)
    {
        serverPlayer.sendMessage(new StringTextComponent(((message).replaceAll("&([0-9a-fk-or])", "\u00a7$1"))), serverPlayer.getUniqueID());
    }


}
