package com.postmage.util.garb

/**
 * ./gradlew installDist
 * docker build -t madenmustafa/postmage .
 * docker buildx build --platform linux/amd64,linux/arm64 -t madenmustafa/postmage --push .
 * docker buildx build --platform linux/amd64 -t madenmustafa/postmage --push .
 * -- -- -- -- <!> docker volume create postimage <!> -- -- -- --
 * docker run -did -v postimage:/root/postmage_photos madenmustafa/postmage
 * -- -- -- -- <!> docker volume create mongodbdata <!> -- -- -- --
 * docker run -did -v mongodbdata:/data/db mongo
 *
 * Docker Http Request Must
 * apt-get install bridge-utils
 * pkill docker
 * iptables -t nat -F
 * ifconfig docker0 down
 * brctl delbr docker0
 * service docker restart
 */
