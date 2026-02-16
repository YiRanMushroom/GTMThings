package com.hepdd.gtmthings.mixin;

import com.hepdd.gtmthings.init.OverclockingPatcher;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Inject into Minecraft's Main class to patch OverclockingLogic at the earliest possible moment.
 */
@Mixin(Main.class)
public class MinecraftMainMixin {

    @Unique
    private static boolean gtmthings$patched = false;

    @Inject(method = "main", at = @At("HEAD"), remap = false)
    private static void beforeMinecraftMain(CallbackInfo ci) {
        if (!gtmthings$patched) {
            gtmthings$patched = true;
            try {
                System.out.println("[GTMThings] Patching OverclockingLogic at Minecraft startup...");
                OverclockingPatcher.init();
                System.out.println("[GTMThings] OverclockingLogic patching completed successfully");
            } catch (Exception e) {
                System.err.println("[GTMThings] Failed to patch OverclockingLogic: " + e.getMessage());
            }
        }
    }
}

