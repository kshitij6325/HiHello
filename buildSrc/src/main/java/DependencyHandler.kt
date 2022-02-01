import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandler.addKotlin() = addAllDependency(kotlinCore)

fun DependencyHandler.addAndroidCore() {
    addAllDependency(androidCore)
    addJunit()
}

fun DependencyHandler.addJunit() = addAllDependency(junit)

fun DependencyHandler.addNavigation() = addAllDependency(jetpackNavigation)

fun DependencyHandler.addRoom() = addAllDependency(room)

fun DependencyHandler.addLifeCycle() {
    addAllDependency(archTest)
    addAllDependency(lifecycle)
}

fun DependencyHandler.addArchTest() = addAllDependency(archTest)

fun DependencyHandler.addHilt() = addAllDependency(hilt)

fun DependencyHandler.addAndroidUI() = addAllDependency(androidUI)

fun DependencyHandler.addFcm() = addAllDependency(listOf(Dependency.MainSetDependency.FCM))

fun DependencyHandler.addFirebaseDatabase() {
    //platform(Dependency.MainSetDependency.FirebaseBOM.dependency)
    addAllDependency(listOf(Dependency.MainSetDependency.FirebaseDataBase))
}

fun DependencyHandler.addFirebaseStorage() {
    addAllDependency(listOf(Dependency.MainSetDependency.FirebaseStorage))
}

fun DependencyHandler.addFirebaseAuth() {
    addAllDependency(listOf(Dependency.MainSetDependency.FirebaseAuth))
}

fun DependencyHandler.addRetrofit() =
    addAllDependency(listOf(Dependency.MainSetDependency.Retrofit))

fun DependencyHandler.addWorkManger() = addAllDependency(workmanger)

fun DependencyHandler.addGlide() = addAllDependency(glide)

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