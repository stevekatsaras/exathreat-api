FROM adoptopenjdk/openjdk11:jre-11.0.13_8-alpine
WORKDIR /opt/exathreat/api/
ADD build/libs/exathreat-api.jar /opt/exathreat/api/
EXPOSE 8080
ENTRYPOINT [ "java", \
	"-Djava.security.egd=file:/dev/./urandom", \
	"-Dspring.profiles.active=${PROFILE}", \
	"-Ddb.address=${DB_ADDRESS}", \
	"-Ddb.port=${DB_PORT}", \
	"-Ddb.name=${DB_NAME}", \
	"-Ddb.username=${DB_USERNAME}", \
	"-Ddb.password=${DB_PASSWORD}", \
	"-Des.domain=${ES_DOMAIN}", \
  "-Des.port=${ES_PORT}", \
  "-Des.scheme=${ES_SCHEME}", \
	"-Dcache.timeout=${CACHE_TIMEOUT}", \
	"-jar", \
	"exathreat-api.jar" ]