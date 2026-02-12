import com.example.trashbinproject.R
import com.example.trashbinproject.domain.TrashBin
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView

fun MapUpdate(
    mapView: MapView,
    userLat: Double,
    userLng: Double,
    allBins: List<TrashBin>,
    canScanStatus: Map<Int, Boolean>
) {
    val map = mapView.mapWindow.map
    map.mapObjects.clear()

    val userPoint = Point(userLat, userLng)
    val userCircle = map.mapObjects.addCircle(Circle(userPoint, 300f))
    userCircle.fillColor = 0x400079FFu.toInt()
    userCircle.strokeColor = 0xFF0079FF.toInt()
    userCircle.strokeWidth = 3f

    val userPlacemark = map.mapObjects.addPlacemark(userPoint) as PlacemarkMapObject
    userPlacemark.setIconStyle(
        IconStyle()
            .setScale(2.2f)
            .setZIndex(30f)
    )
    allBins.forEach { bin ->
        val canScanToday = canScanStatus[bin.id] ?: true
        val binPoint = Point(bin.latitude, bin.longitude)

        if (!canScanToday) {
            val circle = map.mapObjects.addCircle(Circle(binPoint, 22f))
            circle.fillColor = 0xCCFF0000u.toInt()
            circle.strokeColor = 0xFFFF0000u.toInt()
            circle.strokeWidth = 2f
        } else {
            val circle = map.mapObjects.addCircle(Circle(binPoint, 24f))
            circle.fillColor = 0xCC0079FFu.toInt()
            circle.strokeColor = 0xFF0079FFu.toInt()
            circle.strokeWidth = 2f
        }
    }

    // ✅ 4. Камера центрируется на ПОЛЬЗОВАТЕЛЕ (не на ближайшей!)
    map.move(
        CameraPosition(userPoint, 15.5f, 0f, 0f),  // ✅ Центр на пользователе
        Animation(Animation.Type.SMOOTH, 1f),
        null
    )

    println("Загружено ${allBins.size} мусорок | Фокус: ВЫ (${userLat}, ${userLng})")
}
