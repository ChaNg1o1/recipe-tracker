#!/bin/bash
cd "$(dirname "$0")"
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" com.chang1o.recipe.Main
echo "已退出"
