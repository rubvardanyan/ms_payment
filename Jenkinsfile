#!groovy

pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'registry.sflpro.com'
    }

    stages {
        stage('Build artifacts') {
            withMaven {
                sh 'mvn clean install -DskiptTests'
            }
        }
        stage('Build & publish docker image') {
             steps {
                 sh 'mvn clean package docker:build'
                 sh 'mvn clean package docker:build -DpushImage'
             }
        }
    }
}
