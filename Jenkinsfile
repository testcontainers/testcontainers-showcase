pipeline {
    agent any

    triggers {
            pollSCM('* * * * *')
    }
    environment {
        TC_CLOUD_TOKEN     = credentials('TC_CLOUD_TOKEN')
    }
    stages {
        stage('TCC SetUp') {
            steps {
                echo "Token: ${env.TC_CLOUD_TOKEN}"
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