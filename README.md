# stressator
Project meant to overload internal services
#Tutorial
## Broker Parameter
### broker.host=rabbit.host.address (default: localhost)
### broker.port=5672 (default)
### broker.username=rabbit.user (default: guest)
### broker.password=user_password (default: guest)

# Fill QA with messages
java -jar target/stressator.jar action=ddsrabbitmq duration={time-that-test-will-run-in-seconds}

# Clear Queue
java -jar target/stressator.jar action=clearrabbitmq durations=1;2;3 totals=8;6;6
