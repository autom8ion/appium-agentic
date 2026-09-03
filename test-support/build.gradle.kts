dependencies {
    api(project(":core"))
    api(project(":page-object"))
    implementation(project(":platform-android"))
    implementation(project(":platform-ios"))
    api(libs.junit.jupiter)
    api(libs.assertj.core)
    implementation(libs.allure.junit5)
}
