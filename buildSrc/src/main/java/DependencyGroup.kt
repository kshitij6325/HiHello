internal val kotlinCore = listOf(
    Dependency.MainSetDependency.KTXCore,
    Dependency.MainSetDependency.KotlinSerialization,
    Dependency.MainSetDependency.CoroutineCore,
    Dependency.TestSetDependency.CoroutineCore,
    Dependency.TestSetDependency.KTXCore,
    Dependency.AndroidTestSetDependency.CoroutineCore,
    Dependency.AndroidTestSetDependency.KTXCore
)

internal val androidCore = listOf(
    Dependency.MainSetDependency.AndroidAppCompat,
    Dependency.MainSetDependency.AndroidMaterial,
    Dependency.MainSetDependency.AndroidLegacy,
    Dependency.AndroidTestSetDependency.EspressoCore
)

internal val junit = listOf(
    Dependency.TestSetDependency.Junit,
    Dependency.AndroidTestSetDependency.Junit,
)

internal val androidUI = listOf(
    Dependency.MainSetDependency.AndroidConstraintLayout
)

internal val jetpackNavigation =
    listOf(Dependency.MainSetDependency.NavFragment, Dependency.MainSetDependency.NavUiKTX)

internal val room =
    listOf(
        Dependency.MainSetDependency.Room,
        Dependency.MainSetDependency.RoomKTX,
        Dependency.AnnotationProcessor.Room
    )

internal val lifecycle =
    listOf(
        Dependency.MainSetDependency.LiveData,
        Dependency.MainSetDependency.ViewModel,
        Dependency.MainSetDependency.LifeCycleKTX,
        Dependency.MainSetDependency.ActivityKTX,
        Dependency.MainSetDependency.FragmentKTX,
    )

internal val archTest = listOf(
    Dependency.TestSetDependency.Arch,
    Dependency.AndroidTestSetDependency.Arch
)

internal val hilt =
    listOf(Dependency.MainSetDependency.Hilt, Dependency.AnnotationProcessor.Hilt)