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
       stage('Publish Test result'){
          cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: '**/target/site/cobertura/coverage.xml', conditionalCoverageTargets: '70, 0, 0', failUnhealthy: false, failUnstable: false, lineCoverageTargets: '80, 0, 0', maxNumberOfBuilds: 0, methodCoverageTargets: '80, 0, 0', onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false
        }
    }

    catch (err) {
        throw err
    }
}
