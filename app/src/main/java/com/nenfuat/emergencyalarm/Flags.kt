package com.nenfuat.emergencyalarm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


class Flags {
    companion object {
        var isMusicPlaying: MutableState<Boolean> = mutableStateOf(false)
        var includeStopButton: MutableState<Boolean> = mutableStateOf(false)
    }
}

