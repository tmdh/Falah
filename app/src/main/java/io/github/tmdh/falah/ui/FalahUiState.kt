package io.github.tmdh.falah.ui

import io.github.tmdh.falah.model.Prayer

data class FalahUiState(
    val allPrayers: List<Prayer> = listOf()
)