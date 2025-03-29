pipeline {
  agent {
      docker {
        image 'eclipse-temurin:21-jdk'
        args '-v $HOME/.m2:/root/.m2'
      }
    }
  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  stages {
    stage('Build and Scan') {
      steps {
        script {
          def services = ['gateway-service', 'passenger-service']

          services.each { service ->
            stage("Build and Scan ${service}") {
              dir(service) {
                sh './mvnw clean compile'

                withSonarQubeEnv(installationName: 'sq1') {
                  sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.0.2155:sonar ' +
                     '-Dsonar.java.binaries=target/classes'
                }
              }
            }
          }
        }
      }
    }
  }
}