pipeline {
    agent any

    triggers {
            pollSCM('* * * * *')
    }

    stages {
        stage('TCC SetUp') {
            steps {
                sh "curl -fsSL https://get.testcontainers.cloud/bash | sh "
            }
        }
        stage('Unit Test') {
            steps {
                sh './mvnw verify'
            }
        }
    }
}