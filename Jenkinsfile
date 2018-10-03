pipeline {
    agent any

    stages {
        stage('Docker login') {
            steps {
                sh "docker login registry.sflpro.com -u jenkins -p J3nk1ns"
            }
        }
        stage('Build artifacts') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Build & publish docker image') {
             steps {
                 sh 'docker build -t qup-payment-test -f payment.Dockerfile .'
                 sh 'docker tag qup-payment-test registry.sflpro.com/qup-payment-test:1 && docker tag qup-payment-test registry.sflpro.com/qup-payment-test:latest'
                 sh 'docker push registry.sflpro.com/qup-payment-test:1 && docker push registry.sflpro.com/qup-payment-test:latest'
                 sh 'docker build -t qup-payment-scheduler-test -f scheduler.Dockerfile .'
                 sh 'docker tag qup-payment-scheduler-test registry.sflpro.com/qup-payment-scheduler-test:1 && docker tag qup-payment-scheduler-test registry.sflpro.com/qup-payment-scheduler-test:latest'
                 sh 'docker push registry.sflpro.com/qup-payment-scheduler-test:1 && docker push registry.sflpro.com/qup-payment-scheduler-test:latest'
                 sh 'docker rmi registry.sflpro.com/qup-payment-scheduler-test:1'
                 sh 'docker rmi registry.sflpro.com/qup-payment-scheduler-test:latest'
                 sh 'docker rmi registry.sflpro.com/qup-payment-test:1'
                 sh 'docker rmi registry.sflpro.com/qup-payment-test:latest'
                 sh 'docker rmi qup-payment-scheduler-test'
                 sh 'docker rmi qup-payment-test'
                //  sh 'cd scheduler/scheduler_web'
                //  sh 'mvn clean package docker:build -Dproject.environment=test -Ddocker.registry.url=registry.sflpro.com -Dbamboo.deploy.release=1'
                //  sh 'mvn clean package docker:build -DpushImage -Dproject.environment=${1} -Ddocker.registry.url=${2} -Dbamboo.deploy.release=${3}   ' 
                //  sh 'cd ../../api/api_internal/api_internal_rest'
                //  sh 'mvn clean package docker:build'
                //  sh 'mvn clean package docker:build -DpushImage'
             }
        }
    }
}

