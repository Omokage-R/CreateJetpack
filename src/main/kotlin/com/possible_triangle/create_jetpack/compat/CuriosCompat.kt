package com.possible_triangle.create_jetpack.compat

import com.possible_triangle.create_jetpack.capability.sources.CuriosSource
import com.possible_triangle.create_jetpack.capability.sources.ISource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import top.theillusivec4.curios.api.CuriosCapability
import top.theillusivec4.curios.api.SlotTypePreset

object CuriosCompat {

    fun register() {
        FORGE_BUS.addListener { _: InterModEnqueueEvent ->
            InterModComms.sendTo("curios", "register_type") {
                SlotTypePreset.BACK.messageBuilder.build()
            }
        }

        ISource.addSource(::getCuriosStacksSafe)
    }

    fun getCuriosStacksSafe(entity: LivingEntity): List<Pair<ItemStack, CuriosSource>> {
        return if (ModList.get().isLoaded("curios")) {
            val curios = entity.getCapability(CuriosCapability.INVENTORY)
            curios.map {
                it.curios.entries.flatMap { (slot, handler) ->
                    val slots = 0 until handler.slots
                    slots.map { index ->
                        val stack = handler.stacks.getStackInSlot(index)
                        stack to CuriosSource(slot, index)
                    }
                }
            }.orElseGet(::emptyList)
        } else {
            emptyList()
        }
    }

}