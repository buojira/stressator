# stressator
Project meant to overload internal services
#Tutorial
## Broker Parameter
### broker.host=rabbit.host.address (default: localhost)
### broker.port=5672 (default)
### broker.username=rabbit.user (default: guest)
### broker.password=user_password (default: guest)
### rbbt.prefix= named user to create exchange, queue and callback

# Fill QA with messages
java -jar target/stressator.jar action=ddsrabbitmq duration={time-that-test-will-run-in-seconds}

# Clear Queue
java -jar target/stressator.jar action=clearrabbitmq durations=1;2;3 totals=8;6;6

# Send a message and wait until it reaches the server before sendind another
java -jar target/stressator.jar action=sendnwait duration=0.01

# Send message with random attachments and wait for its return
java -jar target/stressator.jar action=attach duration=0.01 rbbt.prefix=A

# Creates many connections
java -jar target/stressator.jar action=stressconn totals=10 rbbt.prefix=nConn
