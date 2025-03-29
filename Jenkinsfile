pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  stages {
    stage('Scan All Services') {
      steps {
        script {
          def services = ['gateway-service', 'passenger-service', 'driver-service', 'discovery-service',
          'rating-service', 'rides-service']

          services.each { service ->
            stage("Scan ${service}") {
              dir(service) {
                withSonarQubeEnv(installationName: 'sq1') {
                  sh './mvnw clean org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.0.2155:sonar'
                }
              }
            }
          }
        }
      }
    }
  }
}