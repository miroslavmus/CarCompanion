package uni.fmi.miroslav.carcompanion.models

import java.io.Serializable

data class ModelFix (
    val km: Int,
    val date: String,
    val message: String,
    val partId: Int
) : Model(), Serializable