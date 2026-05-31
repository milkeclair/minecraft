package com.milkeclair.glacage

import net.neoforged.fml.common.Mod
import org.slf4j.LoggerFactory

@Mod(GlacageMod.MOD_ID)
object GlacageMod {
  const val MOD_ID = "glacage"

  private val logger = LoggerFactory.getLogger(MOD_ID)

  init {
    logger.info("Glacage initialized")
  }
}
