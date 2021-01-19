import kotlin.math.roundToLong

data class Mod(
    val info: ModInfo,
    val isEnabled: Boolean,
    val images: List<ModImage>
) {

    val totalBytesForMod by lazy {
        images.sumByDouble { it.bytesUsed.toDouble() }.roundToLong()
    }
}