#!groovy

node('build-slave') {

    try {
        
       stage('Checkout'){
          checkout scm
          sh('git submodule update --init && git submodule update --init --recursive --remote')
       }

       stage('Build Assets'){
          sh ("mvn -f data-pipeline/pom.xml \
                -Dlog4j.configuration=/home/ops/workspace/New_Build/Sunbird_EP_Upgrade_Build/logs \
                -Dcobertura.report.format=xml clean cobertura:cobertura package")
        }
    }

    catch (err) {
        throw err
    }
}
