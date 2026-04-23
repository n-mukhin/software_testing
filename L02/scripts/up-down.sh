#!/usr/bin/env bash
set -e

cd "$(dirname "$0")/.."

mvn test \
  -Dtest=Upper.integration.CosINTest,Upper.integration.CotINTest,Upper.integration.SecINTest,Upper.integration.TanINTest,Lower.integration.BaseLINTest,EquationINTest,MainTest \
  -Dsurefire.printSummary=true \
  -Dsurefire.useFile=false \
  -Dsurefire.reportFormat=plain \
  -Dsurefire.trimStackTrace=false \
  -Dsurefire.redirectTestOutputToFile=false \
  -Dsurefire.showSuccessfulTests=true \
  -Dorg.slf4j.simpleLogger.log.org.apache.maven.plugins.surefire=debug