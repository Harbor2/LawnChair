import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.library)
    // 如果需要 Kotlin 支持
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.negative_screen"

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        compose = true
        aidl = true
        viewBinding = true
    }

    sourceSets {
        getByName("main") {
            // 让这个模块也去加载主项目中的 aidl 目录
            aidl.srcDirs("../lawnchair/aidl")
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
}
