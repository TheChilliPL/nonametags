rootProject.name = "nonametags"

println("Settings read")

plugins {
    id("com.gradle.enterprise") version("3.6.1")
}

gradleEnterprise {
    println("Gradle enterprise configuration")
    buildScan {
        val env = System.getenv("BUILD_SCAN")
        println("Build scan: $env")
        if(env.equals("yes", true)) {
            println("Build scan active")
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        } else {
            println("Build scan not active")
        }
    }
}