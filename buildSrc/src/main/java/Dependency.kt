internal sealed class Dependency(val dependency: String) {

    sealed class MainSetDependency(dep: String) : Dependency(dep) {

        //kotlin + coroutines
        object KTXCore :
            MainSetDependency("androidx.core:core-ktx:1.3.2")

        object KotlinSerialization :
            MainSetDependency("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

        object CoroutineCore :
            MainSetDependency("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

        //android core
        object AndroidAppCompat :
            MainSetDependency("androidx.appcompat:appcompat:1.2.0")

        object AndroidMaterial :
            MainSetDependency("com.google.android.material:material:1.3.0")

        object AndroidLegacy : MainSetDependency("androidx.legacy:legacy-support-v4:1.0.0")

        object AndroidConstraintLayout :
            MainSetDependency("androidx.constraintlayout:constraintlayout:2.0.4")

        //jetpack navigation
        object NavFragment : MainSetDependency("androidx.navigation:navigation-fragment-ktx:2.3.5")
        object NavUiKTX : MainSetDependency("androidx.navigation:navigation-ui-ktx:2.3.5")
        object NavHilt : MainSetDependency("androidx.hilt:hilt-navigation-fragment:1.0.0")

        //room
        object Room : MainSetDependency("androidx.room:room-runtime:2.4.0")
        object RoomKTX : MainSetDependency("androidx.room:room-ktx:2.4.0")

        //livedata + view-model
        object ViewModel : MainSetDependency("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
        object LiveData : MainSetDependency("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
        object LifeCycleKTX : MainSetDependency("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
        object FragmentKTX : MainSetDependency("androidx.fragment:fragment-ktx:1.4.0")
        object ActivityKTX : MainSetDependency("androidx.activity:activity-ktx:1.4.0")

        //hilt
        object Hilt : MainSetDependency("com.google.dagger:hilt-android:2.38.1")

        //firebase
        object FCM : MainSetDependency("com.google.firebase:firebase-messaging:23.0.0")
        object FirebaseBOM : MainSetDependency("com.google.firebase:firebase-bom:29.0.3")
        object FirebaseDataBase :
            MainSetDependency("com.google.firebase:firebase-database-ktx:20.0.3")

        object FirebaseStorage :
            MainSetDependency("com.google.firebase:firebase-storage-ktx:20.0.0")

        object FirebaseAuth : MainSetDependency("com.google.firebase:firebase-auth:21.0.1")

        //retrofit
        object Retrofit : MainSetDependency("com.squareup.retrofit2:retrofit:2.9.0")

        //workmanager
        object WorkManager : MainSetDependency("androidx.work:work-runtime-ktx:2.7.1")
        object WorkMangerHilt : MainSetDependency("androidx.hilt:hilt-work:1.0.0")

        //Glide
        object Glide : MainSetDependency("com.github.bumptech.glide:glide:4.12.0")
    }

    sealed class TestSetDependency(dep: String) : Dependency(dep) {
        object CoroutineCore :
            TestSetDependency("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")

        object Junit : TestSetDependency("junit:junit:4.13.2")

        object AndroidKTXCore : TestSetDependency("androidx.test:core-ktx:1.4.0")

        object Arch : TestSetDependency("androidx.arch.core:core-testing:2.1.0")

        object Mockito : TestSetDependency("org.mockito:mockito-core:2.19.0")
    }

    sealed class AndroidTestSetDependency(dep: String) : Dependency(dep) {

        object CoroutineCore :
            AndroidTestSetDependency("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")

        object EspressoCore : AndroidTestSetDependency("androidx.test.espresso:espresso-core:3.4.0")

        object Junit : AndroidTestSetDependency("androidx.test.ext:junit:1.1.3")

        object AndroidKTXCore : AndroidTestSetDependency("androidx.test:core-ktx:1.4.0")

        object Arch : AndroidTestSetDependency("androidx.arch.core:core-testing:2.1.0")

        object WorkManger : AndroidTestSetDependency("androidx.work:work-testing:2.7.1")
    }

    sealed class AnnotationProcessor(dep: String) : Dependency(dep) {
        object Room : AnnotationProcessor("androidx.room:room-compiler:2.4.0")
        object HiltCompiler : AnnotationProcessor("androidx.hilt:hilt-compiler:1.0.0")
        object Hilt : AnnotationProcessor("com.google.dagger:hilt-compiler:2.38.1")
        object Glide : AnnotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    }
}