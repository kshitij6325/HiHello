import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.addKotlin() = addAllDependency(kotlinCore)

fun DependencyHandler.addAndroidCore() = addAllDependency(androidCore)

fun DependencyHandler.addNavigation() = addAllDependency(jetpackNavigation)

fun DependencyHandler.addRoom() = addAllDependency(room)

fun DependencyHandler.addLifeCycle() = addAllDependency(lifecycle)

fun DependencyHandler.addHilt() = addAllDependency(hilt)

fun DependencyHandler.addAndroidUI() = addAllDependency(androidUI)

fun DependencyHandler.addFcm() = addAllDependency(listOf(Dependency.MainSetDependency.FCM))

internal fun DependencyHandler.addAllDependency(list: List<Dependency>) {
    list.forEach {
        when (it) {
            is Dependency.MainSetDependency -> add(
                "implementation",
                it.dependency
            )
            is Dependency.TestSetDependency -> add(
                "testImplementation",
                it.dependency
            )
            is Dependency.AndroidTestSetDependency -> add(
                "androidTestImplementation",
                it.dependency
            )
            is Dependency.AnnotationProcessor -> add(
                "kapt",
                it.dependency
            )
        }
    }
}